package de.openfiresource.falarm.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import de.openfiresource.falarm.R;
import de.openfiresource.falarm.ui.MainActivity;

/**
 * Created by stieglit on 19.08.2016.
 */
public class MainMultiplePermissionsListener implements MultiplePermissionsListener {

    private Context mContext;

    public MainMultiplePermissionsListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {

    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        new AlertDialog.Builder(mContext).setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    token.cancelPermissionRequest();
                })
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    token.continuePermissionRequest();
                })
                .setOnDismissListener(dialog -> token.cancelPermissionRequest())
                .show();
    }
}
