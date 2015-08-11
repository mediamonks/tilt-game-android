package com.mediamonks.googleflip.data.vo;

import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mediamonks.googleflip.data.constants.LevelDifficulty;
import com.mediamonks.googleflip.data.database.DataProvider;

import nl.qbusict.cupboard.annotation.Column;

/**
 * VO class for storing level data
 */
public class LevelVO implements Parcelable {
    public static final Uri URI = Uri.parse("content://" + DataProvider.PROVIDER_AUTHORITY + "/" + DataProvider.BASE_LEVEL);

    public static final String FIELD_ID = "_id";
    public static final String FIELD_LEVEL_CLASS = "levelClass";
    public static final String FIELD_CONTROLLER_CLASS = "controllerClass";
    public static final String FIELD_DIFFICULTY = "difficulty";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_UNLOCKED = "unlocked";

    @Column(FIELD_ID)
    @SerializedName(FIELD_ID)
    public Long id;
    @Column(FIELD_LEVEL_CLASS)
    @SerializedName(FIELD_LEVEL_CLASS)
    public String levelClass;
    @Column(FIELD_CONTROLLER_CLASS)
    @SerializedName(FIELD_CONTROLLER_CLASS)
    public String controllerClass;
    @Column(FIELD_DIFFICULTY)
    @SerializedName(FIELD_DIFFICULTY)
    public int difficulty;
    @Column(FIELD_DURATION)
    @SerializedName(FIELD_DURATION)
    public int duration;
    @Column(FIELD_UNLOCKED)
    @SerializedName(FIELD_UNLOCKED)
    public boolean unlocked;

    public static LevelVO createFromXML (XmlResourceParser parser, String levelPackage, String controllerPackge) {
        LevelVO levelVO = new LevelVO();
        levelVO.id = (long)parser.getAttributeIntValue(null, "id", 0);
        levelVO.levelClass = levelPackage + parser.getAttributeValue(null, "levelclass");
        levelVO.controllerClass = controllerPackge + parser.getAttributeValue(null, "controllerclass");
        levelVO.difficulty = parser.getAttributeIntValue(null, "difficulty", 0);
        levelVO.duration = parser.getAttributeIntValue(null, "duration", 25);

        return levelVO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.levelClass);
        dest.writeString(this.controllerClass);
        dest.writeInt(this.difficulty);
        dest.writeInt(this.duration);
    }

    public LevelVO() {
    }

    protected LevelVO(Parcel in) {
        this.id = in.readLong();
        this.levelClass = in.readString();
        this.controllerClass = in.readString();
        this.difficulty = in.readInt();
        this.duration = in.readInt();
    }

    public static final Parcelable.Creator<LevelVO> CREATOR = new Parcelable.Creator<LevelVO>() {
        public LevelVO createFromParcel(Parcel source) {
            return new LevelVO(source);
        }

        public LevelVO[] newArray(int size) {
            return new LevelVO[size];
        }
    };

    @Override
    public String toString() {
        return "LevelVO{" +
                "id='" + id + '\'' +
                ", levelClass='" + levelClass + '\'' +
                ", controllerClass='" + controllerClass + '\'' +
                ", difficulty=" + difficulty +
                ", duration=" + duration +
                '}';
    }
}
