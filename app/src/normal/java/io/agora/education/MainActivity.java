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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.agora.edu.launch.LaunchCallback;
import io.agora.education.api.EduCallback;
import io.agora.education.api.base.EduError;
import io.agora.edu.launch.EduLaunch;
import io.agora.edu.launch.EduLaunchConfig;

import static io.agora.edu.launch.EduLaunch.REQUEST_CODE_RTC;
import static io.agora.education.Constants.KEY_SP;
import static io.agora.education.EduApplication.getAppId;
import static io.agora.education.EduApplication.getCustomerCer;
import static io.agora.education.EduApplication.getCustomerId;

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
        String userUuid = yourNameStr + UserRole.STUDENT;
        String roomUuid = roomNameStr + roomType;

        int eyeProtect = PreferenceManager.get(KEY_SP, false) ? 1 : 0;
        String whiteBoardAppId = getString(R.string.whiteboard_app_id);

        assert getAppId() != null;
        assert getCustomerId() != null;
        assert getCustomerCer() != null;
        assert !TextUtils.isEmpty(whiteBoardAppId);
        EduLaunchConfig eduLaunchConfig = new EduLaunchConfig(this, whiteBoardAppId, eyeProtect,
                yourNameStr, userUuid, roomNameStr, roomUuid, roomType, getAppId(), getCustomerId(),
                getCustomerCer(), "");
        EduLaunch.launch(eduLaunchConfig, () -> {
            Log.e(TAG, "启动课堂完成");
            notifyBtnJoinEnable(true);
        });
    }

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
            return RoomType.MEDIUM_CLASS.getValue();
        }
    }

    private void notifyBtnJoinEnable(boolean enable) {
        runOnUiThread(() -> {
            if (btnJoin != null) {
                btnJoin.setEnabled(enable);
            }
        });
    }

}
