package webcrawler.crawling;

import java.util.ArrayList;
import java.util.List;

import webcrawler.pattern.HtmlPattern;

/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public abstract class CrawlingProfile {

	List<HtmlPattern> htmlPatterns = new ArrayList<>();

	public CrawlingProfile(){
		configure();
	}

	protected void addPattern(HtmlPattern pattern){
		htmlPatterns.add(pattern);
	}

	public List<HtmlPattern> getPatterns(){
		return htmlPatterns;
	}

	public abstract String getName();

	protected abstract void configure();

	public abstract String getSite();
}
