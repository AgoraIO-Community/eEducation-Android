package io.agora.education;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.agora.base.ToastManager;
import io.agora.base.callback.ThrowableCallback;
import io.agora.base.network.BusinessException;
import io.agora.base.network.RetrofitManager;
import io.agora.education.api.EduCallback;
import io.agora.education.api.base.EduError;
import io.agora.education.api.manager.EduManager;
import io.agora.education.api.manager.EduManagerOptions;
import io.agora.education.api.room.data.RoomCreateOptions;
import io.agora.education.api.room.data.RoomType;
import io.agora.education.api.statistics.AgoraError;
import io.agora.education.api.user.data.EduUserRole;
import io.agora.education.base.BaseActivity;
import io.agora.education.classroom.BaseClassActivity;
import io.agora.education.classroom.BaseClassActivity_bak;
import io.agora.education.classroom.BreakoutClassActivity;
import io.agora.education.classroom.MediumClassActivity;
import io.agora.education.classroom.LargeClassActivity;
import io.agora.education.classroom.OneToOneClassActivity;
import io.agora.education.classroom.SmallClassActivity;
import io.agora.education.classroom.bean.channel.Room;
import io.agora.education.service.CommonService;
import io.agora.education.service.bean.ResponseBody;
import io.agora.education.service.bean.request.RoomCreateOptionsReq;
import io.agora.education.util.AppUtil;
import io.agora.education.widget.PolicyDialog;

