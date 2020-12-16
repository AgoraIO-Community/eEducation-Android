package io.agora.education.classroom.bean.group

import android.text.TextUtils
import io.agora.education.api.stream.data.EduStreamEvent
import io.agora.education.api.stream.data.EduStreamInfo
import io.agora.education.api.user.data.EduUserInfo

class RoomGroupInfo() {
    companion object {
        const val GROUPSTATES = "groupsStates"
        const val INTERACTOUTGROUPS = "interactOutGroups"
        val INTERACTOUTGROUPKEYS = arrayOf("g1", "g2")
        const val GROUPS = "groups"
        const val STUDENTS = "students"
        const val GROUPUUID = "groupUuid"
        const val USERUUID = "userUuid"
    }

    /*分组状态*/
    var groupStates: GroupStateInfo? = null

    /*参与组外互动的小组id集合*/
    var interactOutGroups: MutableList<String>? = null

    /*分组后的各小组信息集合*/
    var groups: MutableList<GroupInfo>? = null

    /*班级的全体学生名单(包含在线和不在线)*/
    var allStudent: MutableList<GroupMemberInfo>? = null

    fun updateInteractOutGroups(data: MutableMap<String, String>?) {
        if (data != null) {
            interactOutGroups = mutableListOf()
            data[INTERACTOUTGROUPKEYS[0]]?.let {
                interactOutGroups!!.add(it)
            }
            data[INTERACTOUTGROUPKEYS[1]]?.let {
                interactOutGroups!!.add(it)
            }
        } else {
            interactOutGroups = null
        }
    }

    fun updateGroups(data: MutableMap<String, GroupInfo>?) {
        if (data != null) {
            groups = mutableListOf()
            val iterable = data.entries.iterator()
            while (iterable.hasNext()) {
                val element = iterable.next()
                val groupInfo = element.value
                groupInfo.groupUuid = element.key
                groups!!.add(groupInfo)
            }
        } else {
            groups = null
        }
    }

    fun updateAllStudent(data: MutableMap<String, GroupMemberInfo>?,
                         onlineUsers: MutableList<EduUserInfo>,
                         streams: MutableList<EduStreamInfo>) {
        val onlineUserIds = mutableListOf<String>()
        onlineUsers.forEach {
            onlineUserIds.add(it.userUuid)
        }
        val onStageUserIds = mutableListOf<String>()
        streams?.forEach {
            onStageUserIds.add(it.publisher.userUuid)
        }
        data?.let {
            allStudent = mutableListOf()
            val iterable = it.entries.iterator()
            while (iterable.hasNext()) {
                val element = iterable.next()
                val memberInfo = element.value
                memberInfo.uuid = element.key
                if (onStageUserIds.contains(memberInfo.uuid)) {
                    /*在台上的用户肯定在线*/
                    memberInfo.onStage()
                    memberInfo.online()
                    memberInfo.enableVideo = streams[onStageUserIds.indexOf(memberInfo.uuid)].hasVideo
                    memberInfo.enableAudio = streams[onStageUserIds.indexOf(memberInfo.uuid)].hasAudio
                } else {
                    /*单独判断当前用户是否在线*/
                    memberInfo.online = onlineUserIds.contains(memberInfo.uuid)
                }
                memberInfo.reward = getGroupReward(memberInfo.uuid)
                allStudent!!.add(memberInfo)
            }
        }
    }

    fun updateRewardByGroup(groupUuid: String) {
        groups?.forEach {
            if (it.groupUuid == groupUuid) {
                val reward = it.reward.toInt() + 1
                it.reward = reward.toString()
            }
        }
    }

    fun updateRewardByUser(userUuid: String) {
        allStudent?.forEach {
            if (it.uuid == userUuid) {
                it.reward = it.reward + 1
            }
        }
    }

    private fun getGroupReward(userUuid: String): Int {
        var reward = 0
        groups?.forEach {
            if (it.members.contains(userUuid)) {
                reward = if (TextUtils.isEmpty(it.reward)) {
                    0
                } else {
                    it.reward.toInt()
                }
            }
        }
        return reward
    }

    /**根据用户的uuid获取用户的奖励*/
    fun getStudentReward(uuid: String): Int {
        var reward = 0
        allStudent?.forEach {
            if (it.uuid == uuid) {
                reward += it.reward
            }
        }
        return reward
    }

    /**是否开启分组*/
    fun isEnableGroup(): Boolean {
        groupStates?.let {
            return it.state == GroupState.ENABLE.value
        }
        return false
    }

    /**是否开启PK*/
    fun isEnablePK(): Boolean {
        groupStates?.let {
            return isEnableGroup() && it.interactOutGroup == InteractState.ENABLE.value
        }
        return false
    }

    /**
     * 用户上台
     */
    fun membersOnStage(streamEvents: List<EduStreamEvent>) {
        allStudent?.forEach {
            for ((streamInfo) in streamEvents) {
                if (it.uuid == streamInfo.publisher.userUuid) {
                    it.onStage()
                }
            }
        }
    }

    /**
     * 用户下台
     */
    fun membersOffStage(streamEvents: List<EduStreamEvent>) {
        allStudent?.forEach {
            for ((streamInfo) in streamEvents) {
                if (it.uuid == streamInfo.publisher.userUuid) {
                    it.offStage()
                }
            }
        }
    }

    fun isOnStage(userUuid: String): Boolean {
        allStudent?.forEach {
            if (it.uuid == userUuid) {
                return it.onStage
            }
        }
        return false
    }
}