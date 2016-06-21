package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/19.
 */
public class CommentPraise extends BaseBean {
    public String commentsId;
    public String goodStatus;

    public boolean isPraise() {
        return "1".equals(goodStatus);
    }

    @Override
    public String toString() {
        return "CommentPraise{" +
                "commentsId='" + commentsId + '\'' +
                ", goodStatus='" + goodStatus + '\'' +
                '}';
    }
}
