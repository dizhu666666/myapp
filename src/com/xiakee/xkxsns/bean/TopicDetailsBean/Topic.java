package com.xiakee.xkxsns.bean.TopicDetailsBean;

import com.xiakee.xkxsns.bean.BaseBean;

/**
 * Created by William on 2015/11/7.
 */
public class Topic  extends BaseBean{
    public TopicDetails topicDetail;

    @Override
    public String toString() {
        return "Topic{" +
                "topicDetail=" + topicDetail +
                '}';
    }
}
