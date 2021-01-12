package io.agora.edu.launch;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.agora.base.PreferenceManager;
import io.agora.base.ToastManager;
import io.agora.base.network.RetrofitManager;
import io.agora.base.util.CryptoUtil;
import io.agora.edu.classroom.ReplayActivity;
import io.agora.edu.common.api.RoomPre;
import io.agora.edu.common.bean.request.RoomPreCheckReq;
import io.agora.edu.common.bean.response.EduRemoteConfigRes;
import io.agora.edu.common.bean.response.RoomPreCheckRes;
import io.agora.edu.common.impl.RoomPreImpl;
import io.agora.education.api.EduCallback;
import io.agora.education.api.base.EduError;
import io.agora.education.api.manager.EduManager;
import io.agora.education.api.manager.EduManagerOptions;
import io.agora.education.api.room.data.EduRoomState;
import io.agora.education.api.room.data.RoomType;
import io.agora.edu.classroom.BaseClassActivity;
import io.agora.edu.classroom.BreakoutClassActivity;
import io.agora.edu.classroom.LargeClassActivity;
import io.agora.edu.classroom.MediumClassActivity;
import io.agora.edu.classroom.OneToOneClassActivity;
import io.agora.edu.classroom.SmallClassActivity;
import kotlin.text.Charsets;

import static io.agora.edu.classroom.BaseClassActivity.setEduManager;

public class AgoraEduSDK {
    private static final String TAG = "EduLaunch";

