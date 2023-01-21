package me.lauriichan.build.builder.ui.util;

public final class TimeHelper {

    public static double SECOND_RATIO = 1000000000;
    public static double MILLIS_RATIO = 1000000;

    private TimeHelper() {}

    public static double nanoAsSecond(final long nano) {
        return nano / SECOND_RATIO;
    }

    public static double nanoAsMillis(final long nano) {
        return nano / MILLIS_RATIO;
    }

}