import static io.agora.education.EduApplication.getAppId;
import static io.agora.education.EduApplication.getCustomerCer;
import static io.agora.education.EduApplication.getCustomerId;
import static io.agora.education.EduApplication.setManager;
import static io.agora.education.api.BuildConfig.API_BASE_URL;
import static io.agora.education.classroom.BaseClassActivity.RESULT_CODE;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private final int REQUEST_CODE_RTC = 101;
    public final static int REQUEST_CODE_RTE = 909;
    public static final String CODE = "code";
    public static final String REASON = "reason";

    @BindView(R.id.et_room_name)
    protected EditText et_room_name;
    @BindView(R.id.et_your_name)
    protected EditText et_your_name;
    @BindView(R.id.et_room_type)
    protected EditText et_room_type;
    @BindView(R.id.card_room_type)
    protected CardView card_room_type;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        new PolicyDialog().show(getSupportFragmentManager(), null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                ToastManager.showShort(R.string.no_enough_permissions);
                return;
            }
        }
        switch (requestCode) {
            case REQUEST_CODE_RTC:
                start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == REQUEST_CODE_RTE && resultCode == RESULT_CODE) {
            int code = data.getIntExtra(CODE, -1);
            String reason = data.getStringExtra(REASON);
            ToastManager.showShort(String.format(getString(R.string.function_error), code, reason));
        }
    }

    @OnClick({R.id.iv_setting, R.id.et_room_type, R.id.btn_join, R.id.tv_one2one, R.id.tv_small_class,
            R.id.tv_large_class, R.id.tv_breakout_class, R.id.tv_intermediate_class})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.btn_join:
                if (AppUtil.isFastClick()) {
                    return;
                }
                if (AppUtil.checkAndRequestAppPermission(this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE_RTC)) {
                    start();
                }
                break;
            case R.id.tv_one2one:
                et_room_type.setText(R.string.one2one_class);
                card_room_type.setVisibility(View.GONE);
                break;
            case R.id.tv_small_class:
                et_room_type.setText(R.string.small_class);
                card_room_type.setVisibility(View.GONE);
                break;
            case R.id.tv_large_class:
                et_room_type.setText(R.string.large_class);
                card_room_type.setVisibility(View.GONE);
                break;
            case R.id.tv_breakout_class:
                et_room_type.setText(R.string.breakout);
                card_room_type.setVisibility(View.GONE);
                break;
            case R.id.tv_intermediate_class:
                et_room_type.setText(R.string.intermediate);
                card_room_type.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @OnTouch(R.id.et_room_type)
    public void onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (card_room_type.getVisibility() == View.GONE) {
                card_room_type.setVisibility(View.VISIBLE);
            } else {
                card_room_type.setVisibility(View.GONE);
            }
        }
    }

    private void start() {
        String roomNameStr = et_room_name.getText().toString();
        if (TextUtils.isEmpty(roomNameStr)) {
            ToastManager.showShort(R.string.room_name_should_not_be_empty);
            return;
        }

        String yourNameStr = et_your_name.getText().toString();
        if (TextUtils.isEmpty(yourNameStr)) {
            ToastManager.showShort(R.string.your_name_should_not_be_empty);
            return;
        }

        String roomTypeStr = et_room_type.getText().toString();
        if (TextUtils.isEmpty(roomTypeStr)) {
            ToastManager.showShort(R.string.room_type_should_not_be_empty);
            return;
        }

        /**userUuid和roomUuid需用户自己指定，并保证唯一性*/
        int roomType = getClassType(roomTypeStr);
        String userUuid = yourNameStr + EduUserRole.STUDENT.getValue();
        String roomUuid = roomNameStr + roomType;

        assert getAppId() != null;
        assert getCustomerId() != null;
        assert getCustomerCer() != null;
        EduManagerOptions options = new EduManagerOptions(this, getAppId(), getCustomerId(),
                getCustomerCer(), userUuid, yourNameStr);
        options.setLogFileDir(getCacheDir().getAbsolutePath());
        EduManager.init(options, new EduCallback<EduManager>() {
            @Override
            public void onSuccess(@Nullable EduManager res) {
                if (res != null) {
                    Log.e(TAG, "初始化EduManager成功");
                    setManager(res);
                    createRoom(yourNameStr, userUuid, roomNameStr, roomUuid, roomType);
                }
            }

            @Override
            public void onFailure(@NotNull EduError error) {
                Log.e(TAG, "初始化EduManager失败->code:" + error.getType() + ",reason:" + error.getMsg());
            }
        });
    }

    @Room.Type
    private int getClassType(String roomTypeStr) {
        if (roomTypeStr.equals(getString(R.string.one2one_class))) {
            return RoomType.ONE_ON_ONE.getValue();
        } else if (roomTypeStr.equals(getString(R.string.small_class))) {
            return RoomType.SMALL_CLASS.getValue();
        } else if (roomTypeStr.equals(getString(R.string.large_class))) {
            return RoomType.LARGE_CLASS.getValue();
        } else if (roomTypeStr.equals(getString(R.string.breakout))) {
            return RoomType.BREAKOUT_CLASS.getValue();
        } else {
            return RoomType.INTERMEDIATE_CLASS.getValue();
//            return RoomType.LARGE_CLASS.getValue();
        }
    }

    private void createRoom(String yourNameStr, String yourUuid, String roomNameStr, String roomUuid, int roomType) {
        /**createClassroom时，room不存在则新建，存在则返回room信息(此接口非必须调用)，
         * 只要保证在调用joinClassroom之前，classroom在服务端存在即可*/
        RoomCreateOptions options = new RoomCreateOptions(roomUuid, roomNameStr, roomType);
        Log.e(TAG, "调用scheduleClass函数");

        RoomCreateOptionsReq optionsReq = RoomCreateOptionsReq.convertRoomCreateOptions(options);
        RetrofitManager.instance().getService(API_BASE_URL, CommonService.class)
                .createClassroom(getAppId(), options.getRoomUuid(), optionsReq)
                .enqueue(new RetrofitManager.Callback<>(0, new ThrowableCallback<ResponseBody<String>>() {
                    @Override
                    public void onSuccess(@Nullable ResponseBody<String> res) {
                        Log.e(TAG, "调用scheduleClass函数成功");
                        Intent intent = createIntent(yourNameStr, yourUuid, roomNameStr, roomUuid, roomType);
                        startActivityForResult(intent, REQUEST_CODE_RTE);
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
                            Intent intent = createIntent(yourNameStr, yourUuid, roomNameStr, roomUuid, roomType);
                            startActivityForResult(intent, REQUEST_CODE_RTE);
                        } else {
                            Log.e(TAG, "排课失败");
                        }
                    }
                }));
    }

    private Intent createIntent(String yourNameStr, String yourUuid, String roomNameStr,
                                String roomUuid, @Room.Type int roomType) {
        RoomEntry roomEntry = new RoomEntry(yourNameStr, yourUuid, roomNameStr, roomUuid, roomType);

        Intent intent = new Intent();
        if (roomType == RoomType.ONE_ON_ONE.getValue()) {
            intent.setClass(this, OneToOneClassActivity.class);
        } else if (roomType == RoomType.SMALL_CLASS.getValue()) {
            intent.setClass(this, SmallClassActivity.class);
        } else if (roomType == RoomType.LARGE_CLASS.getValue()) {
            intent.setClass(this, LargeClassActivity.class);
        } else if (roomType == RoomType.BREAKOUT_CLASS.getValue()) {
            intent.setClass(this, BreakoutClassActivity.class);
        } else if (roomType == RoomType.INTERMEDIATE_CLASS.getValue()) {
            intent.setClass(this, MediumClassActivity.class);
        }
        intent.putExtra(BaseClassActivity_bak.ROOMENTRY, roomEntry);
        return intent;
    }

}
