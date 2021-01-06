package io.agora.education;

import android.app.Application;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;

import io.agora.base.PreferenceManager;
import io.agora.base.ToastManager;
import io.agora.base.network.RetrofitManager;
import io.agora.base.util.CryptoUtil;
import io.agora.edu.service.bean.response.AppConfigRes;
import kotlin.text.Charsets;

public class EduApplication extends Application {
    private static final String TAG = "EduApplication";

    public static EduApplication instance;

    private AppConfigRes config;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        CrashReport.initCrashReport(getApplicationContext(), "04948355be", true);
        PreferenceManager.init(this);
        ToastManager.init(this);

        String appId, customerId, customerCertificate;
        appId = getString(R.string.agora_app_id);
        customerId = getString(R.string.agora_customer_id);
        customerCertificate = getString(R.string.agora_customer_cer);
        setAppId(appId);
        setCustomerId(customerId);
        setCustomerCer(customerCertificate);
        /**为OKHttp添加Authorization的header*/
        String auth = Base64.encodeToString((customerId + ":" + customerCertificate)
                .getBytes(Charsets.UTF_8), Base64.DEFAULT).replace("\n", "").trim();
        RetrofitManager.instance().addHeader("Authorization", CryptoUtil.getAuth(auth));
    }

    @Nullable
    public static String getAppId() {
        if (instance.config == null) {
            return null;
        }
        return instance.config.appId;
    }

    @Nullable
    public static String getCustomerId() {
        if (instance.config == null) {
            return null;
        }
        return instance.config.customerId;
    }

    @Nullable
    public static String getCustomerCer() {
        if (instance.config == null) {
            return null;
        }
        return instance.config.customerCer;
    }

    public static void setAppId(String appId) {
        if (instance.config == null) {
            instance.config = new AppConfigRes();
        }
        instance.config.appId = appId;
    }

    public static void setCustomerId(String customerId) {
        if (instance.config == null) {
            instance.config = new AppConfigRes();
        }
        instance.config.customerId = customerId;
    }

    public static void setCustomerCer(String customerCer) {
        if (instance.config == null) {
            instance.config = new AppConfigRes();
        }
        instance.config.customerCer = customerCer;
    }

    @Nullable
    public static Map<String, Map<Integer, String>> getMultiLanguage() {
        if (instance.config == null) {
            return null;
        }
        return instance.config.multiLanguage;
    }

}
