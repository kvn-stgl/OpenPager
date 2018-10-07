package de.openfiresource.falarm.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gun0912.tedpermission.TedPermissionResult;
import com.tedpark.tedpermission.rx2.TedRx2Permission;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.ui.settings.RuleListActivity;
import de.openfiresource.falarm.ui.settings.SettingsActivity;
import de.openfiresource.falarm.utils.PlayServiceUtils;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

public class MainActivity extends BaseRevealActivity implements HasSupportFragmentInjector {

    public static final String PREF_SHOW_WELCOME_CARD_VERSION = "showWelcomeCardVersion";

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        checkPermissions();

        int lastVersion = sharedPreferences.getInt(PREF_SHOW_WELCOME_CARD_VERSION, 0);
        if (lastVersion < getVersionCode()) {
            showWelcomeDialog();
        }
    }

    private void checkPermissions() {
        TedRx2Permission.with(this)
                .setRationaleTitle(R.string.permission_rationale_title)
                .setRationaleMessage(R.string.permission_rationale_message)
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request()
                .subscribe(new DisposableSingleObserver<TedPermissionResult>() {
                    @Override
                    public void onSuccess(TedPermissionResult tedPermissionResult) {
                        if (tedPermissionResult.isGranted()) {
                            Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Permission Denied\n" + tedPermissionResult.getDeniedPermissions().toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                        dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "onError: Error while fetching permissions");
                        dispose();
                    }
                });
    }

    private int getVersionCode() {
        PackageManager pm = getBaseContext().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getBaseContext().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            Timber.e(ex, "getVersionCode: ");
        }
        return 0;
    }

    @Override
    public void onResume() {
        PlayServiceUtils.checkPlayServices(this);
        super.onResume();
    }

    private void showWelcomeDialog() {
        @StringRes int text = 0;
        int lastVersion = sharedPreferences.getInt(PREF_SHOW_WELCOME_CARD_VERSION, 0);
        if (lastVersion != 0) {
            switch (getVersionCode()) {
                case 3:
                    text = R.string.welcome_card_desc_v3;
                    break;
                case 4:
                    text = R.string.welcome_card_desc_v4;
                    break;
                case 5:
                    text = R.string.welcome_card_desc_v5;
                    break;
                case 6:
                    text = R.string.welcome_card_desc_v6;
                    break;
                case 7:
                    text = R.string.welcome_card_desc_v7;
                    break;
            }
        } else {
            text = R.string.welcome_card_desc;
        }

        if (text != 0) {
            new AlertDialog.Builder(this)
                    .setMessage(text)
                    .setPositiveButton(android.R.string.ok, (dialog1, which) -> sharedPreferences.edit()
                            .putInt(PREF_SHOW_WELCOME_CARD_VERSION, getVersionCode())
                            .apply())
                    .create()
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        switch (id) {
            case R.id.action_rules:
                intent = new Intent(this, RuleListActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                intent = new Intent(this, LogoutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                // todo: Create About section
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentDispatchingAndroidInjector;
    }
}
