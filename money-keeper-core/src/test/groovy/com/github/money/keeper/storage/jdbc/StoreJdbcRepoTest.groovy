package com.github.money.keeper.storage.jdbc

import com.github.money.keeper.app.AppConfig
import com.github.money.keeper.app.JdbcConfig
import com.github.money.keeper.model.core.AbstractEntity
import com.github.money.keeper.model.core.Category
import com.github.money.keeper.model.core.Store
import com.github.money.keeper.storage.CategoryRepo
import com.github.money.keeper.storage.StoreRepo
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.inject.Inject

@ContextConfiguration(classes = [AppConfig, JdbcConfig])
class StoreJdbcRepoTest extends Specification {

    @Inject StoreRepo storeRepo
    @Inject CategoryRepo categoryRepo

    def "save; get; update; get"() {
        given:
        def categories = categoryRepo.save([
                new Category('category1', [] as Set),
                new Category('category2', [] as Set),
                new Category('category3', [] as Set)
        ])
        def store = new Store('name', categories.get(0).getId())

        when:
        def saved = storeRepo.save(store)

        then:
        !AbstractEntity.isFakeId(saved.getId())

        when:
        def loaded = storeRepo.get(saved.getId())

        then:
        loaded.isPresent()
        loaded.get() == saved

        when:
        def updated = storeRepo.save(loaded.get().withName('new-name').withCategory(categories.get(1)))

        then:
        updated.name == 'new-name'
        updated.categoryId == categories.get(1).id

        when:
        loaded = storeRepo.get(saved.id)

        then:
        loaded.isPresent()
        loaded.get() == updated
    }

}
