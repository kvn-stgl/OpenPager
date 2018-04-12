package de.openfiresource.falarm.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import de.openfiresource.falarm.R;
import de.openfiresource.falarm.dagger.Injectable;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.ui.custom.SimpleDividerItemDecoration;
import de.openfiresource.falarm.ui.operation.OperationActivity;

public class OperationListFragment extends Fragment implements Injectable {

    @Inject
    AppDatabase database;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operation_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_operations);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(requireActivity()));

        OperationListAdapter adapter = new OperationListAdapter();
        adapter.setOperationEventListener(new OperationListAdapter.OperationEventListener() {
            @Override
            public void onSelectClick(OperationMessage operation) {
                Intent intent = new Intent(requireActivity(), OperationActivity.class);
                intent.putExtra(OperationActivity.OPERATION_ID, operation.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(OperationMessage operation) {
                database.operationMessageDao().deleteOperationMessage(operation);
            }
        });

        recyclerView.setAdapter(adapter);

        OperationListViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(OperationListViewModel.class);
        viewModel.getOperationMessageList().observe(this, adapter::setOperations);
    }
}
