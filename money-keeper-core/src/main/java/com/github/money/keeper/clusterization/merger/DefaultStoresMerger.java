package com.github.money.keeper.clusterization.merger;

import com.github.money.keeper.clusterization.ClusterizableStore;
import com.github.money.keeper.clusterization.StorePrototype;
import com.github.money.keeper.clusterization.merger.action.StoreMergeAction;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.service.CategoryService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

@Service
public class DefaultStoresMerger implements StoresMerger {

    private static final List<StoreProcessor> STORE_PROCESSORS = Lists.newArrayList(
            new ManualyCreatedStoreProcessor(),
            new RegularStoreProcessor()
    );

    private final CategoryService categoryService;

    @Inject
    public DefaultStoresMerger(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public List<StoreMergeAction> merge(Iterable<ClusterizableStore> stores, Iterable<StorePrototype> prototypes) {

        Map<SalePoint, StorePrototype> pointToPrototype = StreamSupport.stream(prototypes.spliterator(), false)
                .flatMap(
                        prototype -> prototype.getSalePoints().stream()
                                .map(sp -> Tuple.tuple(sp, prototype))
                ).collect(toMap(Tuple2::v1, Tuple2::v2));


        // actions to perform on currently existing stores
        List<StoreMergeAction> storeActions = StreamSupport.stream(stores.spliterator(), false)
                .flatMap(store ->
                        STORE_PROCESSORS.stream()
                                .filter(processor -> processor.isApplicable(store))
                                .map(processor -> processor.process(store, pointToPrototype))
                                .flatMap(List::stream)
                )
                .collect(toList());

        // Now we need to build create action on left prototypes in global mapping. To do it we also need to
        // determine category of each prototype.
        //
        // * If prototype has sale points from existing store, then we can talk that each point has category and we can
        // use category to which belongs the most of sale points of the prototype
        // * If there Category for prototype's category, we can use it
        // * Otherwise create a new Category for this prototype using name from prototype
        //
        // During this process we also need to hold Categories to create set to prevent duplications, during category
        // creation

        Set<StorePrototype> prototypesToCreate = pointToPrototype.values().stream().collect(toSet());
        Map<SalePoint, Category> pointToCategory = StreamSupport.stream(stores.spliterator(), false)
                .flatMap(
                        store -> store.getSalePoints().stream()
                                .map(sp -> Tuple.tuple(sp, store.getCategory()))
                ).collect(toMap(Tuple2::v1, Tuple2::v2));
        Map<String, Category> existingCategories = categoryService.findByNames(
                prototypesToCreate.stream().map(StorePrototype::getCategory).collect(toList())
        );

        Set<Category> categoriesToCreate = new HashSet<>();
        List<StorePrototype> prototypeNeedNewCategory = new ArrayList<>();
        for (StorePrototype prototype : prototypesToCreate) {
            // try to determine category via sale points
            Optional<Category> categoryOpt = prototype.getSalePoints().stream()
                    // group points by category (category -> list of points)
                    .collect(groupingBy(pointToCategory::get))
                    .entrySet()
                    .stream()
                    // count points in each category: Tuple(category -> count)
                    .map(entry -> Tuple.tuple(entry.getKey(), entry.getValue().size()))
                    // select category with max points
                    .max(Comparator.comparing(Tuple2::v2))
                    .map(Tuple2::v1);

            if (!categoryOpt.isPresent()) {
                // try to find existing category with same name
                categoryOpt = Optional.ofNullable(existingCategories.get(prototype.getCategory()));
            }

            if (!categoryOpt.isPresent()) {
                // queue category creation
                categoriesToCreate.add(new Category(prototype.getCategory(), Collections.singleton(prototype.getCategory())));
                prototypeNeedNewCategory.add(prototype);
                continue;
            }
            storeActions.add(StoreMergeAction.create(prototype, categoryOpt.get()));
        }
        // create necessary categories
        Map<String, Category> createdCategoryByName = categoryService.save(categoriesToCreate).stream()
                .collect(toMap(Category::getName, Function.identity()));
        // create previously skipped actions with just created categories
        for (StorePrototype prototype : prototypeNeedNewCategory) {
            storeActions.add(StoreMergeAction.create(prototype, createdCategoryByName.get(prototype.getCategory())));
        }
        return storeActions;
    }

    private interface StoreProcessor {

        /**
         * Process current store and generate a set of action that have to be performed with this store. Also remove
         * all sale points, that are now considered to belong to this store from global mapping
         *
         * @param store            store
         * @param pointToPrototype global point to prototype mapping
         * @return set of actions to perform
         */
        List<StoreMergeAction> process(ClusterizableStore store,
                                       Map<SalePoint, StorePrototype> pointToPrototype);

        boolean isApplicable(ClusterizableStore store);
    }

    /**
     * We have manually created store. So its sale points can belong only to it and can't be
     * moved to any other store. So we have two cases:
     * <ul>
     * <li>All sale point from manually created store belong to single prototype (possibly with different name).<br/>
     * In this case just assign all sale points from prototype to store
     * </li>
     * <li>Sale points from manually created store splitted between some 2 or more prototypes.<br/>
     * In this case remove them from all prototypes and suppose that prototypes are
     * independent stores.
     */
    private static final class ManualyCreatedStoreProcessor implements StoreProcessor {
        @Override
        public List<StoreMergeAction> process(ClusterizableStore store,
                                              Map<SalePoint, StorePrototype> pointToPrototype) {
            Set<StorePrototype> prototypes = store.getSalePoints().stream()
                    .map(pointToPrototype::get)
                    .collect(toSet());

            // Current store sale point need not any further processing, so remove them from mapping
            store.getSalePoints().forEach(pointToPrototype::remove);

            if (prototypes.size() == 1) {
                // Store is manually created so renaming is forbidden!
                return ImmutableList.of(StoreMergeAction.assign(store, prototypes.iterator().next().getSalePoints()));
            }

            // Remove sale point of current store from all prototypes
            prototypes.forEach(p -> p.removeAll(store.getSalePoints()));

            return Collections.emptyList();
        }

        @Override public boolean isApplicable(ClusterizableStore store) {
            return store.isManuallyCreated();
        }
    }

    /**
     * We need to find successor of the store between prototypes. To do it find the prototype, that contains most part
     * of store's sale points. This prototype will successor. So we just need to update store to it successor
     */
    private static final class RegularStoreProcessor implements StoreProcessor {
        @Override
        public List<StoreMergeAction> process(ClusterizableStore store,
                                              Map<SalePoint, StorePrototype> pointToPrototype) {
            Map<StorePrototype, List<SalePoint>> prototypeToPoints = store.getSalePoints().stream()
                    .collect(groupingBy(pointToPrototype::get));

            // Successor is the prototype with max count of points from current store
            //noinspection OptionalGetWithoutIsPresent successor present because there exactly on prototype for each point and no stores without points
            StorePrototype successor = prototypeToPoints.entrySet().stream()
                    .map(entry -> Tuple.tuple(entry.getKey(), entry.getValue().size()))
                    .max(Comparator.comparing(Tuple2::v2))
                    .map(Tuple2::v1)
                    .get();

            // Remove all successor sale points from further processing
            successor.getSalePoints().forEach(pointToPrototype::remove);

            return ImmutableList.of(
                    StoreMergeAction.rename(store, successor.getName()),
                    StoreMergeAction.assign(store, successor.getSalePoints())
            );

        }

        @Override public boolean isApplicable(ClusterizableStore store) {
            return !store.isManuallyCreated();
        }
    }

}