    public static final int REQUEST_CODE_RTC = 101;
    public static final int REQUEST_CODE_RTE = 909;
    public static final String CODE = "code";
    public static final String REASON = "reason";
    public static AgoraEduLaunchCallback agoraEduLaunchCallback = state -> Log.e(TAG, "This is the default null implementation!");
    private static RoomPre roomPre;
    private static AgoraEduSDKConfig agoraEduSDKConfig;
    private static final AgoraEduClassRoom classRoom = new AgoraEduClassRoom();
    private static final AgoraEduReplay replay = new AgoraEduReplay();
    private static ActivityLifecycleListener classRoomListener = new ActivityLifecycleListener() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            classRoom.add(activity);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            classRoom.updateState(AgoraEduEvent.AgoraEduEventDestroyed);
        }
    };
    private static ActivityLifecycleListener replayListener = new ActivityLifecycleListener() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            replay.add(activity);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            replay.updateState(AgoraEduEvent.AgoraEduEventDestroyed);
            agoraEduLaunchCallback.onCallback(AgoraEduEvent.AgoraEduEventDestroyed);
        }
    };

    public static String version() {
        return EduManager.Companion.version();
    }

    public static void setAgoraEduSDKConfig(AgoraEduSDKConfig agoraEduSDKConfig) {
        AgoraEduSDK.agoraEduSDKConfig = agoraEduSDKConfig;
    }

    public static AgoraEduClassRoom launch(@NotNull AgoraEduLaunchConfig config,
                                           @NotNull AgoraEduLaunchCallback callback)
            throws IllegalStateException {
        if (!classRoom.isReady()) {
            throw new IllegalStateException("curState is not AgoraEduEventDestroyed, launch() cannot be called");
        }

//        ((Application) config.getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(replayListener);
        ((Application) config.getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(classRoomListener);
        ((Application) config.getContext().getApplicationContext()).registerActivityLifecycleCallbacks(classRoomListener);

        agoraEduLaunchCallback = state -> {
            callback.onCallback(state);
            classRoom.updateState(state);
        };
        ToastManager.init(config.getContext().getApplicationContext());
        PreferenceManager.init(config.getContext().getApplicationContext());

        /**step-0:get agoraEduSDKConfig and to configure*/
        if (agoraEduSDKConfig == null) {
            Log.e(TAG, "agoraEduSDKConfig is null!");
            return null;
        }
        config.setAppId(agoraEduSDKConfig.getAppId());
        config.setOpenEyeCare(agoraEduSDKConfig.getOpenEyeCare());
        RetrofitManager.instance().addHeader("x-agora-token", config.getToken());
        RetrofitManager.instance().addHeader("x-agora-uid", config.getUserUuid());

        /**step-1:pull remote config*/
        roomPre = new RoomPreImpl(config.getAppId(), config.getRoomUuid());
        roomPre.pullRemoteConfig(new EduCallback<EduRemoteConfigRes>() {
            @Override
            public void onSuccess(@Nullable EduRemoteConfigRes res) {
                config.setCustomerId(res.getCustomerId());
                config.setCustomerCer(res.getCustomerCertificate());
                /**为OKHttp添加Authorization的header*/
                String auth = Base64.encodeToString((config.getCustomerId() + ":" + config.getCustomerCer())
                        .getBytes(Charsets.UTF_8), Base64.DEFAULT).replace("\n", "").trim();
                RetrofitManager.instance().addHeader("Authorization", CryptoUtil.getAuth(auth));
                EduRemoteConfigRes.NetLessConfig netLessConfig = res.getNetless();
                config.setWhiteBoardAppId(netLessConfig.getAppId());
                /**step-2:check classRoom and init EduManager*/
                checkAndInit(config);
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                String msg = "pullRemoteConfig failed->code:" + error.getType() + ",msg:" + error.getMsg();
                errorTips(config.getContext(), msg);
            }
        });

        return classRoom;
    }

    private static void checkAndInit(@NotNull AgoraEduLaunchConfig config) {
        RoomPreCheckReq req = new RoomPreCheckReq(config.getRoomName(), config.getRoomType());
        roomPre.preCheckClassRoom(req, new EduCallback<RoomPreCheckRes>() {
            @Override
            public void onSuccess(@Nullable RoomPreCheckRes res) {
                if (res.getState() != EduRoomState.END.getValue()) {
                    EduManagerOptions options = new EduManagerOptions(config.getContext(), config.getAppId(),
                            config.getCustomerId(), config.getCustomerCer(), config.getUserUuid(),
                            config.getUserName());
                    options.setLogFileDir(config.getContext().getCacheDir().getAbsolutePath());
                    EduManager.init(options, new EduCallback<EduManager>() {
                        @Override
                        public void onSuccess(@Nullable EduManager res) {
                            if (res != null) {
                                Log.e(TAG, "初始化EduManager成功");
                                setEduManager(res);
                                Intent intent = createIntent(config);
                                ((Activity) config.getContext()).startActivityForResult(intent, REQUEST_CODE_RTE);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull EduError error) {
                            String msg = "初始化EduManager失败->code:" + error.getType() + ",reason:" + error.getMsg();
                            errorTips(config.getContext(), msg);
                        }
                    });
                } else {
                    String msg = "Room is End!";
                    errorTips(config.getContext(), msg);
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                String msg = "preCheckClassRoom failed->code:" + error.getType() + ",msg:" + error.getMsg();
                errorTips(config.getContext(), msg);
            }
        });
    }

    private static Intent createIntent(AgoraEduLaunchConfig config) {
        Intent intent = new Intent();
        int roomType = config.getRoomType();
        if (roomType == RoomType.ONE_ON_ONE.getValue()) {
            intent.setClass(config.getContext(), OneToOneClassActivity.class);
        } else if (roomType == RoomType.SMALL_CLASS.getValue()) {
            intent.setClass(config.getContext(), SmallClassActivity.class);
        } else if (roomType == RoomType.LARGE_CLASS.getValue()) {
            intent.setClass(config.getContext(), LargeClassActivity.class);
        } else if (roomType == RoomType.BREAKOUT_CLASS.getValue()) {
            intent.setClass(config.getContext(), BreakoutClassActivity.class);
        } else if (roomType == RoomType.MEDIUM_CLASS.getValue()) {
            intent.setClass(config.getContext(), MediumClassActivity.class);
        }
        intent.putExtra(BaseClassActivity.LAUNCHCONFIG, config);
        return intent;
    }

    private static void errorTips(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
        agoraEduLaunchCallback.onCallback(AgoraEduEvent.AgoraEduEventDestroyed);
    }


    public static final String WHITEBOARD_APP_ID = "whiteboardAppId";
    public static final String WHITEBOARD_START_TIME = "whiteboardStartTime";
    public static final String WHITEBOARD_END_TIME = "whiteboardEndTime";
    public static final String VIDEO_URL = "videoURL";
    public static final String WHITEBOARD_ID = "whiteboardId";
    public static final String WHITEBOARD_TOKEN = "whiteboardToken";

    public static AgoraEduReplay replay(AgoraEduReplayConfig config, @NotNull AgoraEduLaunchCallback callback)
            throws IllegalStateException {
        if (!replay.isReady()) {
            throw new IllegalStateException("curState is not AgoraEduEventDestroyed, replay() cannot be called");
        }

        ((Application) config.getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(replayListener);
//        ((Application) config.getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(classRoomListener);
        ((Application) config.getContext().getApplicationContext()).registerActivityLifecycleCallbacks(replayListener);

        agoraEduLaunchCallback = callback;
        ToastManager.init(config.getContext().getApplicationContext());
        PreferenceManager.init(config.getContext().getApplicationContext());

        Intent intent = new Intent(config.getContext(), ReplayActivity.class);
        intent.putExtra(WHITEBOARD_APP_ID, config.getWhiteBoardAppId());
        intent.putExtra(WHITEBOARD_START_TIME, config.getBeginTime());
        intent.putExtra(WHITEBOARD_END_TIME, config.getEndTime());
        intent.putExtra(VIDEO_URL, config.getVideoUrl());
        intent.putExtra(WHITEBOARD_ID, config.getWhiteBoardId());
        intent.putExtra(WHITEBOARD_TOKEN, config.getWhiteBoardToken());
        config.getContext().startActivity(intent);
        replay.updateState(AgoraEduEvent.AgoraEduEventReady);
        agoraEduLaunchCallback.onCallback(AgoraEduEvent.AgoraEduEventReady);
        return replay;
    }
}
