package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/7.
 */
public class CommentReply extends BaseBean {
    public List<Comment.CommentData2> commentsList;

    @Override
    public String toString() {
        return "Comment{" +
                "commentsList=" + commentsList +
                '}';
    }
}
