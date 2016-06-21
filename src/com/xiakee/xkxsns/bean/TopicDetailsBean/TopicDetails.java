package com.xiakee.xkxsns.bean.TopicDetailsBean;

import java.util.List;

import com.xiakee.xkxsns.bean.BaseBean;

/**
 * Created by William on 2015/11/7.
 */
public class TopicDetails extends BaseBean {

    public String topicTime;
    public String topicId;
    public String collectStatus;
    public String title;
    public String userId;
    public String userName;
    public String photo;
    public int commentCount;
    public int goodCount;
    public String goodStatus;
    public int lv;
    public String sex;
    public String postCity;
    public List<TopicDetailsImage> topicImgs;
    public List<TopicLabel> topicLabels;

    /***
     * photo":"/commpics/bbs/2015/10/21a71ba9-a701-4134-80d1-b101844b9a43.png"，"commentCount":0,"goodCount":1
     * "
     */

    public List<TopicDetailsGoodUser> goodUsers;

    public boolean isPraise() {
        return "1".equals(goodStatus);
    }

    @Override
    public String toString() {
        return "TopicDetails{" +
                "topicTime='" + topicTime + '\'' +
                ", topicId='" + topicId + '\'' +
                ", collectStatus='" + collectStatus + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", photo='" + photo + '\'' +
                ", commentCount=" + commentCount +
                ", goodCount=" + goodCount +
                ", goodStatus='" + goodStatus + '\'' +
                ", lv=" + lv +
                ", sex='" + sex + '\'' +
                ", postCity='" + postCity + '\'' +
                ", topicImgs=" + topicImgs +
                ", goodUsers=" + goodUsers +
                '}';
    }
}
