package com.a21vianet.sample.customview.countdown;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.a21vianet.sample.customview.R;

public class CountdownActivity extends AppCompatActivity implements View.OnClickListener {
    private CountdownPathMeasureView mCountdownPathMeasureView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        mCountdownPathMeasureView = (CountdownPathMeasureView) findViewById(R.id
                .countdownPathMeasureView);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mCountdownPathMeasureView.setOnClickListener(this);
        mCountdownPathMeasureView.setOnStatusListener(new CountdownPathMeasureView
                .OnStatusListener() {

            @Override
            public void start() {
                Toast.makeText(CountdownActivity.this, "开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void end() {
                Toast.makeText(CountdownActivity.this, "结束", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                mCountdownPathMeasureView.statAnim();
                break;
            case R.id.countdownPathMeasureView:
                Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
