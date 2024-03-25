package com.example.chatapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.chatapplication.R
import com.example.chatapplication.model.entity.MediaStore
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


object TimeUtils {
    const val FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss"
    private const val FORMAT_CLIENT = "HH:mm dd-MM-yyyy"
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss"
    private const val TIME_MINUS_FORMAT = "HH:mm"

    //Tính toán thời gian của thông báo
    fun formatDatetimeNotification(context: Context, date: String): String {
        return try {
            val past = formatDateFromString(date)
            val now = Date()
            val calendar: Calendar = GregorianCalendar()
            calendar.time = past!!
            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time) //giây
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time) //phút
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time) //giờ
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time) // ngày
            val year = days / 365 //năm
            if (seconds < 60) {
                context.getString(R.string.finish_now)
            } else if (minutes < 60) {
                String.format(context.getString(R.string.minute_ago_notify), minutes)
            } else if (hours < 24) {
                String.format(context.getString(R.string.hour_ago_notify), hours)
            } else if (days < 8) {
                String.format(context.getString(R.string.day_ago_notify), days)
            } else if (days in 8..365) {
                String.format(
                    "%s/%s lúc %s:%s",
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    String.format("%02d", calendar.get(Calendar.MINUTE))
                )
            } else if (year > 1) {
                String.format(
                    "%s/%s/%s lúc %s:%s",
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    String.format("%02d", calendar.get(Calendar.MINUTE))
                )
            } else {
                date
            }
        } catch (j: Exception) {
            date
        }
    }

    //Tính thời gian user chat online
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeUserOnlineStatus(context: Context, timestampStr: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val timestamp = LocalDateTime.parse(timestampStr, formatter).toEpochSecond(ZoneOffset.UTC)
            val currentTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            val timeDifference = currentTimestamp - timestamp

            return when {
                timeDifference < 60 -> "hoạt động $timeDifference giây trước"
                timeDifference < 3600 -> "hoạt động ${timeDifference / 60} phút trước"
                timeDifference < 86400 -> "hoạt động ${timeDifference / 3600} giờ trước"
                else -> {
                    val daysAgo = timeDifference / 86400
                    "hoạt động $daysAgo ngày trước"
                }
            }
        } catch (j: Exception) {
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeUserOnlineStatus2(context: Context, timestampStr: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val timestamp = LocalDateTime.parse(timestampStr, formatter).toEpochSecond(ZoneOffset.UTC)
            val currentTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            val timeDifference = currentTimestamp - timestamp

            return when {
                timeDifference < 60 -> "$timeDifference giây trước"
                timeDifference < 3600 -> "${timeDifference / 60} phút trước"
                timeDifference < 86400 -> "${timeDifference / 3600} giờ trước"
                timeDifference < DateUtils.WEEK_IN_MILLIS / 1000 -> {
                    // Display the day of the week
                    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
                    val dayOfWeek = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("vi"))
                    "$dayOfWeek"
                }
                else -> {
                    // Display the specific date
                    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
                    val dayOfMonth = dateTime.dayOfMonth
                    val month = dateTime.month.getDisplayName(TextStyle.FULL, Locale("vi"))
                    "$dayOfMonth $month"
                }
            }
        } catch (j: Exception) {
            ""
        }
    }

    //Tính toán thời gian last message
    fun formatDatetimeChat(context: Context, date: String): String {
        return try {
            val past = formatDateFromString(date)
            val now = Date()
            val calendar: Calendar = GregorianCalendar()
            calendar.time = past!!
            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time) //giây
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time) //phút
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time) //giờ
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time) // ngày
            val year = days / 365 //năm
            if (seconds < 60) {
                context.getString(R.string.finish_now)
            } else if (minutes < 60) {
                String.format(context.getString(R.string.minute_ago), minutes)
            } else if (hours < 24) {
                String.format(context.getString(R.string.hour_ago), hours)
            } else if (days < 8) {
                String.format(context.getString(R.string.day_ago), days)
            } else if (days in 8..365) {
                String.format(
                    "%s/%s",
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1)
                )
            } else if (year > 1) {
                String.format(
                    "%s/%s/%s",
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                    calendar.get(Calendar.YEAR)
                )
            } else {
                date
            }
        } catch (j: Exception) {
            date
        }
    }

    //Tính toán thời gian bình luận
    fun formatDatetimeComment(context: Context, date: String): String {
        return try {
            val past = formatDateFromString(date)
            val now = Date()
            val calendar: Calendar = GregorianCalendar()
            calendar.time = past!!
            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time) //giây
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time) //phút
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time) //giờ
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time) // ngày
            val year = days / 365 //năm
            if (seconds < 60) {
                context.getString(R.string.finish_now)
            } else if (minutes < 60) {
                String.format(context.getString(R.string.minute_ago), minutes)
            } else if (hours < 24) {
                String.format(context.getString(R.string.hour_ago), hours)
            } else if (days < 8) {
                String.format(context.getString(R.string.day_ago), days)
            } else if (days in 8..364) {
                String.format("%s %s", days / 7, "Tuần")
            } else if (year >= 1) {
                String.format("%s %s", year, "Năm")
            } else {
                date
            }
        } catch (j: Exception) {
            date
        }
    }

    //Sử dụng ở các bài post(timeline)
    fun formatDatetimePost(context: Context, date: String): String {
        return try {
            val past = formatDateFromString(date)
            val now = Date()
            val calendar: Calendar = GregorianCalendar()
            calendar.time = past!!
            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time) //giây
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time) //phút
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time) //giờ
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time) // ngày
            val year = days / 365 //năm
            if (seconds < 60) {
                context.getString(R.string.finish_now)
            } else if (minutes < 60) {
                String.format(context.getString(R.string.minute_ago), minutes)
            } else if (hours < 24) {
                String.format(context.getString(R.string.hour_ago), hours)
            } else if (days < 8) {
                String.format(context.getString(R.string.day_ago), days)
            } else if (days in 8..365) {
                String.format(
                    "%s tháng %s lúc %s:%s",
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    String.format("%02d", calendar.get(Calendar.MINUTE))
                )
            } else if (year > 1) {
                String.format(
                    "%s tháng %s, %s lúc %s:%s",
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)),
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    String.format("%02d", calendar.get(Calendar.MINUTE)),
                )
            } else {
                date
            }
        } catch (j: Exception) {
            date
        }
    }

    fun formatDatetimeAgo(context: Context, date: String): String {
        return try {
            val past = formatDateFromString(date)
            val now = Date()
            val calendar: Calendar = GregorianCalendar()
            calendar.time = past!!
            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time) //giây
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time) //phút
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time) //giờ
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time) // ngày
            val year = days / 365 //năm
            if (seconds < 60) {
                context.getString(R.string.finish_now)
            } else if (minutes < 60) {
                String.format(context.getString(R.string.minute_ago), minutes)
            } else if (hours < 24) {
                String.format(context.getString(R.string.hour_ago), hours)
            } else if (days < 8) {
                String.format(context.getString(R.string.day_ago), days)
            } else if (days in 8..365) {
                String.format(
                    context.getString(R.string.month_ago),
                    String.format("%02d", calendar[Calendar.DAY_OF_MONTH]),
                    String.format("%02d", calendar[Calendar.MONTH] + 1)
                )
            } else if (year > 1) {
                String.format(
                    context.getString(R.string.year_ago),
                    calendar[Calendar.DAY_OF_MONTH],
                    calendar[Calendar.MONTH] + 1,
                    calendar[Calendar.YEAR]
                )
            } else {
                date
            }
        } catch (j: java.lang.Exception) {
            date
        }
    }

    //Format thời gian trong bảng tin nhóm
    fun formatDateTimeGroupNews(date: String): String {
        return try {
            val calendar: Calendar = GregorianCalendar()
            calendar.time = formatDateFromString(date)!!
            val minutes = String.format("%02d", calendar.get(Calendar.MINUTE))
            val hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
            val days = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
            val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            val year: Int = calendar.get(Calendar.YEAR)
            "$days/$month/$year lúc $hours:$minutes"
        } catch (e: Exception) {
            date
        }
    }

    //Format thời gian trong Bình chọn
    fun formatDateTimeVote(date: String, isEnd: Boolean): String {
        return try {
            val calendar: Calendar = GregorianCalendar()
            calendar.time = formatDateFromString(date)!!
            val minutes = String.format("%02d", calendar.get(Calendar.MINUTE))
            val hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
            val days = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
            val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            val year: Int = calendar.get(Calendar.YEAR)
            if (isEnd) {
                "Đã kết thúc vào $days/$month/$year lúc $hours:$minutes"
            } else {
                "Kết thúc vào $days/$month/$year lúc $hours:$minutes"
            }
        } catch (e: Exception) {
            date
        }
    }

    //Format thời gian của sự kiện của group OA
    fun formatDateTimeOAEvent(date: String): String {
        return try {
            val calendar: Calendar = GregorianCalendar()
            calendar.time = formatDateFromString(date)!!
            val minutes = String.format("%02d", calendar.get(Calendar.MINUTE))
            val hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
            val days = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
            val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            val year: Int = calendar.get(Calendar.YEAR)
            "$hours:$minutes $days/$month/$year"
        } catch (e: Exception) {
            date
        }
    }

    //Format thời gian 1 cụm chat trong danh sách tin nhắn
    @SuppressLint("SimpleDateFormat")
    fun getToDayMessage(strDate: String): String {
        try {
            val dateTime = formatDateFromString(strDate)!!

            val dayFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dayFormat.format(dateTime)

            val timeFormat = SimpleDateFormat("HH:mm")
            val time = timeFormat.format(dateTime)

            val currentDate = dayFormat.format(Date())
            return if (currentDate == date) {
                String.format(
                    "%s%s%s", time, ", ", "Hôm nay"
                )
            } else {
                String.format(
                    "%s%s%s", time, ", ", date
                )
            }
        } catch (ex: Exception) {
            return strDate
        }
    }

    //Thời gian của tin nhắn
    @SuppressLint("SimpleDateFormat")
    fun getTimeMessage(strDate: String): String {
        return try {
            val format = SimpleDateFormat(DATE_TIME_FORMAT)
            val time = format.parse(changeFormatTimeMessageChat(strDate))
            val newFormat = SimpleDateFormat(TIME_MINUS_FORMAT)
            newFormat.format(time!!)
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    //Format thời gian thời gian event bắt đầu của OA
    @SuppressLint("SimpleDateFormat")
    fun formatDateTimeStartEvent(strDate: String): String {
        try {
            val dateTime = getDateFromString(
                strDate, "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm", isTimeZoneUTC = false
            )

            val dayFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dayFormat.format(dateTime)

            val timeFormat = SimpleDateFormat("HH:mm")
            val time = timeFormat.format(dateTime)

            return "$time ngày $date"
        } catch (ex: Exception) {
            return strDate
        }
    }

    //Format thời gian 1 cụm media trong media store
    @SuppressLint("SimpleDateFormat")
    fun formatTimeGroupMediaStore(strDate: String): String {
        return try {
            val dateTime = formatDateFromString(strDate)!!

            val dayFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dayFormat.format(dateTime)

            val currentDate = dayFormat.format(Date())
            if (currentDate == date) {
                "Hôm nay"
            } else {
                "Ngày $date"
            }
        } catch (ex: Exception) {
            strDate
        }
    }

    /**
     * Format Datetime của server
     */
    @SuppressLint("SimpleDateFormat")
    fun formatDateFromString(dateTimeString: String): Date? {
        //Thời gian trên server là múi giờ 0 và theo định dạng "yyyy-MM-dd HH:mm:ss"
        val oldDateFormat = SimpleDateFormat(FORMAT_SERVER)
        oldDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        //Thời gian dưới client là múi giờ hiện tại và theo định dạng "HH:mm dd-MM-yyyy"
        val newDateFormat = SimpleDateFormat(FORMAT_CLIENT)
        newDateFormat.timeZone = TimeZone.getDefault()

        val originalDate = oldDateFormat.parse(dateTimeString)//Chuyển thời gian trên qua kiểu Date
        val newDateTimeString =
            originalDate?.let { newDateFormat.format(it) }//format lại theo kiểu dưới client
        return newDateTimeString?.let { newDateFormat.parse(it) }//Sau khi format thì parse ra kiểu Date lại
    }

    //Tính toán thời gian chat
    @SuppressLint("SimpleDateFormat")
    fun changeFormatTimeMessageChat(strDate: String): String {
        val of = SimpleDateFormat(FORMAT_SERVER)
        of.timeZone = TimeZone.getTimeZone("UTC")

        val NEW_FORMAT = "dd/MM/yyyy HH:mm:ss"
        val af = SimpleDateFormat(NEW_FORMAT)
        af.timeZone = TimeZone.getDefault()

        return try {
            val dateTime = of.parse(strDate)
            af.format(dateTime!!)
        } catch (e: java.lang.Exception) {
            strDate
        }
    }

    /**
     * Lấy thời gian hiện tại theo múi giờ hiện tại theo format mong muốn
     * Tham số pattern là format mong muốn (VD: dd/MM/yyyy HH:mm:ss)
     */
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTimeFormat(pattern: String, isTimeZoneUTC: Boolean): String {
        val dateFormat = SimpleDateFormat(pattern)
        dateFormat.timeZone =
            if (isTimeZoneUTC) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
        return dateFormat.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateFromString(
        dateTime: String, oldFormat: String, newFormat: String, isTimeZoneUTC: Boolean
    ): Date {
        val oldDateFormat = SimpleDateFormat(oldFormat)
        oldDateFormat.timeZone =
            if (isTimeZoneUTC) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
        val newDateFormat = SimpleDateFormat(newFormat)
        newDateFormat.timeZone = TimeZone.getDefault()
        return try {
            val originalDate = oldDateFormat.parse(dateTime)
            val newDateTimeString = newDateFormat.format(originalDate!!)
            newDateFormat.parse(newDateTimeString)!!
        } catch (e: Exception) {
            Date()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun changeDateStringToUTCDateString(
        dateTime: String, oldFormat: String, newFormat: String
    ): String {
        val oldDateFormat = SimpleDateFormat(oldFormat)
        oldDateFormat.timeZone = TimeZone.getDefault()
        val newDateFormat = SimpleDateFormat(newFormat)
        newDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = oldDateFormat.parse(dateTime)
            newDateFormat.format(date!!)
        } catch (e: Exception) {
            newDateFormat.format(Date())
        }
    }

    fun calculateTimeWithNowInHours(
        dateTime: String,
        oldFormat: String,
        newFormat: String,
        isTimeZoneUTC: Boolean,
        hoursParam: Int
    ): Boolean {
        val date = getDateFromString(dateTime, oldFormat, newFormat, isTimeZoneUTC)
        val now = Calendar.getInstance(TimeZone.getDefault())
        val hours = TimeUnit.MILLISECONDS.toHours(now.time.time - date.time) //giờ
        return hours < hoursParam
    }

    fun checkDateTimeEqualOrMoreThanCurrentDateTime(
        dateTime: String, format: String, isTimeZoneUTC: Boolean
    ): Boolean {
        val currentDateTime = Calendar.getInstance(TimeZone.getDefault())
        val dateTimeInput = getDateFromString(dateTime, format, format, isTimeZoneUTC)
        return currentDateTime.time >= dateTimeInput
    }

    fun showOrHideTimeHeader(
        position: Int, timeMessage: TextView, data: ArrayList<MediaStore>
    ) {
        if (data.size > 1) {
            if (position != 0 && position < data.size - 1) {//Nằm ở giữa
                if (formatTimeGroupMediaStore(data[position].createdAt) != formatTimeGroupMediaStore(
                        data[position - 1].createdAt
                    )
                ) {//Phía trước ko cùng ngày
                    timeMessage.show()
                    timeMessage.text = formatTimeGroupMediaStore(data[position].createdAt)
                } else {
                    timeMessage.hide()
                }
            } else if (position == 0) {//Nằm ở đầu
                timeMessage.show()
                timeMessage.text = formatTimeGroupMediaStore(data[position].createdAt)
            } else {//Nằm ở cuối
                if (formatTimeGroupMediaStore(data[position].createdAt) == formatTimeGroupMediaStore(
                        data[position - 1].createdAt
                    )
                ) {
                    timeMessage.hide()
                } else {
                    timeMessage.show()
                    timeMessage.text = formatTimeGroupMediaStore(data[position].createdAt)
                }
            }
        } else {
            timeMessage.show()
            timeMessage.text = formatTimeGroupMediaStore(data[position].createdAt)
        }
    }

    fun getDateForPass(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("ddMMyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateAndTime(date: String): Pair<String, String> {
        var dateTime = Date()
        if (date != "") {
            dateTime = formatDateFromString(date)!!
        }
        val dayFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateNew = dayFormat.format(dateTime)
        val timeFormat = SimpleDateFormat("HH:mm")
        val time = timeFormat.format(dateTime)
        return Pair(time, dateNew)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateAndTime2(date: String): Pair<String, String> {
        var dateTime = Date()
        if (date != "") {
            dateTime = convertStringToDate(date)!!
        }
        val dayFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateNew = dayFormat.format(dateTime)
        val timeFormat = SimpleDateFormat("HH:mm")
        val time = timeFormat.format(dateTime)
        return Pair(time, dateNew)
    }

    private fun convertStringToDate(dateString: String): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}