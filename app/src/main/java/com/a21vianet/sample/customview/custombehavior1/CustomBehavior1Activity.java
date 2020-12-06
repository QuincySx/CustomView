package com.a21vianet.sample.customview.custombehavior1;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a21vianet.sample.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang.rongqiang on 2017/7/12.
 */
public class CustomBehavior1Activity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    List<String> mStringList = new ArrayList<>();
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior1);

        for (int i = 0; i < 20; i++) {
            mStringList.add("测试" + i);
        }

        mMyAdapter = new MyAdapter(this, mStringList);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMyAdapter);
        mMyAdapter.notifyDataSetChanged();
    }

    private static class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Context mContext;
        private List<String> mStrings;

        public MyAdapter(Context context, List<String> strings) {
            mContext = context;
            mStrings = strings;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_text, parent, false);
            return new ViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mStrings.get(position));
        }

        @Override
        public int getItemCount() {
            return mStrings.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_text);
        }
    }
}
