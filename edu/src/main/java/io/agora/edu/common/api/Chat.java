package io.agora.edu.common.api;

import io.agora.education.api.EduCallback;
import io.agora.education.api.message.EduChatMsg;

public interface Chat {
    void roomChat(String message, EduCallback<EduChatMsg> callback);
}
