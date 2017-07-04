package com.ducnd.customgallery.libs;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.ducnd.customgallery.R;
import com.ducnd.customgallery.libs.utils.BitmapUtils;
import com.ducnd.customgallery.libs.utils.ItemGallaryImage;
import com.ducnd.customgallery.libs.utils.PairInt;
import com.ducnd.customgallery.libs.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GalleryImageAdapter.IGalleryImageAdapter, SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY_RETURE_GALLERY = "KEY_RETURE_GALLERY";
    private static final int COUNT_OF_PAGE_DEFAULT_LOAD_GALLARY = 30;
    private RecyclerView rcImage;
    private SwipeRefreshLayout refresh;
    private GalleryImageAdapter galleryImageAdapter;
    private int totalImage;
    private List<ItemGallaryImage> mGallerys;
    private AsyncTask<Integer, Void, Void> mAsyncTask;
    private boolean mReload;
    private boolean mLoadMore;
    private int mWidhtImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        findViewByIds();
        initComponents();
        setEvents();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gallery image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void findViewByIds() {
        rcImage = (RecyclerView) findViewById(R.id.rc_img);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refres);
    }

    private void initComponents() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidhtImage = displayMetrics.widthPixels / 3;


        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mGallerys.size()) {
                    return 3;
                }
                return 1;
            }
        });
        galleryImageAdapter = new GalleryImageAdapter(this);
        rcImage.setLayoutManager(manager);
        rcImage.setAdapter(galleryImageAdapter);

        if (PermissionUtils.checkPermissionStore(this, 101)) {
            grantPermission();
        }
    }

    private void grantPermission() {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor == null) {
            return;
        }
        totalImage = cursor.getCount();
        cursor.close();

        mReload = true;
        refresh.setRefreshing(true);
        mGallerys = new ArrayList<>();
        loadMore(0);
    }


    private void setEvents() {
        refresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (mReload || mLoadMore) {
            refresh.setRefreshing(false);
            return;
        }
        if (totalImage == 0) {
            refresh.setRefreshing(false);
            return;
        }
        mReload = true;
        loadMore(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public int getCount() {
        if (mGallerys == null) {
            return 0;
        }
        if (hasLoadMore()) {
            return mGallerys.size() + 1;
        }
        return mGallerys.size();
    }

    @Override
    public ItemGallaryImage getData(int position) {
        return mGallerys.get(position);
    }

    @Override
    public boolean hasLoadMore() {
        if (mGallerys.size() >= totalImage) {
            return false;
        }
        return true;
    }

    @Override
    public void loadMore() {
        if (mLoadMore || mReload) {
            return;
        }
        mLoadMore = true;
        loadMore(mGallerys.size());
    }

    public void loadMore(int current) {
        if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        mAsyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().encodedQuery("limit=" + params[0] + "," + COUNT_OF_PAGE_DEFAULT_LOAD_GALLARY).build(),
                        new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID}, null, null,
                        MediaStore.Images.Media.DATE_ADDED + " DESC");
                if (cursor == null) return null;
                cursor.moveToFirst();
                String pathFile;
                PairInt pairInt;
                final int columnData = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                final int columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                long id;
                if (mReload) {
                    mGallerys.clear();
                }
                while (!cursor.isAfterLast()) {
                    pathFile = cursor.getString(columnData);
                    if (pathFile != null && new File(pathFile).exists()) {
                        pairInt = BitmapUtils.calculateResizeImage(pathFile, mWidhtImage);
                        id = cursor.getLong(columnId);
                        mGallerys.add(new ItemGallaryImage(pairInt, pathFile, id));
                    }
                    cursor.moveToNext();
                }
                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mReload) {
                    mReload = false;
                    refresh.setRefreshing(false);
                } else {
                    mLoadMore = false;
                }

                galleryImageAdapter.notifyDataSetChanged();
            }
        };
        mAsyncTask.execute(current);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        if (requestCode == 101) {
            grantPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (PermissionUtils.checkPermissionStoreMediaNotShowDialog(this)) {
                grantPermission();
            }
        }
    }

    @Override
    public void onClickItemImage(int position) {
        Intent intent = new Intent();
        intent.putExtra(KEY_RETURE_GALLERY, mGallerys.get(position));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }
}
