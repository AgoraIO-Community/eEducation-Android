package io.agora.education.classroom.bean.record;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import io.agora.education.api.message.EduFromUserInfo;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.classroom.bean.msg.ChannelMsg;

public class RecordMsg extends ChannelMsg.ChatMsg {
    private String roomUuid;

    public RecordMsg(@NonNull String roomUuid, @NotNull EduFromUserInfo fromUser, @NotNull String message,
                     long timestamp , int type) {
        super(fromUser, message, timestamp, type);
        this.roomUuid = roomUuid;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid;
    }
}
