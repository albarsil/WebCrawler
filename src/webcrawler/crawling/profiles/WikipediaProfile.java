/**
 * 
 */
package webcrawler.crawling.profiles;

import webcrawler.crawling.CrawlingProfile;
import webcrawler.pattern.HtmlPattern;

/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class WikipediaProfile  extends CrawlingProfile{

	@Override
	protected void configure() {
		//Jsoup tag for "document.select(YOUR_TAG_HERE)"
        HtmlPattern htmlPatternDiv = new HtmlPattern(".*", "div#mw-content-text");
       HtmlPattern htmlPatternParagrafo = new HtmlPattern(".*\\..*", "p");

		addPattern(htmlPatternDiv);
		addPattern(htmlPatternParagrafo);
	}

	@Override
	public String getSite() {
		return "pt.wikipedia.org";
	}

	/* (non-Javadoc)
	 * @see webcrawler.crawling.CrawlingProfile#getName()
	 */
	@Override
	public String getName() {
		return "Wikipedia Brasil";
	}
}
