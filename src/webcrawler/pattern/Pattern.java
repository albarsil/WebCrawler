package webcrawler.pattern;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:54:15
 */
public class Pattern {

    private final String pattern;

    public Pattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean matches(String someString) {
        return someString.length() > 0 && someString.matches(pattern);
    }

}
