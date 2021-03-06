package com.danasoft.spendtrak;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.FormatterClosedException;
import java.util.Locale;

public class TextUtils {
    private static final int BEFORE = -1;
    private static final int SAME = 0;
    private static final int AFTER = 1;
    private static NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
    // All transactions are created with a timestamp
    private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat time_stamp_format =
            new SimpleDateFormat(TIME_STAMP_FORMAT, Locale.getDefault());

    private static final String MAIN_DATE_FORMAT = "E dd-MMM";
    private static DateFormat main_date_format =
            new SimpleDateFormat(MAIN_DATE_FORMAT, Locale.getDefault());

    private static final String TRACK_VIEW_DATE_FORMAT = "dd-MMM";
    private static DateFormat track_view_date_format =
            new SimpleDateFormat(TRACK_VIEW_DATE_FORMAT, Locale.getDefault());
    private static final String TRACK_VIEW_TIME_FORMAT = "HH:mm";
    private static DateFormat track_view_time_format =
            new SimpleDateFormat(TRACK_VIEW_TIME_FORMAT, Locale.getDefault());

    public static String getDateFromTimestamp(long timeStamp) {
        return track_view_date_format.format(new Date(timeStamp));
    }

    public static String getTimeFromTimestamp(long timeStamp) {
        return track_view_time_format.format(new Date(timeStamp));
    }
    @Contract("null -> null")
    public static String getTrackViewTime(String timeStamp) {
        if (timeStamp != null) {
            try {
                Date d = time_stamp_format.parse(timeStamp);
                return track_view_time_format.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }
    public static String getFormattedCurrencyString(double amount) {
        try {
            return format.format(amount / 100d);
        } catch (FormatterClosedException e) {
            e.printStackTrace();
        }
        return " ";
    }
    public static double cleanCurrencyInput(@NotNull String currencyInput) {
        try {
            return Double.parseDouble(cleanString(currencyInput));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    @NotNull public static String cleanString(@NotNull String dirtyString) {
        return dirtyString.replaceAll("[$,.]", "");
    }
    public static String getDate() { return main_date_format.format(new Date()); }

    @Nullable
    public static Calendar cleanDateInput(@NotNull String dateInput) {
        if (dateInput.isEmpty()) return null;
        String[] parts = dateInput.split("-");
        Calendar retVal = Calendar.getInstance();
        retVal.set(Integer.decode(parts[0]), Integer.decode(parts[1]), Integer.decode(parts[2]));
        return retVal;
    }
}