package com.vereskul.tc51versusxml.data.network


import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter: JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(format.format(src))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return LocalDateTime.parse(json?.asString, format)
    }
}