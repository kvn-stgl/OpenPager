package de.openfiresource.openpager.ui.settings;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.openfiresource.openpager.R;
import de.openfiresource.openpager.models.database.OperationRule;

public class RuleRecyclerViewAdapter
        extends RecyclerView.Adapter<RuleRecyclerViewAdapter.ViewHolder> {

    private final List<OperationRule> operationRules;
    private final FragmentManager fragmentManager;
    private final Context context;

    RuleRecyclerViewAdapter(Context context, FragmentManager fragmentManager, List<OperationRule> operationRules) {
        this.context = context;
        this.operationRules = operationRules;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.operationRule = operationRules.get(position);
        holder.countView.setText(Integer.toString(position + 1));
        holder.contentView.setText(context.getString(R.string.operation_rules_name,
                holder.operationRule.getTitle(),
                holder.operationRule.getStartTime(),
                holder.operationRule.getStopTime()));

        holder.view.setOnClickListener((clickedView) -> {
            if (fragmentManager != null) {
                Bundle arguments = new Bundle();
                arguments.putLong(RuleDetailFragment.ARG_RULE_ID, holder.operationRule.getId());
                RuleDetailFragment fragment = new RuleDetailFragment();
                fragment.setArguments(arguments);
                fragmentManager.beginTransaction()
                        .replace(R.id.rule_detail_container, fragment)
                        .commit();
            } else {
                Context context = clickedView.getContext();
                Intent intent = new Intent(context, RuleDetailActivity.class);
                intent.putExtra(RuleDetailFragment.ARG_RULE_ID, holder.operationRule.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return operationRules.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView countView;

        TextView contentView;

        View view;

        OperationRule operationRule;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            countView = view.findViewById(R.id.id);
            contentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "countView=" + countView +
                    ", contentView=" + contentView +
                    ", view=" + view +
                    ", operationRule=" + operationRule +
                    '}';
        }
    }
}
