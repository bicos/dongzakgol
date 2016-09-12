package com.pockru.dongzakgol;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pockru.dongzakgol.model.Category;
import com.pockru.dongzakgol.module.imgur.helpers.IntentHelper;
import com.pockru.dongzakgol.module.realm.DzgRealm;
import com.pockru.dongzakgol.view.FavoriteCategoryView;
import com.pockru.dongzakgol.webview.DZGWebView;
import com.pockru.dongzakgol.webview.DZGWebViewClient;
import com.pockru.dongzakgol.webview.UrlConts;
import com.tumblr.jumblr.types.PhotoPost;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity
        implements DZGWebViewClient.InteractWithAvtivity,
        DZGWebView.PageScrollState,
        ActivityCompat.OnRequestPermissionsResultCallback,
        FavoriteCategoryView.InteractionFavoriteView {

//    private static final String FIRE_BASE_URL = "https://dongzakgol.firebaseio.com/";
    private static final String FIREBASE_STORAGE_REF = "gs://project-1969400518475156086.appspot.com";

    public static final String EXTRA_URL = "extra_url";

    private DZGWebView mWebView;

    private String mMid = "";
    private String mSrl = "";

    private DrawerLayout mDrawer;
    private SwipeRefreshLayout mRefreshLayout;
    private NavigationView navigationView;

    private TextView mDrawerHeader;
    private Button mBtnLogin;
    private Button mBtnLogout;

    private boolean isWriteMode = false;

    private Realm realm;
    RealmResults<Category> mCateList;

    private AdView mAdView;

    private FirebaseStorage mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Firebase.setAndroidContext(this);
//        Firebase myFirebaseRef = new Firebase(FIRE_BASE_URL);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mStore = FirebaseStorage.getInstance();

        realm = DzgRealm.getInstance(this);

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

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Category category = realm.where(Category.class).equalTo("name", String.valueOf(item.getTitle())).findFirst();
                if (category != null) {
                    loadMid(category.getKey());
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;
                }

                return false;
            }
        });
        mDrawerHeader = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_header_msg);
        mBtnLogin = (Button) navigationView.getHeaderView(0).findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(UrlConts.getLoginUrl(mMid, mSrl));
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });
        mBtnLogout = (Button) navigationView.getHeaderView(0).findViewById(R.id.btn_logout);
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(UrlConts.getLogoutUrl(mMid, mSrl));
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.refresh_progress_1),
                ContextCompat.getColor(this, R.color.refresh_progress_2));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (DZGWebView) findViewById(R.id.webview);
        mWebView.setProgressBar((ProgressBar) findViewById(R.id.pb_webview));
        mWebView.setOnPageScrollSateListener(this);

        mAdView = (AdView) findViewById(R.id.adView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            adRequestBuilder.setGender(AdRequest.GENDER_FEMALE);
            mAdView.loadAd(adRequestBuilder.build());
        } else {
            mAdView.setVisibility(View.GONE);
        }

        FavoriteCategoryView categoryView = (FavoriteCategoryView) findViewById(R.id.favorite_category);
        categoryView.setInteractionFavoriteView(this);

        mCateList = realm.where(Category.class).findAll();

        if (mCateList.size() > 0) {
            for (Category category : mCateList) {
                MenuItem item = navigationView.getMenu().findItem(R.id.nav_cate_list);
                item.getSubMenu().add(Menu.NONE, category.getId().intValue(), category.getOrder().intValue(), category.getName());
            }
        }

        mDatabase.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MenuItem item = navigationView.getMenu().findItem(R.id.nav_cate_list);
                SubMenu subMenu = item.getSubMenu();

                realm.beginTransaction();

                RealmQuery<Category> q = realm.where(Category.class);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    q = q.notEqualTo("id", (Long) child.child("id").getValue());

                    Category category = realm.where(Category.class).equalTo("id", (Long) child.child("id").getValue()).findFirst();
                    if (category == null) {
                        category = new Category();
                    }
                    category.setKey(child.getKey());
                    category.setId((Long) child.child("id").getValue());
                    category.setOrder((Long) child.child("order").getValue());
                    category.setName((String) child.child("name").getValue());
                    realm.copyToRealmOrUpdate(category);

                    MenuItem cateItem;
                    if ((cateItem = subMenu.findItem(category.getId().intValue())) != null) {
                        cateItem.setTitle(category.getName());
                    } else {
                        subMenu.add(Menu.NONE, category.getId().intValue(), category.getOrder().intValue(), category.getName());
                    }
                }

                if (q.count() > 0) {
                    q.findAll().deleteAllFromRealm();
                }

                realm.commitTransaction();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mWebView.loadUrl(UrlConts.getMainUrl());

        if (getIntent() != null) {
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent == null) return;

