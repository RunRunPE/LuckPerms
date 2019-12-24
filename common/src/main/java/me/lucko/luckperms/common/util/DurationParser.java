/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses durations from a string format
 */
public final class DurationParser {
    private DurationParser() {}

    private static final ChronoUnit[] UNITS = new ChronoUnit[]{
            ChronoUnit.YEARS,
            ChronoUnit.MONTHS,
            ChronoUnit.WEEKS,
            ChronoUnit.DAYS,
            ChronoUnit.HOURS,
            ChronoUnit.MINUTES,
            ChronoUnit.SECONDS
    };

    private static final String PATTERN_STRING = Arrays.stream(UNITS)
            .limit(UNITS.length - 1) // skip seconds
            .map(unit -> {
                if (unit == ChronoUnit.MONTHS) {
                    return "mo";
                }
                // use the first char as the abbreviation
                return String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
            })
            .map(abbreviation -> "(?:([0-9]+)\\s*" + abbreviation + "[a-z]*[,\\s]*)?")
            .collect(Collectors.joining())
            .concat("(?:([0-9]+)\\s*(?:s[a-z]*)?)?");

    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);

    public static Duration parseDuration(String input) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(input);

        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }

            Duration duration = Duration.ZERO;
            for (int i = 0; i < UNITS.length; i++) {
                ChronoUnit unit = UNITS[i];
                int g = i + 1;

                if (matcher.group(g) != null && !matcher.group(g).isEmpty()) {
                    int n = Integer.parseInt(matcher.group(g));
                    duration = duration.plus(n, unit);
                }
            }

            return duration;
        }

        throw new IllegalArgumentException("unable to parse duration: " + input);
    }

}