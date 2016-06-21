package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/4.
 */
public class Collection extends BaseBean {

    public List<CollectionData> topicList;

    @Override
    public String toString() {
        return "Collection{" +
                "resmsg='" + resmsg + '\'' +
                ", rescode='" + rescode + '\'' +
                ", topicList=" + topicList +
                '}';
    }

    public static class CollectionData {

        public String topicTime;

        public String topicId;

        public String topicDesc;

        public String commentCount;

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

        @Override
        public String toString() {
            return "Data{" +
                    "topicTime='" + topicTime + '\'' +
                    ", topicId=" + topicId +
                    ", topicDesc='" + topicDesc + '\'' +
                    ", commentCount=" + commentCount +
                    ", goodCount=" + goodCount +
                    ", imgCount=" + imgCount +
                    ", imgUrl1='" + imgUrl1 + '\'' +
                    ", imgUrl2='" + imgUrl2 + '\'' +
                    ", imgUrl3='" + imgUrl3 + '\'' +
                    ", readCount=" + readCount +
                    ", goodStatus=" + goodStatus +
                    ", title='" + title + '\'' +
                    ", userId=" + userId +
                    ", userName='" + userName + '\'' +
                    ", photo='" + photo + '\'' +
                    '}';
        }
    }

}
