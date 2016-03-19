package com.pockru.dongzakgol;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pockru.dongzakgol.module.Category;
import com.pockru.dongzakgol.module.imgur.helpers.DocumentHelper;
import com.pockru.dongzakgol.module.imgur.helpers.IntentHelper;
import com.pockru.dongzakgol.module.tumblr.service.TumblrOAuthActivity;
import com.pockru.dongzakgol.module.tumblr.service.TumblrUploadImg;
import com.pockru.dongzakgol.util.Preference;
import com.pockru.dongzakgol.util.UrlCheckUtils;
import com.pockru.dongzakgol.webview.DZGWebView;
import com.pockru.dongzakgol.webview.DZGWebViewClient;
import com.pockru.dongzakgol.webview.UrlConts;
import com.tumblr.jumblr.types.PhotoPost;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity
        implements DZGWebViewClient.InteractWithAvtivity,
        DZGWebView.PageScrollState,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String FIRE_BASE_URL = "https://dongzakgol.firebaseio.com/";

    private DZGWebView mWebView;

    private String mMid = UrlConts.MAIN_MID;

    private SwipeRefreshLayout mRefreshLayout;
//    private TextView mTvHeaderMsg;

    private NavigationView navigationView;
//
    private AdView mAdView;

    boolean isWriteMode = false;

    Firebase myFirebaseRef;

    private Realm realm;
    private RealmConfiguration realmConfig;
    RealmResults<Category> mCateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(FIRE_BASE_URL);

        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ImageButton btnMoveUp = (ImageButton) findViewById(R.id.btn_move_up);
        btnMoveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.scrollTo(0, 0);
            }
        });

        ImageButton btnMoveDown = (ImageButton) findViewById(R.id.btn_move_down);
        btnMoveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.scrollTo(0, (int) Math.floor(mWebView.getContentHeight() * mWebView.getScale()));
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Category category = realm.where(Category.class).equalTo("name", String.valueOf(item.getTitle())).findFirst();
                if (category != null) {
                    mWebView.loadUrl(UrlConts.MAIN_URL + "/" + category.getKey());
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }

                return false;
            }
        });

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeColors(R.color.refresh_progress_1, R.color.refresh_progress_2);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (DZGWebView) findViewById(R.id.webview);
        mWebView.setProgressBar((ProgressBar)findViewById(R.id.pb_webview));
        mWebView.setOnPageScrollSateListener(this);
        mWebView.loadUrl(UrlConts.getMainUrl());

        mAdView = (AdView) findViewById(R.id.adView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            adRequestBuilder.setGender(AdRequest.GENDER_FEMALE);
            mAdView.loadAd(adRequestBuilder.build());
        } else {
            mAdView.setVisibility(View.GONE);
        }

        mCateList = realm.where(Category.class).findAll();

        if (mCateList.size() > 0) {
            for (Category category : mCateList) {
                MenuItem item = navigationView.getMenu().findItem(R.id.nav_cate_list);
                item.getSubMenu().add(Menu.NONE, category.getId().intValue(), category.getOrder().intValue(), category.getName());
            }
        }

        myFirebaseRef.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MenuItem item = navigationView.getMenu().findItem(R.id.nav_cate_list);
                SubMenu subMenu = item.getSubMenu();

                realm.beginTransaction();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Category category = new Category();
                    category.setKey(child.getKey());
                    category.setId((Long) child.child("id").getValue());
                    category.setOrder((Long) child.child("order").getValue());
                    category.setName((String) child.child("name").getValue());
                    realm.copyToRealmOrUpdate(category);

                    MenuItem cateItem;
                    if ((cateItem = subMenu.findItem(category.getId().intValue())) != null){
                        cateItem.setTitle(category.getName());
                    } else {
                        subMenu.add(Menu.NONE, category.getId().intValue(), category.getOrder().intValue(), category.getName());
                    }
                }
                realm.commitTransaction();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("test","onCancelled : "+firebaseError.toString());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent == null || intent.getAction() == null) return;

        switch (intent.getAction()) {
            case Intent.ACTION_VIEW:

                Uri uri = intent.getData();

                if (mWebView != null && uri != null) {
                    mWebView.loadUrl(uri.toString());
                }

                break;
            default:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();

        if (webBackForwardList != null
                && webBackForwardList.getSize() > 1
                && webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1) != null) {
            String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
            UrlCheckUtils.checkUrl(backUrl, this);
        }

        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.onPause();
        }
