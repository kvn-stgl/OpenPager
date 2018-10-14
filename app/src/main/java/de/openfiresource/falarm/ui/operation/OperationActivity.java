package de.openfiresource.falarm.ui.operation;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.models.database.OperationRule;
import de.openfiresource.falarm.service.SpeakService;
import de.openfiresource.falarm.utils.Preferences;

public class OperationActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String TAG = "OperationActivity";

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    public static final String OPERATION_ID = "operation_id";
    public static final String IS_ALARM = "alarm";

    @Nullable
    private OperationMessage operation;

    private Menu menu;

    private boolean isAlarm;

    @Inject
    AppDatabase database;

    @Inject
    Preferences preferences;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Wake up if operation is an active alarm
        isAlarm = getIntent().getBooleanExtra(IS_ALARM, false);
        if (isAlarm) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }


        OperationViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(OperationViewModel.class);
        long operationId = getIntent().getLongExtra(OPERATION_ID, 0);
        if (operationId != 0) {
            viewModel.setOperationId(operationId);
        }

        viewModel.getOperation()
                .observe(this, operationMessage -> {
                    operation = operationMessage;

                    if (operation == null) {
                        return;
                    }

                    SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this, operationMessage);

                    // Set up the ViewPager with the sections adapter.
                    ViewPager viewPager = findViewById(R.id.container);
                    viewPager.setAdapter(mSectionsPagerAdapter);

                    TabLayout tabLayout = findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);

                    setupMenu();
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operation, menu);
        this.menu = menu;
        setupMenu();
        return true;
    }

    private void setupMenu() {
        if (menu != null && operation != null) {

            OperationRule operationRule = operation.getRule();
            Notification notification = Notification.byRule(operationRule, getApplicationContext());

            if (operation.getLatLngPair() == null) {
                menu.getItem(0).setVisible(false);
            }

            if (!isAlarm || !notification.isSpeakServiceEnabled()) {
                menu.getItem(1).setVisible(false);
            }
        }
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
                if (operation != null && operation.getLatLngPair() != null) {
                    Pair<Double, Double> latlng = operation.getLatLngPair();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latlng.first + "," + latlng.second);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    try {
                        startActivity(mapIntent);
                    } catch (ActivityNotFoundException e) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.operation_map_activity_alert)
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    }
                }
                return true;

            case R.id.action_stopSpeak:
                Intent intentData = new Intent(getBaseContext(), SpeakService.class);
                intentData.putExtra(SpeakService.INTENT_STOP_NOW, true);
                startService(intentData);
                return true;

            case R.id.action_delete:
                if (operation != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.main_dialog_delete)
                            .setPositiveButton(android.R.string.ok, (dialog, id1) -> {
                                database.operationMessageDao().deleteOperationMessage(operation);
                                NavUtils.navigateUpFromSameTask(this);
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog1, which) -> {
                            })
                            .setCancelable(true);

                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
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

        private final List<String> pageTitles = new ArrayList<>();
        private final List<Fragment> pageFragments = new ArrayList<>();

        private final OperationMessage operationMessage;

        SectionsPagerAdapter(FragmentManager fm, Context context, OperationMessage operationMessage) {
            super(fm);
            this.operationMessage = operationMessage;

            String alarmMaps = preferences.getAlarmMaps();

            pageTitles.add(context.getString(R.string.operation_tab_info));
            pageFragments.add(OperationFragment.newInstance());

            Pair<Double, Double> latlng = operationMessage.getLatLngPair();
            if (latlng != null) {
                if (alarmMaps.equals("both") || alarmMaps.equals("gmap")) {
                    pageTitles.add(context.getString(R.string.operation_tab_map));
                    pageFragments.add(MapFragment.newInstance(latlng.first, latlng.second));
                }

                if (alarmMaps.equals("both") || alarmMaps.equals("ofm")) {
                    pageTitles.add(context.getString(R.string.operation_tab_osm));
                    pageFragments.add(OsmMapFragment.newInstance());
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            return pageFragments.get(position);
        }

        @Override
        public int getCount() {
            if (operationMessage.getLatLngPair() != null) {
                return pageTitles.size();
            } else {
                return 1;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles.get(position);
        }
    }
}
