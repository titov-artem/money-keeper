package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.service.BankStatementUploadService;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.view.contoller.dto.StatementUploadResult;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@Controller
@Path("/bank/statement")
public class BankStatementController implements REST {
    private static final Logger log = LoggerFactory.getLogger(BankStatementController.class);

    private final AccountRepo accountRepo;
    private final BankStatementUploadService bankStatementUploadService;

    @Inject
    public BankStatementController(AccountRepo accountRepo,
                                   BankStatementUploadService bankStatementUploadService) {
        this.accountRepo = accountRepo;
        this.bankStatementUploadService = bankStatementUploadService;
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    @Path("/upload/{accountId}")
    public StatementUploadResult upload(@PathParam("accountId") Long accountId,
                                        @Multipart("statement") Attachment attachment) {
        Account account = accountRepo.get(accountId).orElseThrow(NotFoundException::new);
        if (account == null) {
            return StatementUploadResult.failed();
        }
        InputStream data = attachment.getObject(InputStream.class);

        return uploadFile(account, data);
    }

    private StatementUploadResult uploadFile(Account account, InputStream data) {
        try {
            List<UnifiedTransaction> rawTransactions = bankStatementUploadService.uploadFile(account, data);
            return StatementUploadResult.success(rawTransactions);
        } catch (Exception e) {
            log.error("Failed to parse input stream due to exception", e);
            return StatementUploadResult.failed();
        }
    }

}
