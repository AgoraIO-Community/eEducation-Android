package io.agora.education.classroom

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.herewhite.sdk.domain.GlobalState
import io.agora.education.R
import io.agora.education.api.EduCallback
import io.agora.education.api.base.EduError
import io.agora.education.api.message.EduChatMsg
import io.agora.education.api.message.EduMsg
import io.agora.education.api.room.EduRoom
import io.agora.education.api.room.data.EduRoomChangeType
import io.agora.education.api.room.data.EduRoomState
import io.agora.education.api.room.data.EduRoomStatus
import io.agora.education.api.statistics.ConnectionState
import io.agora.education.api.statistics.NetworkQuality
import io.agora.education.api.stream.data.EduStreamEvent
import io.agora.education.api.stream.data.EduStreamInfo
import io.agora.education.api.stream.data.VideoSourceType
import io.agora.education.api.user.EduTeacher
import io.agora.education.api.user.EduUser
import io.agora.education.api.user.data.*
import io.agora.education.classroom.adapter.ClassVideoAdapter
import io.agora.education.classroom.bean.board.BoardState
import io.agora.education.classroom.bean.channel.Room
import io.agora.education.classroom.fragment.UserListFragment
import java.util.*

class SmallClassActivity_Teacher : BaseClassActivity_bak(), OnTabSelectedListener {
    @JvmField
    @BindView(R.id.rcv_videos)
    var rcv_videos: RecyclerView? = null

    @JvmField
    @BindView(R.id.layout_im)
    var layout_im: View? = null

    @JvmField
    @BindView(R.id.layout_tab)
    var layout_tab: TabLayout? = null

