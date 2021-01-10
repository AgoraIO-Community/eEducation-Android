package io.agora.education;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.agora.edu.common.bean.ResponseBody;
import io.agora.edu.launch.AgoraEduClassRoom;
import io.agora.edu.launch.AgoraEduReplay;
import io.agora.edu.launch.AgoraEduReplayConfig;
import io.agora.edu.launch.AgoraEduRoleType;
import io.agora.edu.launch.AgoraEduRoomType;
import io.agora.edu.launch.AgoraEduSDK;
import io.agora.edu.launch.AgoraEduLaunchConfig;
import io.agora.edu.launch.AgoraEduSDKConfig;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;

import static io.agora.edu.BuildConfig.API_BASE_URL;
import static io.agora.edu.launch.AgoraEduSDK.REQUEST_CODE_RTC;
import static io.agora.education.Constants.KEY_SP;
import static io.agora.education.EduApplication.getAppId;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.et_room_name)
    protected EditText et_room_name;
    @BindView(R.id.et_your_name)
    protected EditText et_your_name;
    @BindView(R.id.et_room_type)
    protected EditText et_room_type;
    @BindView(R.id.card_room_type)
    protected CardView card_room_type;
    @BindView(R.id.btn_join)
    protected Button btnJoin;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int eyeProtect = PreferenceManager.get(KEY_SP, false) ? 1 : 0;
        AgoraEduSDK.setAgoraEduSDKConfig(new AgoraEduSDKConfig(getAppId(), eyeProtect));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.no_enough_permissions, Toast.LENGTH_SHORT).show();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null && requestCode == REQUEST_CODE_RTE && resultCode == BaseClassActivity.RESULT_CODE) {
//            int code = data.getIntExtra(CODE, -1);
//            String reason = data.getStringExtra(REASON);
//            String msg = String.format(getString(R.string.function_error), code, reason);
//            Log.e(TAG, msg);
//            ToastManager.showShort(msg);
//        }
//    }

    @OnClick({R.id.iv_setting, R.id.et_room_type, R.id.btn_join, R.id.tv_one2one, R.id.tv_small_class,
            R.id.tv_large_class, R.id.tv_breakout_class, R.id.tv_intermediate_class})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.btn_join:
//                AgoraEduReplayConfig config = new AgoraEduReplayConfig(this, 1610194790798L, 1610194828584L,
//                        "scenario/recording/f488493d1886435f963dfb3d95984fd4/5ff99f6612c83b045ed90495/6b6c425515445a49a26e29aa7a828f33_1235542.m3u8",
//                        "646/P8Kb7e_DJZVAQw", "4c25df50526f11eb881eb12e7d1eca69", "WHITEcGFydG5lcl9pZD0xTnd5aDBsMW9ZazhaRWNuZG1kaWgwcmJjVWVsQnE1UkpPMVMmc2lnPTcwODlkYzdjNzA2YTFmMjZkZDdlMmEyYWI0YjFhMzQ4MDQ4YzY2N2Y6YWs9MU53eWgwbDFvWWs4WkVjbmRtZGloMHJiY1VlbEJxNVJKTzFTJmNyZWF0ZV90aW1lPTE2MTAxOTIzNTM5OTAmZXhwaXJlX3RpbWU9MTY0MTcyODM1Mzk5MCZub25jZT0xNjEwMTkyMzUzOTkwMDAmcm9sZT1yb29tJnJvb21JZD00YzI1ZGY1MDUyNmYxMWViODgxZWIxMmU3ZDFlY2E2OSZ0ZWFtSWQ9NjQ2");
//                AgoraEduReplay replay = AgoraEduSDK.replay(config, state -> {
//                    Log.e(TAG, "replay-课堂状态:" + state.name());
//                });
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(10000);
//                        Log.e(TAG, "replay-主动自动结束课堂");
//                        replay.destroy();
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }).start();
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
        notifyBtnJoinEnable(false);

        String roomNameStr = et_room_name.getText().toString();
        if (TextUtils.isEmpty(roomNameStr)) {
            Toast.makeText(this, R.string.room_name_should_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        String yourNameStr = et_your_name.getText().toString();
        if (TextUtils.isEmpty(yourNameStr)) {
            Toast.makeText(this, R.string.your_name_should_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        String roomTypeStr = et_room_type.getText().toString();
        if (TextUtils.isEmpty(roomTypeStr)) {
            Toast.makeText(this, R.string.room_type_should_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        /**userUuid和roomUuid需用户自己指定，并保证唯一性*/
        int roomType = getClassType(roomTypeStr);
        String userUuid = yourNameStr + AgoraEduRoleType.AgoraEduRoleTypeStudent.getValue();
        String roomUuid = roomNameStr + roomType;

        fetchToken(getAppId(), roomUuid, userUuid, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notifyBtnJoinEnable(true);
                Log.e(TAG, "fetchToken onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                ResponseBody<String> res = new Gson().fromJson(body, new TypeToken<ResponseBody<String>>() {
                }.getType());
                if (res != null && !TextUtils.isEmpty(res.data)) {
                    Log.d(TAG, "fetchToken onResponse:" + body);
                    runOnUiThread(() -> {
                        AgoraEduLaunchConfig agoraEduLaunchConfig = new AgoraEduLaunchConfig(
                                MainActivity.this, yourNameStr, userUuid, roomNameStr, roomUuid,
                                roomType, res.data);
                        AgoraEduClassRoom classRoom = AgoraEduSDK.launch(agoraEduLaunchConfig, (state) -> {
                            Log.e(TAG, "launch-课堂状态:" + state.name());
                            notifyBtnJoinEnable(true);
                        });
//        new Thread(() -> {
//            try {
//                Thread.sleep(10000);
//                Log.e(TAG, "launch-主动自动结束课堂");
//                classRoom.destroy();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
                    });
                } else {
                    Log.e(TAG, "fetchToken onFailure: response data is empty!");
                }
            }
        });
    }

    private int getClassType(String roomTypeStr) {
        if (roomTypeStr.equals(getString(R.string.one2one_class))) {
            return AgoraEduRoomType.AgoraEduRoomType1V1.getValue();
        } else if (roomTypeStr.equals(getString(R.string.small_class))) {
            return AgoraEduRoomType.AgoraEduRoomTypeSmall.getValue();
        } else {
            return AgoraEduRoomType.AgoraEduRoomTypeBig.getValue();
        }
    }

    private void fetchToken(String appId, String roomUuid, String userUuid, Callback callback) {
        String url = API_BASE_URL + "/token/apps/" + appId + "/rooms/" + roomUuid +
                "/roles/2/users/" + userUuid;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }

    private void notifyBtnJoinEnable(boolean enable) {
        runOnUiThread(() -> {
            if (btnJoin != null) {
                btnJoin.setEnabled(enable);
            }
        });
    }

}
