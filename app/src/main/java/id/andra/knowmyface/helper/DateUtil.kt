package id.andra.knowmyface.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {

    private val locale = Locale("id", "ID")

    fun getCurrentDateTime(pattern: String = "yyyy-MM-dd_HH-mm"): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat(pattern, locale)
        return simpleDateFormat.format(date)
    }

}