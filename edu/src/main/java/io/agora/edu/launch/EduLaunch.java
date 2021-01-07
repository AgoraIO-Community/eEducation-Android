package io.agora.edu.launch;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.agora.base.PreferenceManager;
import io.agora.base.ToastManager;
import io.agora.base.callback.ThrowableCallback;
import io.agora.base.network.BusinessException;
import io.agora.base.network.RetrofitManager;
import io.agora.base.util.CryptoUtil;
import io.agora.education.api.EduCallback;
import io.agora.education.api.base.EduError;
import io.agora.education.api.manager.EduManager;
import io.agora.education.api.manager.EduManagerOptions;
import io.agora.education.api.room.data.RoomCreateOptions;
import io.agora.education.api.room.data.RoomType;
import io.agora.education.api.statistics.AgoraError;
import io.agora.edu.classroom.BaseClassActivity;
import io.agora.edu.classroom.BreakoutClassActivity;
import io.agora.edu.classroom.LargeClassActivity;
import io.agora.edu.classroom.MediumClassActivity;
import io.agora.edu.classroom.OneToOneClassActivity;
import io.agora.edu.classroom.SmallClassActivity;
import io.agora.edu.service.CommonService;
import io.agora.edu.service.bean.ResponseBody;
import io.agora.edu.service.bean.request.RoomCreateOptionsReq;
import kotlin.text.Charsets;

import static io.agora.edu.BuildConfig.API_BASE_URL;
import static io.agora.edu.classroom.BaseClassActivity.setEduManager;

public class EduLaunch {
    private static final String TAG = "EduLaunch";

    public static final int REQUEST_CODE_RTC = 101;
    public static final int REQUEST_CODE_RTE = 909;
    public static final String CODE = "code";
    public static final String REASON = "reason";
    public static LaunchCallback launchCallback;

    public static void launch(EduLaunchConfig config, LaunchCallback callback) {
        launchCallback = callback;
        ToastManager.init(config.getContext().getApplicationContext());
        PreferenceManager.init(config.getContext().getApplicationContext());
        /**为OKHttp添加Authorization的header*/
        String auth = Base64.encodeToString((config.getCustomerId() + ":" + config.getCustomerCer())
                .getBytes(Charsets.UTF_8), Base64.DEFAULT).replace("\n", "").trim();
        RetrofitManager.instance().addHeader("Authorization", CryptoUtil.getAuth(auth));

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
                    createRoom(config, launchCallback);
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                Log.e(TAG, "初始化EduManager失败->code:" + error.getType() + ",reason:" + error.getMsg());
                launchCallback.onComplete();
            }
        });
    }

    public static String version() {
        return EduManager.Companion.version();
    }

    private static void createRoom(EduLaunchConfig config, LaunchCallback callback) {
        /**createClassroom时，room不存在则新建，存在则返回room信息(此接口非必须调用)，
         * 只要保证在调用joinClassroom之前，classroom在服务端存在即可*/
        RoomCreateOptions options = new RoomCreateOptions(config.getRoomUuid(), config.getRoomName(),
                config.getRoomType());
        Log.e(TAG, "调用scheduleClass函数");
        RoomCreateOptionsReq optionsReq = RoomCreateOptionsReq.convertRoomCreateOptions(options);
        RetrofitManager.instance().getService(API_BASE_URL, CommonService.class)
                .createClassroom(config.getAppId(), options.getRoomUuid(), optionsReq)
                .enqueue(new RetrofitManager.Callback<>(0, new ThrowableCallback<ResponseBody<String>>() {
                    @Override
                    public void onSuccess(@Nullable ResponseBody<String> res) {
                        Log.e(TAG, "调用scheduleClass函数成功");
                        Intent intent = createIntent(config);
                        ((Activity) config.getContext()).startActivityForResult(intent, REQUEST_CODE_RTE);
                    }

                    @Override
                    public void onFailure(@Nullable Throwable throwable) {
                        BusinessException error;
                        if (throwable instanceof BusinessException) {
                            error = (BusinessException) throwable;
                        } else {
                            error = new BusinessException(throwable.getMessage());
                        }
                        Log.e(TAG, "调用scheduleClass函数失败->" + error.getCode() + ", reason:" +
                                error.getMessage());
                        if (error.getCode() == AgoraError.ROOM_ALREADY_EXISTS.getValue()) {
                            Intent intent = createIntent(config);
                            ((Activity) config.getContext()).startActivityForResult(intent, REQUEST_CODE_RTE);
                        } else {
                            Log.e(TAG, "排课失败");
                            callback.onComplete();
                        }
                    }
                }));
    }

    private static Intent createIntent(EduLaunchConfig config) {
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
}
