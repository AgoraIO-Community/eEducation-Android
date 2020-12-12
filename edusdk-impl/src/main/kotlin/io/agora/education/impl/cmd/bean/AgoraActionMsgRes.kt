package io.agora.education.impl.cmd.bean

class AgoraActionMsgRes(
        val processUuid: String,
        val fromUserUuid: String,
        val payload: Map<String, Any>?
) {
}