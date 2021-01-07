package io.agora.edu.launch;

import android.content.Intent;

import io.agora.edu.classroom.ReplayActivity;

public class ReplayLaunch {
    public static final String WHITEBOARD_APP_ID = "whiteboardAppId";
    public static final String WHITEBOARD_ROOM_ID = "whiteboardRoomId";
    public static final String WHITEBOARD_START_TIME = "whiteboardStartTime";
    public static final String WHITEBOARD_END_TIME = "whiteboardEndTime";
    public static final String WHITEBOARD_URL = "whiteboardUrl";
    public static final String WHITEBOARD_ID = "whiteboardId";
    public static final String WHITEBOARD_TOKEN = "whiteboardToken";

    public static void replay(ReplayLaunchConfig config) {
        Intent intent = new Intent(config.getContext(), ReplayActivity.class);
        intent.putExtra(WHITEBOARD_APP_ID, config.getWhiteBoardAppId());
        intent.putExtra(WHITEBOARD_ROOM_ID, config.getRoomUuid());
        intent.putExtra(WHITEBOARD_START_TIME, config.getWhiteBoardStartTime());
        intent.putExtra(WHITEBOARD_END_TIME, config.getWhiteBoardEndTime());
        intent.putExtra(WHITEBOARD_URL, config.getWhiteBoardUrl());
        intent.putExtra(WHITEBOARD_ID, config.getWhiteBoardId());
        intent.putExtra(WHITEBOARD_TOKEN, config.getWhiteBoardToken());
        config.getContext().startActivity(intent);
    }
}
