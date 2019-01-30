package nhk

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

object DateUtil {
    fun nhkDateToUtc(nhkDate: Date): ZonedDateTime {
        return ZonedDateTime.ofInstant(nhkDate.toInstant(), ZoneId.of(Constants.TIME_ZONE_UTC))
    }
}