//        if (intent.getExtras() != null) {
//            for (String key : intent.getExtras().keySet()){
//                Log.i("test", "key : " + key + ", value : "+intent.getExtras().get(key));
//            }
//        }

        if (mWebView != null && intent.hasExtra(EXTRA_URL)) {
            mWebView.loadUrl(intent.getStringExtra(EXTRA_URL));
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
//            WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();
//
//            if (webBackForwardList != null
//                    && webBackForwardList.getSize() > 1
//                    && webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1) != null) {
//                String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
//                UrlCheckUtils.checkUrl(backUrl, this);
//            }

            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
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
            showChooseFile();
//            if (TextUtils.isEmpty(Preference.getTumblrToken(getApplicationContext()))) {
//                Intent intent = new Intent(MainActivity.this, TumblrOAuthActivity.class);
//                startActivityForResult(intent, REQ_TUMBLR_AUTH);
//            } else {
//                showChooseFile();
//            }
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
//                String filePath = DocumentHelper.getPath(this, returnUri);

                StorageReference storageRef = mStore.getReferenceFromUrl(FIREBASE_STORAGE_REF);

                StorageReference uploadImgRef = storageRef.child("storage/" + returnUri.getLastPathSegment());

                UploadTask uploadTask = uploadImgRef.putFile(returnUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot != null && taskSnapshot.getDownloadUrl() != null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_success_upload_image), Toast.LENGTH_SHORT).show();
                            insertIntoImg(taskSnapshot.getDownloadUrl().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_msg_failed_to_save_link), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_msg_failed_to_save_link), Toast.LENGTH_SHORT).show();
                    }
                });

//                new TumblrUploadImg(MainActivity.this, new TumblrUploadImg.TumblrUploadListener() {
//                    @Override
//                    public void getResponse(PhotoPost result) {
//                        if (result != null && result.getPhotos() != null && result.getPhotos().size() > 0) {
//                            Toast.makeText(getApplicationContext(), getString(R.string.msg_success_upload_image), Toast.LENGTH_SHORT).show();
//                            insertIntoImg(result);
//                        } else {
//                            Toast.makeText(getApplicationContext(), getString(R.string.error_msg_failed_to_save_link), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).execute(
//                        Preference.getTumblrToken(getApplicationContext()),
//                        Preference.getTumblrSecret(getApplicationContext()),
//                        filePath
//                );

                break;

        }
    }

    private void insertIntoImg(PhotoPost result) {
        if (result == null || result.getPhotos() == null || result.getPhotos().isEmpty()) return;
        mWebView.loadJavaScript(UrlConts.insertImageJS(result.getPhotos().get(0).getOriginalSize().getUrl()));
    }

    private void insertIntoImg(String imgUrl) {
        mWebView.loadJavaScript(UrlConts.insertImageJS(imgUrl));
    }

    @Override
    public void setMid(String mid) {
        mMid = mid;
    }

    private void loadMid(String mid) {
        mWebView.loadUrl(UrlConts.MAIN_URL + "/" + mid);
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
    public void setSrl(String srl) {
        mSrl = srl;
    }

    @Override
    public void notifyUrlLoadStart() {
        mRefreshLayout.setRefreshing(false);
//        collapseFab();
    }

    @Override
    public void notifyUrlLoading(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri.isHierarchical()) {
                if (uri.getQuery() != null && uri.getQueryParameter("mid") != null && uri.getQueryParameter("document_srl") != null) {
                    sendEvent(uri.getQueryParameter("mid"), uri.getQueryParameter("document_srl"), "");
                } else {
                    if (uri.getPathSegments() != null){
                        if (uri.getPathSegments().size() > 1) {
                            sendEvent(uri.getPathSegments().get(0), uri.getPathSegments().get(1), "");
                        } else if (uri.getPathSegments().size() > 0) {
                            sendEvent(uri.getPathSegments().get(0), "", "");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void notifyUrlLoadFinish() {
        if (mAdView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAdView.setVisibility(View.VISIBLE);
        }
    }

    boolean isLogin = false;

    @Override
    public void onLogin(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isLogin) {
                    isLogin = true;
                    mDrawerHeader.setText(msg + "님 환영합니다!");

                    mBtnLogin.setVisibility(View.GONE);
                    mBtnLogout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onLogout(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    isLogin = false;
                    mDrawerHeader.setText(msg);

                    mBtnLogin.setVisibility(View.VISIBLE);
                    mBtnLogout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void isShowAd(final boolean isShowAd) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (mAdView != null) {
                        mAdView.setVisibility(isShowAd ? View.GONE : View.VISIBLE);
                    }
                }
            }
        });
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
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "권한 동의를 하셔야 서비스를 이용할 수 있습니다.", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

                IntentHelper.chooseFileIntent(MainActivity.this);
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "권한 동의를 하셔야 서비스를 이용할 수 있습니다.", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

                mWebView.saveImg();
                break;
        }
    }

    @Override
    public void clickFavoriteItem(Category category) {
        if (category != null && mWebView != null) {
            loadMid(category.getKey());
        }
    }
}
