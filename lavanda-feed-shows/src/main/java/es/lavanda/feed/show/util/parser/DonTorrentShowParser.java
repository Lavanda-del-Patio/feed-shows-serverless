// package es.lavanda.feed.show.util.parser;

// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Objects;

// import com.amazonaws.util.NumberUtils;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;
// import org.springframework.util.StringUtils;

// import es.lavanda.lib.common.model.ShowModelTorrent;
// import es.lavanda.lib.common.model.TorrentModel.Page;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// public class DonTorrentShowParser extends AbstractShowParser {

//     private static final String URL_DONTORRENTS_BLURAY1080 = "https://dontorrents.one/descargar-peliculas/hd";
//     private static final String URL_DONTORRENTS_4K = "https://dontorrents.one/peliculas/4K";
//     private static final String URL_DONTORRENTS_DOMAIN = "https://dontorrents.one";
//     private static final String URL_HTTPS = "https:";

//     protected List<ShowModelTorrent> execute() {
//         return getNewShows();
//     }

//     public List<ShowModelTorrent> getNewShows() {
//         List<ShowModelTorrent> newshow = new ArrayList<>();
//         log.info("Ejecutando DonTorrent feed shows Parser");
//         String textNewshow = getHTML(URL_DONTORRENTS_BLURAY1080, StandardCharsets.UTF_8);
//         Document doc = Jsoup.parse(textNewshow);
//         Elements listLi = doc.getElementsByClass("noticiasContent");
//         for (Element element : listLi) {
//             Elements elementshow = element.getElementsByClass("text-center");
//             for (Element elementShow : elementshow) {
//                 for (Element elementUrl : elementShow.getElementsByTag("a")) {
//                     if (Boolean.TRUE.equals(elementUrl.hasAttr("href"))) {
//                         ShowModelTorrent showModelTorrent = new ShowModelTorrent();
//                         showModelTorrent.setTorrentPage(Page.DON_TORRENT);
//                         analizeShow(URL_DONTORRENTS_DOMAIN + elementUrl.attr("href"), showModelTorrent);
//                         newshow.add(showModelTorrent);
//                     }
//                 }
//             }
//         }
//         log.info("Finalizado ejecución DonTorrent feed shows Parser");
//         return newshow;
//     }

//     private void analizeShow(String urlShow, ShowModelTorrent showModelTorrent) {
//         log.info("Analize show by url {}", urlShow);
//         String textNewShow = getHTML(urlShow, StandardCharsets.UTF_8);
//         Document doc = Jsoup.parse(textNewShow);
//         Elements images = doc.getElementsByClass("img-thumbnail float-left");
//         if (Boolean.FALSE.equals(images.isEmpty())) {
//             showModelTorrent.setTorrentCroppedTitle(images.first().attr("alt"));
//             showModelTorrent.setTorrentImage(URL_HTTPS + images.first().attr("src"));
//         }
//         Elements year = doc.getElementsByAttributeValueContaining("onclick", "campo: 'anyo'");
//         if (Boolean.FALSE.equals(year.isEmpty()) && Objects.nonNull(NumberUtils.tryParseInt(year.text()))) {
//             showModelTorrent.setTorrentYear(Integer.parseInt(year.text()));
//         }

//         Elements format = doc.getElementsContainingOwnText("Formato:");
//         if (Boolean.FALSE.equals(format.isEmpty())) {
//             showModelTorrent.setTorrentQuality(format.first().parent().text().split("Formato: ")[1]);
//         }
//         Elements size = doc.getElementsContainingOwnText("Tama");
//         if (Boolean.FALSE.equals(size.isEmpty())) {
//             showModelTorrent.setTorrentSize(size.first().parent().text().split(": ")[1]);
//         }
//         Elements torrentUrl = doc
//                 .getElementsByClass("text-white bg-primary rounded-pill d-block shadow text-decoration-none p-1");
//         if (Boolean.FALSE.equals(torrentUrl.isEmpty())) {
//             showModelTorrent.setTorrentUrl(URL_HTTPS + torrentUrl.first().attr("href"));
//         }
//         Elements torrentName = doc.getElementsByAttributeValueMatching("name", "description");
//         if (Boolean.FALSE.equals(torrentName.isEmpty())) {
//             showModelTorrent.setTorrentTitle(
//                     torrentName.first().attr("content").split("cula ")[1].split("torrent gratis en Español")[0]);
//         }
//         log.info(showModelTorrent.toString());
//     }
// }
