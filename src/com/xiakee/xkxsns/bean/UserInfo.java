package com.xiakee.xkxsns.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by William on 2015/11/5.
 */
public class UserInfo extends BaseBean {
    public UserInfoData userInfo;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userInfo=" + userInfo +
                '}';
    }

    /***
     * "credit":11,
     * "focusCount":1,"fansCount":0,"lv":1,"photo":"1","sign":"",
     * "userId":1898,"userName":"dizhu666",
     * "sex":1,"signStatus":0,"age":19,"province":"北京","city":"大兴"
     */
    public static class UserInfoData implements Parcelable {
        public String credit;
        public String sex;
        public String age;
        public String province;
        public String city;
        public String focusCount;
        public String fansCount;
        public String photo;
        public String userId;
        public String userName;
        public String signStatus;//签到状态
        public String sign;//个性签名
        public int lv;//等级
        public String birthdays;
        public String token;

        @Override
        public String toString() {
            return "UserInfoData{" +
                    "credit='" + credit + '\'' +
                    ", sex='" + sex + '\'' +
                    ", age='" + age + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", focusCount='" + focusCount + '\'' +
                    ", fansCount='" + fansCount + '\'' +
                    ", photo='" + photo + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", signStatus='" + signStatus + '\'' +
                    ", sign='" + sign + '\'' +
                    ", lv=" + lv +
                    ", birthdays='" + birthdays + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }

        protected UserInfoData(Parcel in) {
            credit = in.readString();
            sex = in.readString();
            age = in.readString();
            province = in.readString();
            city = in.readString();
            focusCount = in.readString();
            fansCount = in.readString();
            photo = in.readString();
            userId = in.readString();
            userName = in.readString();
            signStatus = in.readString();
            sign = in.readString();
            lv = in.readInt();
            birthdays = in.readString();
            token = in.readString();
        }

        public static final Creator<UserInfoData> CREATOR = new Creator<UserInfoData>() {
            @Override
            public UserInfoData createFromParcel(Parcel in) {
                return new UserInfoData(in);
            }

            @Override
            public UserInfoData[] newArray(int size) {
                return new UserInfoData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(credit);
            dest.writeString(sex);
            dest.writeString(age);
            dest.writeString(province);
            dest.writeString(city);
            dest.writeString(focusCount);
            dest.writeString(fansCount);
            dest.writeString(photo);
            dest.writeString(userId);
            dest.writeString(userName);
            dest.writeString(signStatus);
            dest.writeString(sign);
            dest.writeInt(lv);
            dest.writeString(birthdays);
            dest.writeString(token);
        }
    }

}