    @JvmField
    @BindView(R.id.userUuid0)
    var userUuid0: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.userUuid1)
    var userUuid1: AppCompatEditText? = null

    private var classVideoAdapter: ClassVideoAdapter? = null
    private var userListFragment: UserListFragment? = null
    override fun getLayoutResId(): Int {
        return R.layout.activity_small_class_teacher
    }

    override fun initData() {
        super.initData()
        joinRoomAsTeacher(mainEduRoom, roomEntry.userName, roomEntry.userUuid, true, true, true,
                object : EduCallback<EduTeacher?> {
                    override fun onSuccess(res: EduTeacher?) {
                        runOnUiThread { showFragmentWithJoinSuccess() }
                        notifyStreamAndUser()
                        mainEduRoom.getLocalUser(object : EduCallback<EduUser> {
                            override fun onSuccess(user: EduUser?) {
                                userListFragment!!.setLocalUserUuid(user!!.userInfo.userUuid)
                            }

                            override fun onFailure(error: EduError) {}
                        })
                    }

                    override fun onFailure(error: EduError) {
                        joinFailed(error.type, error.msg)
                    }
                })
        classVideoAdapter = ClassVideoAdapter()
    }

    override fun initView() {
        super.initView()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcv_videos!!.layoutManager = layoutManager
        rcv_videos!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.left = resources.getDimensionPixelSize(R.dimen.dp_2_5)
                }
            }
        })
        rcv_videos!!.adapter = classVideoAdapter
        layout_tab!!.addOnTabSelectedListener(this)
        userListFragment = UserListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.layout_chat_room, userListFragment!!)
                .show(userListFragment!!)
                .commitNow()
        findViewById<View>(R.id.send1).setOnClickListener { v: View? -> }
    }

    override fun getClassType(): Int {
        return Room.Type.SMALL
    }

    var userUuid: String? = null

    @OnClick(R.id.iv_float, R.id.roomState, R.id.allMute, R.id.singleMute, R.id.singleUnMute,
            R.id.openAudio, R.id.closeAudio, R.id.openVideo, R.id.closeVideo, R.id.delStream)
    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_float -> {
                val isSelected = view.isSelected
                view.isSelected = !isSelected
                layout_im!!.visibility = if (isSelected) View.VISIBLE else View.GONE
            }
            R.id.roomState -> mainEduRoom.getRoomStatus(object : EduCallback<EduRoomStatus> {
                override fun onSuccess(status: EduRoomStatus?) {
                    var state = status!!.courseState
                    when {
                        state === EduRoomState.INIT -> {
                            state = EduRoomState.START
                        }
                        state === EduRoomState.START -> {
                            state = EduRoomState.END
                        }
                        state === EduRoomState.END -> {
                            state = EduRoomState.START
                        }
                    }
                    val finalState = state
                    getLocalUser(object : EduCallback<EduUser> {
                        override fun onSuccess(user: EduUser?) {
                            user?.let {
                                (user as EduTeacher).updateCourseState(finalState,
                                        object : EduCallback<Unit> {
                                    override fun onSuccess(res: Unit?) {}
                                    override fun onFailure(error: EduError) {}
                                })
                            }
                        }

                        override fun onFailure(error: EduError) {}
                    })
                }

                override fun onFailure(error: EduError) {}
            })
            R.id.allMute -> mainEduRoom.getRoomStatus(object : EduCallback<EduRoomStatus> {
                override fun onSuccess(status: EduRoomStatus?) {
                    val isStudentChatAllowed = !status!!.isStudentChatAllowed
                    getLocalUser(object : EduCallback<EduUser> {
                        override fun onSuccess(user: EduUser?) {
                            user?.let {
                                (user as EduTeacher).allowAllStudentChat(isStudentChatAllowed,
                                        object : EduCallback<Unit> {
                                    override fun onSuccess(res: Unit?) {}
                                    override fun onFailure(error: EduError) {}
                                })
                            }
                        }

                        override fun onFailure(error: EduError) {}
                    })
                }

                override fun onFailure(error: EduError) {}
            })
            R.id.singleMute -> {
                userUuid = userUuid0!!.text.toString()
                getCurFullUser(object : EduCallback<List<EduUserInfo>> {
                    override fun onSuccess(userList: List<EduUserInfo>?) {
                        userList?.forEach {
                            val userInfo = it
                            if (userInfo.userUuid == userUuid) {
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).allowStudentChat(false, userInfo,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            R.id.singleUnMute -> {
                userUuid = userUuid0!!.text.toString()
                getCurFullUser(object : EduCallback<List<EduUserInfo>> {
                    override fun onSuccess(userList: List<EduUserInfo>?) {
                        userList?.forEach {
                            val userInfo = it
                            if (userInfo.userUuid == userUuid) {
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).allowStudentChat(true, userInfo,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            R.id.openAudio -> {
                val streamInfos0: MutableList<EduStreamInfo> = ArrayList()
                userUuid = userUuid1!!.text.toString()
                getCurFullStream(object : EduCallback<List<EduStreamInfo>> {
                    override fun onSuccess(streams: List<EduStreamInfo>?) {
                        streams?.forEach {
                            val stream = it
                            if (stream.publisher.userUuid == userUuid) {
                                stream.hasAudio = true
                                streamInfos0.add(stream)
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).upsertStudentStreams(streamInfos0,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            R.id.closeAudio -> {
                val streamInfos1: MutableList<EduStreamInfo> = ArrayList()
                userUuid = userUuid1!!.text.toString()
                getCurFullStream(object : EduCallback<List<EduStreamInfo>> {
                    override fun onSuccess(streams: List<EduStreamInfo>?) {
                        streams?.forEach {
                            val stream = it
                            if (stream.publisher.userUuid == userUuid) {
                                stream.hasAudio = false
                                streamInfos1.add(stream)
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).upsertStudentStreams(streamInfos1,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            R.id.openVideo -> {
                val streamInfos2: MutableList<EduStreamInfo> = ArrayList()
                userUuid = userUuid1!!.text.toString()
                getCurFullStream(object : EduCallback<List<EduStreamInfo>> {
                    override fun onSuccess(streams: List<EduStreamInfo>?) {
                        streams?.forEach {
                            val stream = it
                            if (stream.publisher.userUuid == userUuid) {
                                stream.hasVideo = true
                                streamInfos2.add(stream)
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).upsertStudentStreams(streamInfos2,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            R.id.closeVideo -> {
                val streamInfos3: MutableList<EduStreamInfo> = ArrayList()
                userUuid = userUuid1!!.text.toString()
                getCurFullStream(object : EduCallback<List<EduStreamInfo>> {
                    override fun onSuccess(streams: List<EduStreamInfo>?) {
                        streams?.forEach {
                            val stream = it
                            if (stream.publisher.userUuid == userUuid) {
                                stream.hasVideo = false
                                streamInfos3.add(stream)
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).upsertStudentStreams(streamInfos3,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            R.id.delStream -> {
                val streamInfos4: MutableList<EduStreamInfo> = ArrayList()
                userUuid = userUuid1!!.text.toString()
                getCurFullStream(object : EduCallback<List<EduStreamInfo>> {
                    override fun onSuccess(streams: List<EduStreamInfo>?) {
                        streams?.forEach {
                            val stream = it
                            if (stream.publisher.userUuid == userUuid) {
                                streamInfos4.add(stream)
                                getLocalUser(object : EduCallback<EduUser?> {
                                    override fun onSuccess(user: EduUser?) {
                                        user?.let {
                                            (user as EduTeacher).deleteStudentStreams(streamInfos4,
                                                    object : EduCallback<Unit> {
                                                        override fun onSuccess(res: Unit?) {}
                                                        override fun onFailure(error: EduError) {}
                                                    })
                                        }
                                    }

                                    override fun onFailure(error: EduError) {}
                                })
                                return@forEach
                            }
                        }
                    }

                    override fun onFailure(error: EduError) {}
                })
            }
            else -> {
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tab.position == 0) {
            transaction.show(chatRoomFragment).hide(userListFragment!!)
        } else {
            transaction.show(userListFragment!!).hide(chatRoomFragment)
        }
        transaction.commitNow()
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabReselected(tab: TabLayout.Tab) {}

    /**
     * 刷新流和user列表
     * 流：屏幕分享流和摄像头流
     */
    private fun notifyStreamAndUser() {
        getCurFullStream(object : EduCallback<List<EduStreamInfo?>?> {
            override fun onSuccess(streams: List<EduStreamInfo?>?) {
                if (streams != null) {
                    for (streamInfo in streams) {
                        when (streamInfo!!.videoSourceType) {
                            VideoSourceType.SCREEN -> runOnUiThread {
                                layout_whiteboard.visibility = View.GONE
                                layout_share_video.visibility = View.VISIBLE
                                layout_share_video.removeAllViews()
                                renderStream(mainEduRoom, streamInfo, layout_share_video)
                            }
                            else -> {
                            }
                        }
                    }
                    userListFragment!!.setUserList(streams)
                    showVideoList(streams)
                }
            }

            override fun onFailure(error: EduError) {}
        })
    }

    override fun onRemoteUsersInitialized(users: List<EduUserInfo>, classRoom: EduRoom) {
        super.onRemoteUsersInitialized(users, classRoom)
    }

    override fun onRemoteUsersJoined(users: List<EduUserInfo>, classRoom: EduRoom) {
        super.onRemoteUsersJoined(users, classRoom)
    }

    override fun onRemoteUserLeft(userEvent: EduUserEvent, classRoom: EduRoom) {
        super.onRemoteUserLeft(userEvent, classRoom)
    }

    override fun onRemoteUserUpdated(userEvent: EduUserEvent, type: EduUserStateChangeType,
                                     classRoom: EduRoom) {
        super.onRemoteUserUpdated(userEvent, type, classRoom)
    }

    override fun onRoomMessageReceived(message: EduMsg, classRoom: EduRoom) {
        super.onRoomMessageReceived(message, classRoom)
    }

    override fun onUserMessageReceived(message: EduMsg) {
        super.onUserMessageReceived(message)
    }

    override fun onRoomChatMessageReceived(eduChatMsg: EduChatMsg, classRoom: EduRoom) {
        super.onRoomChatMessageReceived(eduChatMsg, classRoom)
    }

    override fun onUserChatMessageReceived(chatMsg: EduChatMsg) {
        super.onUserChatMessageReceived(chatMsg)
    }

    override fun onRemoteStreamsInitialized(streams: List<EduStreamInfo>, classRoom: EduRoom) {
        super.onRemoteStreamsInitialized(streams, classRoom)
        notifyStreamAndUser()
        classRoom.getLocalUser(object : EduCallback<EduUser> {
            override fun onSuccess(user: EduUser?) {
                userListFragment!!.setLocalUserUuid(user!!.userInfo.userUuid)
            }

            override fun onFailure(error: EduError) {}
        })
    }

    override fun onRemoteStreamsAdded(streamEvents: MutableList<EduStreamEvent>, classRoom: EduRoom) {
        super.onRemoteStreamsAdded(streamEvents, classRoom)
        var notify = false
        for ((streamInfo) in streamEvents) {
            when (streamInfo.videoSourceType) {
                VideoSourceType.CAMERA -> notify = true
                else -> {
                }
            }
        }
        if (notify) {
            Log.e(TAG, "有远端Camera流添加，刷新视频列表")
        }
        notifyVideoUserList(notify)
    }

    override fun onRemoteStreamUpdated(streamEvents: MutableList<EduStreamEvent>, classRoom: EduRoom) {
        super.onRemoteStreamUpdated(streamEvents, classRoom)
        var notify = false
        for ((streamInfo) in streamEvents) {
            when (streamInfo.videoSourceType) {
                VideoSourceType.CAMERA -> notify = true
                else -> {
                }
            }
        }
        if (notify) {
            Log.e(TAG, "有远端Camera流被修改，刷新视频列表")
        }
        notifyVideoUserList(notify)
    }

    override fun onRemoteStreamsRemoved(streamEvents: MutableList<EduStreamEvent>, classRoom: EduRoom) {
        super.onRemoteStreamsRemoved(streamEvents, classRoom)
        var notify = false
        for ((streamInfo) in streamEvents) {
            when (streamInfo.videoSourceType) {
                VideoSourceType.CAMERA -> notify = true
                else -> {
                }
            }
        }
        if (notify) {
            Log.e(TAG, "有远端Camera流被移除，刷新视频列表")
        }
        notifyVideoUserList(notify)
    }

    override fun onRoomStatusChanged(event: EduRoomChangeType, operatorUser: EduUserInfo, classRoom: EduRoom) {
        super.onRoomStatusChanged(event, operatorUser, classRoom)
    }

    override fun onRoomPropertiesChanged(classRoom: EduRoom, cause: MutableMap<String, Any>?) {
        super.onRoomPropertiesChanged(classRoom, cause)
    }

    override fun onNetworkQualityChanged(quality: NetworkQuality, user: EduUserInfo,
                                         classRoom: EduRoom) {
        super.onNetworkQualityChanged(quality, user, classRoom)
    }

    override fun onConnectionStateChanged(state: ConnectionState, classRoom: EduRoom) {
        super.onConnectionStateChanged(state, classRoom)
    }

    override fun onLocalUserUpdated(userEvent: EduUserEvent, type: EduUserStateChangeType) {
        super.onLocalUserUpdated(userEvent, type)
        /**更新视频列表和用户列表 */
        notifyVideoUserListForLocal()
    }

    override fun onLocalStreamAdded(streamEvent: EduStreamEvent) {
        super.onLocalStreamAdded(streamEvent)
        notifyVideoUserListForLocal()
    }

    override fun onLocalStreamUpdated(streamEvent: EduStreamEvent) {
        super.onLocalStreamUpdated(streamEvent)
        notifyVideoUserListForLocal()
    }

    override fun onLocalStreamRemoved(streamEvent: EduStreamEvent) {
        super.onLocalStreamRemoved(streamEvent)
        /**小班课场景下，此回调被调用就说明classroom结束，人员退出；所以此回调可以不处理 */
    }

    override fun onGlobalStateChanged(state: GlobalState) {
        super.onGlobalStateChanged(state)
        val boardState = state as BoardState
        val grantedUuids = boardState.grantUsers
        userListFragment!!.setGrantedUuids(grantedUuids)
    }

    private fun showVideoList(list: List<EduStreamInfo?>?) {
        runOnUiThread {}
        getLocalUserInfo(object : EduCallback<EduUserInfo?> {
            override fun onSuccess(res: EduUserInfo?) {
                if (res != null) {
                    for (i in list!!.indices) {
                        val userInfo = list[i]!!.publisher
                        if (userInfo.role == EduUserRole.TEACHER && userInfo.userUuid == res.userUuid && i != 0) {
                            Collections.swap(list, 0, i)
                            break
                        }
                    }
                    classVideoAdapter!!.setNewList(list)
                }
            }

            override fun onFailure(error: EduError) {}
        })
    }

    /**
     * 刷新视频列表和学生列表
     */
    private fun notifyVideoUserList(notifyCameraVideo: Boolean) {
        getCurFullStream(object : EduCallback<List<EduStreamInfo?>?> {
            override fun onSuccess(streamInfos: List<EduStreamInfo?>?) {
                /**刷新视频列表 */
                if (notifyCameraVideo) {
                    showVideoList(streamInfos)
                }
                userListFragment!!.setUserList(streamInfos)
            }

            override fun onFailure(error: EduError) {}
        })
    }

    /**
     * 因为本地用户或本地流的改变而刷新视频列表和学生列表
     */
    private fun notifyVideoUserListForLocal() {
        getCurFullStream(object : EduCallback<List<EduStreamInfo?>?> {
            override fun onSuccess(streamInfos: List<EduStreamInfo?>?) {
                showVideoList(streamInfos)
                userListFragment!!.updateLocalStream(localCameraStream)
                userListFragment!!.setUserList(streamInfos)
            }

            override fun onFailure(error: EduError) {}
        })
    }

    override fun onLocalUserLeft(userEvent: EduUserEvent, leftType: EduUserLeftType) {}

    companion object {
        private const val TAG = "SmallClassActivity"
    }
}