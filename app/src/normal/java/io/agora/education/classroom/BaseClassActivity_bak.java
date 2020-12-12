package io.agora.education.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.herewhite.sdk.domain.GlobalState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import io.agora.base.ToastManager;
import io.agora.base.callback.ThrowableCallback;
import io.agora.base.network.RetrofitManager;
import io.agora.education.EduApplication;
import io.agora.education.R;
import io.agora.education.RoomEntry;
import io.agora.education.api.EduCallback;
import io.agora.education.api.base.EduError;
import io.agora.education.api.logger.DebugItem;
import io.agora.education.api.manager.listener.EduManagerEventListener;
import io.agora.education.api.message.AgoraActionMessage;
import io.agora.education.api.message.EduChatMsg;
import io.agora.education.api.message.EduChatMsgType;
import io.agora.education.api.message.EduFromUserInfo;
import io.agora.education.api.message.EduMsg;
import io.agora.education.api.room.EduRoom;
import io.agora.education.api.room.data.EduRoomChangeType;
import io.agora.education.api.room.data.EduRoomInfo;
import io.agora.education.api.room.data.EduRoomState;
import io.agora.education.api.room.data.EduRoomStatus;
import io.agora.education.api.room.data.RoomCreateOptions;
import io.agora.education.api.room.data.RoomJoinOptions;
import io.agora.education.api.room.data.RoomMediaOptions;
import io.agora.education.api.room.data.RoomType;
import io.agora.education.api.room.listener.EduRoomEventListener;
import io.agora.education.api.statistics.ConnectionState;
import io.agora.education.api.statistics.NetworkQuality;
import io.agora.education.api.stream.data.EduStreamEvent;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.api.stream.data.LocalStreamInitOptions;
import io.agora.education.api.stream.data.VideoSourceType;
import io.agora.education.api.user.EduStudent;
import io.agora.education.api.user.EduUser;
import io.agora.education.api.user.data.EduLocalUserInfo;
import io.agora.education.api.user.data.EduUserEvent;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.api.user.data.EduUserRole;
import io.agora.education.api.user.data.EduUserStateChangeType;
import io.agora.education.api.user.listener.EduUserEventListener;
import io.agora.education.base.BaseActivity;
import io.agora.education.classroom.bean.board.BoardBean;
import io.agora.education.classroom.bean.board.BoardFollowMode;
import io.agora.education.classroom.bean.board.BoardInfo;
import io.agora.education.classroom.bean.board.BoardState;
import io.agora.education.classroom.bean.channel.Room;
import io.agora.education.classroom.bean.channel.User;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.record.RecordBean;
import io.agora.education.classroom.bean.record.RecordMsg;
import io.agora.education.classroom.fragment.ChatRoomFragment;
import io.agora.education.classroom.fragment.WhiteBoardFragment;
import io.agora.education.classroom.widget.TitleView;
import io.agora.education.service.BoardService;
import io.agora.education.service.bean.ResponseBody;
import io.agora.education.widget.ConfirmDialog;
import io.agora.whiteboard.netless.listener.GlobalStateChangeListener;
import kotlin.Unit;

import static io.agora.education.EduApplication.getAppId;
import static io.agora.education.EduApplication.getManager;
import static io.agora.education.MainActivity.CODE;
import static io.agora.education.MainActivity.REASON;
import static io.agora.education.api.BuildConfig.API_BASE_URL;
import static io.agora.education.classroom.bean.board.BoardBean.BOARD;
import static io.agora.education.classroom.bean.record.RecordBean.RECORD;
import static io.agora.education.classroom.bean.record.RecordState.END;

