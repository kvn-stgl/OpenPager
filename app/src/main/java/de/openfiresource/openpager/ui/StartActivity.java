package de.openfiresource.openpager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import javax.inject.Inject;

import de.openfiresource.openpager.dagger.Injectable;
import de.openfiresource.openpager.utils.Preferences;

public class StartActivity extends AppCompatActivity implements Injectable {

    @Inject
    public Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class cls = TextUtils.isEmpty(preferences.getUserKey().get())
                ? LoginActivity.class
                : MainActivity.class;

        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        finish();
    }

}
