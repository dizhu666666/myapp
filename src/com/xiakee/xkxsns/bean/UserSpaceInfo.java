package com.xiakee.xkxsns.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by william on 2015/11/5.
 */
public class UserSpaceInfo extends BaseBean implements Parcelable {

    public int focusStatus;
    public int focusCount;
    public int fansCount;
    public String photo;
    public int userId;
    public String userName;
    public int topicCount;
    public int goodCount;
    public int lv;
    public int credit;
    public String age;
    public String province;
    public String city;
    public String sex;
    public String sign;

    protected UserSpaceInfo(Parcel in) {
        focusStatus = in.readInt();
        focusCount = in.readInt();
        fansCount = in.readInt();
        photo = in.readString();
        userId = in.readInt();
        userName = in.readString();
        topicCount = in.readInt();
        goodCount = in.readInt();
        lv = in.readInt();
        credit = in.readInt();
        age = in.readString();
        province = in.readString();
        city = in.readString();
        sex = in.readString();
        sign = in.readString();
    }

    public static final Creator<UserSpaceInfo> CREATOR = new Creator<UserSpaceInfo>() {
        @Override
        public UserSpaceInfo createFromParcel(Parcel in) {
            return new UserSpaceInfo(in);
        }

        @Override
        public UserSpaceInfo[] newArray(int size) {
            return new UserSpaceInfo[size];
        }
    };

    @Override
    public String toString() {
        return "UserSpaceInfo{" +
                "focusStatus=" + focusStatus +
                ", focusCount=" + focusCount +
                ", fansCount=" + fansCount +
                ", photo='" + photo + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", topicCount=" + topicCount +
                ", goodCount=" + goodCount +
                ", lv=" + lv +
                ", credit=" + credit +
                ", age='" + age + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", sex='" + sex + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(focusStatus);
        dest.writeInt(focusCount);
        dest.writeInt(fansCount);
        dest.writeString(photo);
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeInt(topicCount);
        dest.writeInt(goodCount);
        dest.writeInt(lv);
        dest.writeInt(credit);
        dest.writeString(age);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(sex);
        dest.writeString(sign);
    }
}
