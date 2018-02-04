package com.layoutxml.sabs.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUrlPatternsMatch {


    public static boolean wildcardValid (String domain){
        String wildcardPattern = "(?i)^([\\*]?)([A-Z0-9-_.]+)([\\*]?)$";
        Pattern r = Pattern.compile(wildcardPattern);
        Matcher m = r.matcher(domain);
        boolean matchResult = m.matches();
        return matchResult;
    }

    public static boolean domainValid (String domain){
        String domainPattern = "(?i)(?=^.{4,253}$)(^((?!-)[a-z0-9-]{1,63}(?<!-)\\.)+[a-z]{2,63}$)";
        Pattern r = Pattern.compile(domainPattern);
        Matcher m = r.matcher(domain);
        boolean matchResult = m.matches();
        return matchResult;
    }

}