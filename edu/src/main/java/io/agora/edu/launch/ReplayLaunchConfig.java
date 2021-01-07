package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class ReplayLaunchConfig implements Parcelable {
    @NotNull
    private Context context;
    @NotNull
    private String roomUuid;
    @NotNull
    private String whiteBoardAppId;
    private long startTime;
    private long endTime;
    @NotNull
    private String whiteBoardUrl;
    @NotNull
    private String whiteBoardId;
    @NotNull
    private String whiteBoardToken;

    public ReplayLaunchConfig(@NotNull Context context, @NotNull String roomUuid,
                              @NotNull String whiteBoardAppId, long startTime,
                              long endTime, @NotNull String whiteBoardUrl,
                              @NotNull String whiteBoardId, @NotNull String whiteBoardToken) {
        this.context = context;
        this.roomUuid = roomUuid;
        this.whiteBoardAppId = whiteBoardAppId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.whiteBoardUrl = whiteBoardUrl;
        this.whiteBoardId = whiteBoardId;
        this.whiteBoardToken = whiteBoardToken;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public String getWhiteBoardAppId() {
        return whiteBoardAppId;
    }

    public void setWhiteBoardAppId(String whiteBoardAppId) {
        this.whiteBoardAppId = whiteBoardAppId;
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

    public static Creator<ReplayLaunchConfig> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomUuid);
        dest.writeString(this.whiteBoardAppId);
        dest.writeLong(this.startTime);
        dest.writeLong(this.endTime);
        dest.writeString(this.whiteBoardUrl);
        dest.writeString(this.whiteBoardId);
        dest.writeString(this.whiteBoardToken);
    }

    protected ReplayLaunchConfig(Parcel in) {
        this.roomUuid = in.readString();
        this.whiteBoardAppId = in.readString();
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
