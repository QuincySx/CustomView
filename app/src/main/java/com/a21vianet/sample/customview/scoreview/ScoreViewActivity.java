package com.a21vianet.sample.customview.scoreview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.a21vianet.sample.customview.R;

public class ScoreViewActivity extends AppCompatActivity {
    private ScoreView mScoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_view);
        mScoreView = (ScoreView) findViewById(R.id.scoreView);
    }

    public void onClick(View view) {
        mScoreView
                .setNumberText(96)
                .setDescribeText("正确率")
                .setTextColor(0XFFFFFFFF)
                .start();
    }
}
