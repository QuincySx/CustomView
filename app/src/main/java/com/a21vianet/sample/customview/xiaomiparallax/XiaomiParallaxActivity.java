package com.a21vianet.sample.customview.xiaomiparallax;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.a21vianet.sample.customview.R;

import java.util.ArrayList;
import java.util.List;

public class XiaomiParallaxActivity extends AppCompatActivity {
    private ParallaxRecyclerView mParallaxRecyclerView;
    private List<ImageBean> mImageBeen = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaomi_parallax);

        for (int i = 0; i < 50; i++) {
            mImageBeen.add(new ImageBean(R.drawable.ic_head));
        }

        mParallaxRecyclerView = (ParallaxRecyclerView) findViewById(R.id.parallax_recyclerview);

        mParallaxRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mParallaxRecyclerView.setAdapter(new MyAdapter(this, mImageBeen));

    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Context mContext;
        private List<ImageBean> mImageBeen;

        public MyAdapter(Context context, List<ImageBean> imageBeen) {
            mContext = context;
            mImageBeen = imageBeen;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_hred, parent, false);
            return new MyViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mImageView.setBackground(mContext.getResources().getDrawable(mImageBeen.get(position).getImageid()));
        }

        @Override
        public int getItemCount() {
            return mImageBeen.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_hard);
        }
    }
}
