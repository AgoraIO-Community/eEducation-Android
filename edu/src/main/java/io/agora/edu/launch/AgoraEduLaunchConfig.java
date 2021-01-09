package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import io.agora.education.api.user.data.EduUserRole;

/**
 * @author cjw
 */
public class AgoraEduLaunchConfig implements Parcelable {
    /**
     * 不参与序列化，只作为启动Activity时所用
     */
    @NotNull
    public Context context;
    @NotNull
    private String userName;
    @NotNull
    private String userUuid;
    @NotNull
    private String roomName;
    @NotNull
    private String roomUuid;
    private int roleType = EduUserRole.STUDENT.getValue();
    private int roomType;
    @NotNull
    private String token;

    private String appId;
    private int openEyeCare;
    private String whiteBoardAppId;
    private String customerId;
    private String customerCer;

    public AgoraEduLaunchConfig(@NotNull Context context, @NotNull String userName, @NotNull String userUuid,
                                @NotNull String roomName, @NotNull String roomUuid, int roleType,
                                int roomType, @NotNull String token) {
        this.context = context;
        this.userName = userName;
        this.userUuid = userUuid;
        this.roomName = roomName;
        this.roomUuid = roomUuid;
        this.roleType = roleType;
        this.roomType = roomType;
        this.token = token;
    }

    public AgoraEduLaunchConfig(@NotNull Context context, @NotNull String userName, @NotNull String userUuid,
                                @NotNull String roomName, @NotNull String roomUuid, int roomType,
                                @NotNull String token) {
        this.context = context;
        this.userName = userName;
        this.userUuid = userUuid;
        this.roomName = roomName;
        this.roomUuid = roomUuid;
        this.roomType = roomType;
        this.token = token;
    }

    @NotNull
    public Context getContext() {
        return context;
    }

    public void setContext(@NotNull Context context) {
        this.context = context;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getOpenEyeCare() {
        return openEyeCare;
    }

    public void setOpenEyeCare(int openEyeCare) {
        this.openEyeCare = openEyeCare;
    }

    public String getWhiteBoardAppId() {
        return whiteBoardAppId;
    }

    public void setWhiteBoardAppId(String whiteBoardAppId) {
        this.whiteBoardAppId = whiteBoardAppId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCer() {
        return customerCer;
    }

    public void setCustomerCer(String customerCer) {
        this.customerCer = customerCer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userUuid);
        dest.writeString(this.roomName);
        dest.writeString(this.roomUuid);
        dest.writeInt(this.roleType);
        dest.writeInt(this.roomType);
        dest.writeString(this.token);
        dest.writeString(this.appId);
        dest.writeInt(this.openEyeCare);
        dest.writeString(this.whiteBoardAppId);
        dest.writeString(this.customerId);
        dest.writeString(this.customerCer);
    }

    protected AgoraEduLaunchConfig(Parcel in) {
        this.userName = in.readString();
        this.userUuid = in.readString();
        this.roomName = in.readString();
        this.roomUuid = in.readString();
        this.roleType = in.readInt();
        this.roomType = in.readInt();
        this.token = in.readString();
        this.appId = in.readString();
        this.openEyeCare = in.readInt();
        this.whiteBoardAppId = in.readString();
        this.customerId = in.readString();
        this.customerCer = in.readString();
    }

    public static final Creator<AgoraEduLaunchConfig> CREATOR = new Creator<AgoraEduLaunchConfig>() {
        @Override
        public AgoraEduLaunchConfig createFromParcel(Parcel source) {
            return new AgoraEduLaunchConfig(source);
        }

        @Override
        public AgoraEduLaunchConfig[] newArray(int size) {
            return new AgoraEduLaunchConfig[size];
        }
    };
}
