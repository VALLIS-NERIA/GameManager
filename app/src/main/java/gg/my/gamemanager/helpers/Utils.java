package gg.my.gamemanager.helpers;

import java.util.Calendar;

public class Utils {
    public static String formatDate(Calendar date) {
        return String.format("%d-%d-%d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }
}
