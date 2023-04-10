package com.zhulin.guideshop.core.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object DateUtils {
    fun String.formatDateTime(): LocalDateTime {
        val time = ZonedDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime()
        val oldZone = ZoneId.of("UTC")
        val newZone = ZoneId.systemDefault()
        return time.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime()
    }

    fun LocalDateTime.toFormat(): String {
        return this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }
}