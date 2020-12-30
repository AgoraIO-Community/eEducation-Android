package io.agora.education.classroom.bean.group

import io.agora.education.api.stream.data.EduStreamInfo

data class StageStreamInfo(var streamInfo: EduStreamInfo, val groupUuid: String?, var reward: Int)