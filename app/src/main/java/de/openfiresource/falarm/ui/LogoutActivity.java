package de.openfiresource.falarm.ui;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.auth.api.credentials.Credential;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.dagger.Injectable;
import de.openfiresource.falarm.models.UserRepository;
import de.openfiresource.falarm.models.api.UserKey;
import de.openfiresource.falarm.utils.ValidatonHelper;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A logout screen.
 */
public class LogoutActivity extends AppCompatActivity implements Injectable {

    @Inject
    public UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        userRepository.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        startActivity(LoginActivity.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        new AlertDialog.Builder(LogoutActivity.this)
                                .setMessage(R.string.error_invalid_logout)
                                .setOnCancelListener(dialog -> startActivity(MainActivity.class))
                                .create()
                                .show();

                        Timber.e(e, "Logout failed");
                    }
                });
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

