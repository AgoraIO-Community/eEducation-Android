package io.agora.education.classroom;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.herewhite.sdk.domain.GlobalState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.agora.agoraactionprocess.AgoraActionWaitACK;
import io.agora.agoraactionprocess.AgoraStartActionOptions;
import io.agora.agoraactionprocess.AgoraStopActionOptions;
import io.agora.base.callback.ThrowableCallback;
import io.agora.base.network.ResponseBody;
import io.agora.education.R;
import io.agora.education.api.EduCallback;
import io.agora.education.api.base.EduError;
import io.agora.education.api.message.EduActionMessage;
import io.agora.education.api.message.EduChatMsg;
import io.agora.education.api.message.EduMsg;
import io.agora.education.api.message.GroupMemberInfoMessage;
import io.agora.education.api.room.EduRoom;
import io.agora.education.api.room.data.EduRoomChangeType;
import io.agora.education.api.room.data.EduRoomInfo;
import io.agora.education.api.statistics.ConnectionState;
import io.agora.education.api.statistics.NetworkQuality;
import io.agora.education.api.stream.data.EduStreamEvent;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.api.stream.data.LocalStreamInitOptions;
import io.agora.education.api.stream.data.VideoSourceType;
import io.agora.education.api.user.EduStudent;
import io.agora.education.api.user.EduUser;
import io.agora.education.api.user.data.EduBaseUserInfo;
import io.agora.education.api.user.data.EduLocalUserInfo;
import io.agora.education.api.user.data.EduUserEvent;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.api.user.data.EduUserLeftType;
import io.agora.education.api.user.data.EduUserRole;
import io.agora.education.api.user.data.EduUserStateChangeType;
import io.agora.education.classroom.adapter.StageVideoAdapter;
import io.agora.education.classroom.bean.board.BoardBean;
import io.agora.education.classroom.bean.board.BoardInfo;
import io.agora.education.classroom.bean.channel.Room;
import io.agora.education.classroom.bean.group.GroupInfo;
import io.agora.education.classroom.bean.group.GroupMemberInfo;
import io.agora.education.classroom.bean.group.GroupStateInfo;
import io.agora.education.classroom.bean.group.RoomGroupInfo;
import io.agora.education.classroom.bean.group.StageStreamInfo;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.fragment.StudentGroupListFragment;
import io.agora.education.classroom.fragment.StudentListFragment;
import io.agora.education.classroom.widget.RtcVideoView;
import io.agora.agoraactionprocess.AgoraActionMsgRes;
import io.agora.agoraactionprocess.AgoraActionConfigInfo;
import io.agora.raisehand.AgoraCoVideoAction;
import io.agora.raisehand.AgoraCoVideoFromRoom;
import io.agora.raisehand.AgoraCoVideoListener;
import io.agora.raisehand.AgoraCoVideoView;
import io.agora.raisehand.AgoraCoVideoFromUser;
import kotlin.Unit;

import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.CMD;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.GROUOREWARD;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.GROUPMEDIA;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.STUDENTLISTCHANGED;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.STUDENTREWARD;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.SWITCHAUTOCOVIDEO;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.SWITCHCOVIDEO;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.SWITCHGROUP;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.SWITCHINTERACTIN;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.SWITCHINTERACTOUT;
import static io.agora.education.classroom.bean.group.IntermediateClassPropertyCauseType.UPDATEGROUP;
import static io.agora.education.classroom.bean.group.RoomGroupInfo.GROUPS;
import static io.agora.education.classroom.bean.group.RoomGroupInfo.GROUPSTATES;
import static io.agora.education.classroom.bean.group.RoomGroupInfo.GROUPUUID;
import static io.agora.education.classroom.bean.group.RoomGroupInfo.INTERACTOUTGROUPS;
import static io.agora.education.classroom.bean.group.RoomGroupInfo.STUDENTS;
import static io.agora.education.classroom.bean.group.RoomGroupInfo.USERUUID;
import static io.agora.agoraactionprocess.AgoraActionType.AgoraActionTypeApply;
import static io.agora.agoraactionprocess.AgoraActionType.AgoraActionTypeCancel;
import static io.agora.education.classroom.bean.msg.PeerMsg.Cmd.UnMutePeerCMD;
import static io.agora.agoraactionprocess.AgoraActionWaitACK.DISABLE;
import static io.agora.agoraactionprocess.AgoraActionWaitACK.ENABLE;

