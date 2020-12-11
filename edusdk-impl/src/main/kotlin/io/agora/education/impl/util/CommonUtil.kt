package io.agora.education.impl.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.agora.education.api.room.EduRoom
import io.agora.education.api.room.data.RoomType
import io.agora.education.impl.BuildConfig
import io.agora.education.impl.room.EduRoomImpl
import io.agora.education.impl.room.data.EduRoomInfoImpl

internal class CommonUtil {
    companion object {
        fun buildRtcOptionalInfo(tag: Int?): String {
            tag?.let {
                val info = JsonObject()
                info.addProperty("demo_ver", BuildConfig.VERSION_NAME)
                when (tag) {
                    RoomType.ONE_ON_ONE.value -> {
                        info.addProperty("demo_scenario", "One on One Classroom")
                    }
                    RoomType.SMALL_CLASS.value -> {
                        info.addProperty("demo_scenario", "Small Classroom")
                    }
                    RoomType.LARGE_CLASS.value -> {
                        info.addProperty("demo_scenario", "Lecture Hall")
                    }
                    RoomType.BREAKOUT_CLASS.value -> {
                        info.addProperty("demo_scenario", "Breakout Classroom")
                    }
                    RoomType.INTERMEDIATE_CLASS.value -> {
                        info.addProperty("demo_scenario", "Intermediate Classroom")
                    }
                }
                return Gson().toJson(info)
            }
            return ""
        }
    }
}