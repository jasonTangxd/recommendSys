package com.xxo;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
/**
 * Created by xiaoxiaomo on 2015/6/28.
 */
public class PropertyHelper {
    private ResourceBundle propBundle;

    public PropertyHelper(String bundle){
        propBundle = PropertyResourceBundle.getBundle(bundle);
    }

    public  String getValue(String key){
        return this.propBundle.getString(key);
    }
}
