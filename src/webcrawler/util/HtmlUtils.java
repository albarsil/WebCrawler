package webcrawler.util;

import org.jsoup.Jsoup;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 05/11/2016 - 16:39:26
 */
public class HtmlUtils {

    public static String removeTags(String html) {
        return Jsoup.parse(html).text();
    }
}
