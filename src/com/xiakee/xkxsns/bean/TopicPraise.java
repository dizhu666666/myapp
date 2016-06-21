package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/17.
 */
public class TopicPraise extends BaseBean {
    public String topicId;
    public String goodStatus;

    public boolean isPraise(){
        return "1".equals(goodStatus);
    }

    @Override
    public String toString() {
        return "TopicPraise{" +
                "topicId='" + topicId + '\'' +
                ", goodStatus='" + goodStatus + '\'' +
                '}';
    }
}
