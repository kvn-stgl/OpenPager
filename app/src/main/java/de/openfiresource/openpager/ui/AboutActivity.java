package de.openfiresource.openpager.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import de.openfiresource.openpager.BuildConfig;
import de.openfiresource.openpager.R;
import de.openfiresource.openpager.ui.custom.OpenSourceLicensesDialog;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("OpenPager - Die effiziente Möglichkeit der Alarmierung!")
                .setImage(R.drawable.openpager_logo)
                .addItem(new Element().setTitle("Version: " + BuildConfig.VERSION_NAME))
                .addGroup("Connect with us")
                .addWebsite("http://openfiresource.de/")
                .addPlayStore(BuildConfig.APPLICATION_ID)
                .addGitHub("openfiresource")
                .addItem(getCopyRightsElement())
                .create();


        FrameLayout content = findViewById(R.id.content);
        content.addView(aboutPage);
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = getString(R.string.oss_licenses);
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_baseline_copyright_24px);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setOnClickListener(v -> OpenSourceLicensesDialog.showOpenSourceLicenses(AboutActivity.this));
        return copyRightsElement;
    }
}
