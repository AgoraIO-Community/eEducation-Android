package io.agora.agoraactionprocess

import com.google.gson.Gson

class AgoraActionMsgRes(
        val action: Int = AgoraActionType.AgoraActionTypeApply.value,
        val processUuid: String,
        val fromUserUuid: String,
        val payload: Map<String, Any>?
) {

    fun getPayloadJson(): String {
        return Gson().toJson(payload ?: "")
    }
}