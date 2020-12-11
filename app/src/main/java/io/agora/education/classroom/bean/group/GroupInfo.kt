package io.agora.education.classroom.bean.group


class GroupInfo(
        var groupUuid: String,
        val groupName: String,
        /*组员id集合*/
        val members: MutableList<String>,
        var reward: String,
        /*小组内的自定义属性*/
        val groupProperties: MutableMap<String, Any>?
) {
}