package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.parser.AbstractTransactionParser;
import com.github.money.keeper.parser.ParsingResult;
import com.github.money.keeper.parser.TransactionParserProvider;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.storage.TransactionRepo;
import com.github.money.keeper.view.contoller.dto.StatementUploadResult;
import com.google.common.collect.ImmutableList;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Path("/bank/statement")
public class BankStatementController implements REST {
    private static final Logger log = LoggerFactory.getLogger(BankStatementController.class);

    private TransactionParserProvider transactionParserProvider;
    private StoreService storeService;
    private CategoryService categoryService;
    private TransactionService transactionService;
    private TransactionRepo transactionRepo;
    private AccountRepo accountRepo;

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    @Path("/upload/{accountId}")
    public StatementUploadResult upload(@PathParam("accountId") Integer accountId,
                                        @Multipart("statement") Attachment attachment) {
        Account account = accountRepo.load(accountId);
        if (account == null) {
            return StatementUploadResult.failed();
        }
        InputStream data = attachment.getObject(InputStream.class);

        return uploadFile(account, data);
    }

    private StatementUploadResult uploadFile(Account account, InputStream data) {
        // todo move this logic from controller
        AbstractTransactionParser parser = transactionParserProvider.getParser(account.getParserType());
        try {
            ParsingResult result = parser.parse(account, data);
            ImmutableList<RawTransaction> transactions = result.getTransactions();
            transactionRepo.save(transactions);
            storeService.rebuildFromTransactionsLog();
            categoryService.updateCategories();

            LocalDate from = LocalDate.MAX;
            LocalDate to = LocalDate.MIN;
            for (final RawTransaction transaction : transactions) {
                if (transaction.getDate().compareTo(from) < 0) from = transaction.getDate();
                if (transaction.getDate().compareTo(to) > 0) to = transaction.getDate();
            }
            List<UnifiedTransaction> duplicates = transactionService.getDuplicates(from, to);

            return StatementUploadResult.success(duplicates);
        } catch (Exception e) {
            log.error("Failed to parse input stream due to exception", e);
            return StatementUploadResult.failed();
        }
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

    @Required
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Required
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Required
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
}
