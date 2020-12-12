package io.agora.raisehand

class AgoraActionConfig(
        val maxWait: Int,
        val maxAccept: Int,
        val timeout: Int
) {
    lateinit var processUuid: String

    companion object {
        const val PROCESSES = "processes"
    }
}