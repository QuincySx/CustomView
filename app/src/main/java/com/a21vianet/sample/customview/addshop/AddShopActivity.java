package com.a21vianet.sample.customview.addshop;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.a21vianet.sample.customview.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AddShopActivity extends AppCompatActivity {
    private GridView mGridView;
    private FloatingActionButton mButton;
    private ConstraintLayout mView;

    private MyAdapter mAdapter;
    private List<Integer> mIntegers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        mGridView = (GridView) findViewById(R.id.grid_view);
        mButton = (FloatingActionButton) findViewById(R.id.btn_car);
        mView = (ConstraintLayout) findViewById(R.id.root_view);

        for (int i = 0; i <= 45; i++) {
            mIntegers.add(R.drawable.icon_image);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startanim(view.findViewById(R.id.img_head), mButton);
            }
        });

        mAdapter = new MyAdapter(this, mIntegers);
        mGridView.setAdapter(mAdapter);
    }


    class MyAdapter extends BaseAdapter {
        private List<Integer> mList;
        private Context mContext;

        public MyAdapter(Context context, @NonNull List list) {
            mList = list;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                convertView = mInflater.inflate(R.layout.item_addshop, parent, false);

                viewHolder.head = (ImageView) convertView.findViewById(R.id.img_head);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Glide.with(mContext).load(mList.get(position)).into(viewHolder.head);
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView head;
    }

    private void startanim(@NonNull View beginview, @NonNull View endview) {
        if (beginview instanceof ImageView) {
            ImageView beginview1 = (ImageView) beginview;

            AddShopDrawableView addShopDrawableView = new AddShopDrawableView(this);

            addShopDrawableView.startAnim(beginview1, endview, mView);
        }
    }
}
