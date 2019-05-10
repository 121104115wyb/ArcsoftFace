package com.ytlz.mylibrary;

import org.litepal.crud.LitePalSupport;

/**
 * Created by wyb on 2019-04-26.
 */

public class CommonData extends LitePalSupport {

    String country;

    String length;

    String with;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }
}
