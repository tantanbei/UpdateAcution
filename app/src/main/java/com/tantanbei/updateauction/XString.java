package com.tantanbei.updateauction;

public class XString {

    static public boolean IsEmpty(String str) {
        if (str == null || str.length() == 0 || str.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
