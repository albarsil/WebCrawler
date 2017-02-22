package webcrawler.crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import webcrawler.pattern.HtmlPattern;
import webcrawler.pattern.Pattern;
import webcrawler.queue.Page;
import webcrawler.queue.QueueManager;
import webcrawler.util.HtmlUtils;


/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class PageProcessor{

	private int currentLevel;
	private String currenUrl;
	private final Pattern urlPattern;
	private final List<HtmlPattern> htmlPattern;
	private List<String> stringsToRemove;
		
	public PageProcessor(int currentLevel, String currenUrl, Pattern urlPattern, List<HtmlPattern> htmlPattern,
			List<String> stringsToRemove) {
		
        Character nbsp = 160;/*html "&nbsp;" is the same as char code 160*/
        this.stringsToRemove = stringsToRemove == null ? Arrays.asList(nbsp.toString()) : stringsToRemove;
        
		this.currentLevel = currentLevel;
		this.currenUrl = currenUrl;
		this.urlPattern = urlPattern;
		this.htmlPattern = htmlPattern;
	}

	public String process() {
		
		System.out.println(">> Level: " + currentLevel + " >> Crawling the page: " + currenUrl);
		
		if(QueueManager.getThreadsWaiting() == QueueManager.MAX_THREADS_WAITING){
			try {
				QueueManager.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
				Logger.getLogger(PageProcessor.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		Document html = null;
		
		try {
			html = Jsoup.parse(getHtml(currenUrl));
		} catch (Exception e) {
			return null;
		}
		
		if(html == null)
			return "";
		
		// Check for new child pages
		List<Page> child = createChildPages(html, urlPattern);
		
		if(child != null && !child.isEmpty())
			QueueManager.push(child);
		
		// Return the textual content
		return getContentToSave(html, htmlPattern);
	}
	
	private String getHtml(String address) throws IOException{
        URL url = new URL(address);
        URLConnection con = url.openConnection();
		BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder buf=new StringBuilder();
		String line=null;
		while ((line=reader.readLine()) != null) {
		  buf.append(line);
		}
		
		return buf.toString();
	}


	private Elements applyPatternFilter(Elements all, HtmlPattern pattern) {
		all = all.select(pattern.getJsoupSelectorFilter());
		return pattern.matches(HtmlUtils.removeTags(all.toString())) ? all : null;
	}

	public String getContentToSave(Document html, List<HtmlPattern> htmlPattern) {

		StringBuilder sb = new StringBuilder();
		Elements all = html.select("*");
		for (HtmlPattern pattern : htmlPattern) {
			all = applyPatternFilter(all, pattern);
			if (all == null) {
				return "";
			}
		}

		for (Element element : all) {
			String text = HtmlUtils.removeTags(element.ownText());
			for (String stringToRemove : stringsToRemove) {
				text = text.replace(stringToRemove, "");
			}
			if (!text.isEmpty()) {
				sb.append(text + System.lineSeparator());
			}
		}

		return sb.toString();
	}

	public List<Page> createChildPages(Document html, Pattern urlPattern) {
		String stringLink;
		Elements listLinks = html.select("a[href]");
		List<Page> childPages = new ArrayList<Page>();
		for (Element tempLink : listLinks) {
			stringLink = tempLink.attr("abs:href");
			if (stringLink.endsWith("/")) {
				stringLink = stringLink.substring(0, stringLink.length() - 1);
			}
			if (urlPattern.matches(stringLink)) {
				Page page = new Page(currentLevel + 1, stringLink);

				if (!childPages.contains(page) && !page.getUrl().equals(currenUrl)) {
					childPages.add(page);
				}
			}
		}
		
		return childPages;
	}
}
