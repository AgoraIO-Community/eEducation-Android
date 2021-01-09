package io.agora.edu.common.service;

import io.agora.edu.common.bean.ResponseBody;
import io.agora.edu.common.bean.board.BoardBean;
import io.agora.education.impl.user.data.request.EduRoomChatMsgReq;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    @POST("/edu/apps/{appId}/v2/rooms/{roomUuid}/chat")
    Call<ResponseBody<BoardBean>> roomChat(
            @Path("appId") String appId,
            @Path("roomUuid") String roomUuid,
            @Body EduRoomChatMsgReq req);
}
