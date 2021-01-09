package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class AgoraEduReplayConfig implements Parcelable {
    @NotNull
    private Context context;
    private long beginTime;
    private long endTime;
    @NotNull
    private String videoUrl;
    @NotNull
    private String whiteBoardAppId;
    @NotNull
    private String whiteBoardId;
    @NotNull
    private String whiteBoardToken;
    @NotNull
    private String token;

    public AgoraEduReplayConfig(@NotNull Context context, long beginTime, long endTime,
                                @NotNull String videoUrl, @NotNull String whiteBoardAppId,
                                @NotNull String whiteBoardId, @NotNull String whiteBoardToken) {
        this.context = context;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.videoUrl = videoUrl;
        this.whiteBoardAppId = whiteBoardAppId;
        this.whiteBoardId = whiteBoardId;
        this.whiteBoardToken = whiteBoardToken;
    }

    @NotNull
    public Context getContext() {
        return context;
    }

    public void setContext(@NotNull Context context) {
        this.context = context;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @NotNull
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(@NotNull String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @NotNull
    public String getWhiteBoardAppId() {
        return whiteBoardAppId;
    }

    public void setWhiteBoardAppId(@NotNull String whiteBoardAppId) {
        this.whiteBoardAppId = whiteBoardAppId;
    }

    @NotNull
    public String getWhiteBoardId() {
        return whiteBoardId;
    }

    public void setWhiteBoardId(@NotNull String whiteBoardId) {
        this.whiteBoardId = whiteBoardId;
    }

    @NotNull
    public String getWhiteBoardToken() {
        return whiteBoardToken;
    }

    public void setWhiteBoardToken(@NotNull String whiteBoardToken) {
        this.whiteBoardToken = whiteBoardToken;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    public void setToken(@NotNull String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.beginTime);
        dest.writeLong(this.endTime);
        dest.writeString(this.videoUrl);
        dest.writeString(this.whiteBoardAppId);
        dest.writeString(this.whiteBoardId);
        dest.writeString(this.whiteBoardToken);
        dest.writeString(this.token);
    }

    protected AgoraEduReplayConfig(Parcel in) {
        this.beginTime = in.readLong();
        this.endTime = in.readLong();
        this.videoUrl = in.readString();
        this.whiteBoardAppId = in.readString();
        this.whiteBoardId = in.readString();
        this.whiteBoardToken = in.readString();
        this.token = in.readString();
    }

    public static final Creator<AgoraEduReplayConfig> CREATOR = new Creator<AgoraEduReplayConfig>() {
        @Override
        public AgoraEduReplayConfig createFromParcel(Parcel source) {
            return new AgoraEduReplayConfig(source);
        }

        @Override
        public AgoraEduReplayConfig[] newArray(int size) {
            return new AgoraEduReplayConfig[size];
        }
    };
}
