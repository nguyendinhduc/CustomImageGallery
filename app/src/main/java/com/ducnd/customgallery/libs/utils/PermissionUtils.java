package com.ducnd.customgallery.libs.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.ducnd.customgallery.R;
import com.ducnd.customgallery.libs.dialog.ConfirmDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducnd on 7/5/17.
 */

public class PermissionUtils {

    public static boolean checkPermissionStore(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> pers = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (SharfUtils.getNumberDeniedNotAgainPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) > 0) {
                showDialogConfirmOpenSetting(activity, requestCode);
                return false;
            }
            pers.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (SharfUtils.getNumberDeniedNotAgainPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) > 0) {
                showDialogConfirmOpenSetting(activity, requestCode);
                return false;
            }
            pers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (pers.size() == 0) {
            return true;
        }
        String arrs[] = new String[pers.size()];
        int index = 0;
        for (String per : pers) {
            arrs[index] = per;
            index++;
        }
        ActivityCompat.requestPermissions(activity, arrs, requestCode);
        return false;
    }

    private static void showDialogConfirmOpenSetting(final Activity activity, final int requestCode) {
        Dialog dialog = new ConfirmDialog(activity, activity.getString(R.string.Do_you_want_grant_permision_store), new ConfirmDialog.IMessageDialog() {
            @Override
            public void onClickOk() {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, requestCode);
            }

            @Override
            public void onClickCancel() {

            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public static void checkPermission(Activity activity, String[] pernissions, int[] granted) {
        for (int i = 0; i < granted.length; i++) {
            if (granted[i] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, pernissions[i])) {
                    SharfUtils.increateDeniedNotAgainPermission(activity, pernissions[i], 1);
                }
            } else {
                SharfUtils.saveDeniedNotAgainPermission(activity, pernissions[i], 0);
            }
        }
    }

    public static boolean checkPermissionStoreMediaNotShowDialog(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean check = true;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            check = false;
        } else {
            SharfUtils.saveDeniedNotAgainPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            check = false;
        } else {
            SharfUtils.saveDeniedNotAgainPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 0);
        }
        return check;
    }
}
