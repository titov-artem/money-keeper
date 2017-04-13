package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.storage.TransactionRepo;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class TransactionServiceImplTest {

    private TransactionServiceImpl transactionService;
    private TransactionRepo transactionRepo;
    private StoreService storeService;

    @Before
    public void setUp() {
        transactionService = new TransactionServiceImpl(transactionRepo, storeService);
        storeService = mock(StoreService.class);
        transactionRepo = mock(TransactionRepo.class);

//        transactionService.setStoreService(storeService);
//        transactionService.setTransactionRepo(transactionRepo);
    }

    @Test
    public void testGetDuplicates() throws Exception {
//        LocalDate from = LocalDate.now();
//        LocalDate to = from.plusDays(1);
//
//        when(transactionRepo.load(from, to)).thenReturn(Lists.newArrayList(
//                new RawTransaction(1L, 1, from, new SalePoint("a", "b"), BigDecimal.valueOf(1), "", "1"),
//                new RawTransaction(2L, 1, from, new SalePoint("a", "b"), BigDecimal.valueOf(1), "", "2"),
//                new RawTransaction(3L, 1, from, new SalePoint("a1", "b"), BigDecimal.valueOf(1), "", "3"),
//                new RawTransaction(4L, 1, from, new SalePoint("a", "b1"), BigDecimal.valueOf(1), "", "4"),
//                new RawTransaction(5L, 1, from, new SalePoint("a", "b"), BigDecimal.valueOf(2), "", "5"),
//                new RawTransaction(6L, 1, to, new SalePoint("c", "b"), BigDecimal.valueOf(1), "", "1"),
//                new RawTransaction(7L, 1, to, new SalePoint("a1", "b"), BigDecimal.valueOf(1), "", "2"),
//                new RawTransaction(8L, 1, to, new SalePoint("c", "b"), BigDecimal.valueOf(1), "", "3"),
//                new RawTransaction(9L, 1, to, new SalePoint("c", "b"), BigDecimal.valueOf(1), "", "4"),
//                new RawTransaction(10L, 1, to, new SalePoint("a1", "b"), BigDecimal.valueOf(1), "", "5"),
//                new RawTransaction(11L, 1, to, new SalePoint("c", "b"), BigDecimal.valueOf(1), "", "6"),
//                new RawTransaction(12L, 1, to, new SalePoint("a1", "b"), BigDecimal.valueOf(1), "", "7"),
//                new RawTransaction(13L, 1, to, new SalePoint("c", "b"), BigDecimal.valueOf(1), "", "8")
//        ));
//        when(storeService.getStoreInjector()).thenReturn(source -> new UnifiedTransaction(
//                source,
//                new Store(
//                        source.getSalePoint().getName(),
//                        source.getSalePoint().getRawCategory(),
//                        Collections.singleton(source.getSalePoint())
//                )
//        ));
//
//        List<UnifiedTransaction> duplicates = transactionService.getDuplicates(from, to).stream()
//                .sorted((u1, u2) -> {
//                    int compareResult = u1.getDate().compareTo(u2.getDate());
//                    if (compareResult != 0) return compareResult;
//                    return u1.getSalePoint().getName().compareTo(u2.getSalePoint().getName());
//                })
//                .collect(toList());
//
//        assertThat(duplicates.size(), is(10));
//        assertTransaction(duplicates.get(0), from, "a", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(1), from, "a", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(2), to, "a1", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(3), to, "a1", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(4), to, "a1", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(5), to, "c", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(6), to, "c", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(7), to, "c", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(8), to, "c", "b", BigDecimal.valueOf(1));
//        assertTransaction(duplicates.get(9), to, "c", "b", BigDecimal.valueOf(1));
    }

    private void assertTransaction(UnifiedTransaction transaction,
                                   LocalDate date,
                                   String name,
                                   String categoryDescription,
                                   BigDecimal amount) {
        assertThat(transaction.getDate(), is(date));
        assertThat(transaction.getStore().getName(), is(name));
        assertThat(transaction.getStore().getCategoryId(), is(categoryDescription));
        assertThat(transaction.getAmount(), is(amount));
    }
}