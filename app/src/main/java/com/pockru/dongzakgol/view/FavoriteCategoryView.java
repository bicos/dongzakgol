package com.pockru.dongzakgol.view;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.model.Category;
import com.pockru.dongzakgol.module.realm.DzgRealm;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Ravy on 16. 3. 20..
 */
public class FavoriteCategoryView extends LinearLayout {

    public FavoriteCategoryView(Context context) {
        super(context);

        init(context);
    }

    public FavoriteCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    Realm realm;
    RealmResults<Category> mResults;

    LinearLayout container;
    View mHint;
    Button mBtnRemove;

    InteractionFavoriteView interactionFavoriteView;

    private void init(final Context context) {

        realm = DzgRealm.getInstance(context);

        inflate(context, R.layout.view_favorite_cate, this);

        final Button btnAdd = (Button) findViewById(R.id.btn_add_favorite_cate);
        mBtnRemove = (Button) findViewById(R.id.btn_remove_favorite_cate);
        mHint = findViewById(R.id.hint_add_favorite_cate);
        container = (LinearLayout) findViewById(R.id.main_container);

        mResults = realm.where(Category.class).equalTo("isFavorite", true).findAllSorted("favOrder", Sort.ASCENDING);
        addCateBtns(mResults);

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCateList(v);
            }
        });
        mBtnRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFavCateList(v);
            }
        });
    }

    private void changeUi(){
        if (mResults.size() > 0) {
            mHint.setVisibility(View.GONE);
            mBtnRemove.setVisibility(View.VISIBLE);

        } else {
            mHint.setVisibility(View.VISIBLE);
            mBtnRemove.setVisibility(View.GONE);
        }
    }

    private void showCateList(View view) {
        final RealmResults<Category> results = realm.where(Category.class).equalTo("isFavorite", false).findAll();

        PopupMenu popup = new PopupMenu(getContext(), view);

        for (Category category : results) {
            popup.getMenu().add(Menu.NONE,
                    category.getId().intValue(),
                    category.getOrder().intValue(),
                    category.getName());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int index = mResults.size();

                if (index == 0) {
                    container.removeAllViews();
                }

                realm.beginTransaction();
                Category category = results.where().equalTo("name", (String) item.getTitle()).findFirst();
                category.setIsFavorite(true);
                category.setFavOrder((long) index);
                realm.commitTransaction();

                addCateBtn(category);
                return false;
            }
        });
        popup.show();
    }

    private void showFavCateList(View view) {
        final RealmResults<Category> results = realm.where(Category.class).equalTo("isFavorite", true).findAll();

        PopupMenu popup = new PopupMenu(getContext(), view);

        for (Category category : results) {
            popup.getMenu().add(Menu.NONE,
                    category.getId().intValue(),
                    category.getOrder().intValue(),
                    category.getName());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                realm.beginTransaction();
                Category category = results.where().equalTo("name", (String) item.getTitle()).findFirst();
                category.setIsFavorite(false);
                category.setFavOrder(0L);
                realm.commitTransaction();

                removeCateBtn(category);

                return false;
            }
        });
        popup.show();
    }



    private void addCateBtns(RealmResults<Category> results) {
        for (final Category category : results) {
            addCateBtn(category);
        }
    }

    private void addCateBtn(final Category category) {
        final Context context = getContext();

        final Button btnCate = (Button) LayoutInflater.from(context).inflate(R.layout.view_btn_cate, container, false);
        btnCate.setId(category.getId().intValue());
        btnCate.setText(category.getName());
        btnCate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interactionFavoriteView != null)
                    interactionFavoriteView.clickFavoriteItem(category);
            }
        });

        container.addView(btnCate);

        changeUi();
    }

    private void removeCateBtn(Category category) {
        View view = container.findViewById(category.getId().intValue());
        if (view != null) {
            container.removeView(view);
        }

        changeUi();
    }

    public void setInteractionFavoriteView(InteractionFavoriteView interactionFavoriteView) {
        this.interactionFavoriteView = interactionFavoriteView;
    }

    public interface InteractionFavoriteView {
        void clickFavoriteItem(Category category);
    }

}
