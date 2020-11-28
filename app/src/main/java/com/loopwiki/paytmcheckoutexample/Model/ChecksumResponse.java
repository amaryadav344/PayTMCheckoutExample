package com.loopwiki.paytmcheckoutexample.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChecksumResponse {

    @SerializedName("checksum")
    @Expose
    private String checksum;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}