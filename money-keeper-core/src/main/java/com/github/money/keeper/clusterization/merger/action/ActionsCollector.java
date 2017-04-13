package com.github.money.keeper.clusterization.merger.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionsCollector {

    private final List<AssignPointsAction> assignPointsActions = new ArrayList<>();
    private final List<CreateStoreAction> createStoreActions = new ArrayList<>();
    private final List<RenameStoreAction> renameStoreActions = new ArrayList<>();

    public void collect(StoreMergeAction action) {
        action.apply(this);
    }

    public void collect(AssignPointsAction action) {
        assignPointsActions.add(action);
    }

    public void collect(CreateStoreAction action) {
        createStoreActions.add(action);
    }

    public void collect(RenameStoreAction action) {
        renameStoreActions.add(action);
    }

    public List<AssignPointsAction> getAssignPointsActions() {
        return Collections.unmodifiableList(assignPointsActions);
    }

    public List<CreateStoreAction> getCreateStoreActions() {
        return Collections.unmodifiableList(createStoreActions);
    }

    public List<RenameStoreAction> getRenameStoreActions() {
        return Collections.unmodifiableList(renameStoreActions);
    }
}
