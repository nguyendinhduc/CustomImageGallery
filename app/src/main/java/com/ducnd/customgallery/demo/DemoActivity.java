package com.ducnd.customgallery.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ducnd.customgallery.libs.BaseActivity;
import com.ducnd.customgallery.libs.MainActivity;
import com.ducnd.customgallery.R;
import com.ducnd.customgallery.libs.utils.ItemGallaryImage;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by ducnd on 7/5/17.
 */

public class DemoActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvPath;
    private ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        tvPath = (TextView) findViewById(R.id.tv_path);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        findViewById(R.id.btn_choose_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            ItemGallaryImage gallaryImage = (ItemGallaryImage) data.getSerializableExtra(MainActivity.KEY_RETURE_GALLERY);
            int width = ivImage.getWidth();
            int height = width * gallaryImage.getPairInt().getSecond() / gallaryImage.getPairInt().getFirst();
            Picasso.with(this)
                    .load(new File(gallaryImage.getPathFile()))
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .resize(width, height)
                    .into(ivImage);
            tvPath.setText(gallaryImage.getPathFile());
        }

    }
}
