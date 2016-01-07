package com.github.money.keeper.contoller;

import com.github.money.keeper.parser.ParsingResult;
import com.github.money.keeper.parser.SupportedParsers;
import com.github.money.keeper.parser.TransactionParser;
import com.github.money.keeper.parser.TransactionParserProvider;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.storage.TransactionRepo;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.File;
import java.io.FileInputStream;

@Path("/bank/statement")
public class BankStatementController {
    private static final Logger log = LoggerFactory.getLogger(BankStatementController.class);

    private TransactionParserProvider transactionParserProvider;
    private TransactionRepo transactionRepo;
    private StoreService storeService;

    @POST
    public UploadResult upload(SupportedParsers parserType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter(parserType.getFileExtensionPattern(), parserType.getFileExtensionPattern())
        );
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return uploadFile(parserType, selectedFile);
        } else {
            return UploadResult.NO_FILE_CHOSEN;
        }
    }

    private UploadResult uploadFile(SupportedParsers parserType, File selectedFile) {
        TransactionParser parser = transactionParserProvider.getParser(parserType);
        try {
            ParsingResult result = parser.parse(new FileInputStream(selectedFile));
            transactionRepo.save(result.getTransactions());
            storeService.rebuildFromTransactionsLog();
            return UploadResult.SUCCESS;
        } catch (Exception e) {
            log.error("Failed to parse file " + selectedFile + " due toe exception", e);
            return UploadResult.FAILED;
        }
    }

    public enum UploadResult {
        SUCCESS, FAILED, NO_FILE_CHOSEN
    }

    @Required
    public void setTransactionParserProvider(TransactionParserProvider transactionParserProvider) {
        this.transactionParserProvider = transactionParserProvider;
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Required
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }
}