public abstract class BaseClassActivity_bak extends BaseActivity implements EduRoomEventListener, EduUserEventListener,
        EduManagerEventListener, GlobalStateChangeListener {
    private static final String TAG = BaseClassActivity_bak.class.getSimpleName();

    public static final String ROOMENTRY = "roomEntry";
    public static final int RESULT_CODE = 808;

    @BindView(R.id.title_view)
    protected TitleView title_view;
    @BindView(R.id.layout_whiteboard)
    protected FrameLayout layout_whiteboard;
    @BindView(R.id.layout_share_video)
    protected FrameLayout layout_share_video;

    protected WhiteBoardFragment whiteboardFragment = new WhiteBoardFragment();
    protected ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

    protected RoomEntry roomEntry;
    private volatile boolean isJoining = false, joinSuccess = false;
    //    private EduUser localUser;
    private EduRoom mainEduRoom;
    //    private EduRoomInfo mainRoomInfo;
    private EduStreamInfo localCameraStream, localScreenStream;
    protected BoardBean mainBoardBean;
    protected RecordBean mainRecordBean;
    protected volatile boolean revRecordMsg = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    protected void initData() {
        getManager().setEduManagerEventListener(this);
        roomEntry = getIntent().getParcelableExtra(ROOMENTRY);
        RoomCreateOptions createOptions = new RoomCreateOptions(roomEntry.getRoomUuid(),
                roomEntry.getRoomName(), roomEntry.getRoomType());
        mainEduRoom = buildEduRoom(createOptions, null);
    }

    @Override
    protected void initView() {
        if (getClassType() == RoomType.ONE_ON_ONE.getValue()) {
            whiteboardFragment.setInputWhileFollow(true);
        }
    }

    protected void showFragmentWithJoinSuccess() {
        title_view.setTitle(roomEntry.getRoomName());
        getSupportFragmentManager().beginTransaction()
                .remove(whiteboardFragment)
                .remove(chatRoomFragment)
                .commitNowAllowingStateLoss();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_whiteboard, whiteboardFragment)
                .add(R.id.layout_chat_room, chatRoomFragment)
                .show(whiteboardFragment)
                .show(chatRoomFragment)
                .commitNowAllowingStateLoss();
    }

    /**
     * @param options        创建room对象需要的参数
     * @param parentRoomUuid 父房间的uuid
     */
    protected EduRoom buildEduRoom(RoomCreateOptions options, String parentRoomUuid) {
        int roomType = options.getRoomType();
        if (options.getRoomType() == RoomType.BREAKOUT_CLASS.getValue()
                || options.getRoomType() == RoomType.INTERMEDIATE_CLASS.getValue()) {
            roomType = TextUtils.isEmpty(parentRoomUuid) ? RoomType.LARGE_CLASS.getValue() :
                    RoomType.SMALL_CLASS.getValue();
        }
        options = new RoomCreateOptions(options.getRoomUuid(), options.getRoomName(), roomType);
        EduRoom room = EduApplication.buildEduRoom(options);
        room.setEventListener(BaseClassActivity_bak.this);
        return room;
    }

    protected void joinRoom(EduRoom eduRoom, String yourNameStr, String yourUuid, boolean autoSubscribe,
                            boolean autoPublish, boolean needUserListener, EduCallback<EduStudent> callback) {
        if (isJoining) {
            return;
        }
        isJoining = true;
        RoomJoinOptions options = new RoomJoinOptions(yourUuid, yourNameStr, EduUserRole.STUDENT,
                new RoomMediaOptions(autoSubscribe, autoPublish), roomEntry.getRoomType());
        eduRoom.joinClassroom(options, new EduCallback<EduStudent>() {
            @Override
            public void onSuccess(@Nullable EduStudent user) {
                if (user != null) {
                    /**设置全局的userToken(注意同一个user在不同的room内，token不一样)*/
                    RetrofitManager.instance().addHeader("token", user.getUserInfo().getUserToken());
                    joinSuccess = true;
                    isJoining = false;
                    if (needUserListener) {
                        user.setEventListener(BaseClassActivity_bak.this);
                    }
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(EduError.Companion.internalError("localUser is null"));
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                isJoining = false;
                callback.onFailure(error);
            }
        });
    }

    /**
     * 加入失败，回传数据并结束当前页面
     */
    protected void joinFailed(int code, String reason) {
        Intent intent = getIntent().putExtra(CODE, code).putExtra(REASON, reason);
        setResult(RESULT_CODE, intent);
        finish();
    }

    protected void recoveryFragmentWithConfigChanged() {
        if (joinSuccess) {
            showFragmentWithJoinSuccess();
        }
    }

    /**
     * 禁止本地音频
     */
    public final void muteLocalAudio(boolean isMute) {
        if (localCameraStream != null) {
            switchLocalVideoAudio(getMyMediaRoom(), localCameraStream.getHasVideo(), !isMute);
        }
    }

    public final void muteLocalVideo(boolean isMute) {
        if (localCameraStream != null) {
            switchLocalVideoAudio(getMyMediaRoom(), !isMute, localCameraStream.getHasAudio());
        }
    }

    private void switchLocalVideoAudio(EduRoom room, boolean openVideo, boolean openAudio) {
        /**先更新本地流信息和rte状态*/
        if (localCameraStream != null) {
            room.getLocalUser(new EduCallback<EduUser>() {
                @Override
                public void onSuccess(@Nullable EduUser eduUser) {
                    if (eduUser != null) {
                        eduUser.initOrUpdateLocalStream(new LocalStreamInitOptions(localCameraStream.getStreamUuid(),
                                openVideo, openAudio), new EduCallback<EduStreamInfo>() {
                            @Override
                            public void onSuccess(@Nullable EduStreamInfo res) {
                                /**把更新后的流信息同步至服务器*/
                                eduUser.muteStream(res, new EduCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(@Nullable Boolean res) {
                                    }

                                    @Override
                                    public void onFailure(@NotNull EduError error) {
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NotNull EduError error) {
                            }
                        });
                    } else {
                    }
                }

                @Override
                public void onFailure(@NotNull EduError error) {

                }
            });
        }
    }

    public EduRoom getMainEduRoom() {
        return mainEduRoom;
    }

    public EduRoom getMyMediaRoom() {
        return mainEduRoom;
    }

    public void getLocalUser(EduCallback<EduUser> callback) {
        if (getMainEduRoom() != null) {
            getMainEduRoom().getLocalUser(callback);
        }
        callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
    }

    public void getLocalUserInfo(EduCallback<EduUserInfo> callback) {
        getLocalUser(new EduCallback<EduUser>() {
            @Override
            public void onSuccess(@Nullable EduUser res) {
                if (res != null) {
                    callback.onSuccess(res.getUserInfo());
                } else {
                    callback.onFailure(EduError.Companion.internalError("current eduUser is null"));
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                callback.onFailure(error);
            }
        });

    }

    public EduStreamInfo getLocalCameraStream() {
        return localCameraStream;
    }

    protected void setLocalCameraStream(EduStreamInfo streamInfo) {
        this.localCameraStream = streamInfo;
    }

    public void sendRoomChatMsg(String msg, EduCallback<EduChatMsg> callback) {
        getLocalUser(new EduCallback<EduUser>() {
            @Override
            public void onSuccess(@Nullable EduUser res) {
                if (res != null) {
                    res.sendRoomChatMessage(msg, callback);
                } else {
                    callback.onFailure(EduError.Companion.internalError("current eduUser is null"));
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                callback.onFailure(error);
            }
        });
    }

    protected void getCurFullStream(EduCallback<List<EduStreamInfo>> callback) {
        if (getMyMediaRoom() == null) {
            callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
        } else {
            getMyMediaRoom().getFullStreamList(callback);
        }
    }

    protected void getCurFullUser(EduCallback<List<EduUserInfo>> callback) {
        if (getMyMediaRoom() == null) {
            callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
        } else {
            getMyMediaRoom().getFullUserList(callback);
        }
    }

    protected void getTeacher(EduCallback<EduUserInfo> callback) {
        getCurFullUser(new EduCallback<List<EduUserInfo>>() {
            @Override
            public void onSuccess(@Nullable List<EduUserInfo> res) {
                for (EduUserInfo userInfo : res) {
                    if (userInfo.getRole().equals(EduUserRole.TEACHER)) {
                        callback.onSuccess(userInfo);
                        return;
                    }
                }
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                callback.onFailure(error);
            }
        });
    }

    protected String getRoleStr(int role) {
        int resId;
        switch (role) {
            case User.Role.TEACHER:
                resId = R.string.teacher;
                break;
            case User.Role.ASSISTANT:
                resId = R.string.assistant;
                break;
            case User.Role.STUDENT:
            default:
                resId = R.string.student;
                break;
        }
        return getString(resId);
    }

    protected void getScreenShareStream(EduCallback<EduStreamInfo> callback) {
        getCurFullStream(new EduCallback<List<EduStreamInfo>>() {
            @Override
            public void onSuccess(@Nullable List<EduStreamInfo> res) {
                for (EduStreamInfo stream : res) {
                    if (stream.getVideoSourceType().equals(VideoSourceType.SCREEN)) {
                        callback.onSuccess(stream);
                        return;
                    }
                }
                callback.onFailure(EduError.Companion.internalError("there is no screenShare"));
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * 尝试解析录制消息
     */
    protected void parseRecordMsg(Map<String, Object> roomProperties) {
        String recordJson = getProperty(roomProperties, RECORD);
        if (!TextUtils.isEmpty(recordJson)) {
            RecordBean tmp = RecordBean.fromJson(recordJson, RecordBean.class);
            if (mainRecordBean == null || tmp.getState() != mainRecordBean.getState()) {
                mainRecordBean = tmp;
                if (mainRecordBean.getState() == END) {
                    getLocalUserInfo(new EduCallback<EduUserInfo>() {
                        @Override
                        public void onSuccess(@Nullable EduUserInfo userInfo) {
                            EduFromUserInfo fromUser = new EduFromUserInfo(userInfo.getUserUuid(),
                                    userInfo.getUserName(), userInfo.getRole());
                            RecordMsg recordMsg = new RecordMsg(roomEntry.getRoomUuid(), fromUser,
                                    getString(R.string.replay_link), System.currentTimeMillis(),
                                    EduChatMsgType.Text.getValue());
                            recordMsg.isMe = true;
                            chatRoomFragment.addMessage(recordMsg);
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {
                        }
                    });
                }
            }
        }
    }

    @Room.Type
    protected abstract int getClassType();

    @Override
    protected void onDestroy() {
        /**尝试主动释放TimeView中的handle*/
        title_view.setTimeState(false, 0);
        /**退出activity之前释放eduRoom资源*/
        mainEduRoom = null;
        whiteboardFragment.releaseBoard();
        getManager().setEduManagerEventListener(null);
        getManager().release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showLeaveDialog();
    }

    public final void showLeaveDialog() {
        ConfirmDialog.normal(getString(R.string.confirm_leave_room_content), confirm -> {
            if (confirm) {
                /**退出activity之前离开eduRoom*/
                if (getMainEduRoom() != null) {
                    getMainEduRoom().leave(new EduCallback<Unit>() {
                        @Override
                        public void onSuccess(@Nullable Unit res) {
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {
                        }
                    });
                    BaseClassActivity_bak.this.finish();
                }
            }
        }).show(getSupportFragmentManager(), null);
    }

    public final void showRemovedDialog() {
        ConfirmDialog.single(getString(R.string.confirm_removed_from_room_content), confirm -> {
            if (confirm) {
                /**退出activity之前离开eduRoom*/
                if (getMainEduRoom() != null) {
                    getMainEduRoom().leave(new EduCallback<Unit>() {
                        @Override
                        public void onSuccess(@Nullable Unit res) {
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {
                        }
                    });
                    BaseClassActivity_bak.this.finish();
                }
            }
        }).show(getSupportFragmentManager(), null);
    }

    private final void showLogId(String logId) {
        if (!this.isFinishing() || !this.isDestroyed()) {
            ConfirmDialog.single(getString(R.string.uploadlog_success).concat(logId), null)
                    .show(getSupportFragmentManager(), null);
        }
    }

    public final void uploadLog() {
        if (getManager() != null) {
            getManager().uploadDebugItem(DebugItem.LOG, new EduCallback<String>() {
                @Override
                public void onSuccess(@Nullable String res) {
                    if (res != null) {
                        showLogId(res);
                    }
                }

                @Override
                public void onFailure(@NotNull EduError error) {
                    ToastManager.showShort(String.format(getString(R.string.function_error),
                            error.getType(), error.getMsg()));
                }
            });
        }
    }

    protected void getMediaRoomInfo(EduCallback<EduRoomInfo> callback) {
        if (getMyMediaRoom() == null) {
            callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
        } else {
            getMyMediaRoom().getRoomInfo(callback);
        }
    }

    protected void getMediaRoomStatus(EduCallback<EduRoomStatus> callback) {
        if (getMyMediaRoom() == null) {
            callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
        } else {
            getMyMediaRoom().getRoomStatus(callback);
        }
    }

    protected final void getMediaRoomUuid(EduCallback<String> callback) {
        if (getMyMediaRoom() == null) {
            callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
        } else {
            getMyMediaRoom().getRoomInfo(new EduCallback<EduRoomInfo>() {
                @Override
                public void onSuccess(@Nullable EduRoomInfo res) {
                    callback.onSuccess(res.getRoomUuid());
                }

                @Override
                public void onFailure(@NotNull EduError error) {
                    callback.onFailure(error);
                }
            });
        }
    }

    protected final void getMediaRoomName(EduCallback<String> callback) {
        if (getMyMediaRoom() == null) {
            callback.onFailure(EduError.Companion.internalError("current eduRoom is null"));
        } else {
            getMyMediaRoom().getRoomInfo(new EduCallback<EduRoomInfo>() {
                @Override
                public void onSuccess(@Nullable EduRoomInfo res) {
                    callback.onSuccess(res.getRoomName());
                }

                @Override
                public void onFailure(@NotNull EduError error) {
                    callback.onFailure(error);
                }
            });
        }
    }

    /**
     * 为流(主要是视频流)设置一个渲染区域
     */
    public void renderStream(EduRoom room, EduStreamInfo eduStreamInfo, @Nullable ViewGroup viewGroup) {
        runOnUiThread(() -> getLocalUser(new EduCallback<EduUser>() {
            @Override
            public void onSuccess(@Nullable EduUser eduUser) {
                room.getRoomInfo(new EduCallback<EduRoomInfo>() {
                    @Override
                    public void onSuccess(@Nullable EduRoomInfo res) {
                        eduUser.setStreamView(eduStreamInfo, res.getRoomUuid(), viewGroup);
                    }

                    @Override
                    public void onFailure(@NotNull EduError error) {
                    }
                });
            }

            @Override
            public void onFailure(@NotNull EduError error) {
            }
        }));
    }

    protected String getProperty(Map<String, Object> properties, String key) {
        if (properties != null) {
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                if (property.getKey().equals(key)) {
                    return String.valueOf(property.getValue());
                }
            }
        }
        return null;
    }

    /**
     * 当前白板是否开启跟随模式
     */
    protected boolean whiteBoardIsFollowMode(BoardState state) {
        if (state == null) {
            return false;
        }
        return state.getFollow() == BoardFollowMode.FOLLOW;
    }

    /**
     * 当前本地用户是否得到白板授权
     */
    protected void whiteBoardIsGranted(BoardState state, EduCallback<Boolean> callback) {
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo userInfo) {
                if (state != null) {
                    if (state.getGrantUsers() != null) {
                        for (String uuid : state.getGrantUsers()) {
                            if (uuid.equals(userInfo.getUserUuid())) {
                                callback.onSuccess(true);
                                return;
                            }
                        }
                        callback.onSuccess(false);
                    } else {
                        callback.onSuccess(false);
                    }
                } else {
                    callback.onFailure(EduError.Companion.internalError("current boardState is null"));
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                callback.onFailure(error);
            }
        });
    }

    protected void requestBoardInfo(String userToken, String appId, String roomUuid) {
        RetrofitManager.instance().getService(API_BASE_URL, BoardService.class)
                .getBoardInfo(userToken, appId, roomUuid)
                .enqueue(new RetrofitManager.Callback<>(0, new ThrowableCallback<ResponseBody<BoardBean>>() {
                    @Override
                    public void onFailure(@androidx.annotation.Nullable Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(@Nullable ResponseBody<BoardBean> res) {
                    }
                }));
    }

    protected void setTitleClassName() {
        getMediaRoomName(new EduCallback<String>() {
            @Override
            public void onSuccess(@Nullable String roomName) {
                title_view.setTitle(String.format(Locale.getDefault(), "%s", roomName));
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    protected void initTitleTimeState() {
        getMediaRoomStatus(new EduCallback<EduRoomStatus>() {
            @Override
            public void onSuccess(@Nullable EduRoomStatus status) {
                title_view.setTimeState(status.getCourseState() == EduRoomState.START,
                        System.currentTimeMillis() - status.getStartTime());
                chatRoomFragment.setMuteAll(!status.isStudentChatAllowed());
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    protected void initParseBoardInfo(EduRoom classRoom) {
        /**处理roomProperties*/
        Map<String, Object> roomProperties = classRoom.getRoomProperties();
        /**判断roomProperties中是否有白板属性信息，如果没有，发起请求,等待RTM通知*/
        String boardJson = getProperty(roomProperties, BOARD);
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo userInfo) {
                if (TextUtils.isEmpty(boardJson) && mainBoardBean == null) {
                    requestBoardInfo(((EduLocalUserInfo) userInfo).getUserToken(), getAppId(),
                            roomEntry.getRoomUuid());
                } else {
                    mainBoardBean = new Gson().fromJson(boardJson, BoardBean.class);
                    BoardInfo info = mainBoardBean.getInfo();
                    Log.e(TAG, "白板信息已存在->" + boardJson);
                    runOnUiThread(() -> whiteboardFragment.initBoardWithRoomToken(info.getBoardId(),
                            info.getBoardToken(), userInfo.getUserUuid()));
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onRemoteUsersInitialized(@NotNull List<? extends EduUserInfo> users, @NotNull EduRoom classRoom) {
        initTitleTimeState();
        initParseBoardInfo(getMainEduRoom());
    }

    @Override
    public void onRemoteUsersJoined(@NotNull List<? extends EduUserInfo> users, @NotNull EduRoom classRoom) {
        Log.e(TAG, "收到远端用户加入的回调");
    }

    @Override
    public void onRemoteUserLeft(@NotNull EduUserEvent userEvent, @NotNull EduRoom classRoom) {
        Log.e(TAG, "收到远端用户离开的回调");
    }

    @Override
    public void onRemoteUserUpdated(@NotNull EduUserEvent userEvent, @NotNull EduUserStateChangeType type,
                                    @NotNull EduRoom classRoom) {
        Log.e(TAG, "收到远端用户修改的回调");
    }

    @Override
    public void onRoomMessageReceived(@NotNull EduMsg message, @NotNull EduRoom classRoom) {

    }

    @Override
    public void onRoomChatMessageReceived(@NotNull EduChatMsg eduChatMsg, @NotNull EduRoom classRoom) {
        /**收到群聊消息，进行处理并展示*/
        ChannelMsg.ChatMsg chatMsg = new ChannelMsg.ChatMsg(eduChatMsg.getFromUser(),
                eduChatMsg.getMessage(), eduChatMsg.getTimestamp(), eduChatMsg.getType());
        classRoom.getLocalUser(new EduCallback<EduUser>() {
            @Override
            public void onSuccess(@Nullable EduUser user) {
                chatMsg.isMe = chatMsg.getFromUser().equals(user.getUserInfo());
                chatRoomFragment.addMessage(chatMsg);
                Log.e(TAG, "成功添加一条聊天消息");
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onRemoteStreamsInitialized(@NotNull List<? extends EduStreamInfo> streams, @NotNull EduRoom classRoom) {
        Log.e(TAG, "onRemoteStreamsInitialized");
    }

    @Override
    public void onRemoteStreamsAdded(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        Log.e(TAG, "收到添加远端流的回调");
        Iterator<EduStreamEvent> iterator = streamEvents.iterator();
        while (iterator.hasNext()) {
            EduStreamEvent streamEvent = iterator.next();
            EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            if (streamInfo.getPublisher().getRole() == EduUserRole.TEACHER
                    && streamInfo.getVideoSourceType().equals(VideoSourceType.SCREEN)) {
                /**老师打开了屏幕分享，此时把这个流渲染出来*/
                runOnUiThread(() -> {
                    layout_whiteboard.setVisibility(View.GONE);
                    layout_share_video.setVisibility(View.VISIBLE);
                    layout_share_video.removeAllViews();
                    renderStream(getMainEduRoom(), streamInfo, layout_share_video);
                });
                /**屏幕分享流已处理，移出集合*/
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void onRemoteStreamUpdated(@NotNull List<EduStreamEvent> streamEvents,
                                      @NotNull EduRoom classRoom) {
        Log.e(TAG, "收到修改远端流的回调");
        Iterator<EduStreamEvent> iterator = streamEvents.iterator();
        while (iterator.hasNext()) {
            EduStreamEvent streamEvent = iterator.next();
            EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            if (streamInfo.getPublisher().getRole() == EduUserRole.TEACHER
                    && streamInfo.getVideoSourceType().equals(VideoSourceType.SCREEN)) {
                runOnUiThread(() -> {
                    layout_whiteboard.setVisibility(View.GONE);
                    layout_share_video.setVisibility(View.VISIBLE);
                    layout_share_video.removeAllViews();
                    renderStream(getMainEduRoom(), streamInfo, layout_share_video);
                });
                /**屏幕分享流已处理，移出集合*/
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void onRemoteStreamsRemoved(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        Log.e(TAG, "收到移除远端流的回调");
        for (EduStreamEvent streamEvent : streamEvents) {
            EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            if (streamInfo.getPublisher().getRole() == EduUserRole.TEACHER
                    && streamInfo.getVideoSourceType().equals(VideoSourceType.SCREEN)) {
                /**老师关闭了屏幕分享，移除屏幕分享的布局*/
                runOnUiThread(() -> {
                    layout_whiteboard.setVisibility(View.VISIBLE);
                    layout_share_video.setVisibility(View.GONE);
                    layout_share_video.removeAllViews();
                    renderStream(getMainEduRoom(), streamInfo, null);
                });
                break;
            }
        }
    }

    @Override
    public void onRoomStatusChanged(@NotNull EduRoomChangeType event, @NotNull EduUserInfo operatorUser, @NotNull EduRoom classRoom) {
        classRoom.getRoomStatus(new EduCallback<EduRoomStatus>() {
            @Override
            public void onSuccess(@Nullable EduRoomStatus roomStatus) {
                switch (event) {
                    case CourseState:
                        title_view.setTimeState(roomStatus.getCourseState() == EduRoomState.START,
                                System.currentTimeMillis() - roomStatus.getStartTime());
                        break;
                    case AllStudentsChat:
                        chatRoomFragment.setMuteAll(!roomStatus.isStudentChatAllowed());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onRoomPropertiesChanged(@NotNull EduRoom classRoom, @Nullable Map<String, Object> cause) {
        Log.e(TAG, "收到roomProperty改变的数据");
        Map<String, Object> roomProperties = classRoom.getRoomProperties();
        String boardJson = getProperty(roomProperties, BOARD);
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo userInfo) {
                if (mainBoardBean == null) {
                    Log.e(TAG, "首次获取到白板信息->" + boardJson);
                    /**首次获取到白板信息*/
                    mainBoardBean = new Gson().fromJson(boardJson, BoardBean.class);
                    runOnUiThread(() -> {
                        whiteboardFragment.initBoardWithRoomToken(mainBoardBean.getInfo().getBoardId(),
                                mainBoardBean.getInfo().getBoardToken(), userInfo.getUserUuid());
                    });
                }
                String recordJson = getProperty(roomProperties, RECORD);
                if (!TextUtils.isEmpty(recordJson)) {
                    RecordBean tmp = RecordBean.fromJson(recordJson, RecordBean.class);
                    if (mainRecordBean == null || tmp.getState() != mainRecordBean.getState()) {
                        mainRecordBean = tmp;
                        if (mainRecordBean.getState() == END) {
                            getMediaRoomUuid(new EduCallback<String>() {
                                @Override
                                public void onSuccess(@Nullable String uuid) {
                                    revRecordMsg = true;
                                    EduFromUserInfo fromUser = new EduFromUserInfo(userInfo.getUserUuid(),
                                            userInfo.getUserName(), userInfo.getRole());
                                    RecordMsg recordMsg = new RecordMsg(uuid, fromUser,
                                            getString(R.string.replay_link), System.currentTimeMillis(),
                                            EduChatMsgType.Text.getValue());
                                    recordMsg.isMe = true;
                                    chatRoomFragment.addMessage(recordMsg);
                                }

                                @Override
                                public void onFailure(@NotNull EduError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onNetworkQualityChanged(@NotNull NetworkQuality quality, @NotNull EduUserInfo user, @NotNull EduRoom classRoom) {
//        Log.e(TAG, "onNetworkQualityChanged->" + quality.getValue());
        title_view.setNetworkQuality(quality);
    }

    @Override
    public void onConnectionStateChanged(@NotNull ConnectionState state, @NotNull EduRoom classRoom) {
        classRoom.getRoomInfo(new EduCallback<EduRoomInfo>() {
            @Override
            public void onSuccess(@Nullable EduRoomInfo roomInfo) {
                Log.e(TAG, "onNetworkQualityChanged->" + state.getValue() + ",room:"
                        + roomInfo.getRoomUuid());
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onLocalUserUpdated(@NotNull EduUserEvent userEvent, @NotNull EduUserStateChangeType type) {
        /**更新用户信息*/
        EduUserInfo userInfo = userEvent.getModifiedUser();
        chatRoomFragment.setMuteLocal(!userInfo.isChatAllowed());
    }

    @Override
    public void onLocalStreamAdded(@NotNull EduStreamEvent streamEvent) {
        Log.e(TAG, "收到添加本地流的回调");
        switch (streamEvent.getModifiedStream().getVideoSourceType()) {
            case CAMERA:
                localCameraStream = streamEvent.getModifiedStream();
                Log.e(TAG, "收到添加本地Camera流的回调");
                break;
            case SCREEN:
                localScreenStream = streamEvent.getModifiedStream();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocalStreamUpdated(@NotNull EduStreamEvent streamEvent) {
        Log.e(TAG, "收到更新本地流的回调");
        switch (streamEvent.getModifiedStream().getVideoSourceType()) {
            case CAMERA:
                localCameraStream = streamEvent.getModifiedStream();
                break;
            case SCREEN:
                localScreenStream = streamEvent.getModifiedStream();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocalStreamRemoved(@NotNull EduStreamEvent streamEvent) {
        Log.e(TAG, "收到移除本地流的回调");
        switch (streamEvent.getModifiedStream().getVideoSourceType()) {
            case CAMERA:
                localCameraStream = null;
                break;
            case SCREEN:
                localScreenStream = null;
                break;
            default:
                break;
        }
    }

    /**
     * eduManager的回调
     */
    @Override
    public void onUserMessageReceived(@NotNull EduMsg message) {

    }

    @Override
    public void onUserChatMessageReceived(@NotNull EduChatMsg chatMsg) {

    }

    @Override
    public void onUserActionMessageReceived(@NotNull AgoraActionMessage actionMessage) {

    }

    private boolean followTips = false;
    private boolean curFollowState = false;

    /**
     * 白板的全局回调
     */
    @Override
    public void onGlobalStateChanged(GlobalState state) {
        BoardState boardState = (BoardState) state;
        boolean follow = whiteBoardIsFollowMode(boardState);
        if (followTips) {
            if (curFollowState != follow) {
                curFollowState = follow;
                ToastManager.showShort(follow ? R.string.open_follow_board : R.string.relieve_follow_board);
            }
        } else {
            followTips = true;
            curFollowState = follow;
        }
        whiteboardFragment.disableCameraTransform(follow);
        if (getClassType() == RoomType.ONE_ON_ONE.getValue()) {
            /**一对一，学生始终有输入权限*/
            whiteboardFragment.disableDeviceInputs(false);
            return;
        }
        whiteBoardIsGranted(boardState, new EduCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean granted) {
                whiteboardFragment.disableDeviceInputs(!granted);
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }
}
