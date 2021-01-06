package io.agora.education;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.agora.edu.base.BaseActivity;
import io.agora.edu.widget.EyeProtection;
import io.agora.edu.BuildConfig;

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    @BindView(R.id.switch_eye_care)
    protected Switch switch_eye_care;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        switch_eye_care.setChecked(EyeProtection.isNeedShow());
    }

    @OnClick({R.id.iv_back, R.id.layout_policy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.layout_policy:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.POLICY_URL));
                startActivity(intent);
                break;
            case R.id.layout_log:
//                /**有bug,暂时隐藏不使用*/
//                /**上传log*/
//                getManager().uploadDebugItem(DebugItem.LOG, new EduCallback<String>() {
//                    @Override
//                    public void onSuccess(@Nullable String res) {
//                        Log.e(TAG, "日志上传成功->" + res);
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull EduError error) {
//                        Log.e(TAG, "日志上传错误->code:" + error.getType() + ", reason:" + error.getMsg());
//                    }
//                });
                break;
        }
    }

    @OnCheckedChanged(R.id.switch_eye_care)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        EyeProtection.setNeedShow(isChecked);
        if (isChecked) {
            showEyeProtection();
        } else {
            dismissEyeProtection();
        }
    }

}
