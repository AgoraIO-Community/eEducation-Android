package io.agora.education.api.manager.listener

import io.agora.education.api.message.AgoraActionMessage
import io.agora.education.api.message.EduChatMsg
import io.agora.education.api.message.EduMsg

interface EduManagerEventListener {

    fun onUserMessageReceived(message: EduMsg)

    fun onUserChatMessageReceived(chatMsg: EduChatMsg)

    fun onUserActionMessageReceived(actionMessage: AgoraActionMessage)
}