package com.a21vianet.sample.customview.pullview;

import android.util.Pair;

/**
 * Created by quincysx on 2017/8/9.
 */

public class BezierUtiltiy {
    /**
     * 计算贝塞尔曲线的点，支持高阶贝塞尔曲线，但是不建议使用四阶以上贝塞尔曲线，因为计算量呈几何倍造成手机卡顿
     *
     * @param progress
     * @param pairs
     * @return
     */
    public static Pair<Float, Float> computeBezier(float progress, float[]... pairs) {
        for (int i = pairs.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                pairs[j][0] = (pairs[j + 1][0] - pairs[j][0]) * progress;
                pairs[j][1] = (pairs[j + 1][1] - pairs[j][1]) * progress;
            }
        }
        return new Pair(pairs[0][0], pairs[0][1]);
    }
}
