// package es.lavanda.feed.show.util.parser;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.Reader;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Objects;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;
// import org.springframework.util.StringUtils;

// import es.lavanda.feed.show.exception.FeedShowsException;
// import es.lavanda.lib.common.model.ShowModelTorrent;
// import es.lavanda.lib.common.model.ShowModelTorrent.Page;
// import lombok.extern.slf4j.Slf4j;
// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.Reader;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Objects;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// public class PctfenixShowParser extends AbstractShowParser {
//     private static final String URL_PCTFENIX_SHOWS_HD = "https://pctfenix.com/descargar-series/hd/";
//     private static final String URL_PCTFENIX_HTTPS = "https://pctfenix.com";
//     private static final String URL_PCTFENIX_SHOW_CONTROLLER = "https://pctfenix.com/controllers/show.chapters.php";
//     private static final String URL_HTTPS = "https:";
//     private static final String DATA_UT = "data-ut";

//     @Override
//     protected List<ShowModelTorrent> execute() {
//         // TODO Auto-generated method stub
//         return null;
//     }
//     public void getNewShows() {
//         log.info("Ejecutando Pctfenix feed shows Parser");
//         String textNewShows = getHTML(URL_PCTFENIX_SHOWS_HD);
//         Document doc = Jsoup.parse(textNewShows);
//         Elements listLi = doc.getElementsByClass("slide-it slick-slide slick-current slick-active");
//         for (Element element : listLi) {
//             ObjectToDownload objectToDownload = new ObjectToDownload();
//             getImage(element, objectToDownload);
//             try {
//                 String urlOfElement = getUrlOfElement(element);
//                 fillShow(urlOfElement, objectToDownload);
//                 if (!Objects.isNull(objectToDownload.getShow())) {
//                     log.info("Sending to download: {}", objectToDownload.toString());
//                     producerService.sendShow(objectToDownload);
//                 }
//             } catch (Exception e) {
//                 log.error("Show not filled", e);
//             }
//         }
//         log.info("Se ha terminado de ejecutar FeedDownloadShowDescargas2020Impl Parser");
//     }

//     private void fillShow(String urlOfElement, ObjectToDownload objectToDownload) throws PctfenixFeedShowsException {
//         String textShow;
//         try {
//             textShow = getHTML(urlOfElement);
//         } catch (PctfenixFeedShowsException e1) {
//             throw new PctfenixFeedShowsException("Cannot not retrieve page ");
//         }
//         Document doc = Jsoup.parse(textShow);
//         Elements elementsName = doc.getElementsByClass("title-hd");
//         String title = elementsName.get(0).getElementsByTag("h2").text();
//         Elements boxContentLinkx = doc.getElementsByClass("box-content-links");
//         List<Show> showList = new ArrayList<>();
//         for (Element element : boxContentLinkx) {
//             Elements links = element.getElementsByAttribute("onClick");
//             for (Element link : links) {
//                 String modCap = link.attr("onClick");
//                 String nameSeasonQualityChapters = link.text();
//                 String linkTorrent;
//                 try {
//                     linkTorrent = getTorrent(modCap);
//                     Show show = getShow(nameSeasonQualityChapters);
//                     show.setTorrent(linkTorrent);
//                     showList.add(show);
//                 } catch (PctfenixFeedShowsException e) {
//                     log.error("Show not filled", e);
//                 }
//             }
//         }
//         objectToDownload.setTitle(title);
//         objectToDownload.setShow(showList);
//     }

