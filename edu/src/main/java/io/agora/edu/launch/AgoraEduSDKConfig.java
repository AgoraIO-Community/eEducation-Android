package io.agora.edu.launch;

import org.jetbrains.annotations.NotNull;

public class AgoraEduSDKConfig {
    @NotNull
    private String appId;
    private int eyeCare;

    public AgoraEduSDKConfig(@NotNull String appId, int eyeCare) {
        this.appId = appId;
        this.eyeCare = eyeCare;
    }

    @NotNull
    public String getAppId() {
        return appId;
    }

    public void setAppId(@NotNull String appId) {
        this.appId = appId;
    }

    public int getEyeCare() {
        return eyeCare;
    }

    public void setEyeCare(int eyeCare) {
        this.eyeCare = eyeCare;
    }
}
