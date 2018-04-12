package de.openfiresource.falarm.ui;

import android.support.v7.util.DiffUtil;

import java.util.List;

import de.openfiresource.falarm.models.database.OperationMessage;

public class OperationListDiffCallback extends DiffUtil.Callback {

    private final List<OperationMessage> oldOperationList;
    private final List<OperationMessage> newOperationList;

    OperationListDiffCallback(List<OperationMessage> oldOperationList, List<OperationMessage> newOperationList) {
        this.oldOperationList = oldOperationList;
        this.newOperationList = newOperationList;
    }

    @Override
    public int getOldListSize() {
        return oldOperationList.size();
    }

    @Override
    public int getNewListSize() {
        return newOperationList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldOperationList.get(oldItemPosition).getId() == newOperationList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldOperationList.get(oldItemPosition).equals(newOperationList.get(newItemPosition));
    }
}
