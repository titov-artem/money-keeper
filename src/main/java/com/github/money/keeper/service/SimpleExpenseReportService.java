package com.github.money.keeper.service;

import com.github.money.keeper.clusterization.SimpleByNameClusterizer;
import com.github.money.keeper.clusterization.StoreClusterizer;
import com.github.money.keeper.contoller.ClusterizationConctroller;
import com.github.money.keeper.contoller.ClusterizationConctroller.ClusterizationResult;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.model.report.SimpleExpenseReport;
import com.github.money.keeper.model.report.SimpleExpenseReport.Builder;
import com.github.money.keeper.parser.ParsingResult;
import com.github.money.keeper.parser.RaiffeisenTransactionParser;
import com.github.money.keeper.parser.TransactionParser;
import com.github.money.keeper.serializer.html.SimpleExpenseReportHtmlSerializer;
import com.github.money.keeper.serializer.html.TemplateSupport;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SimpleExpenseReportService {

    private TransactionParser parser;
    private ClusterizationConctroller clusterizationConctroller;

    private StoreRepo storeRepo;
    private CategoryRepo categoryRepo;

    public SimpleExpenseReport buildSimpleReport(String fileName) {
        ParsingResult result = parseFile(fileName);

        List<Store> stores = storeRepo.loadAll();
        ClusterizationResult clusterizationResult = clusterizationConctroller.clusterize(
                stores,
                result.getTransactions().stream().map(RawTransaction::getSalePoint).collect(toList())
        );


        Builder builder = new Builder(result.getAccount(), categoryRepo.loadAll());
        result.getTransactions().stream()
                .map(t -> new UnifiedTransaction(t, clusterizationResult.getStore(t.getSalePoint())))
                .forEach(builder::append);
        return builder.build();
    }

    private ParsingResult parseFile(String fileName) {
        ParsingResult result;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName))) {
            result = parser.parse(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input file", e);
        }
        return result;
    }

    public void setParser(TransactionParser parser) {
        this.parser = parser;
    }

    public void setClusterizationConctroller(ClusterizationConctroller clusterizationConctroller) {
        this.clusterizationConctroller = clusterizationConctroller;
    }

    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }

    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public static void main(String[] args) throws IOException {
        SimpleExpenseReportService service = new SimpleExpenseReportService();

        SimpleByNameClusterizer clusterizer = new SimpleByNameClusterizer();
        StoreClusterizer storeClusterizer = new StoreClusterizer(clusterizer);
        ClusterizationConctroller clusterizationConctroller = new ClusterizationConctroller();
        clusterizationConctroller.setStoreClusterizer(storeClusterizer);
        service.setClusterizationConctroller(clusterizationConctroller);

        service.setParser(new RaiffeisenTransactionParser());

        service.setStoreRepo(new StoreRepo() {
            @Override public void save(Iterable<Store> stores) {
            }

            @Override public List<Store> loadAll() {
                return Collections.emptyList();
            }
        });
        service.setCategoryRepo(new CategoryRepo() {
            @Override public void save(Iterable<Category> categories) {
            }

            @Override public List<Category> loadAll() {
                return Collections.emptyList();
            }
        });

        SimpleExpenseReport report = service.buildSimpleReport("/Users/scorpion/Downloads/rba-card-statement-09-12.csv");

        TemplateSupport templateSupport = new TemplateSupport();
        templateSupport.initTemplateEngine();

        SimpleExpenseReportHtmlSerializer serializer = new SimpleExpenseReportHtmlSerializer();
        serializer.setTemplateSupport(templateSupport);

        String serializedReport = serializer.serialize(report);
        try (BufferedWriter out = new BufferedWriter(new FileWriter("out.html"))) {
            out.write(serializedReport);
        }
    }
}
