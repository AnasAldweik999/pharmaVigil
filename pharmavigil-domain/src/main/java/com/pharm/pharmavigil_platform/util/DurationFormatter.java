package com.pharm.pharmavigil_platform.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts between a total-minutes count (how duration is stored) and the "HH:MM" wire format
 * (how duration is submitted/returned), with unbounded hours, e.g. 7392 minutes -> "123:12".
 * Injected as a plain collaborator rather than called as a static utility.
 */
public class DurationFormatter {

    private final Pattern pattern = Pattern.compile("^(\\d+):([0-5]\\d)$");

    public boolean isValidFormat(String hhmm) {
        return hhmm != null && pattern.matcher(hhmm).matches();
    }

    public long toMinutes(String hhmm) {
        Matcher matcher = pattern.matcher(hhmm == null ? "" : hhmm);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: " + hhmm);
        }
        long hours = Long.parseLong(matcher.group(1));
        long minutes = Long.parseLong(matcher.group(2));
        return hours * 60 + minutes;
    }

    public String toHHMM(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours + ":" + (minutes < 10 ? "0" + minutes : String.valueOf(minutes));
    }
}
