package com.github.money.keeper.service;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.service.UnifiedTransaction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface BankStatementUploadService {

    /**
     * Upload bank statement to specified account
     *
     * @param account account
     * @param data    input stream with bank statement data
     * @return list of found duplicates
     */
    List<UnifiedTransaction> uploadFile(Account account, InputStream data) throws IOException;

}
