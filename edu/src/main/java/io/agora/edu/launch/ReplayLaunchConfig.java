package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class ReplayLaunchConfig extends LaunchConfig implements Parcelable {
    private long startTime;
    private long endTime;
    @NotNull
    private String whiteBoardUrl;
    @NotNull
    private String whiteBoardId;
    @NotNull
    private String whiteBoardToken;

    public ReplayLaunchConfig(@NotNull Context context, @NotNull String whiteBoardAppId,
                              int eyeProtect, long startTime, long endTime,
                              @NotNull String whiteBoardUrl, @NotNull String whiteBoardId,
                              @NotNull String whiteBoardToken) {
        super(context, whiteBoardAppId, eyeProtect);
        this.startTime = startTime;
        this.endTime = endTime;
        this.whiteBoardUrl = whiteBoardUrl;
        this.whiteBoardId = whiteBoardId;
        this.whiteBoardToken = whiteBoardToken;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getWhiteBoardUrl() {
        return whiteBoardUrl;
    }

    public void setWhiteBoardUrl(String whiteBoardUrl) {
        this.whiteBoardUrl = whiteBoardUrl;
    }

    public String getWhiteBoardId() {
        return whiteBoardId;
    }

    public void setWhiteBoardId(String whiteBoardId) {
        this.whiteBoardId = whiteBoardId;
    }

    public String getWhiteBoardToken() {
        return whiteBoardToken;
    }

    public void setWhiteBoardToken(String whiteBoardToken) {
        this.whiteBoardToken = whiteBoardToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.startTime);
        dest.writeLong(this.endTime);
        dest.writeString(this.whiteBoardUrl);
        dest.writeString(this.whiteBoardId);
        dest.writeString(this.whiteBoardToken);
    }

    protected ReplayLaunchConfig(Parcel in) {
        super(in);
        this.startTime = in.readLong();
        this.endTime = in.readLong();
        this.whiteBoardUrl = in.readString();
        this.whiteBoardId = in.readString();
        this.whiteBoardToken = in.readString();
    }

    public static final Creator<ReplayLaunchConfig> CREATOR = new Creator<ReplayLaunchConfig>() {
        @Override
        public ReplayLaunchConfig createFromParcel(Parcel source) {
            return new ReplayLaunchConfig(source);
        }

        @Override
        public ReplayLaunchConfig[] newArray(int size) {
            return new ReplayLaunchConfig[size];
        }
    };
}
