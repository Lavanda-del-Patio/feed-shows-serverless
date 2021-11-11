package es.lavanda.feed.show;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.lavanda.feed.show.util.parser.PctmixShowParser;

@ExtendWith(MockitoExtension.class)
public class PctmixShowParserTest {

    private static final Pattern PATTERN_CHAPTERS = Pattern.compile("(\\d{1,2})(\\d{2})");
    private static final Pattern PATTERN_CHAPTERS_DOUBLE = Pattern.compile("(\\d{1,2})(\\d{2})_(\\d{1,2})(\\d{2})");

    @InjectMocks
    private PctmixShowParser pctmixShowsParser;

    @Test
    public void test() {
        Assertions.assertNotNull(pctmixShowsParser);
        Assertions.assertDoesNotThrow(() -> pctmixShowsParser.getNewShows());
    }

    @Test
    @Disabled
    public void parseText() {
        String tes = "Cap.1104";
        String tes2 = "Cap.104";
        String tes3 = "Cap.302_305";
        Matcher matcherDate = PATTERN_CHAPTERS.matcher(tes);
        if (matcherDate.find()) {
            String season = matcherDate.group(1);
            String chapters = matcherDate.group(2);
            assertEquals("11", season);
            assertEquals("04", chapters);
        }
        Matcher matcherDate2 = PATTERN_CHAPTERS.matcher(tes2);
        if (matcherDate2.find()) {
            String season = matcherDate2.group(1);
            String chapters = matcherDate2.group(2);
            assertEquals("1", season);
            assertEquals("04", chapters);
        }

        Matcher matcherDate3 = PATTERN_CHAPTERS_DOUBLE.matcher(tes3);
        Map<Integer, List<Integer>> map = new HashMap<>();

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
                map.put(season, list);
            }
            assertEquals(3, season);
            assertEquals(02, chapters);
            assertEquals(3, season2);
            assertEquals(05, chapters2);

        } else {
            Matcher matcherDate4 = PATTERN_CHAPTERS.matcher(tes3);
            if (matcherDate4.find()) {
                String season = matcherDate3.group(1);
                String chapters = matcherDate3.group(2);
                assertEquals("1", season);
                assertEquals("04", chapters);
            }
        }

    }
}