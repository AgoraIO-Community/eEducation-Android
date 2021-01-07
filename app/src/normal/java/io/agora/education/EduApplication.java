package io.agora.education;

import android.app.Application;

import androidx.annotation.Nullable;

import com.tencent.bugly.crashreport.CrashReport;

public class EduApplication extends Application {
    private static final String TAG = "EduApplication";

    public static EduApplication instance;

    private AppConfig config;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        PreferenceManager.init(this);

        CrashReport.initCrashReport(getApplicationContext(), "04948355be", true);

        String appId, customerId, customerCertificate;
        appId = getString(R.string.agora_app_id);
        customerId = getString(R.string.agora_customer_id);
        customerCertificate = getString(R.string.agora_customer_cer);
        setAppId(appId);
        setCustomerId(customerId);
        setCustomerCer(customerCertificate);
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
            instance.config = new AppConfig();
        }
        instance.config.appId = appId;
    }

    public static void setCustomerId(String customerId) {
        if (instance.config == null) {
            instance.config = new AppConfig();
        }
        instance.config.customerId = customerId;
    }

    public static void setCustomerCer(String customerCer) {
        if (instance.config == null) {
            instance.config = new AppConfig();
        }
        instance.config.customerCer = customerCer;
    }

}
