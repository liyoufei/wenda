package com.sss.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
    private Map<String,Object> objs = new HashMap<String,Object>();

    public Object get(String key){
        return objs.get(key);
    }
    public void set(String key,Object obj){
        objs.put(key,obj);
    }
}
