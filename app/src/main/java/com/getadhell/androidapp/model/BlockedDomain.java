package com.getadhell.androidapp.model;

public class BlockedDomain {
    public int id;
    public String url;
    public long blockTimestamp;
    public String packageName;


    public BlockedDomain() {
    }

    public BlockedDomain(int id, String url, long blockTimestamp) {
        this.id = id;
        this.url = url;
        this.blockTimestamp = blockTimestamp;
    }

}
