package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.core.SalePoint;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Artem Titov
 */
public class StoreClusterizerTest {

    @Test
    public void clusterize() throws Exception {
//        StoreClusterizer clusterizer = new StoreClusterizer(new SimpleByNameClusterizer(), storesMerger);

//        ClusterizationResult result = clusterizer.clusterize(ImmutableList.of(), loadPointsFromStatement("src/test/resources/card-statement-20151226.csv"));

//        assertThat(result.getStores().size(), is(4));
//        System.out.println(result.getStores());
    }

    private List<SalePoint> loadPointsFromStatement(String statementFile) throws IOException {
//        try (InputStream in = new FileInputStream(statementFile)) {
//            ParsingResult result = new RaiffeisenTransactionParser().parse(new Account(1, "1234", ParserType.RAIFFEISEN_CARD), in);
//            return result.getTransactions().stream().map(RawTransaction::getSalePoint).collect(toList());
//        }
        return null;
    }
}