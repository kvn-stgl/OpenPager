package de.openfiresource.falarm.ui.operation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.models.database.OperationRule;
import de.openfiresource.falarm.service.SpeakService;

public class OperationActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    public static final String OPERATION_ID = "extra_id";
    public static final String EXTRA_TYPE_ALARM = "alarm";

    private OperationMessage mOperationMessage;
    private Notification mNotification;
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

    @Inject
    AppDatabase database;

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

        long notificationId = getIntent().getLongExtra(OPERATION_ID, 0);
        if (notificationId != 0) {
            mOperationMessage = database.operationMessageDao().findById(notificationId);
            if(mOperationMessage != null) {
                OperationRule operationRule = mOperationMessage.getRule();
                mNotification = Notification.byRule(operationRule, this);

                mHaveMap = !TextUtils.isEmpty(mOperationMessage.getLatlng());

                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.
                SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mHaveMap);

                // Set up the ViewPager with the sections adapter.
                mViewPager.setAdapter(mSectionsPagerAdapter);

                tabLayout.setupWithViewPager(mViewPager);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operation, menu);

        if (!this.mHaveMap)
            menu.getItem(0).setVisible(false);

        if (!this.mIsAlarm
                || !mNotification.isSpeakServiceEnabled())
            menu.getItem(1).setVisible(false);

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

            case R.id.action_stopSpeak:
                Intent intentData = new Intent(getBaseContext(), SpeakService.class);
                intentData.putExtra(SpeakService.STOP_NOW, true);
                startService(intentData);
                return true;

            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.main_dialog_delete)
                        .setPositiveButton(android.R.string.ok, (dialog, id1) -> {

                            // todo delete operation

                            NavUtils.navigateUpFromSameTask(this);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog1, which) -> {
                        })
                        .setCancelable(true);

                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private boolean mWithMap;
        private List<String> mItemNames = new ArrayList<>();
        private List<Fragment> mItemValues = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm, boolean withMap) {
            super(fm);
            mWithMap = withMap;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(OperationActivity.this);
            String alarmMaps = preferences.getString("general_alarm_maps", "both");

            mItemNames.add(getString(R.string.operation_tab_info));
            mItemValues.add(OperationFragment.newInstance(mOperationMessage.getId(), OperationActivity.this.mIsAlarm));

            if (mWithMap) {
                String[] latlng = mOperationMessage.getLatlng().split(";");
                double lat = Double.parseDouble(latlng[0]);
                double lng = Double.parseDouble(latlng[1]);

                if (alarmMaps.equals("both") || alarmMaps.equals("gmap")) {
                    mItemNames.add(getString(R.string.operation_tab_map));
                    mItemValues.add(MapFragment.newInstance(lat, lng));
                }

                if (alarmMaps.equals("both") || alarmMaps.equals("ofm")) {
                    mItemNames.add(getString(R.string.operation_tab_osm));
                    mItemValues.add(OsmMapFragment.newInstance(lat, lng));
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mItemValues.get(position);
        }

        @Override
        public int getCount() {
            if (mWithMap)
                return mItemNames.size();
            else
                return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mItemNames.get(position);
        }
    }
}
