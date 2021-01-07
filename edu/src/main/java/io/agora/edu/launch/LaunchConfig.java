package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class LaunchConfig implements Parcelable {
    /**
     * 不参与序列化，只作为启动Activity时所用
     */
    @NotNull
    public Context context;
    private String whiteBoardAppId;
    private int eyeProtect;

    public LaunchConfig(Context context, String whiteBoardAppId, int eyeProtect) {
        this.context = context;
        this.whiteBoardAppId = whiteBoardAppId;
        this.eyeProtect = eyeProtect;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getWhiteBoardAppId() {
        return whiteBoardAppId;
    }

    public void setWhiteBoardAppId(String whiteBoardAppId) {
        this.whiteBoardAppId = whiteBoardAppId;
    }

    public boolean isEyeProtect() {
        return eyeProtect == 1;
    }

    public void setEyeProtect(int eyeProtect) {
        this.eyeProtect = eyeProtect;
    }

    public static Creator<LaunchConfig> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.whiteBoardAppId);
        dest.writeInt(this.eyeProtect);
    }

    protected LaunchConfig(Parcel in) {
        this.whiteBoardAppId = in.readString();
        this.eyeProtect = in.readInt();
    }

    public static final Creator<LaunchConfig> CREATOR = new Creator<LaunchConfig>() {
        @Override
        public LaunchConfig createFromParcel(Parcel source) {
            return new LaunchConfig(source);
        }

        @Override
        public LaunchConfig[] newArray(int size) {
            return new LaunchConfig[size];
        }
    };
}
