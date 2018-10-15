package de.openfiresource.falarm.ui.custom;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import de.openfiresource.falarm.R;

public class OpenSourceLicensesDialog extends DialogFragment {

    public OpenSourceLicensesDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/open_source_licenses.html");

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.oss_licenses)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create();
    }

    public static void showOpenSourceLicenses(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_licenses");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new OpenSourceLicensesDialog().show(ft, "dialog_licenses");
    }
}