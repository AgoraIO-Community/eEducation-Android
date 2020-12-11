package io.agora.education.impl.stream.data.request

import io.agora.education.api.stream.data.AudioSourceType
import io.agora.education.api.stream.data.VideoSourceType


/**@param streamUuid 同一appId下流的唯一id，rtc中uid*/
class EduUpsertStreamReq constructor(var userUuid: String,
                                     var streamUuid: String,
                                     var streamName: String?,
                                     var videoSourceType: VideoSourceType,
                                     var videoState: Int, var audioState: Int) {
    var audioSourceType: Int = AudioSourceType.MICROPHONE.value

}