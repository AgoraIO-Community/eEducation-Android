package io.agora.raisehand

import io.agora.education.api.base.EduError

interface AgoraEduCoVideoListener {

    /**申请连麦的请求成功发送完成*/
    fun onApplyCoVideoComplete()

    /**申请连麦申请连麦的请求发送失败*/
    fun onApplyCoVideoFailed(error: EduError)

    /**取消连麦成功(老师处理前取消和老师同意连麦后主动退出)*/
    fun onCancelCoVideoSuccess()

    /**取消连麦失败*/
    fun onCancelCoVideoFailed(error: EduError)

    /**连麦被老师强制终止*/
    fun onCoVideoAborted()

    /**连麦申请被接受*/
    fun onCoVideoAccepted()

    /**连麦申请被拒绝*/
    fun onCoVideoRejected()

}