package io.agora.edu.record.bean;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import io.agora.edu.classroom.bean.msg.ChannelMsg;
import io.agora.education.api.message.EduFromUserInfo;

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