//        mAdView.pause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.onResume();
        }
//        mAdView.resume();
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
//        if (mAdView != null) {
//            mAdView.destroy();
//            mAdView = null;
//        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_finish) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("앱을 종료하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
            return true;
        } else if (id == R.id.action_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_write) {
            mWebView.loadUrl(UrlConts.getWriteUrl(mMid));
            return true;
        } else if (id == R.id.action_uploadPicture) {
            if (TextUtils.isEmpty(Preference.getTumblrToken(getApplicationContext()))) {
                Intent intent = new Intent(MainActivity.this, TumblrOAuthActivity.class);
                startActivityForResult(intent, REQ_TUMBLR_AUTH);
            } else {
                showChooseFile();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            menu.findItem(R.id.action_uploadPicture).setVisible(isWriteMode);
            menu.findItem(R.id.action_write).setVisible(!isWriteMode);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_TUMBLR_AUTH:
                if (resultCode == RESULT_OK) {
                    showChooseFile();
                }
                break;
            case REQ_FILECHOOSER_FOR_TUMBLR:
                if (resultCode != RESULT_OK) return;

                Uri returnUri = data.getData();
                String filePath = DocumentHelper.getPath(this, returnUri);

                new TumblrUploadImg(MainActivity.this, new TumblrUploadImg.TumblrUploadListener() {
                    @Override
                    public void getResponse(PhotoPost result) {
                        if (result != null && result.getPhotos() != null && result.getPhotos().size() > 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_success_upload_image), Toast.LENGTH_SHORT).show();
                            insertIntoImg(result);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_msg_failed_to_save_link), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute(
                        Preference.getTumblrToken(getApplicationContext()),
                        Preference.getTumblrSecret(getApplicationContext()),
                        filePath
                );

                break;

        }
    }

    private void insertIntoImg(PhotoPost result) {
        if (result == null || result.getPhotos() == null || result.getPhotos().isEmpty()) return;
        mWebView.loadJavaScript(UrlConts.insertImageJS(result.getPhotos().get(0).getOriginalSize().getUrl()));
    }

    private static final String PREFIX_BOARD = "board";

    @Override
    public void setMid(String mid) {
        if (mid != null && mMid.equalsIgnoreCase(mid) == false) {
            sendEvent("category", mid, "");
        }

        mMid = mid;
    }

    @Override
    public void setAct(String act) {
        switch (act) {
            case UrlConts.ACT_WRITE:
                mRefreshLayout.setEnabled(false);
                isWriteMode = true;
                break;
            default:
                mRefreshLayout.setEnabled(true);
                isWriteMode = false;
                break;
        }

        supportInvalidateOptionsMenu();
    }

    @Override
    public void notifyUrlLoadStart() {
        mRefreshLayout.setRefreshing(false);
//        collapseFab();
    }

    @Override
    public void notifyUrlLoadFinish() {
    }

    @Override
    public void onStateUp() {
//        fabControll(true);
    }

    @Override
    public void onStateDown() {
//        fabControll(false);
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void showChooseFile() {
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            IntentHelper.chooseFileIntent(MainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (Arrays.asList(grantResults).contains(PackageManager.PERMISSION_GRANTED)) {
                    IntentHelper.chooseFileIntent(MainActivity.this);
                } else {
                    Toast.makeText(getApplicationContext(), "권한 동의를 하셔야 서비스를 이용할 수 있습니다.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
