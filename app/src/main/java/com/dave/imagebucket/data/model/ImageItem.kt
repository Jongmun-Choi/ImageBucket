package com.dave.imagebucket.data.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ImageItem(
    val thumbnailUrl: String,
    val dateTime: String,
    val isSaved: Boolean = false
) {
    fun getFormattedDateTime(): String {
        val zonedDateTime = ZonedDateTime.parse(dateTime)

        // 3. 원하는 출력 형식을 정의
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

        // 4. ZonedDateTime 객체를 원하는 형식의 문자열로 변환
        return zonedDateTime.format(outputFormatter)
    }
}