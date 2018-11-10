
package com.earaujo.usinglivedata.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gildings {

    @SerializedName("gid_1")
    @Expose
    private int gid1;
    @SerializedName("gid_2")
    @Expose
    private int gid2;
    @SerializedName("gid_3")
    @Expose
    private int gid3;

    public int getGid1() {
        return gid1;
    }

    public void setGid1(int gid1) {
        this.gid1 = gid1;
    }

    public int getGid2() {
        return gid2;
    }

    public void setGid2(int gid2) {
        this.gid2 = gid2;
    }

    public int getGid3() {
        return gid3;
    }

    public void setGid3(int gid3) {
        this.gid3 = gid3;
    }

}
