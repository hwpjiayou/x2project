package com.renren.mobile.x2.core.xmpp;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: xiaobo.yuan
 * Date: 10/12/12
 * Time: 11:25 PM
 */
public class Utils {
    public static String showAllFields(int length ,Object object){
        StringBuilder builder = new StringBuilder();
        Field[] fields = object.getClass().getFields();
        builder.append("[\r\n");
        for(Field f:fields){
            f.setAccessible(true);
            try {
                if(!f.getName().equals("mParent")){
                    String text  = (length>0?"	":"")+f.getName()+"="+f.get(object);
                    text.trim();
                    builder.append(text+"\r\n");
                }
            } catch (Exception e) {}
        }
        if(length==0){
            builder.append("]\r\n");
        }else{
            builder.append("	]");
        }
        return builder.toString();
    }
}
