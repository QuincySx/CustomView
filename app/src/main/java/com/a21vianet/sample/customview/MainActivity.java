package com.a21vianet.sample.customview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.a21vianet.sample.customview.scoreview.ScoreViewActivity;
import com.a21vianet.sample.customview.xiaomiparallax.XiaomiParallaxActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_xiaomi_parallax:
                startActivity(new Intent(this, XiaomiParallaxActivity.class));
                break;
            case R.id.btn_scoreview:
                startActivity(new Intent(this, ScoreViewActivity.class));
                break;
        }
    }
}
