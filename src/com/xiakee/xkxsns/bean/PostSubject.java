package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/13.
 */
public class PostSubject {
    public List<PostSubjectData> topicList;

    @Override
    public String toString() {
        return "PostSubject{" +
                "topicList=" + topicList +
                '}';
    }

    public static class PostSubjectData {

        public String topicTime;
        public String topicId;
        public int status;
        public String title;

        @Override
        public String toString() {
            return "PostSubjectData{" +
                    "topicTime='" + topicTime + '\'' +
                    ", topicId='" + topicId + '\'' +
                    ", status=" + status +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}
