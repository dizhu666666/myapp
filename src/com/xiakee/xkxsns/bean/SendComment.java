package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/7.
 */
public class SendComment {
    public SendCommentData comment;

    public static class SendCommentData {
        /***
         * "title":”评论内容”,
         * " userId":1898,
         * "topicId":155,
         * parentId":3,
         * " parentUserId":1898
         */

        public String title;
        public String userId;
        public String topicId;
        public String parentId;
        public String parentUserId;
        public int type;
        public String token;
        public String parentCommentsId;

    }
}
