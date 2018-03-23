package de.openfiresource.falarm.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.dialogs.MainMultiplePermissionsListener;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.utils.PlayServiceUtils;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_RECEIVED_MESSAGE = "de.openfiresource.falarm.ui.receivedMessage";
    public static final String SHOW_WELCOME_CARD_VERSION = "showWelcomeCardVersion";

    private SharedPreferences mSharedPreferences;

    @BindView(android.R.id.content)
    ViewGroup rootView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifications();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Toolbar
        setSupportActionBar(mToolbar);

        updateNotifications();

        //Load permissions
        CompositeMultiplePermissionsListener compositeMultiplePermissionsListener
                = new CompositeMultiplePermissionsListener(new MainMultiplePermissionsListener(this),
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                        R.string.permission_rationale_message)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(compositeMultiplePermissionsListener).check();
    }

    public int getVersionCode() {
        PackageManager pm = getBaseContext().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getBaseContext().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return 0;
    }

    @Override
    public void onResume() {
        PlayServiceUtils.checkPlayServices(this);
        super.onResume();
    }

    @Override
    protected void onStart() {
        //Broadcast receiver
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction(INTENT_RECEIVED_MESSAGE);
        registerReceiver(receiver, filterSend);

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(this.receiver);
    }

    private void updateNotifications() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = mSharedPreferences.getInt(SHOW_WELCOME_CARD_VERSION, 0);
        if (lastVersion < getVersionCode()) {
            createWelcomeCard();
        }

        List<OperationMessage> notifications = AppDatabase.getInstance(this).operationMessageDao().getAll();

        for (OperationMessage operationMessage : notifications) {
            DateFormat df = new DateFormat();
            String message = "Alarmiert am " + DateFormat.format("dd.MM.yyyy HH:mm:ss", operationMessage.getTimestamp());
            createCard(operationMessage.getTitle(), message, operationMessage.getId());
        }
    }

    private void createWelcomeCard() {
        String text = null;
        int lastVersion = mSharedPreferences.getInt(SHOW_WELCOME_CARD_VERSION, 0);
        if (lastVersion != 0) {
            switch (getVersionCode()) {
                case 3:
                    text = getString(R.string.welcome_card_desc_v3);
                    break;
                case 4:
                    text = getString(R.string.welcome_card_desc_v4);
                    break;
                case 5:
                    text = getString(R.string.welcome_card_desc_v5);
                    break;
                case 6:
                    text = getString(R.string.welcome_card_desc_v6);
                    break;
                case 7:
                    text = getString(R.string.welcome_card_desc_v7);
                    break;
            }
        }

        if (text == null)
            text = getString(R.string.welcome_card_desc);

//        Card card = new Card.Builder(this)
//                .withProvider(new CardProvider())
//                .setLayout(R.layout.material_welcome_card_layout)
//                .setTitle(getString(R.string.welcome_card_title))
//                .setTitleColor(Color.WHITE)
//                .setDescription(text)
//                .setDescriptionColor(Color.WHITE)
//                .setSubtitle(getString(R.string.welcome_card_subtitle))
//                .setSubtitleColor(Color.WHITE)
//                .setBackgroundColor(Color.RED)
//                .addAction(R.id.ok_button, new WelcomeButtonAction(this)
//                        .setText("Okay!")
//                        .setTextColor(Color.WHITE)
//                        .setListener((view, card1) -> {
//                            mListView.getAdapter().remove(card1, true);
//                            mSharedPreferences.edit()
//                                    .putInt(SHOW_WELCOME_CARD_VERSION, getVersionCode())
//                                    .commit();
//                        }))
//                .endConfig()
//                .build();
//        card.setDismissible(true);
//        mListView.getAdapter().add(card);
    }

    private void createCard(String title, String desc, long id) {
//        Card card = new Card.Builder(this)
//                .setTag(id)
//                .withProvider(new CardProvider())
//                .setLayout(R.layout.material_basic_buttons_card)
//                .setTitle(title)
//                .setDescription(desc)
//                .endConfig()
//                .build();
//
//        mListView.getAdapter().add(mListView.getAdapter().getItemCount(), card, false);
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

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_rules:
                intent = new Intent(this, RuleListActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                // todo: Create About section
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void onItemClick(@NonNull Card card, int position) {
//        if (card.getTag() != null) {
//            long id = (long) card.getTag();
//
//            Intent intent = new Intent(this, OperationActivity.class);
//            intent.putExtra(OperationActivity.EXTRA_ID, id);
//            startActivity(intent);
//        }
//    }
//
//    public void onItemLongClick(@NonNull Card card, int position) {
//        if (card.getTag() != null) {
//            long id = (long) card.getTag();
//
//            final CharSequence[] items = {getString(R.string.delete)};
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.main_long_click_header);
//            builder.setItems(items, (dialog, item) -> {
//                switch (item) {
//                    case 0:
//                        OperationMessage operationMessage = OperationMessage.findById(OperationMessage.class, id);
//                        operationMessage.delete();
//                        updateNotifications();
//                        break;
//                }
//            });
//            AlertDialog alert = builder.create();
//            alert.show();
//        }
//    }
}
