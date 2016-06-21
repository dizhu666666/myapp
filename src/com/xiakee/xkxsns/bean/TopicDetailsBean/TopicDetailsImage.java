package com.xiakee.xkxsns.bean.TopicDetailsBean;

import java.util.List;

import com.xiakee.xkxsns.bean.BaseBean;

/**
 * Created by William on 2015/11/7.
 */
public class TopicDetailsImage extends BaseBean {
    /***
     *  /***
     * topicImgs":[{"text":"","sort":0,"url":"/commpics/topic/2015/10/bee18dd19dd647ddbafd6092e47d0404.jpg",
     * "imgLabels":[]},
     * {"text":"sdfasdfsadfsadf","sort":1,"url":"","imgLabels":[]},
     * {"text":"","sort":2,"url":"/commpics/topic/2015/10/02e5c0e0e1ee45f8afd505a39a8ab3ca.jpg","imgLabels":[]},
     * {"text":"asdfsadfsadfsadf","sort":3,"url":"","imgLabels":[]},
     * {"text":"","sort":4,"url":"/commpics/topic/2015/10/2685f4728f164938995e0582e6607716.jpg","imgLabels":[]}]
     */

    public String text;
    public String sort;
    public String url;
    public List<TopicDetailsImgLabel> imgLabels;

    @Override
    public String toString() {
        return "TopicDetailsImage{" +
                "text='" + text + '\'' +
                ", sort='" + sort + '\'' +
                ", url='" + url + '\'' +
                ", imgLabels=" + imgLabels +
                '}';
    }
}
