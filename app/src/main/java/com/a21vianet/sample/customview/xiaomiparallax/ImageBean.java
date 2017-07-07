package com.a21vianet.sample.customview.xiaomiparallax;

import android.support.annotation.DrawableRes;

/**
 * Created by wang.rongqiang on 2017/7/7.
 */

public class ImageBean {
    @DrawableRes
    private int Imageid;

    public ImageBean(int imageid) {
        Imageid = imageid;
    }

    public int getImageid() {
        return Imageid;
    }

    public void setImageid(int imageid) {
        Imageid = imageid;
    }
}
