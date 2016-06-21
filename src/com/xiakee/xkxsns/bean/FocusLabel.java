package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/26.
 */
public class FocusLabel extends BaseBean {
    public List<FocusLabelData> labelList;


    @Override
    public String toString() {
        return "FocusLabel{" +
                "labelList=" + labelList +
                '}';
    }

    public static class FocusLabelData {
        /**
         * "labelId":10,"title":"始祖鸟","type":1,"logo":"/commpics/topic/2015/11/4369160d6ec741e5af2303631adb80a5.jpg","labelDesc":"非常好看的始祖鸟"
         */

        public String labelId;
        public String title;
        public String type;
        public String logo;
        public String labelDesc;

        @Override
        public String toString() {
            return "FocusLabelData{" +
                    "labelId='" + labelId + '\'' +
                    ", title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", logo='" + logo + '\'' +
                    ", labelDesc='" + labelDesc + '\'' +
                    '}';
        }
    }
}
