package de.stryi.uhrzeitansage.model

data class DayOfWeekInfo(
    val dayNumber: Int, // 1 = Montag, 7 = Sonntag
    val shortName: String,
    val fullName: String
) {
    companion object {
        val ALL_DAYS = listOf(
            DayOfWeekInfo(1, "Mo", "Montag"),
            DayOfWeekInfo(2, "Di", "Dienstag"),
            DayOfWeekInfo(3, "Mi", "Mittwoch"),
            DayOfWeekInfo(4, "Do", "Donnerstag"),
            DayOfWeekInfo(5, "Fr", "Freitag"),
            DayOfWeekInfo(6, "Sa", "Samstag"),
            DayOfWeekInfo(7, "So", "Sonntag")
        )

        fun getShortName(dayNumber: Int): String {
            return ALL_DAYS.find { it.dayNumber == dayNumber }?.shortName ?: ""
        }

        fun getFullName(dayNumber: Int): String {
            return ALL_DAYS.find { it.dayNumber == dayNumber }?.fullName ?: ""
        }

        fun fromCalendarDayOfWeek(calendarDay: Int): Int {
            return when (calendarDay) {
                java.util.Calendar.MONDAY -> 1
                java.util.Calendar.TUESDAY -> 2
                java.util.Calendar.WEDNESDAY -> 3
                java.util.Calendar.THURSDAY -> 4
                java.util.Calendar.FRIDAY -> 5
                java.util.Calendar.SATURDAY -> 6
                java.util.Calendar.SUNDAY -> 7
                else -> 1
            }
        }
    }
}
