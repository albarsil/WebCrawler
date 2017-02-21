package webcrawler.crawling.profiles;

import webcrawler.crawling.CrawlingProfile;
import webcrawler.pattern.HtmlPattern;

/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class GloboProfile extends CrawlingProfile{

	@Override
	protected void configure() {
		//Jsoup tag for "document.select(YOUR_TAG_HERE)"
		HtmlPattern htmlPatternDiv = new HtmlPattern(".*", "div.mc-column content-text active-extra-styles");
		HtmlPattern htmlPattern2 = new HtmlPattern(".*\\..*", "div");
		HtmlPattern htmlPatternParagrafo = new HtmlPattern(".*\\..*", "p");

		addPattern(htmlPatternDiv);
		addPattern(htmlPattern2);
		addPattern(htmlPatternParagrafo);
	}

	@Override
	public String getSite() {
		return "g1.globo.com";
	}

	/* (non-Javadoc)
	 * @see webcrawler.crawling.CrawlingProfile#getName()
	 */
	@Override
	public String getName() {
		return "G1 Globo";
	}

	
}
