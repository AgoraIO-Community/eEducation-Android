package io.agora.agoraactionprocess

import com.google.gson.Gson

enum class AgoraActionType(val value: Int) {
    AgoraActionTypeApply(1),
    AgoraActionTypeInvitation(2),
    AgoraActionTypeAccept(3),
    AgoraActionTypeReject(4),
    AgoraActionTypeCancel(5)
}

class AgoraActionProcessConfig(
        val appId: String,
        val roomUuid: String,
        val userToken: String,
        val customerId: String,
        val customerCertificate: String,
        val baseUrl: String
)

/**config */
class AgoraActionConfigInfo(
        val maxWait: Int,
        val maxAccept: Int,
        /*超时时间(秒)*/
        val timeout: Int
) {
    lateinit var processUuid: String

    companion object {
        const val PROCESSES = "processes"
    }
}

/**for set action*/
class AgoraActionOptions(
        val actionType: AgoraActionType = AgoraActionType.AgoraActionTypeApply,
        val maxWait: Int = 4,
        /*超时时间(秒)*/
        val timeout: Int = 10,
        val processUuid: String
)


class AgoraStartActionOptions(
        val toUserUuid: String,
        val processUuid: String,
        val body: AgoraStartActionMsgReq
) {
}

class AgoraStartActionMsgReq(
        val fromUserUuid: String?,
        val payload: Map<String, Any>?
)

class AgoraStopActionOptions(
        val toUserUuid: String,
        val processUuid: String,
        val body: AgoraStopActionMsgReq) {
}

class AgoraStopActionMsgReq(
        val action: Int = AgoraActionType.AgoraActionTypeAccept.value,
        val fromUserUuid: String?,
        var payload: Map<String, Any>?,
        var waitAck: Int = AgoraActionWaitACK.DISABLE.value
)

enum class AgoraActionWaitACK(val value: Int) {
    DISABLE(0),
    ENABLE(1)
}

class AgoraActionMsgRes(
        val action: Int = AgoraActionType.AgoraActionTypeApply.value,
        val processUuid: String,
        val fromUser: AgoraActionFromUser,
        private val payload: Map<String, Any>?
) {

    fun getPayloadJson(): String {
        return Gson().toJson(payload ?: "")
    }
}

class AgoraActionFromUser(
        val uuid: String,
        val name: String,
        val role: String
)