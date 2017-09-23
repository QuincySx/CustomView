package com.a21vianet.sample.customview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.a21vianet.sample.customview.addshop.AddShopActivity;
import com.a21vianet.sample.customview.countdown.CountdownActivity;
import com.a21vianet.sample.customview.custombehavior1.CustomBehavior1Activity;
import com.a21vianet.sample.customview.custombehavior2.CustomBehavior2Activity;
import com.a21vianet.sample.customview.pullview.PullViewActivity;
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
            case R.id.btn_addcar:
                startActivity(new Intent(this, AddShopActivity.class));
                break;
            case R.id.btn_countdown:
                startActivity(new Intent(this, CountdownActivity.class));
                break;
            case R.id.btn_custom_behavior1:
                startActivity(new Intent(this, CustomBehavior1Activity.class));
                break;
            case R.id.btn_custom_behavior2:
                startActivity(new Intent(this, CustomBehavior2Activity.class));
                break;
            case R.id.btn_pull_view:
                startActivity(new Intent(this, PullViewActivity.class));
                break;
        }
    }
}
