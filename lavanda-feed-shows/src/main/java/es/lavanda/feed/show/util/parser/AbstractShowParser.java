package es.lavanda.feed.show.util.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.stereotype.Component;

import es.lavanda.feed.show.exception.FeedShowsException;
import es.lavanda.lib.common.model.ShowModelTorrent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractShowParser {

    public List<ShowModelTorrent> executeBeans() {
        return execute();
    }

    protected abstract List<ShowModelTorrent> execute();

    /**
     * Method to get html from a uri
     * 
     * @param urlHtml the uri to realize the get request
     * @return string with the content of the uri
     * @throws Exception
     * @throws AutomatizeTasksException
     */
    protected String getHTML(String urlHtml, Charset charset) {
        try {
            log.info("Getting html from : {}", urlHtml);
            URL url = new URL(urlHtml);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == 200) {
                Reader in2 = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(),charset));
                StringBuilder stringBuilder = new StringBuilder();
                for (int c; (c = in2.read()) >= 0;)
                    stringBuilder.append((char) c);
                return stringBuilder.toString();
            } else {
                throw new FeedShowsException("HTTP code not 200");
            }
        } catch (IOException e) {
            log.error("IO Exception getting html from: {}", urlHtml, e);
            throw new FeedShowsException(e);
        }
    }

}
