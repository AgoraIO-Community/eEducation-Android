package io.agora.raisehand

import io.agora.base.bean.MapBean

class AgoraCoVideoAction(
        val action: Int,
        val fromUser: AgoraCoVideoFromUser,
        val fromRoom: AgoraCoVideoFromRoom) : MapBean() {
}

class AgoraCoVideoFromUser(
        val uuid: String,
        val name: String,
        val role: String
)

class AgoraCoVideoFromRoom(
        val uuid: String,
        val name: String
)