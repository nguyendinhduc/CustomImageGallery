package com.ducnd.customgallery.libs;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.ducnd.customgallery.libs.utils.PermissionUtils;

/**
 * Created by ducnd on 7/5/17.
 */

public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.checkPermission(this, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
