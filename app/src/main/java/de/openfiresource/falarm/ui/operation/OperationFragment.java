package de.openfiresource.falarm.ui.operation;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import de.openfiresource.falarm.dagger.Injectable;
import de.openfiresource.falarm.databinding.FragmentOperationBinding;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.service.AlarmService;
import de.openfiresource.falarm.service.SpeakService;
import de.openfiresource.falarm.utils.Preferences;
import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class OperationFragment extends Fragment implements Injectable {

    private static final String TAG = "OperationFragment";

    private OperationViewModel viewModel;

    @Inject
    AppDatabase database;

    @Inject
    Preferences preferences;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public OperationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OperationFragment.
     */
    public static OperationFragment newInstance() {
        return new OperationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(OperationViewModel.class);

        // Inflate the layout for this fragment
        FragmentOperationBinding binding = FragmentOperationBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.textviewMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferences.getAlarmFontSize());
        binding.buttonConfirmAlarm.setOnClickListener(v -> operationAlarmConfirmed());

        return binding.getRoot();
    }

    public void operationAlarmConfirmed() {
        OperationMessage operation = viewModel.getOperation().getValue();

        if (getActivity() == null || operation == null) {
            return;
        }

        Intent intent = new Intent(getActivity(), AlarmService.class);
        getActivity().stopService(intent);

        operation.setAlarm(false);

        Completable.fromAction(() -> database.operationMessageDao().updateOperationMessage(operation))
                .observeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Timber.d("onComplete: saving operation");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "onError: saving operation");
                    }
                });

        //Start speak service
        Notification notification = Notification.byRule(operation.getRule(), getContext());

        if (notification.isSpeakServiceEnabled() && notification.isPlayingSound()) {
            Intent intentData = new Intent(getContext(), SpeakService.class);
            intentData.putExtra(OperationActivity.OPERATION_ID, operation.getId());
            getActivity().startService(intentData);
        }
    }
}
