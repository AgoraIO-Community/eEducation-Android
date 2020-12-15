package io.agora.agoraactionprocess

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
        val fromUserUuid: String?,
        val payload: Map<String, Any>?
) {
}

class AgoraStopActionOptions(
        val toUserUuid: String,
        val processUuid: String,
        val action: Int = AgoraActionType.AgoraActionTypeAccept.value,
        val fromUserUuid: String?,
        var payload: Map<String, Any>?) {
}

