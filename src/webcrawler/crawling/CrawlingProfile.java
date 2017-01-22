package webcrawler.crawling;

import java.util.ArrayList;
import java.util.List;

import webcrawler.pattern.HtmlPattern;

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

	public String getName(){
		if(getSite().contains("."))
			return getSite().replace(".", ";").split(";")[0];
		else
			throw new NullPointerException();
	}

	protected abstract void configure();

	public abstract String getSite();
}