public class MediumClassActivity extends BaseClassActivity_bak implements TabLayout.OnTabSelectedListener,
        AgoraCoVideoListener {
    private static final String TAG = MediumClassActivity.class.getSimpleName();

    @BindView(R.id.layout_video_teacher)
    FrameLayout layoutVideoTeacher;
    @BindView(R.id.stage_videos_one)
    RecyclerView stageVideosOne;
    @BindView(R.id.stage_videos_two)
    RecyclerView stageVideosTwo;
    @BindView(R.id.coVideoView)
    AgoraCoVideoView agoraCoVideoView;
    @BindView(R.id.layout_tab)
    TabLayout tabLayout;

    private RtcVideoView videoTeacher;
    private StudentListFragment studentListFragment;
    private StudentGroupListFragment studentGroupListFragment;
    /*当前班级的分组情况*/
    private RoomGroupInfo roomGroupInfo = new RoomGroupInfo();
    private StageVideoAdapter stageVideoAdapterOne = new StageVideoAdapter(),
            getStageVideoAdapterTwo = new StageVideoAdapter();
    private List<StageStreamInfo> stageStreamInfosOne = new ArrayList<>();
    private List<StageStreamInfo> stageStreamInfosTwo = new ArrayList<>();

    @Override
    protected int getClassType() {
        return Room.Type.INTERMEDIATE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_medium_class;
    }

    @Override
    protected void initData() {
        super.initData();
        joinRoom(getMainEduRoom(), roomEntry.getUserName(), roomEntry.getUserUuid(), true, false, true,
                new EduCallback<EduStudent>() {
                    @Override
                    public void onSuccess(@org.jetbrains.annotations.Nullable EduStudent res) {
                        runOnUiThread(() -> {
                            showFragmentWithJoinSuccess();
                            /*disable operation in intermediateClass`s mainClass*/
                            whiteboardFragment.disableDeviceInputs(true);
                            whiteboardFragment.setWritable(false);
                        });
                        initTitleTimeState();
                        buildActionProcessManager();
                        parseAgoraActionConfig(getMainEduRoom());
                        /*初始化举手连麦组件*/
                        agoraCoVideoView.init(getMainEduRoom());
                        initParseBoardInfo(getMainEduRoom());
                        /*获取班级的roomProperties中可能存在的分组信息*/
                        syncRoomGroupProperty(getMainEduRoom().getRoomProperties());
                        /*检查并更新课堂名单*/
                        updateStudentList();
                        notifyUserList();
                        notifyStageVideoList();
                    }

                    @Override
                    public void onFailure(@NotNull EduError error) {
                        joinFailed(error.getType(), error.getMsg());
                    }
                });
    }

    @Override
    protected void initView() {
        super.initView();

        tabLayout.addOnTabSelectedListener(this);

        if (videoTeacher == null) {
            videoTeacher = new RtcVideoView(this);
            videoTeacher.init(R.layout.layout_video_large_class, false);
        }
        removeFromParent(videoTeacher);
        layoutVideoTeacher.addView(videoTeacher, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        studentListFragment = new StudentListFragment(roomEntry.getUserUuid());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_chat_room, studentListFragment)
                .show(studentListFragment)
                .hide(studentListFragment)
                .commitNowAllowingStateLoss();

        studentGroupListFragment = new StudentGroupListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_chat_room, studentGroupListFragment)
                .show(studentGroupListFragment)
                .hide(studentGroupListFragment)
                .commitNowAllowingStateLoss();

        stageVideosOne.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        stageVideosOne.setAdapter(stageVideoAdapterOne);
        stageVideosTwo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        stageVideosTwo.setAdapter(getStageVideoAdapterTwo);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (tab.getPosition() == 0) {
            Fragment fragment = roomGroupInfo.isEnableGroup() ? studentGroupListFragment : studentListFragment;
            transaction.show(fragment).hide(chatRoomFragment);
        } else {
            transaction.show(chatRoomFragment).hide(studentListFragment).hide(studentGroupListFragment);
        }
        transaction.commitNow();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    protected void showFragmentWithJoinSuccess() {
        super.showFragmentWithJoinSuccess();
        getSupportFragmentManager().beginTransaction()
                .hide(chatRoomFragment)
                .commitNowAllowingStateLoss();
    }

    private void switchUserFragment(boolean showGroup) {
        runOnUiThread(() -> {
            if ((showGroup && studentGroupListFragment.isVisible()) ||
                    (!showGroup && studentListFragment.isVisible())) {
                return;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (showGroup) {
                transaction = transaction.show(studentGroupListFragment)
                        .hide(studentListFragment);
            } else {
                transaction = transaction.show(studentListFragment)
                        .hide(studentGroupListFragment);
            }
            transaction.commitNowAllowingStateLoss();
        });
    }

    private void showTeacherStream(EduStreamInfo stream, FrameLayout viewGroup) {
        switch (stream.getVideoSourceType()) {
            case CAMERA:
                videoTeacher.setName(stream.getPublisher().getUserName());
                renderStream(getMainEduRoom(), stream, viewGroup);
                videoTeacher.muteVideo(!stream.getHasVideo());
                videoTeacher.muteAudio(!stream.getHasAudio());
                break;
            case SCREEN:
                runOnUiThread(() -> {
                    if (viewGroup == null) {
                        layout_whiteboard.setVisibility(View.VISIBLE);
                        layout_share_video.setVisibility(View.GONE);
                    } else {
                        layout_whiteboard.setVisibility(View.GONE);
                        layout_share_video.setVisibility(View.VISIBLE);
                    }
                    layout_share_video.removeAllViews();
                    renderStream(getMainEduRoom(), stream, layout_share_video);
                });
                break;
            default:
                break;
        }
    }

    private void initBoard(BoardBean boardBean) {
        BoardInfo info = boardBean.getInfo();
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo userInfo) {
                runOnUiThread(() -> whiteboardFragment.initBoardWithRoomToken(
                        info.getBoardId(), info.getBoardToken(), userInfo.getUserUuid()));
            }

            @Override
            public void onFailure(@NotNull EduError error) {
            }
        });
    }

    private void syncRoomGroupProperty(Map<String, Object> roomProperties) {
        String groupStatesJson = getProperty(roomProperties, GROUPSTATES);
        GroupStateInfo groupStateInfo = new Gson().fromJson(groupStatesJson, GroupStateInfo.class);
        roomGroupInfo.setGroupStates(groupStateInfo);
        String interactOutGroupsJson = getProperty(roomProperties, INTERACTOUTGROUPS);
        Map<String, String> interactOutGroups = new Gson().fromJson(interactOutGroupsJson,
                new TypeToken<Map<String, String>>() {
                }.getType());
        roomGroupInfo.updateInteractOutGroups(interactOutGroups);
        String groupsJson = getProperty(roomProperties, GROUPS);
        Map<String, GroupInfo> groups = new Gson().fromJson(groupsJson,
                new TypeToken<Map<String, GroupInfo>>() {
                }.getType());
        roomGroupInfo.updateGroups(groups);
        /*解析当前的学生名单*/
        Map<String, GroupMemberInfo> allStudent = new Gson()
                .fromJson(new Gson().toJson(roomProperties.get(STUDENTS)),
                        new TypeToken<Map<String, GroupMemberInfo>>() {
                        }.getType());
        getCurFullUser(new EduCallback<List<EduUserInfo>>() {
            @Override
            public void onSuccess(@Nullable List<EduUserInfo> onLineUsers) {
                getCurFullStream(new EduCallback<List<EduStreamInfo>>() {
                    @Override
                    public void onSuccess(@Nullable List<EduStreamInfo> streams) {
                        if (onLineUsers != null && streams != null) {
                            roomGroupInfo.updateAllStudent(allStudent, onLineUsers, streams);
                        }
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
    }

    /**
     * 检查并更新课堂的学生名单
     * 学生名单名单包括所有进入过课堂的学生，只增不减
     */
    private void updateStudentList() {
        getLocalUser(new EduCallback<EduUser>() {
            @Override
            public void onSuccess(@Nullable EduUser user) {
                if (user != null) {
                    EduUserInfo userInfo = user.getUserInfo();
                    GroupMemberInfoMessage memberInfo = new GroupMemberInfoMessage(userInfo.getUserUuid(),
                            userInfo.getUserName(), "", 0);
                    Map<String, GroupMemberInfoMessage> memberInfoMap = new HashMap<>();
                    memberInfoMap.put(STUDENTS.concat(".").concat(memberInfo.getUuid()),
                            memberInfo);
                    Map<String, String> cause = new HashMap<>();
                    cause.put(CMD, String.valueOf(STUDENTLISTCHANGED));
                    user.setRoomProperties(memberInfoMap, cause, new EduCallback<Unit>() {
                        @Override
                        public void onSuccess(@Nullable Unit res) {
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {
                            Log.e(TAG, "更新本地用户信息至课堂名单中失败->" + error.getMsg());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    /**
     * 显示用户列表
     * 分为分组显示和直接列表显示
     */
    private void notifyUserList() {
        if (roomGroupInfo.isEnableGroup()) {
            /*开启了分组，需要分组显示学生列表*/
            switchUserFragment(true);
            List<GroupInfo> groupInfos = roomGroupInfo.getGroups();
            List<GroupMemberInfo> allStudent = roomGroupInfo.getAllStudent();
            if (groupInfos != null && groupInfos.size() > 0 && allStudent != null
                    && allStudent.size() > 0) {
                studentGroupListFragment.updateGroupList(groupInfos, roomGroupInfo.getAllStudent());
            }
        } else {
            /*未开启分组，直接列表显示学生*/
            switchUserFragment(false);
            studentListFragment.updateStudentList(roomGroupInfo.getAllStudent());
        }
    }

    private void notifyStageVideoList() {
        if (roomGroupInfo.isEnablePK()) {
            List<GroupInfo> groupInfos = roomGroupInfo.getGroups();
            List<String> stageGroupIds = roomGroupInfo.getInteractOutGroups();
            List<GroupInfo> stageGroups = new ArrayList<>(2);
            Iterator<GroupInfo> iterator = groupInfos.iterator();
            while (iterator.hasNext()) {
                GroupInfo element = iterator.next();
                if (stageGroupIds.contains(element.getGroupUuid())) {
                    stageGroups.add(element);
                }
            }
            /*打开pk,肯定有一个组整体上台*/
            List<String> stageMemberIdsOne = stageGroups.get(0).getMembers();
            final List<String> stageMemberIdsTwo = stageGroups.size() > 1 ?
                    stageGroups.get(1).getMembers() : new ArrayList<>();
//            /**测试代码*/
//            List<EduStreamInfo> curFullStreams = new ArrayList<>();
//            if (roomGroupInfo.getAllStudent() != null) {
//                for (GroupMemberInfo memberInfo : roomGroupInfo.getAllStudent()) {
//                    EduBaseUserInfo baseUserInfo = new EduBaseUserInfo(memberInfo.getUuid(),
//                            memberInfo.getUserName(), EduUserRole.STUDENT);
//                    EduStreamInfo streamInfo = new EduStreamInfo(memberInfo.getUuid().concat("000"),
//                            "stream-".concat(memberInfo.getUserName()), VideoSourceType.CAMERA,
//                            true, true, baseUserInfo);
//                    curFullStreams.add(streamInfo);
//                }
//            }
//            stageStreamInfosOne.clear();
//            stageStreamInfosTwo.clear();
//            if (curFullStreams != null && curFullStreams.size() > 0) {
//                for (EduStreamInfo stream : curFullStreams) {
//                    String userUuid = stream.getPublisher().getUserUuid();
//                    if (stageMemberIdsOne.contains(userUuid)) {
//                        StageStreamInfo stageStream = new StageStreamInfo(stream,
//                                roomGroupInfo.getStudentReward(userUuid));
//                        stageStreamInfosOne.add(stageStream);
//                    } else if (stageMemberIdsTwo.contains(userUuid)) {
//                        StageStreamInfo stageStream = new StageStreamInfo(stream,
//                                roomGroupInfo.getStudentReward(userUuid));
//                        stageStreamInfosTwo.add(stageStream);
//                    }
//                }
//                notifyStageVideoListOne();
//                notifyStageVideoListTwo();
//            }
            getCurFullStream(new EduCallback<List<EduStreamInfo>>() {
                @Override
                public void onSuccess(@Nullable List<EduStreamInfo> curFullStreams) {
                    if (curFullStreams != null && curFullStreams.size() > 0) {
                        for (EduStreamInfo stream : curFullStreams) {
                            String userUuid = stream.getPublisher().getUserUuid();
                            if (stageMemberIdsOne.contains(userUuid)) {
                                StageStreamInfo stageStream = new StageStreamInfo(stream,
                                        roomGroupInfo.getStudentReward(userUuid));
                                stageStreamInfosOne.add(stageStream);
                            } else if (stageMemberIdsTwo.contains(userUuid)) {
                                StageStreamInfo stageStream = new StageStreamInfo(stream,
                                        roomGroupInfo.getStudentReward(userUuid));
                                stageStreamInfosTwo.add(stageStream);
                            }
                        }
                        notifyStageVideoListOne();
                        notifyStageVideoListTwo();
                    }
                }

                @Override
                public void onFailure(@NotNull EduError error) {

                }
            });
        } else {
            List<EduStreamInfo> curStageStreams = new ArrayList<>();
            if (roomGroupInfo.getAllStudent() != null) {
                for (GroupMemberInfo element : roomGroupInfo.getAllStudent()) {
                    if (element.getOnStage()) {
                        EduBaseUserInfo baseUserInfo = new EduBaseUserInfo(element.getUuid(),
                                element.getUserName(), EduUserRole.STUDENT);
                        /*发现streamUuid为空，则去本地流缓存中遍历，补齐数据*/
                        if (element.getStreamUuid() == null) {
                            getCurFullStream(new EduCallback<List<EduStreamInfo>>() {
                                @Override
                                public void onSuccess(@Nullable List<EduStreamInfo> streams) {
                                    if (streams != null) {
                                        for (EduStreamInfo streamInfo : streams) {
                                            if (streamInfo.getPublisher().getUserUuid()
                                                    .equals(element.getUuid())) {
                                                element.setStreamUuid(streamInfo.getStreamUuid());
                                                element.setStreamName(streamInfo.getStreamName());
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull EduError error) {
                                }
                            });
                        }
                        EduStreamInfo streamInfo = new EduStreamInfo(element.getStreamUuid(),
                                element.getStreamName(),
                                VideoSourceType.CAMERA, element.getEnableVideo(), element.getEnableAudio(), baseUserInfo);
                        curStageStreams.add(streamInfo);
                    }
                }
            }
            stageStreamInfosOne.clear();
            stageStreamInfosTwo.clear();
            if (curStageStreams != null && curStageStreams.size() > 0) {
                for (EduStreamInfo stream : curStageStreams) {
                    String userUuid = stream.getPublisher().getUserUuid();
                    StageStreamInfo stageStream = new StageStreamInfo(stream,
                            roomGroupInfo.getStudentReward(userUuid));
                    stageStreamInfosOne.add(stageStream);
                }
            }
            notifyStageVideoListOne();
        }
    }

    private void notifyStageVideoListOne() {
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo res) {
                if (res != null) {
                    runOnUiThread(() -> {
                        stageVideosOne.setVisibility(stageStreamInfosOne.size() > 0 ? View.VISIBLE : View.GONE);
                        stageVideoAdapterOne.setNewList(stageStreamInfosOne, res.getUserUuid());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
            }
        });
    }

    private void notifyStageVideoListTwo() {
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo res) {
                if (res != null) {
                    runOnUiThread(() -> {
                        stageVideosTwo.setVisibility(stageStreamInfosTwo.size() > 0 ? View.VISIBLE : View.GONE);
                        getStageVideoAdapterTwo.setNewList(stageStreamInfosTwo, res.getUserUuid());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
            }
        });
    }


    @Override
    public void onRemoteUsersInitialized(@NotNull List<? extends EduUserInfo> users, @NotNull EduRoom classRoom) {
        if (classRoom.equals(getMainEduRoom())) {
            initParseBoardInfo(getMainEduRoom());
            /*获取班级的roomProperties中可能存在的分组信息*/
            Map<String, Object> roomProperties = getMainEduRoom().getRoomProperties();
            syncRoomGroupProperty(roomProperties);
            /*检查并更新课堂名单*/
            updateStudentList();
            notifyUserList();
            notifyStageVideoList();
        }
    }

    @Override
    public void onRemoteUsersJoined(@NotNull List<? extends EduUserInfo> users, @NotNull EduRoom classRoom) {
        super.onRemoteUsersJoined(users, classRoom);
        notifyUserList();
    }

    @Override
    public void onRemoteUserLeft(@NotNull EduUserEvent userEvent, @NotNull EduRoom classRoom) {
        super.onRemoteUserLeft(userEvent, classRoom);
        notifyUserList();
    }

    @Override
    public void onRemoteUserUpdated(@NotNull EduUserEvent userEvent, @NotNull EduUserStateChangeType type,
                                    @NotNull EduRoom classRoom) {
        super.onRemoteUserUpdated(userEvent, type, classRoom);
        notifyUserList();
    }

    @Override
    public void onRoomMessageReceived(@NotNull EduMsg message, @NotNull EduRoom classRoom) {
        super.onRoomMessageReceived(message, classRoom);
    }

    @Override
    public void onRoomChatMessageReceived(@NotNull EduChatMsg eduChatMsg, @NotNull EduRoom classRoom) {
        super.onRoomChatMessageReceived(eduChatMsg, classRoom);
    }

    @Override
    public void onRemoteStreamsInitialized(@NotNull List<? extends EduStreamInfo> streams, @NotNull EduRoom classRoom) {
        if (classRoom.equals(getMainEduRoom())) {
            /*显示老师的流*/
            getMainEduRoom().getFullStreamList(new EduCallback<List<EduStreamInfo>>() {
                @Override
                public void onSuccess(@Nullable List<EduStreamInfo> res) {
                    if (res != null) {
                        for (EduStreamInfo streamInfo : res) {
                            EduBaseUserInfo publisher = streamInfo.getPublisher();
                            if (publisher.getRole().equals(EduUserRole.TEACHER)) {
                                showTeacherStream(streamInfo, videoTeacher.getVideoLayout());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull EduError error) {
                }
            });
        }
    }

    @Override
    public void onRemoteStreamsAdded(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        if (classRoom.equals(getMainEduRoom())) {
            super.onRemoteStreamsAdded(streamEvents, classRoom);
            roomGroupInfo.membersOnStage(streamEvents);
            boolean needUpdateStudentList = false;
            for (EduStreamEvent streamEvent : streamEvents) {
                EduStreamInfo streamInfo = streamEvent.getModifiedStream();
                EduBaseUserInfo userInfo = streamInfo.getPublisher();
                if (userInfo.getRole().equals(EduUserRole.TEACHER)) {
                    showTeacherStream(streamInfo, videoTeacher.getVideoLayout());
                } else {
                    needUpdateStudentList = updateMemberInfoList(streamInfo, userInfo);
                }
            }
            if (needUpdateStudentList) {
                studentListFragment.updateStudentList(roomGroupInfo.getAllStudent());
            }
            notifyStageVideoList();
        }
    }

    private boolean updateMemberInfoList(EduStreamInfo streamInfo, EduBaseUserInfo userInfo) {
        if (roomGroupInfo.getAllStudent() != null) {
            for (GroupMemberInfo memberInfo : roomGroupInfo.getAllStudent()) {
                if (memberInfo.getUuid().equals(userInfo.getUserUuid())) {
                    memberInfo.setEnableAudio(streamInfo.getHasAudio());
                    memberInfo.setEnableVideo(streamInfo.getHasVideo());
                    memberInfo.setStreamUuid(streamInfo.getStreamUuid());
                    memberInfo.setStreamName(streamInfo.getStreamName());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onRemoteStreamUpdated(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        if (classRoom.equals(getMainEduRoom())) {
            super.onRemoteStreamUpdated(streamEvents, classRoom);
            roomGroupInfo.membersOnStage(streamEvents);
            boolean needUpdateStudentList = false;
            for (EduStreamEvent streamEvent : streamEvents) {
                EduStreamInfo streamInfo = streamEvent.getModifiedStream();
                EduBaseUserInfo userInfo = streamInfo.getPublisher();
                if (userInfo.getRole().equals(EduUserRole.TEACHER)) {
                    showTeacherStream(streamInfo, videoTeacher.getVideoLayout());
                } else {
                    needUpdateStudentList = updateMemberInfoList(streamInfo, userInfo);
                }
            }
            if (needUpdateStudentList) {
                studentListFragment.updateStudentList(roomGroupInfo.getAllStudent());
            }
            notifyStageVideoList();
        }
    }

    @Override
    public void onRemoteStreamsRemoved(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        if (classRoom.equals(getMainEduRoom())) {
            super.onRemoteStreamsRemoved(streamEvents, classRoom);
            roomGroupInfo.membersOffStage(streamEvents);
            notifyStageVideoList();
            boolean needUpdateStudentList = false;
            for (EduStreamEvent streamEvent : streamEvents) {
                EduStreamInfo streamInfo = streamEvent.getModifiedStream();
                EduBaseUserInfo userInfo = streamInfo.getPublisher();
                if (userInfo.getRole().equals(EduUserRole.TEACHER)) {
                    showTeacherStream(streamInfo, null);
                } else {
                    needUpdateStudentList = updateMemberInfoList(streamInfo, userInfo);
                }
            }
            if (needUpdateStudentList) {
                studentListFragment.updateStudentList(roomGroupInfo.getAllStudent());
            }
        }
    }

    @Override
    public void onRoomStatusChanged(@NotNull EduRoomChangeType event, @NotNull EduUserInfo operatorUser, @NotNull EduRoom classRoom) {
        super.onRoomStatusChanged(event, operatorUser, classRoom);
    }

    @Override
    public void onRoomPropertiesChanged(@NotNull EduRoom classRoom, @Nullable Map<String, Object> cause) {
        if (classRoom.equals(getMainEduRoom())) {
            Log.e(TAG, "收到大房间的roomProperty改变的数据");
            initParseBoardInfo(getMainEduRoom());
            Map<String, Object> roomProperties = classRoom.getRoomProperties();
            parseRecordMsg(roomProperties);
            /*处理分组信息*/
            syncRoomGroupProperty(roomProperties);
            if (cause != null && !cause.isEmpty()) {
                int causeType = (int) Float.parseFloat(cause.get(CMD).toString());
                switch (causeType) {
                    case SWITCHGROUP:
                        /*开关分组，*/
                        notifyUserList();
                        break;
                    case UPDATEGROUP:
                        /*分组更新：删除原分组信息*/
                        notifyUserList();
                        break;
                    case SWITCHINTERACTIN:
                        break;
                    case SWITCHINTERACTOUT:
                        /*开关PK，刷新分组列表*/
                        notifyUserList();
                        notifyStageVideoList();
                        break;
                    case GROUPMEDIA:
                        /*开关整组音频*/
                        break;
                    case GROUOREWARD:
                        /*整组奖励，刷新分组列表*/
                        String groupUuid = String.valueOf(cause.get(GROUPUUID));
                        roomGroupInfo.updateRewardByGroup(groupUuid);
                        notifyUserList();
                        notifyStageVideoList();
                        break;
                    case SWITCHCOVIDEO:
                    case SWITCHAUTOCOVIDEO:
                        /*同步举手开关的状态至coVideoView*/
                        parseAgoraActionConfig(getMainEduRoom());
                        agoraCoVideoView.syncCoVideoSwitchState(roomProperties);
                        break;
                    case STUDENTLISTCHANGED:
                        /*学生名单发生变化，刷新名单列表*/
                        notifyUserList();
                        break;
                    case STUDENTREWARD:
                        /*学生个人奖励，刷新分组列表*/
                        String userUuid = String.valueOf(cause.get(USERUUID));
                        roomGroupInfo.updateRewardByUser(userUuid);
                        notifyUserList();
                        notifyStageVideoList();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onNetworkQualityChanged(@NotNull NetworkQuality quality, @NotNull EduUserInfo user, @NotNull EduRoom classRoom) {
        super.onNetworkQualityChanged(quality, user, classRoom);
    }

    @Override
    public void onConnectionStateChanged(@NotNull ConnectionState state, @NotNull EduRoom classRoom) {
        super.onConnectionStateChanged(state, classRoom);
    }

    @Override
    public void onLocalUserUpdated(@NotNull EduUserEvent userEvent, @NotNull EduUserStateChangeType type) {
    }

    private void updateLocalStreamInfo(EduStreamEvent streamEvent) {
        EduStreamInfo streamInfo = streamEvent.getModifiedStream();
        updateMemberInfoList(streamInfo, streamInfo.getPublisher());
        studentListFragment.updateStudentList(roomGroupInfo.getAllStudent());
    }

    @Override
    public void onLocalStreamAdded(@NotNull EduStreamEvent streamEvent) {
        super.onLocalStreamAdded(streamEvent);
        agoraCoVideoView.onLinkMediaChanged(true);
        roomGroupInfo.membersOnStage(Collections.singletonList(streamEvent));
        updateLocalStreamInfo(streamEvent);
        notifyStageVideoList();
        notifyUserList();
    }

    @Override
    public void onLocalStreamUpdated(@NotNull EduStreamEvent streamEvent) {
        super.onLocalStreamUpdated(streamEvent);
        agoraCoVideoView.onLinkMediaChanged(true);
        roomGroupInfo.membersOnStage(Collections.singletonList(streamEvent));
        updateLocalStreamInfo(streamEvent);
        notifyStageVideoList();
        notifyUserList();
    }

    @Override
    public void onLocalStreamRemoved(@NotNull EduStreamEvent streamEvent) {
        super.onLocalStreamRemoved(streamEvent);
        /**本地流被移除，被强制下台
         * 1:同步状态至CoVideoView 2:刷新音视频列表*/
        agoraCoVideoView.onLinkMediaChanged(false);
        roomGroupInfo.membersOffStage(Collections.singletonList(streamEvent));
        notifyStageVideoList();
        updateLocalStreamInfo(streamEvent);
        notifyUserList();
    }

    @Override
    public void onUserActionMessageReceived(@NotNull EduActionMessage actionMessage) {
        super.onUserActionMessageReceived(actionMessage);
    }

    @Override
    public void onUserMessageReceived(@NotNull EduMsg message) {
        super.onUserMessageReceived(message);
        String msg = message.getMessage();
        try {
            JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();
            if (jsonObject.has("cmd") && jsonObject.has("payload")) {
                /**老师邀请学生上麦*/
                PeerMsg peerMsg = new Gson().fromJson(msg, PeerMsg.class);
                if (peerMsg.getCmd() == UnMutePeerCMD) {
                    /**此处偷懒直接用了AgoraCoVideoAction.kt，实际应该再自定义类*/
                    AgoraCoVideoAction action = new Gson().fromJson(peerMsg.getPayloadJson(),
                            AgoraCoVideoAction.class);
                    confirmInvite(action);
                }
            } else if (jsonObject.has("fromUserUuid") && jsonObject.has("processUuid")) {
                /**举手的回调结果*/
//                AgoraActionResBody body = new Gson().fromJson(msg, AgoraActionResBody.class);
//                if (body.getCmd() == ApplyInviteActionCMD) {
//                    actionProcessManager.parseActionMsg(body.getDataJson());
//                }
                actionProcessManager.parseActionMsg(msg);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGlobalStateChanged(GlobalState state) {
        super.onGlobalStateChanged(state);
    }

    @Override
    public void onLocalUserLeft(@NotNull EduUserEvent userEvent, @NotNull EduUserLeftType leftType) {
        if (leftType == EduUserLeftType.KickOff) {
            showRemovedDialog();
        }
    }

    /**
     * 申请/邀请的相关回调
     */
    @Override
    public void onAccept(@NotNull AgoraActionMsgRes actionMsgRes) {
        super.onAccept(actionMsgRes);
        agoraCoVideoView.syncCoVideoAction(actionMsgRes.getPayloadJson());
    }

    @Override
    public void onReject(@NotNull AgoraActionMsgRes actionMsgRes) {
        super.onReject(actionMsgRes);
        agoraCoVideoView.syncCoVideoAction(actionMsgRes.getPayloadJson());
    }

    @Override
    public void onCancel(@NotNull AgoraActionMsgRes actionMsgRes) {
        super.onCancel(actionMsgRes);
        agoraCoVideoView.syncCoVideoAction(actionMsgRes.getPayloadJson());
    }

    /**
     * 举手连麦的相关回调
     */
    @Override
    public void onCoVideoApply() {
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo info) {
                if (info != null) {
                    getTeacher(new EduCallback<EduUserInfo>() {
                        @Override
                        public void onSuccess(@Nullable EduUserInfo teacher) {
                            if (teacher != null && actionConfigs.size() > 0) {
                                getMainEduRoom().getRoomInfo(new EduCallback<EduRoomInfo>() {
                                    @Override
                                    public void onSuccess(@Nullable EduRoomInfo roomInfo) {
                                        if (roomInfo != null) {
                                            AgoraActionConfigInfo config = actionConfigs.get(0);
                                            Map<String, Object> payload = new AgoraCoVideoAction(
                                                    AgoraActionTypeApply.getValue(),
                                                    new AgoraCoVideoFromUser(info.getUserUuid(),
                                                            info.getUserName(), info.getRole().name()),
                                                    new AgoraCoVideoFromRoom(roomInfo.getRoomUuid(),
                                                            roomInfo.getRoomName())).toMap();
                                            AgoraStartActionOptions options = new AgoraStartActionOptions(teacher.getUserUuid(),
                                                    config.processUuid, info.getUserUuid(), payload);
                                            actionProcessManager.startAgoraAction(options, new ThrowableCallback<ResponseBody<String>>() {
                                                @Override
                                                public void onSuccess(@Nullable ResponseBody<String> res) {
                                                    agoraCoVideoView.launchCoVideoApplySuccess();
                                                }

                                                @Override
                                                public void onFailure(@Nullable Throwable throwable) {
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull EduError error) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onCoVideoCancel() {
        getLocalUserInfo(new EduCallback<EduUserInfo>() {
            @Override
            public void onSuccess(@Nullable EduUserInfo info) {
                if (info != null) {
                    getTeacher(new EduCallback<EduUserInfo>() {
                        @Override
                        public void onSuccess(@Nullable EduUserInfo teacher) {
                            if (teacher != null && actionConfigs.size() > 0) {
                                getMainEduRoom().getRoomInfo(new EduCallback<EduRoomInfo>() {
                                    @Override
                                    public void onSuccess(@Nullable EduRoomInfo roomInfo) {
                                        if (roomInfo != null) {
                                            AgoraActionConfigInfo config = actionConfigs.get(0);
                                            Map<String, Object> payload = new AgoraCoVideoAction(
                                                    AgoraActionTypeCancel.getValue(),
                                                    new AgoraCoVideoFromUser(info.getUserUuid(),
                                                            info.getUserName(), info.getRole().name()),
                                                    new AgoraCoVideoFromRoom(roomInfo.getRoomUuid(),
                                                            roomInfo.getRoomName())).toMap();
                                            AgoraStopActionOptions options = new AgoraStopActionOptions(
                                                    teacher.getUserUuid(), config.processUuid,
                                                    AgoraActionTypeCancel.getValue(), info.getUserUuid(),
                                                    payload, DISABLE.getValue());
                                            actionProcessManager.stopAgoraAction(options, new ThrowableCallback<ResponseBody<String>>() {
                                                @Override
                                                public void onSuccess(@Nullable ResponseBody<String> res) {
                                                    agoraCoVideoView.launchCoVideoCancelSuccess();
                                                }

                                                @Override
                                                public void onFailure(@Nullable Throwable throwable) {
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull EduError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {

            }
        });
    }

    @Override
    public void onCoVideoAborted() {

    }

    @Override
    public void onCoVideoAccepted() {
        /*如果老师打开了举手即上台则学生需要自己发流*/
        if (agoraCoVideoView.isAutoCoVideo()) {
            Log.e(TAG, "autoCoVideo is enable");
            getLocalUser(new EduCallback<EduUser>() {
                @Override
                public void onSuccess(@Nullable EduUser localUser) {
                    if (localUser != null) {
                        EduLocalUserInfo userInfo = localUser.getUserInfo();
                        /**判断是否已经在台上，如果已经在台上则不进行操作*/
                        if (!roomGroupInfo.isOnStage(userInfo.getUserUuid())) {
                            LocalStreamInitOptions options = new LocalStreamInitOptions(userInfo.streamUuid,
                                    false, true);
                            localUser.initOrUpdateLocalStream(options, new EduCallback<EduStreamInfo>() {
                                @Override
                                public void onSuccess(@Nullable EduStreamInfo streamInfo) {
                                    if (streamInfo != null) {
                                        localUser.publishStream(streamInfo, new EduCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(@Nullable Boolean res) {
                                            }

                                            @Override
                                            public void onFailure(@NotNull EduError error) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull EduError error) {
                                }
                            });
                        } else {
                            Log.e(TAG, "curUser is already onstage, do nothing.");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull EduError error) {

                }
            });
        }
    }

    @Override
    public void onCoVideoRejected() {

    }
}
