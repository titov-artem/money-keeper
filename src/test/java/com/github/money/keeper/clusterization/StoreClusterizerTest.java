package com.github.money.keeper.clusterization;

import com.github.money.keeper.clusterization.StoreClusterizer.ClusterizationResult;
import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.parser.ParserType;
import com.github.money.keeper.parser.ParsingResult;
import com.github.money.keeper.parser.RaiffeisenTransactionParser;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Artem Titov
 */
public class StoreClusterizerTest {

    @Test
    public void clusterize() throws Exception {
        StoreClusterizer clusterizer = new StoreClusterizer(new SimpleByNameClusterizer());

        ClusterizationResult result = clusterizer.clusterize(ImmutableList.of(), loadPointsFromStatement("src/test/resources/card-statement-20151226.csv"));

        assertThat(result.getStores().size(), is(4));
        System.out.println(result.getStores());
    }

    private List<SalePoint> loadPointsFromStatement(String statementFile) throws IOException {
        try (InputStream in = new FileInputStream(statementFile)) {
            ParsingResult result = new RaiffeisenTransactionParser().parse(new Account(1L, "1234", ParserType.RAIFFEISEN_CARD), in);
            return result.getTransactions().stream().map(RawTransaction::getSalePoint).collect(toList());
        }
    }
}