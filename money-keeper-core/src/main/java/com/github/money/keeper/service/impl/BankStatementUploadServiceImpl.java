package com.github.money.keeper.service.impl;

import com.github.money.keeper.clusterization.ClusterizableStore;
import com.github.money.keeper.clusterization.StoreClusterizer;
import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.model.service.DuplicateTransactions;
import com.github.money.keeper.parser.AbstractTransactionParser;
import com.github.money.keeper.parser.ParsedTransaction;
import com.github.money.keeper.parser.ParsingResult;
import com.github.money.keeper.parser.TransactionParserProvider;
import com.github.money.keeper.service.BankStatementUploadService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.SalePointRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.TransactionRepo;
import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Service
public class BankStatementUploadServiceImpl implements BankStatementUploadService {

    private final TransactionParserProvider transactionParserProvider;
    private final TransactionService transactionService;
    private final TransactionRepo transactionRepo;
    private final SalePointRepo salePointRepo;
    private final StoreRepo storeRepo;
    private final CategoryRepo categoryRepo;
    private final StoreClusterizer storeClusterizer;

    @Inject
    public BankStatementUploadServiceImpl(TransactionParserProvider transactionParserProvider,
                                          TransactionService transactionService,
                                          TransactionRepo transactionRepo,
                                          SalePointRepo salePointRepo,
                                          StoreRepo storeRepo,
                                          CategoryRepo categoryRepo,
                                          StoreClusterizer storeClusterizer) {
        this.transactionParserProvider = transactionParserProvider;
        this.transactionService = transactionService;
        this.transactionRepo = transactionRepo;
        this.salePointRepo = salePointRepo;
        this.storeRepo = storeRepo;
        this.categoryRepo = categoryRepo;
        this.storeClusterizer = storeClusterizer;
    }

    @Override
    public List<DuplicateTransactions> uploadFile(Account account, InputStream data) throws IOException {
        AbstractTransactionParser parser = transactionParserProvider.getParser(account.getParserType());
        ParsingResult result = parser.parse(account, data);
        ImmutableList<ParsedTransaction> parsedTransactions = result.getTransactions();

        createSalePoints(parsedTransactions);
        Map<SalePointKey, SalePoint> salePoints = loadSalePoints(SalePointKey::of);
        List<RawTransaction> rawTransactions = parsedTransactions.stream()
                .map(
                        tx -> new RawTransaction(
                                account.getId(),
                                tx.getDate(),
                                salePoints.get(SalePointKey.of(tx)).getId(),
                                tx.getAmount(),
                                tx.getFileHash(),
                                tx.getUploadId()
                        )
                )
                .collect(toList());
        transactionRepo.save(rawTransactions);

        Map<Long, List<SalePoint>> salePointsByStoreId = salePointRepo.getAll().stream()
                .filter(sP -> sP.getStoreId() != null)
                .collect(groupingBy(SalePoint::getStoreId));
        Map<Long, Category> categoriesById = categoryRepo.getAll().stream().collect(toMap(Category::getId, identity()));
        List<ClusterizableStore> stores = storeRepo.getAll().stream()
                .map(
                        store -> {
                            List<SalePoint> points = salePointsByStoreId.get(store.getId());
                            return new ClusterizableStore(
                                    store,
                                    categoriesById.get(store.getCategoryId()),
                                    points == null ? new HashSet<>() : new HashSet<>(points)
                            );
                        }
                )
                .collect(toList());

        storeClusterizer.clusterize(stores, salePoints.values());

        LocalDate from = LocalDate.MAX;
        LocalDate to = LocalDate.MIN;
        for (final ParsedTransaction transaction : parsedTransactions) {
            if (transaction.getDate().compareTo(from) < 0) from = transaction.getDate();
            if (transaction.getDate().compareTo(to) > 0) to = transaction.getDate();
        }

        return transactionService.getDuplicates(from, to);
    }

    private void createSalePoints(ImmutableList<ParsedTransaction> transactions) {
        Map<SalePointKey, SalePoint> existingSalePoints = loadSalePoints(SalePointKey::of);
        Set<SalePoint> salePointsToCreate = transactions.stream()
                .map(SalePointKey::of)
                .filter(key -> !existingSalePoints.containsKey(key))
                .map(SalePointKey::toSalePoint)
                .collect(toSet());
        salePointRepo.save(salePointsToCreate);
    }

    private <K> Map<K, SalePoint> loadSalePoints(Function<SalePoint, K> keyFunction) {
        return salePointRepo.getAll().stream()
                .collect(toMap(keyFunction, identity()));
    }

    private static final class SalePointKey {
        private final String name;
        private final String category;

        private SalePointKey(String name, String category) {
            this.name = name;
            this.category = category;
        }

        public static SalePointKey of(SalePoint salePoint) {
            return new SalePointKey(salePoint.getName(), salePoint.getRawCategory());
        }

        public static SalePointKey of(ParsedTransaction tx) {
            return new SalePointKey(tx.getSalePointName(), tx.getCategory());
        }

        public SalePoint toSalePoint() {
            return new SalePoint(name, category);
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SalePointKey that = (SalePointKey) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(category, that.category);
        }

        @Override public int hashCode() {
            return Objects.hash(name, category);
        }
    }
}