//     private Show getShow(String nameSeasonQualityChapters) {
//         Show show = new Show();
//         List<Integer> seasons = new ArrayList<>();
//         List<Integer> chapters = new ArrayList<>();
//         Matcher matcherSingleChapter = Pattern.compile("(.*) - temporada (.*)cap.(\\d{3,4})")
//                 .matcher(nameSeasonQualityChapters);
//         Matcher matcherChapters = Pattern.compile("(.*) - temporada (.*)cap.(\\d{3,4}\\w\\d{3,4})")
//                 .matcher(nameSeasonQualityChapters);
//         if (matcherChapters.find()) {
//             Integer numberFrom = Integer.parseInt(matcherChapters.group(3).split("_")[0]);
//             Integer numberTo = Integer.parseInt(matcherChapters.group(3).split("_")[1]);
//             int season = Integer.parseInt(String.valueOf(numberFrom).substring(0, 1));
//             int chapterFrom = Integer.parseInt(String.valueOf(numberFrom).substring(1, 3));
//             int chapterTo = Integer.parseInt(String.valueOf(numberTo).substring(1, 3));
//             seasons.add(season);
//             for (int i = chapterFrom; i <= chapterTo; i++) {
//                 chapters.add(i);
//             }
//         } else if (matcherSingleChapter.find()) {
//             Integer number = Integer.parseInt(matcherSingleChapter.group(3));
//             if (number < 999) {
//                 int season = Integer.parseInt(String.valueOf(number).substring(0, 1));
//                 int chapter = Integer.parseInt(String.valueOf(number).substring(1, 3));
//                 seasons.add(season);
//                 chapters.add(chapter);
//             }
//         }
//         show.setChapters(chapters);
//         show.setSeasons(seasons);
//         return show;
//     }

//     private String getTorrent(String modCap) throws PctfenixFeedShowsException {
//         String numberTorrent = "";
//         Matcher matcher = Pattern.compile("modCap\\((\\d+)").matcher(modCap);
//         if (matcher.find()) {
//             numberTorrent = matcher.group(1);
//         }
//         String postHtml = postHTML(URL_PCTFENIX_SHOW_CONTROLLER, numberTorrent);
//         // log.info("PostHtml of modcap {} .{}", modCap, postHtml);
//         Document doc = Jsoup.parse(postHtml);
//         Elements elements = doc.getElementsByAttribute(DATA_UT);
//         if (elements.size() == 1 && !elements.get(0).attr(DATA_UT).isEmpty()) {
//             return URL_HTTPS + elements.get(0).attr(DATA_UT);
//         } else {
//             throw new PctfenixFeedShowsException("No torrent on this modcap");
//         }
//     }

//     private String getUrlOfElement(Element element) {
//         String url = "";
//         Elements elements = element.getElementsByTag("a");
//         for (Element element2 : elements) {
//             url = URL_PCTFENIX_HTTPS + element2.attr("href");
//         }
//         return url;
//     }

//     private void getImage(Element element, ObjectToDownload objectToDownload) {
//         Elements elements = element.getElementsByClass("mv-img");
//         for (Element element2 : elements) {
//             if (element2.getElementsByTag("img").size() == 1) {
//                 String img = URL_HTTPS + element2.getElementsByTag("img").get(0).attr("src");
//                 objectToDownload.setImage(img);
//             } else {
//                 log.debug("not image on this element: {}", element2.text());
//             }

//         }
//     }

//     /**
//      * Method to get html from a uri
//      * 
//      * @param urlHtml the uri to realize the get request
//      * @return string with the content of the uri
//      * @throws Exception
//      * @throws AutomatizeTasksException
//      */
//     private String getHTML(String urlHtml) throws PctfenixFeedShowsException {
//         String s2 = "";
//         HttpURLConnection urlConnection = null;
//         Reader in2 = null;
//         try {
//             // log.debug("Getting html from : {}", urlHtml);
//             URL url = new URL(urlHtml);
//             urlConnection = (HttpURLConnection) url.openConnection();
//             if (urlConnection.getResponseCode() == 200) {
//                 in2 = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                 StringBuilder sb = new StringBuilder();
//                 for (int c; (c = in2.read()) >= 0;) {
//                     sb.append((char) c);
//                 }
//                 String response = sb.toString();
//                 s2 = new String(response.getBytes("ISO-8859-1"), "UTF-8");
//             }
//             // log.debug("Response getHTML: " + s2);
//             return s2;
//         } catch (IOException e) {
//             log.error("IO Exception getting html from: {}", urlHtml, e);
//             throw new PctfenixFeedShowsException(e);
//         } finally {
//             urlConnection.disconnect();
//             try {
//                 in2.close();
//             } catch (IOException e) {
//                 log.error("Close reader", e);
//             }
//         }
//     }

//     private String postHTML(String urlHtml, String param) throws PctfenixFeedShowsException {
//         // log.debug("Post html from : {}", urlHtml);
//         HttpResponse<String> response = Unirest.post(urlHtml).multiPartContent().field("id", param).asString();
//         return response.getBody();
//     }

// }