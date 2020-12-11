package io.agora.education.impl.room.data.request

import io.agora.education.api.message.GroupMemberInfoMessage

class EduUpsertRoomPropertyReq(
        val properties: MutableMap<String, GroupMemberInfoMessage>,
        val cause: MutableMap<String, String>
) {
}

class EduRemoveRoomPropertyReq(
        val properties: MutableList<String>,
        val cause: MutableMap<String, String>
) {
}