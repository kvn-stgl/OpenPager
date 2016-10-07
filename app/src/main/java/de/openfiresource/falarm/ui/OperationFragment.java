package de.openfiresource.falarm.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.service.AlarmService;

/**
 * Created by stieglit on 12.08.2016.
 */
public class OperationFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ALARM = "alarm";
    private static final String ARG_STARTTIME = "starttime";

    private Unbinder mUnbinder;
    private String mTitle;
    private String mMessage;
    private Date mStartTime;
    private Boolean mIsAlarm;
    private Timer mTimer;

    @BindView(R.id.section_title)
    TextView textViewTitle;

    @BindView(R.id.section_message)
    TextView textViewMessage;

    @BindView(R.id.section_timer)
    TextView textViewTimer;

    @BindView(R.id.operation_received)
    Button buttonOperationReceived;

    public OperationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title     Title of the operation.
     * @param message   Message of the operation.
     * @param startTime Time when the operation started.
     * @param alarm     Is ooperation an active alarm
     * @return A new instance of fragment OperationFragment.
     */
    public static OperationFragment newInstance(String title, String message, long startTime, boolean alarm) {
        OperationFragment fragment = new OperationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putLong(ARG_STARTTIME, startTime);
        args.putBoolean(ARG_ALARM, alarm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mMessage = getArguments().getString(ARG_MESSAGE);
            mStartTime = new Date(getArguments().getLong(ARG_STARTTIME, 0));
            mIsAlarm = getArguments().getBoolean(ARG_ALARM, false);
        }

    }

    @OnClick(R.id.operation_received)
    public void operationReceived(View view) {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        getActivity().stopService(intent);

        buttonOperationReceived.setVisibility(View.GONE);
        mIsAlarm = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_operation, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        textViewTitle.setText(mTitle);
        textViewMessage.setText(mMessage);

        if (!mIsAlarm) {
            buttonOperationReceived.setVisibility(View.GONE);
        } else {
            textViewTimer.setTextColor(Color.RED);
        }

        int period = 1000; // repeat every sec.
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Date actual = new Date();
                long diffInSeconds = (actual.getTime() - mStartTime.getTime()) / 1000;
                long diff[] = new long[]{0, 0, 0, 0};
                /* sec */
                diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
                /* min */
                diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
                /* hours */
                diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
                /* days */
                diff[0] = (diffInSeconds = (diffInSeconds / 24));

                //String
                String text = "vor ";
                if (diff[0] > 0) text += String.format("%d Tag%s, ", diff[0], diff[0] > 1 ? "e" : "");
                if (diff[1] > 0) text +=  String.format("%d Stunde%s, ", diff[1], diff[1] > 1 ? "n" : "");
                if (diff[2] > 0) text +=  String.format("%d Minute%s, ", diff[2], diff[2] > 1 ? "n" : "");

                final String finalText = text + String.format("%d Sekunde%s", diff[3], diff[3] > 1 ? "n" : "");

                getActivity().runOnUiThread(() -> {
                    textViewTimer.setText(finalText);
                });
            }
        }, 0, period);

        return view;
    }

    @Override
    public void onDestroyView() {
        mTimer.cancel();

        if (mUnbinder != null)
            mUnbinder.unbind();
        
        super.onDestroyView();
    }
}
