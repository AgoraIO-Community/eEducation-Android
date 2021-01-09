package io.agora.edu.launch;

import org.jetbrains.annotations.NotNull;

public class AgoraEduSDKConfig {
    @NotNull
    private String appId;
    private int openEyeCare;

    public AgoraEduSDKConfig(@NotNull String appId, int openEyeCare) {
        this.appId = appId;
        this.openEyeCare = openEyeCare;
    }

    @NotNull
    public String getAppId() {
        return appId;
    }

    public void setAppId(@NotNull String appId) {
        this.appId = appId;
    }

    public int getOpenEyeCare() {
        return openEyeCare;
    }

    public void setOpenEyeCare(int openEyeCare) {
        this.openEyeCare = openEyeCare;
    }
}
