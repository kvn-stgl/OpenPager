package de.openfiresource.falarm.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.WelcomeButtonAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.google.android.gms.contextmanager.internal.InterestUpdateBatchImpl;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.dialogs.MainMultiplePermissionsListener;
import de.openfiresource.falarm.models.OperationMessage;
import de.openfiresource.falarm.utils.PlayServiceUtils;

public class MainActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {

    public static final String INTENT_RECEIVED_MESSAGE = "de.openfiresource.falarm.ui.receivedMessage";
    public static final String SHOW_WELCOME_CARD = "showWelcomeCard";

    private SharedPreferences mSharedPreferences;

    @BindView(android.R.id.content)
    ViewGroup rootView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.material_listview)
    MaterialListView mListView;

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
        mListView.addOnItemTouchListener(this);


        //Load permissions
        CompositeMultiplePermissionsListener compositeMultiplePermissionsListener
                = new CompositeMultiplePermissionsListener(new MainMultiplePermissionsListener(this),
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                        R.string.permission_rationale_message)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
        Dexter.checkPermissions(compositeMultiplePermissionsListener,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void showPermissionRationale(final PermissionToken token) {

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
        mListView.getAdapter().clearAll();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.getBoolean(SHOW_WELCOME_CARD, true)) {
            createWelcomeCard();
        }

        List<OperationMessage> notifications = OperationMessage.find(OperationMessage.class, null, null, null, "id desc", "15");

        for (OperationMessage operationMessage : notifications) {
            DateFormat df = new DateFormat();
            String message = "Alarmiert am " + df.format("dd.MM.yyyy HH:mm:ss", operationMessage.getTimestamp());
            createCard(operationMessage.getTitle(), message, operationMessage.getId());
        }
    }

    private void createWelcomeCard() {
        Card card = new Card.Builder(this)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_welcome_card_layout)
                .setTitle(getString(R.string.welcome_card_title))
                .setTitleColor(Color.WHITE)
                .setDescription(getString(R.string.welcome_card_desc))
                .setDescriptionColor(Color.WHITE)
                .setSubtitle(getString(R.string.welcome_card_subtitle))
                .setSubtitleColor(Color.WHITE)
                .setBackgroundColor(Color.RED)
                .addAction(R.id.ok_button, new WelcomeButtonAction(this)
                        .setText("Okay!")
                        .setTextColor(Color.WHITE)
                        .setListener((view, card1) -> {
                            mListView.getAdapter().remove(card1, true);
                            mSharedPreferences.edit()
                                    .putBoolean(SHOW_WELCOME_CARD, false)
                                    .commit();
                        }))
                .endConfig()
                .build();
        card.setDismissible(true);
        mListView.getAdapter().add(card);
    }

    private void createCard(String title, String desc, long id) {
        Card card = new Card.Builder(this)
                .setTag(id)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_buttons_card)
                .setTitle(title)
                .setDescription(desc)
                .endConfig()
                .build();

        mListView.getAdapter().add(mListView.getAdapter().getItemCount(), card, false);
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
                new LibsBuilder()
                        .withFields(R.string.class.getFields())
                        .withActivityTitle(getString(R.string.action_about))
                        .withActivityTheme(R.style.AboutLibrariesTheme_Light)
                        .start(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(@NonNull Card card, int position) {
        if (card.getTag() != null) {
            long id = (long) card.getTag();

            Intent intent = new Intent(this, OperationActivity.class);
            intent.putExtra(OperationActivity.EXTRA_ID, id);
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(@NonNull Card card, int position) {
        if (card.getTag() != null) {
            long id = (long) card.getTag();

            final CharSequence[] items = {getString(R.string.delete)};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.main_long_click_header);
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0:
                        OperationMessage operationMessage = OperationMessage.findById(OperationMessage.class, id);
                        operationMessage.delete();
                        updateNotifications();
                        break;
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
