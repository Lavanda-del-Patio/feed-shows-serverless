package es.lavanda.feed.show.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SeasonsChaptersUtil {

    private static final Pattern PATTERN_CHAPTERS = Pattern.compile("(\\d{1,2})(\\d{2})");
    private static final Pattern PATTERN_CHAPTERS_DOUBLE = Pattern.compile("(\\d{1,2})(\\d{2})_(\\d{1,2})(\\d{2})");

    public static Map<Integer, List<Integer>> getSeasonsChapters(String text) {
        Map<Integer, List<Integer>> seasonChapters = new HashMap<>();
        Matcher matcherDate3 = PATTERN_CHAPTERS_DOUBLE.matcher(text);

        if (matcherDate3.find()) {
            int season = Integer.parseInt(matcherDate3.group(1));
            int chapters = Integer.parseInt(matcherDate3.group(2));
            int season2 = Integer.parseInt(matcherDate3.group(3));
            int chapters2 = Integer.parseInt(matcherDate3.group(4));
            if (season == season2) {
                List<Integer> list = new ArrayList<>();
                for (int i = chapters; i <= chapters2; i++) {
                    list.add(i);
                }
                seasonChapters.put(season, list);
            } else {
                log.error("");
            }

        } else {
            Matcher matcherDate4 = PATTERN_CHAPTERS.matcher(text);
            if (matcherDate4.find()) {
                int season = Integer.parseInt(matcherDate4.group(1));
                int chapters = Integer.parseInt(matcherDate4.group(2));
                seasonChapters.put(season, List.of(chapters));
            }
        }
        return seasonChapters;
    }

    private SeasonsChaptersUtil() {
    }
}
