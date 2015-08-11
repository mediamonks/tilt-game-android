package com.mediamonks.googleflip.data.vo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mediamonks.googleflip.data.database.DataProvider;

import nl.qbusict.cupboard.annotation.Column;

/**
 * VO class for storing player result for a single level
 */
public class LevelResultVO implements Parcelable {
    public static final Uri URI = Uri.parse("content://" + DataProvider.PROVIDER_AUTHORITY + "/" + DataProvider.BASE_LEVEL_RESULT);

    public static final String FIELD_ID = "_id";

    @Column(FIELD_ID)
    @SerializedName(FIELD_ID)
    public Long id;
    public float seconds;
    public boolean success;

    public LevelResultVO() {
    }

    public LevelResultVO(Long id, float seconds, boolean success) {
        this.id = id;
        this.seconds = seconds;
        this.success = success;
    }

    public LevelResultVO(Long id) {
        this(id, 0, false);
    }

    public void copyFrom(LevelResultVO source) {
        seconds = source.seconds;
        success = source.success;
    }

    @Override
    public String toString() {
        return "LevelResultVO{" +
                "id='" + id + '\'' +
                ", seconds=" + seconds +
                ", success=" + success +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeFloat(this.seconds);
        dest.writeByte(success ? (byte) 1 : (byte) 0);
    }

    protected LevelResultVO(Parcel in) {
        this.id = in.readLong();
        this.seconds = in.readFloat();
        this.success = in.readByte() != 0;
    }

    public static final Parcelable.Creator<LevelResultVO> CREATOR = new Parcelable.Creator<LevelResultVO>() {
        public LevelResultVO createFromParcel(Parcel source) {
            return new LevelResultVO(source);
        }

        public LevelResultVO[] newArray(int size) {
            return new LevelResultVO[size];
        }
    };
}
