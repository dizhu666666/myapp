package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/5.
 */
public class BaseBean {
    public String resmsg;
    public String rescode;

    public boolean checkData() {
        return "00000".equals(rescode);
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "resmsg='" + resmsg + '\'' +
                ", rescode='" + rescode + '\'' +
                '}';
    }
}
