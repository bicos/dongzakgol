package com.pockru.dongzakgol;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ravy on 16. 3. 21..
 */
public class FavoriteCategoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite_category);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.favorite_category_list);


    }

    private class FavoriteCategoryAdapter extends RecyclerView.Adapter<FavoriteCategoryViewHolder>{

        public FavoriteCategoryAdapter(Context context) {

        }

        @Override
        public FavoriteCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(FavoriteCategoryViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class FavoriteCategoryViewHolder extends RecyclerView.ViewHolder{

        public FavoriteCategoryViewHolder(View itemView) {
            super(itemView);
        }
    }
}
