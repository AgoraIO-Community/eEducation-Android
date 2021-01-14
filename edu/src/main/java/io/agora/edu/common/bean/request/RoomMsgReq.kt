package io.agora.edu.common.bean.request

/**发送channelMsg*/
internal open class RoomMsgReq(val message: String)

internal class RoomChatMsgReq(
        message: String,
        val type: Int) : RoomMsgReq(message)