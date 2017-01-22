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
		HtmlPattern htmlPatternDiv = new HtmlPattern(".*", "div.materia-conteudo");
		HtmlPattern htmlPatternParagrafo = new HtmlPattern(".*\\..*", "p");

		addPattern(htmlPatternDiv);
		addPattern(htmlPatternParagrafo);
	}

	@Override
	public String getSite() {
		return "g1.globo.com";
	}

	
}
