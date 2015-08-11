package temple.core.net;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import retrofit.RetrofitError;

/**
 * Created by dylan on 6-10-14.
 */
public class ApiErrorVO implements Parcelable {

    public static final Creator<ApiErrorVO> CREATOR
            = new Creator<ApiErrorVO>() {
        public ApiErrorVO createFromParcel(Parcel source) {
            return new ApiErrorVO(source);
        }

        public ApiErrorVO[] newArray(int size) {
            return new ApiErrorVO[size];
        }
    };

    @SerializedName("code")
    public String code;
    @SerializedName("message")
    public String message;

    private RetrofitError.Kind mErrorKind;

    public ApiErrorVO() {
    }

    @Override
    public String toString() {
        return "ApiErrorVO{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    private ApiErrorVO(Parcel in) {
        this.code = in.readString();
        this.message = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.message);
    }

    public void setErrorKind(RetrofitError.Kind errorKind) {
        mErrorKind = errorKind;
    }

    public boolean noInternet() {
        return mErrorKind == RetrofitError.Kind.NETWORK;
    }

    // TODO do some error handling here to match error code with string resources etc.
}
