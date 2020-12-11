package io.agora.education.api.message


class GroupMemberInfoMessage(
        var uuid: String,
        val userName: String,
        val avatar: String,
        var reward: Int
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