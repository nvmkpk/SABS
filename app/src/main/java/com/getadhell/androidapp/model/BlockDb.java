package com.getadhell.androidapp.model;

import java.util.List;

public class BlockDb {
    public String copyright;
    public String updatedAt;
    public List<String> urlsToBlock;

    public BlockDb() {
    }

    public BlockDb(String copyright, List<String> urlsToBlock) {
        this.copyright = copyright;
        this.urlsToBlock = urlsToBlock;
    }
}
