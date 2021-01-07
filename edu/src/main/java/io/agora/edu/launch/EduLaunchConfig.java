package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import io.agora.education.api.user.data.EduUserRole;

/**
 * @author cjw
 */
public class EduLaunchConfig implements Parcelable {
    /**
     * 不参与序列化，只作为启动Activity时所用
     */
    @NotNull
    private Context context;
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
    private String appId;
    @NotNull
    private String customerId;
    @NotNull
    private String customerCer;
    @NotNull
    private String whiteBoardAppId;
    @NotNull
    private String token;

    public EduLaunchConfig(@NotNull Context context, @NotNull String userName, @NotNull String userUuid,
                           @NotNull String roomName, @NotNull String roomUuid, int roomType,
                           @NotNull String appId, @NotNull String customerId, @NotNull String customerCer,
                           @NotNull String whiteBoardAppId, @NotNull String token) {
        this.context = context;
        this.userName = userName;
        this.userUuid = userUuid;
        this.roomName = roomName;
        this.roomUuid = roomUuid;
        this.roomType = roomType;
        this.appId = appId;
        this.customerId = customerId;
        this.customerCer = customerCer;
        this.whiteBoardAppId = whiteBoardAppId;
        this.token = token;
    }

    public EduLaunchConfig(@NotNull Context context, @NotNull String userName, @NotNull String userUuid,
                           @NotNull String roomName, @NotNull String roomUuid, int roleType, int roomType,
                           @NotNull String appId, @NotNull String customerId, @NotNull String customerCer,
                           @NotNull String whiteBoardAppId, @NotNull String token) {
        this.context = context;
        this.userName = userName;
        this.userUuid = userUuid;
        this.roomName = roomName;
        this.roomUuid = roomUuid;
        this.roleType = roleType;
        this.roomType = roomType;
        this.appId = appId;
        this.customerId = customerId;
        this.customerCer = customerCer;
        this.whiteBoardAppId = whiteBoardAppId;
        this.token = token;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getWhiteBoardAppId() {
        return whiteBoardAppId;
    }

    public void setWhiteBoardAppId(String whiteBoardAppId) {
        this.whiteBoardAppId = whiteBoardAppId;
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

    public static Creator<EduLaunchConfig> getCREATOR() {
        return CREATOR;
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
        dest.writeString(this.appId);
        dest.writeString(this.customerId);
        dest.writeString(this.customerCer);
        dest.writeString(this.whiteBoardAppId);
        dest.writeString(this.token);
    }

    protected EduLaunchConfig(Parcel in) {
        this.userName = in.readString();
        this.userUuid = in.readString();
        this.roomName = in.readString();
        this.roomUuid = in.readString();
        this.roleType = in.readInt();
        this.roomType = in.readInt();
        this.appId = in.readString();
        this.customerId = in.readString();
        this.customerCer = in.readString();
        this.whiteBoardAppId = in.readString();
        this.token = in.readString();
    }

    public static final Creator<EduLaunchConfig> CREATOR = new Creator<EduLaunchConfig>() {
        @Override
        public EduLaunchConfig createFromParcel(Parcel source) {
            return new EduLaunchConfig(source);
        }

        @Override
        public EduLaunchConfig[] newArray(int size) {
            return new EduLaunchConfig[size];
        }
    };
}
