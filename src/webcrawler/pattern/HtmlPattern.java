package webcrawler.pattern;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 05/11/2016 - 00:42:48
 */
public class HtmlPattern extends Pattern {

    private final String jsoupSelectorFilter;

    public HtmlPattern(String pattern, String jsoupSelectorFilter) {
        super(pattern);
        this.jsoupSelectorFilter = jsoupSelectorFilter;
    }

    public String getJsoupSelectorFilter() {
        return jsoupSelectorFilter;
    }

}
