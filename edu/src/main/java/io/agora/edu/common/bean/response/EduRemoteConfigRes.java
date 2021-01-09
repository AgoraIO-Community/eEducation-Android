package io.agora.edu.common.bean.response;

public class EduRemoteConfigRes {
    private String customerId;
    private String customerCertificate;
    private Object theme;
    private NetLessConfig netless;

    public EduRemoteConfigRes(String customerId, String customerCertificate, Object theme, NetLessConfig netless) {
        this.customerId = customerId;
        this.customerCertificate = customerCertificate;
        this.theme = theme;
        this.netless = netless;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCertificate() {
        return customerCertificate;
    }

    public void setCustomerCertificate(String customerCertificate) {
        this.customerCertificate = customerCertificate;
    }

    public Object getTheme() {
        return theme;
    }

    public void setTheme(Object theme) {
        this.theme = theme;
    }

    public NetLessConfig getNetless() {
        return netless;
    }

    public void setNetless(NetLessConfig netless) {
        this.netless = netless;
    }


    public class NetLessConfig {
        private String appId;
        private Object oss;

        public NetLessConfig(String appId, Object oss) {
            this.appId = appId;
            this.oss = oss;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public Object getOss() {
            return oss;
        }

        public void setOss(Object oss) {
            this.oss = oss;
        }
    }
}
