package de.openfiresource.falarm.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.OperationMessage;

public class OperationActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TYPE_ALARM = "alarm";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private OperationMessage mOperationMessage;
    private boolean mIsAlarm;
    private boolean mHaveMap;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mIsAlarm = getIntent().getBooleanExtra(EXTRA_TYPE_ALARM, false);
        if (mIsAlarm) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        long notificationId = getIntent().getLongExtra(EXTRA_ID, 0);
        if (notificationId != 0) {
            mOperationMessage = OperationMessage.findById(OperationMessage.class, notificationId);

            mHaveMap = true;
            if (TextUtils.isEmpty(mOperationMessage.getLatlng()))
                mHaveMap = false;

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mHaveMap);

            // Set up the ViewPager with the sections adapter.
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout.setupWithViewPager(mViewPager);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operation, menu);

        if (!this.mHaveMap)
            menu.getItem(0).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_navigation:
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mOperationMessage.getLatlng().replace(';', ','));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;

            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.main_dialog_delete)
                        .setPositiveButton(android.R.string.ok, (dialog, id1) -> {
                            mOperationMessage.delete();

                            //Send Broadcast
                            Intent brIntent = new Intent();
                            brIntent.setAction(MainActivity.INTENT_RECEIVED_MESSAGE);
                            sendBroadcast(brIntent);

                            NavUtils.navigateUpFromSameTask(this);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog1, which) -> {})
                        .setCancelable(true);

                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private boolean mWithMap;

        public SectionsPagerAdapter(FragmentManager fm, boolean withMap) {
            super(fm);
            this.mWithMap = withMap;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OperationFragment.newInstance(mOperationMessage.getTitle(), mOperationMessage.getMessage(), mOperationMessage.getTimestamp().getTime(), OperationActivity.this.mIsAlarm);
                case 1:
                    String[] latlng = mOperationMessage.getLatlng().split(";");
                    double lat = Double.parseDouble(latlng[0]);
                    double lng = Double.parseDouble(latlng[1]);
                    return MapFragment.newInstance(lat, lng);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            if (mWithMap)
                return 2;
            else
                return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.operation_tab_info);
                case 1:
                    return getString(R.string.operation_tab_map);
            }
            return null;
        }
    }
}
