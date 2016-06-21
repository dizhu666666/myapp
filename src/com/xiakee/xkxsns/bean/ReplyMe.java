package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/12/2.
 */
public class ReplyMe extends BaseBean {
    public List<ReplyMeData> commentsList;

    @Override
    public String toString() {
        return "ReplyMe{" +
                "commentsList=" + commentsList +
                '}';
    }

    public static class ReplyMeData{

        public String commentTime;
        public String topicId;
        public String floor;
        public String commentId;
        public String commentType;
        public String title;
        public String userId;
        public String userName;
        public String photo;
        public String parentTitle;

        @Override
        public String toString() {
            return "ReplyMeData{" +
                    "commentTime='" + commentTime + '\'' +
                    ", topicId='" + topicId + '\'' +
                    ", floor='" + floor + '\'' +
                    ", commentId='" + commentId + '\'' +
                    ", commentType='" + commentType + '\'' +
                    ", title='" + title + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", photo='" + photo + '\'' +
                    ", parentTitle='" + parentTitle + '\'' +
                    '}';
        }
    }
}
