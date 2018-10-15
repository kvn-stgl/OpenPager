package de.openfiresource.openpager.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.openfiresource.openpager.databinding.ViewOperationItemBinding;
import de.openfiresource.openpager.models.database.OperationMessage;

public class OperationListAdapter extends RecyclerView.Adapter<OperationListAdapter.ViewHolder> {

    private final List<OperationMessage> dataset = new ArrayList<>();

    @Nullable
    private OperationEventListener operationEventListener;

    OperationListAdapter() {

    }

    public void setOperations(List<OperationMessage> operationMessageList) {
        final OperationListDiffCallback diffCallback = new OperationListDiffCallback(this.dataset, operationMessageList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.dataset.clear();
        this.dataset.addAll(operationMessageList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setOperationEventListener(@Nullable OperationEventListener operationEventListener) {
        this.operationEventListener = operationEventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewOperationItemBinding binding = ViewOperationItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding, new OperationEventListener() {
            @Override
            public void onSelectClick(OperationMessage operation) {
                if (operationEventListener != null) {
                    operationEventListener.onSelectClick(operation);
                }
            }

            @Override
            public void onDeleteClick(OperationMessage operation) {
                if (operationEventListener != null) {
                    operationEventListener.onDeleteClick(operation);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setOperationMessage(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewOperationItemBinding binding;

        ViewHolder(ViewOperationItemBinding itemView, @NonNull OperationEventListener listener) {
            super(itemView.getRoot());
            this.binding = itemView;

            itemView.layoutSelect.setOnClickListener(v -> {
                if (binding.getOperation() != null) {
                    listener.onSelectClick(binding.getOperation());
                }
            });

            itemView.layoutDelete.setOnClickListener(v -> {
                if (binding.getOperation() != null) {
                    listener.onDeleteClick(binding.getOperation());
                }
            });
        }

        void setOperationMessage(OperationMessage operationMessage) {
            binding.setOperation(operationMessage);
        }
    }

    interface OperationEventListener {
        void onSelectClick(OperationMessage operation);

        void onDeleteClick(OperationMessage operation);
    }
}
