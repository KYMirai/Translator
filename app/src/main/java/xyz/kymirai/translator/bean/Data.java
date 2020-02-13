package xyz.kymirai.translator.bean;

import java.util.ArrayList;

public class Data {
    public ArrayList<String> translation;
    public String
            l,
            errorCode,
            query;
    public basic basic;
    public ArrayList<web> web;

    public class web {
        public String key;
        public ArrayList<String> value;
    }

    public class basic {
        public String phonetic;
    }
}
