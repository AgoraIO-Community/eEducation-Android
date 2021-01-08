package io.agora.edu.service.bean.response

import io.agora.education.api.room.data.EduRoomInfo

internal class EduRoomInfoRes(
        roomUuid: String,
        roomName: String,
        val createTime: Long
): EduRoomInfo(roomUuid, roomName) {
}