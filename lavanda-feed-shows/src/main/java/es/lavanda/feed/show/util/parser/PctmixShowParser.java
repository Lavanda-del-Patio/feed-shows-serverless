package es.lavanda.feed.show.util.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.lavanda.feed.show.exception.FeedShowsException;
import es.lavanda.feed.show.util.HtmlConstants;
import es.lavanda.feed.show.util.SeasonsChaptersUtil;
import es.lavanda.lib.common.model.ShowModelTorrent;
import es.lavanda.lib.common.model.TorrentModel.Page;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PctmixShowParser extends AbstractShowParser {

    private static final String URL_HTTPS = "https:";
    private static final String URL_HTTPS_PTCTMIX = "https://pctmix1.com";
    private static final Pattern PATTERN_DATE_SPANISH = Pattern.compile("(\\d{4})");
    private static final Pattern PATTERN_YEAR_SPANISH = Pattern.compile("([12]\\d{3})");
    private static final Pattern PATTERN_METADATA = Pattern.compile("\\[(.*)\\]\\[(.*)\\]\\[(.*)\\]\\[(.*)\\]");
    private static final String FEATURED_BOX = "featured-box";
    private static final String CAPTION_TEXT = "caption-text";
    private static final String SHOWS_STRONG = "Series</strong>";

    protected List<ShowModelTorrent> execute() {
        return getNewShows();

    }

    public List<ShowModelTorrent> getNewShows() {
        List<ShowModelTorrent> showModelTorrents = new ArrayList<>();
        log.info("Ejecutando PCTMIX Feed Shows Parser");
        String textNewshow = getHTML(URL_HTTPS_PTCTMIX, StandardCharsets.ISO_8859_1);
        Document doc = Jsoup.parse(textNewshow);
        for (Element elementShowsAndFilms : doc.getElementsByClass(FEATURED_BOX)) {
            if (elementShowsAndFilms.html().contains(SHOWS_STRONG)) {
                Elements elementList = elementShowsAndFilms.getElementsByClass(CAPTION_TEXT);
                if (Boolean.TRUE.equals(elementList.hasAttr(HtmlConstants.HREF))) {
                    showModelTorrents.addAll(elementList.stream().filter(element -> element.hasAttr(HtmlConstants.HREF))
                            .map(element -> analizeShow(element.attr(HtmlConstants.HREF)))
                            .collect(Collectors.toList()));
                }
            }
        }
        log.info("Finalizado ejecución PCTMIX Feed Shows Parser");
        return showModelTorrents;
    }

    private ShowModelTorrent analizeShow(String urlShow) {
        log.info("Analize show by url {}", urlShow);
        ShowModelTorrent showModelTorrent = new ShowModelTorrent();
        String textNewShow = getHTML(urlShow, StandardCharsets.ISO_8859_1);
        Document doc = Jsoup.parse(textNewShow, "UTF_8");
        setTorrentCroppedTitle(showModelTorrent, doc.getElementsByTag("strong"));
        setTorrentImage(showModelTorrent, doc.getElementsByClass("entry-left"));
        setTorrentUrl(showModelTorrent, doc);
        setTorrentTitle(showModelTorrent, doc.getElementsByTag("h1"));
        setTorrentDate(showModelTorrent, doc.getElementsByTag("strong"));
        setTorrentChaptersAndQuality(showModelTorrent, doc.getElementsByTag("h1"));
        setTorrentSize(showModelTorrent, doc.getElementsByTag("strong"));
        showModelTorrent.setTorrentPage(Page.PCTMIX);
        return showModelTorrent;
    }

    private void setTorrentSize(ShowModelTorrent showModelTorrent, Elements elementsByTagStrong) {
        for (Element element : elementsByTagStrong) {
            if (element.text().contains("Size:")) {
                log.debug("Setting size: {}", element.parent().ownText());
                showModelTorrent.setTorrentSize(element.parent().ownText());
            }
        }
    }

    private void setTorrentChaptersAndQuality(ShowModelTorrent showModelTorrent, Elements elementsH1) {
        for (Element element : elementsH1) {
            Matcher matcherDate = PATTERN_METADATA.matcher(element.text());
            if (matcherDate.find()) {
                showModelTorrent.setTorrentQuality(matcherDate.group(1));
                showModelTorrent.setTorrentSeasonsChapters(getChaptersSeaons(matcherDate.group(2)));
                setChaptersAndSeasonIndividualy(showModelTorrent);
                log.debug(matcherDate.group(0));
                log.debug(matcherDate.group(1));
                log.debug(matcherDate.group(2));
                log.debug(matcherDate.group(3));
                log.debug(matcherDate.group(4));
            }
        }
    }

    private void setChaptersAndSeasonIndividualy(ShowModelTorrent showModelTorrent) {
        for (Entry<Integer, List<Integer>> iterable_element : showModelTorrent.getTorrentSeasonsChapters().entrySet()) {
            showModelTorrent.setChapters(iterable_element.getValue());
            showModelTorrent.setSeason(iterable_element.getKey());
        }
    }

    private Map<Integer, List<Integer>> getChaptersSeaons(String text) {
        return SeasonsChaptersUtil.getSeasonsChapters(text);
    }

    private void setTorrentDate(ShowModelTorrent showModelTorrent, Elements elementsByTagStrong) {
        // int year = 0;
        // if (Boolean.FALSE.equals(elementsYear.isEmpty())) {
        // Matcher matcherDate =
        // PATTERN_DATE_SPANISH.matcher(elementsYear.first().text());
        // if (matcherDate.find()) {
        // year = Integer.parseInt(matcherDate.group(0));
        // }
        // }
        for (Element element : elementsByTagStrong) {
            if (element.text().contains("Fecha:")) {
                log.debug("Setting date: {}", element.parent().ownText());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    showModelTorrent.setTorrentDate(dateFormat.parse(element.parent().ownText()));
                } catch (ParseException e) {
                    log.error("ParseException with date {}", element.parent().ownText(), e);
                }
                // showModelTorrent.setTorrentDate(element.parent().text());
            }
        }
        // <strong>Fecha:</strong> 13-09-2021</span>
        // showModelTorrent.setTorrentDate(LocalDate.of(year, month, dayOfMonth));
    }

    private void setTorrentTitle(ShowModelTorrent showModelTorrent, Elements elementsByTag) {
        for (Element element : elementsByTag) {
            showModelTorrent.setTorrentTitle(element.getAllElements().last().text());
        }
    }

    private void setTorrentUrl(ShowModelTorrent showModelTorrent, Document doc) {
        showModelTorrent.setTorrentUrl(getTorrentUrl(doc.getAllElements().first()));
    }

    private void setTorrentImage(ShowModelTorrent showModelTorrent, Elements entryLeftElements) {
        if (entryLeftElements.size() == 1) {
            for (Element element : entryLeftElements) {
                String url = URL_HTTPS + element.getElementsByTag("img").first().attr("src");
                // try {
                // url = getBase64EncodedImage(url);
                // } catch (IOException e) {
                // log.error("Exception encoding base64 image", e);
                // }
                showModelTorrent.setTorrentImage(url);
            }
        }
    }

    private String getBase64EncodedImage(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        InputStream is = url.openStream();
        byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void setTorrentCroppedTitle(ShowModelTorrent showModelTorrent, Elements elementsByTagStrong) {
        showModelTorrent.setTorrentCroppedTitle(elementsByTagStrong.first().text().split("-")[0]);
        // for (Element element : elementsByTagStrong) {

        // // if (Objects.nonNull(element.attr("style", "color:red;"))) {
        // // showModelTorrent.setTorrentCroppedTitle(element.text());
        // if (Objects.nonNull(element.attr("href", urlShow))) {
        // showModelTorrent.setTorrentCroppedTitle(element.text().split("-")[0]);
        // }
        // }
    }

    private String getTorrentUrl(Element filmElement) {
        try {
            String secondPage = URL_HTTPS_PTCTMIX
                    + filmElement.html().split("window.location.href = \"")[1].split("\";")[0];
            return URL_HTTPS
                    + this.getHTML(secondPage, StandardCharsets.ISO_8859_1).split("window.location.href = \"")[1]
                            .split("\";")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Not found window location href", (Throwable) e);
            throw new FeedShowsException("Not found torrent URL");
        }
    }
}
