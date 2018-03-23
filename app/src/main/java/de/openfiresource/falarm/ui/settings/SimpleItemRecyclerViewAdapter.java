package de.openfiresource.falarm.ui.settings;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationRule;

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<OperationRule> mValues;
    private final FragmentManager mFragmentManager;

    public SimpleItemRecyclerViewAdapter(FragmentManager fragmentManager, Context context) {
        mValues = AppDatabase.getInstance(context).operationRuleDao().getAll();
        mFragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rule_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCountView.setText(Integer.toString(position + 1));
        holder.mContentView.setText(mValues.get(position).toString());

        holder.mView.setOnClickListener((v) -> {
                if (mFragmentManager != null) {
                    Bundle arguments = new Bundle();
                    arguments.putLong(RuleDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                    RuleDetailFragment fragment = new RuleDetailFragment();
                    fragment.setArguments(arguments);
                    mFragmentManager.beginTransaction()
                            .replace(R.id.rule_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RuleDetailActivity.class);
                    intent.putExtra(RuleDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                    context.startActivity(intent);
                }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.id)
        TextView mCountView;

        @BindView(R.id.content)
        TextView mContentView;

        OperationRule mItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
