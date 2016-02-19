package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.storage.TransactionRepo;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionServiceImplTest {

    private TransactionServiceImpl transactionService;
    private TransactionRepo transactionRepo;
    private StoreService storeService;

    @Before
    public void setUp() {
        transactionService = new TransactionServiceImpl();
        storeService = mock(StoreService.class);
        transactionRepo = mock(TransactionRepo.class);

        transactionService.setStoreService(storeService);
        transactionService.setTransactionRepo(transactionRepo);
    }

    @Test
    public void testGetDuplicates() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(1);

        when(transactionRepo.load(from, to)).thenReturn(Lists.newArrayList(
                new RawTransaction(from, new SalePoint("a", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(from, new SalePoint("a", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(from, new SalePoint("a1", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(from, new SalePoint("a", "b1"), BigDecimal.valueOf(1)),
                new RawTransaction(from, new SalePoint("a", "b"), BigDecimal.valueOf(2)),
                new RawTransaction(to, new SalePoint("c", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("a1", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("c", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("c", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("a1", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("c", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("a1", "b"), BigDecimal.valueOf(1)),
                new RawTransaction(to, new SalePoint("c", "b"), BigDecimal.valueOf(1))
        ));
        when(storeService.getStoreInjector()).thenReturn(source -> new UnifiedTransaction(
                source,
                new Store(
                        source.getSalePoint().getName(),
                        source.getSalePoint().getCategoryDescription(),
                        Collections.singleton(source.getSalePoint())
                )
        ));

        List<UnifiedTransaction> duplicates = transactionService.getDuplicates(from, to).stream()
                .sorted((u1, u2) -> {
                    int compareResult = u1.getDate().compareTo(u2.getDate());
                    if (compareResult != 0) return compareResult;
                    return u1.getSalePoint().getName().compareTo(u2.getSalePoint().getName());
                })
                .collect(toList());

        assertThat(duplicates.size(), is(10));
        assertTransaction(duplicates.get(0), from, "a", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(1), from, "a", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(2), to, "a1", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(3), to, "a1", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(4), to, "a1", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(5), to, "c", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(6), to, "c", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(7), to, "c", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(8), to, "c", "b", BigDecimal.valueOf(1));
        assertTransaction(duplicates.get(9), to, "c", "b", BigDecimal.valueOf(1));
    }

    private void assertTransaction(UnifiedTransaction transaction,
                                   LocalDate date,
                                   String name,
                                   String categoryDescription,
                                   BigDecimal amount) {
        assertThat(transaction.getDate(), is(date));
        assertThat(transaction.getStore().getName(), is(name));
        assertThat(transaction.getStore().getCategoryDescription(), is(categoryDescription));
        assertThat(transaction.getAmount(), is(amount));
    }
}