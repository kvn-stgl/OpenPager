package de.openfiresource.openpager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import de.openfiresource.openpager.R;
import de.openfiresource.openpager.dagger.Injectable;
import de.openfiresource.openpager.models.UserRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

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

