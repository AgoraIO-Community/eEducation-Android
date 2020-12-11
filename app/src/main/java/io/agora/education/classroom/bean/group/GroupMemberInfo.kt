package io.agora.education.classroom.bean.group


class GroupMemberInfo(
        var uuid: String,
        val userName: String,
        val avatar: String,
        var reward: Int,
        var enableAudio: Boolean,
        var enableVideo: Boolean,
        var streamUuid: String?,
        var streamName: String?
) {

    var online: Boolean = false
    var onStage: Boolean = false

    /**用户上台*/
    fun online() {
        online = true
    }

    fun offLine() {
        online = false
    }

    fun onStage() {
        onStage = true
    }

    fun offStage() {
        onStage = false
    }

}