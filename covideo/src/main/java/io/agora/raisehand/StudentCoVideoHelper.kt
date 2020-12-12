package io.agora.raisehand

import android.content.Context
import com.google.gson.Gson
import io.agora.education.api.EduCallback
import io.agora.education.api.base.EduError
import io.agora.education.api.base.EduError.Companion.customMsgError
import io.agora.education.api.base.EduError.Companion.internalError
import io.agora.education.api.message.AgoraActionType
import io.agora.education.api.room.EduRoom
import io.agora.education.api.user.EduUser
import io.agora.education.api.user.data.EduActionConfig
import io.agora.education.api.user.data.EduUserInfo
import io.agora.education.api.user.data.EduUserRole
import io.agora.raisehand.AgoraActionConfig.Companion.PROCESSES
import io.agora.raisehand.CoVideoState.Applying
import io.agora.raisehand.CoVideoState.CoVideoing
import io.agora.raisehand.CoVideoState.DisCoVideo

internal class StudentCoVideoHelper(
        context: Context,
        eduRoom: EduRoom,
        processUuid: String) :
        StudentCoVideoSession(context, eduRoom, processUuid) {

    init {
        val properties = eduRoom.roomProperties
        /*提取并同步当前的举手开关状态 和 举手即上台开关状态*/
        syncCoVideoSwitchState(properties)
    }

    fun getLocalUser(callback: EduCallback<EduUser>) {
        eduRoom?.let {
            eduRoom.get()?.getLocalUser(callback)
        }
        callback.onFailure(internalError("current eduRoom is null"))
    }

    /**同步当前的举手开关状态 和 举手即上台开关状态*/
    override fun syncCoVideoSwitchState(properties: MutableMap<String, Any>?) {
        properties?.let {
            for ((key, value) in properties) {
                if (key == HANDUPSTATES) {
                    val json = value.toString()
                    val coVideoSwitchStateInfo = Gson().fromJson(json, CoVideoSwitchStateInfo::class.java)
                    enableCoVideo = coVideoSwitchStateInfo.state == CoVideoSwitchState.ENABLE
                    autoCoVideo = coVideoSwitchStateInfo.autoCoVideo == CoVideoApplySwitchState.ENABLE
                }
            }
        }
    }

    /**检查是否老师是否在线，老师不在线无法举手*/
    override fun isAllowCoVideo(callback: EduCallback<Unit>) {
        eduRoom?.let {
            eduRoom.get()?.getFullUserList(object : EduCallback<MutableList<EduUserInfo>> {
                override fun onSuccess(res: MutableList<EduUserInfo>?) {
                    res?.forEach {
                        if (it.role == EduUserRole.TEACHER) {
                            callback.onSuccess(Unit)
                            return
                        }
                    }
                    callback.onFailure(customMsgError(context.get()?.getString(R.string.there_is_no_teacher_disable_covideo)))
                }

                override fun onFailure(error: EduError) {
                    callback.onFailure(error)
                }
            })
        }
    }

    override fun applyCoVideo(callback: EduCallback<Unit>) {
        if (curCoVideoState != DisCoVideo) {
            callback.onFailure(customMsgError("can not apply,because current CoVideoState is not DisCoVideo!"))
            return
        }
        eduRoom.get()?.getFullUserList(object : EduCallback<MutableList<EduUserInfo>> {
            override fun onSuccess(res: MutableList<EduUserInfo>?) {
                if (res != null) {
                    res.forEach {
                        if (it.role == EduUserRole.TEACHER) {
                            val payload = mutableMapOf<String, Any>(Pair(BusinessType.BUSINESS,
                                    BusinessType.RAISEHAND))
                            val config = EduActionConfig(processUuid, AgoraActionType.AgoraActionTypeApply,
                                    it.userUuid, "", 4, payload)
                            getLocalUser(object : EduCallback<EduUser> {
                                override fun onSuccess(res: EduUser?) {
                                    if (res != null) {
                                        res.startActionWithConfig(config, object : EduCallback<Unit> {
                                            override fun onSuccess(res: Unit?) {
                                                curCoVideoState = Applying
                                                callback.onSuccess(Unit)
                                            }

                                            override fun onFailure(error: EduError) {
                                                callback.onFailure(error)
                                            }
                                        })
                                    } else {
                                        callback.onFailure(customMsgError("local user is null!"))
                                    }
                                }

                                override fun onFailure(error: EduError) {
                                    callback.onFailure(error)
                                }
                            })
                            return
                        }
                    }
                    callback.onFailure(customMsgError(context.get()?.getString(R.string.there_is_no_teacher_disable_covideo)))
                } else {
                    callback.onFailure(customMsgError("current room no user!"))
                }
            }

            override fun onFailure(error: EduError) {
                callback.onFailure(error)
            }
        })
    }

    override fun cancelCoVideo(callback: EduCallback<Unit>) {
//        if (curCoVideoState != Applying) {
//            callback.onFailure(customMsgError("can not cancel,because current CoVideoState is not Applying!"))
//            return
//        }
        eduRoom.get()?.getFullUserList(object : EduCallback<MutableList<EduUserInfo>> {
            override fun onSuccess(res: MutableList<EduUserInfo>?) {
                if (res != null) {
                    res.forEach {
                        if (it.role == EduUserRole.TEACHER) {
                            val payload = mutableMapOf<String, Any>(Pair(BusinessType.BUSINESS,
                                    BusinessType.RAISEHAND))
                            val config = EduActionConfig(processUuid, AgoraActionType.AgoraActionTypeCancel,
                                    it.userUuid, null, 4, payload)
                            getLocalUser(object : EduCallback<EduUser> {
                                override fun onSuccess(res: EduUser?) {
                                    if (res != null) {
                                        res.stopActionWithConfig(config, object : EduCallback<Unit> {
                                            override fun onSuccess(res: Unit?) {
                                                curCoVideoState = DisCoVideo
                                                callback.onSuccess(Unit)
//                                                refreshProcessUuid()
                                            }

                                            override fun onFailure(error: EduError) {
                                                callback.onFailure(error)
//                                                refreshProcessUuid()
                                            }
                                        })
                                    } else {
                                        callback.onFailure(customMsgError("local user is null!"))
                                    }
                                }

                                override fun onFailure(error: EduError) {
                                    callback.onFailure(error)
                                }
                            })
                        }
                    }
                    callback.onFailure(customMsgError(context.get()?.getString(R.string.there_is_no_teacher_disable_covideo)))
                } else {
                    callback.onFailure(customMsgError("current room no user!"))
                }
            }

            override fun onFailure(error: EduError) {
                callback.onFailure(error)
            }
        })
//        else if (curCoVideoState == CoVideoing) {
//            /*举手连麦中学生主动退出*/
//            val payload = mutableMapOf<String, Any>(Pair(BusinessType.BUSINESS,
//                    BusinessType.RAISEHAND))
//            val config = EduStopActionConfig(processUuid, EduActionType.EduActionTypeCancel, payload)
//            getLocalUser(object : EduCallback<EduUser> {
//                override fun onSuccess(res: EduUser?) {
//                    if (res != null) {
//                        res.stopActionWithConfig(config, object : EduCallback<Unit> {
//                            override fun onSuccess(res: Unit?) {
//                                curCoVideoState = DisCoVideo
//                                callback.onSuccess(Unit)
//                                refreshProcessUuid()
//                            }
//
//                            override fun onFailure(error: EduError) {
//                                callback.onFailure(error)
//                                refreshProcessUuid()
//                            }
//                        })
//                    } else {
//                        callback.onFailure(customMsgError("local user is null!"))
//                    }
//                }
//
//                override fun onFailure(error: EduError) {
//                    callback.onFailure(error)
//                }
//            })
//        }
    }

    override fun onLinkMediaChanged(onStage: Boolean) {
        curCoVideoState = if (onStage) CoVideoing else DisCoVideo
        //TODO 重置UI
    }

    override fun abortCoVideoing(): Boolean {
        if (isCoVideoing()) {
            curCoVideoState = DisCoVideo
            return true
        }
        return false
    }
}