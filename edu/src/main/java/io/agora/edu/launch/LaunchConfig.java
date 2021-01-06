package io.agora.edu.launch;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author cjw
 */
public class LaunchConfig implements Parcelable {
    /**不参与序列化，只作为启动Activity时所用*/
    private Context context;
    private String userName;
    private String userUuid;
    private String roomName;
    private String roomUuid;
    private int roomType;
    private String appId;
    private String customerId;
    private String customerCer;
    private String whiteBoardAppId;

    public LaunchConfig(Context context, String userName, String userUuid, String roomName,
                        String roomUuid, int roomType, String appId, String customerId,
                        String customerCer, String whiteBoardAppId) {
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
        dest.writeInt(this.roomType);
        dest.writeString(this.appId);
        dest.writeString(this.customerId);
        dest.writeString(this.customerCer);
        dest.writeString(this.whiteBoardAppId);
    }

    protected LaunchConfig(Parcel in) {
        this.userName = in.readString();
        this.userUuid = in.readString();
        this.roomName = in.readString();
        this.roomUuid = in.readString();
        this.roomType = in.readInt();
        this.appId = in.readString();
        this.customerId = in.readString();
        this.customerCer = in.readString();
        this.whiteBoardAppId = in.readString();
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
