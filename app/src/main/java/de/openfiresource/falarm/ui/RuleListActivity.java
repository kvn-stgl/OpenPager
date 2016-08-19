package de.openfiresource.falarm.ui;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.openfiresource.falarm.R;

import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.OperationRule;
import de.openfiresource.falarm.viewadapter.SimpleItemRecyclerViewAdapter;

/**
 * An activity representing a list of Rules. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RuleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RuleListActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String INTENT_RULE_CHANGED = "de.openfiresource.falarm.ui.changedRule";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rule_list)
    RecyclerView recyclerView;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupRecyclerView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_list);
        ButterKnife.bind(this);

        //Action Bar
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        fab.setOnClickListener(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.rule_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        setupRecyclerView();
    }


    @Override
    protected void onStart() {
        //Broadcast receiver
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction(INTENT_RULE_CHANGED);
        registerReceiver(receiver, filterSend);

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(this.receiver);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        assert recyclerView != null;
        FragmentManager fragmentManager = null;
        if (mTwoPane) fragmentManager = getFragmentManager();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(fragmentManager, getApplicationContext()));
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.rule_diag_new_title));
        builder.setMessage(getString(R.string.rule_diag_new_desc));

        final EditText input = new EditText(this);
        input.setLines(1);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.create), (dialog, which) -> {
            OperationRule newRule = new OperationRule(input.getText().toString());
            new Notification(newRule.getId(), getApplication()).loadDefault();
            setupRecyclerView();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }
}
