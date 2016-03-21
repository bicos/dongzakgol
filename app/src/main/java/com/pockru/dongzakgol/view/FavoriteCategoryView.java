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
import android.widget.TextView;

import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.model.Category;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

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

    RealmConfiguration realmConfig;
    Realm realm;

    LinearLayout container;

    InteractionFavoriteView interactionFavoriteView;

    private void init(final Context context) {

        realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);

        inflate(context, R.layout.view_favorite_cate, this);

        TextView hint = (TextView) findViewById(R.id.hint_add_favorite_cate);
        final Button btnAdd = (Button) findViewById(R.id.btn_add_favorite_cate);
        container = (LinearLayout) findViewById(R.id.main_container);

        final RealmResults<Category> results = realm.where(Category.class).equalTo("isFavorite", true).findAll();

        if (results.size() > 0) {
            container.removeView(hint);
            addCateBtns(results);
        }

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCateList(v);
            }
        });
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
                realm.beginTransaction();
                Category category = results.where().equalTo("name", (String) item.getTitle()).findFirst();
                category.setIsFavorite(true);
                realm.commitTransaction();

                addCateBtn(category);
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
        btnCate.setText(category.getName());
        btnCate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interactionFavoriteView != null) interactionFavoriteView.clickFavoriteItem(category);
            }
        });

        container.addView(btnCate);
    }

    public void setInteractionFavoriteView(InteractionFavoriteView interactionFavoriteView) {
        this.interactionFavoriteView = interactionFavoriteView;
    }

    public interface InteractionFavoriteView {
        void clickFavoriteItem(Category category);
    }

}
