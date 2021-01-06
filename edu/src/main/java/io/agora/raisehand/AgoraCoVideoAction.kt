package io.agora.raisehand

import io.agora.base.bean.MapBean

class AgoraCoVideoAction(
        val action: Int,
        val fromRoom: AgoraCoVideoFromRoom) : MapBean() {
}

class AgoraCoVideoFromRoom(
        val uuid: String,
        val name: String
)