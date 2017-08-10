package com.a21vianet.sample.customview.pullview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.a21vianet.sample.customview.R;

public class PullViewActivity extends AppCompatActivity {
    private PullView mPullView;
    private ConstraintLayout mConstraintLayout;

    float mTagY = 0;
    int mTagDis = 0;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPullView.setReset();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pullview);
        mPullView = (PullView) findViewById(R.id.pullview);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.root_view);
        mConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTagY = event.getY();
                        mTagDis = mPullView.getTagPullDistance();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        if (y > mTagY) {
                            float moveSize = y - mTagY;
                            float progress = moveSize >= mTagDis ? 1 : moveSize / mTagDis;
                            mPullView.setProgress(progress);
                            if (progress == 1) {
                                refash();
                            }
                            return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        float moveSize = event.getY() - mTagY;
                        if (moveSize < mTagDis) {
                            mPullView.setReset();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void refash() {
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }


}
