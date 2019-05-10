package com.ytlz.mylibrary;

import org.litepal.crud.LitePalSupport;

/**
 * Created by wyb on 2019-04-26.
 */

public class FaceData extends LitePalSupport {

    String color;
    String type;

     byte[] shuxing;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getShuxing() {
        return shuxing;
    }

    public void setShuxing(byte[] shuxing) {
        this.shuxing = shuxing;
    }
}
