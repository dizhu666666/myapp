package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/5.
 */
public class PostReply extends BaseBean {
    public List<PostReplyData> commentsList;

    @Override
    public String toString() {
        return "PostReply{" +
                "commentsList=" + commentsList +
                '}';
    }

    public static class PostReplyData {

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
        public String title;
        public String comment;
        public String commentUrl;
        public String commentId;

        @Override
        public String toString() {
            return "PostReplyData{" +
                    "topicTime='" + topicTime + '\'' +
                    ", topicId='" + topicId + '\'' +
                    ", topicDesc='" + topicDesc + '\'' +
                    ", commentCount='" + commentCount + '\'' +
                    ", goodCount='" + goodCount + '\'' +
                    ", imgCount='" + imgCount + '\'' +
                    ", imgUrl1='" + imgUrl1 + '\'' +
                    ", imgUrl2='" + imgUrl2 + '\'' +
                    ", imgUrl3='" + imgUrl3 + '\'' +
                    ", readCount='" + readCount + '\'' +
                    ", title='" + title + '\'' +
                    ", comment='" + comment + '\'' +
                    ", commentUrl='" + commentUrl + '\'' +
                    ", commentId='" + commentId + '\'' +
                    '}';
        }
    }
}
