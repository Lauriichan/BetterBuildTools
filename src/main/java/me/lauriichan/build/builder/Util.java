package me.lauriichan.build.builder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Util {

    private Util() {
        throw new UnsupportedOperationException();
    }

    public static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d*");

    public static boolean isNumeric(final String msg) {
        return NUMBER_PATTERN.matcher(msg).matches();
    }

    public static boolean isVersion(final String msg) {
        if (!msg.contains(".")) {
            return isNumeric(msg);
        }
        final String[] parts = msg.split("\\.");
        if (parts.length > 3) {
            return false;
        }
        for (final String part : parts) {
            if (!isNumeric(part)) {
                return false;
            }
        }
        return true;
    }

    public static int sortVersion(final String v1, final String v2) {
        if ("latest".equals(v1)) {
            return 1;
        }
        if (!v1.contains(".")) {
            if (v2.contains(".")) {
                return 1;
            }
            return Integer.compare(Integer.parseInt(v1), Integer.parseInt(v2));
        }
        if (!v2.contains(".")) {
            return -1;
        }
        final int[] vi1 = readVersion(v1);
        final int[] vi2 = readVersion(v2);
        int cmp = Integer.compare(vi1[0], vi2[0]);
        if ((cmp != 0) || ((cmp = Integer.compare(vi1[1], vi2[1])) != 0)) {
            return -cmp;
        }
        return -Integer.compare(vi1[2], vi2[2]);
    }

    public static int[] readVersion(final String version) {
        final int[] out = {
            0,
            0,
            0
        };
        final String[] parts = version.split("\\.");
        out[0] = Integer.parseInt(parts[0]);
        out[1] = Integer.parseInt(parts[1]);
        if (parts.length == 3) {
            out[2] = Integer.parseInt(parts[2]);
        }
        return out;
    }

    public static String downloadAsString(final URL url) {
        if (url == null) {
            return null;
        }
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (final Exception exp) {
            return null;
        }
    }

}
