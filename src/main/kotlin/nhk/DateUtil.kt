package nhk

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object DateUtil {
    fun nhkDateToUtc(nhkDate: Date): Calendar {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE_UTC))
        calendar.time = nhkDate

        return calendar
    }
}