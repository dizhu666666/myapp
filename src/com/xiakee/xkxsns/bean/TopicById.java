package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/26.
 */
public class TopicById extends BaseBean {

    public List<TopicByIdData> topicList;

    @Override
    public String toString() {
        return "TopicById{" +
                "topicList=" + topicList +
                '}';
    }

    public static class TopicByIdData {
        public String topicTime;
        public String topicId;
        public String topicDesc;
        public String commentCount;
        public String postCity;
        public String goodCount;
        public String imgCount;
        public String imgUrl1;
        public String imgUrl2;
        public String imgUrl3;
        public String readCount;
        public String goodStatus;
        public String title;
        public String userId;
        public String userName;
        public String photo;
        public String sex;
        public String lv;

        @Override
        public String toString() {
            return "TopicByIdData{" +
                    "topicTime='" + topicTime + '\'' +
                    ", topicId='" + topicId + '\'' +
                    ", topicDesc='" + topicDesc + '\'' +
                    ", commentCount='" + commentCount + '\'' +
                    ", postCity='" + postCity + '\'' +
                    ", goodCount='" + goodCount + '\'' +
                    ", imgCount='" + imgCount + '\'' +
                    ", imgUrl1='" + imgUrl1 + '\'' +
                    ", imgUrl2='" + imgUrl2 + '\'' +
                    ", imgUrl3='" + imgUrl3 + '\'' +
                    ", readCount='" + readCount + '\'' +
                    ", goodStatus='" + goodStatus + '\'' +
                    ", title='" + title + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", photo='" + photo + '\'' +
                    ", sex='" + sex + '\'' +
                    ", lv='" + lv + '\'' +
                    '}';
        }
    }
}
