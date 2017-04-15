package com.github.money.keeper.model.service;

import java.util.Collections;
import java.util.List;

public class DuplicateTransactions {

    private final UnifiedTransaction origin;
    private final List<UnifiedTransaction> duplicates;

    public DuplicateTransactions(UnifiedTransaction origin,
                                 List<UnifiedTransaction> duplicates) {
        this.origin = origin;
        this.duplicates = duplicates;
    }

    public UnifiedTransaction getOrigin() {
        return origin;
    }

    public List<UnifiedTransaction> getDuplicates() {
        return Collections.unmodifiableList(duplicates);
    }
}
