package io.agora.education.api.message

import io.agora.education.api.room.data.EduRoomInfo
import io.agora.education.api.user.data.EduBaseUserInfo

enum class EduActionType(val value: Int) {
    EduActionTypeApply(1),
    EduActionTypeInvitation(2),
    EduActionTypeAccept(3),
    EduActionTypeReject(4),
    EduActionTypeCancel(5)
}

class EduActionMessage(
        val processUuid: String,
        val action: EduActionType,
        val timeout: Long,
        val fromUser: EduBaseUserInfo,
        val fromRoom: EduRoomInfo,
        var payload: Map<String, Any>?) {
}