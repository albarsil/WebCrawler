package webcrawler.crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import webcrawler.queue.Page;
import webcrawler.queue.QueueManager;


/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class PageProcessor{

	private int currentLevel;
	private String currenUrl;
	private final String urlPattern;

	public PageProcessor(int currentLevel, String currenUrl, String domain) {
		this.currentLevel = currentLevel;
		this.currenUrl = currenUrl;
		this.urlPattern = "(https|http).*" + ".*";
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
		return getContentToSave(html);
	}

	private String getHtml(String address) throws IOException{
		URL url = new URL(address);
		URLConnection con = url.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
		BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		StringBuilder buf=new StringBuilder();
		String line=null;
		while ((line=reader.readLine()) != null) {
			buf.append(line);
		}

		return buf.toString();
	}

	public String getContentToSave(Document html) {

		StringBuilder sb = new StringBuilder();

		String ht = html.select("div").html();
		Iterator<Element> iterator = html.select("div").iterator();
		while(iterator.hasNext()){
			Element t = iterator.next();
			List<Element> a = t.getAllElements();

			if(t.tagName().equals("p")){
				System.out.println(t.attributes().get("class"));
				if(t.attributes().get("class").equals("content-text__container theme-color-primary-first-letter")){
					StringBuilder p = new StringBuilder();

					for (Node child : t.childNodes()) {
						if (child instanceof TextNode) {
							String text = ((TextNode) child).text();
							text = text.replaceAll("/w/d", "");
							if(!text.trim().isEmpty())
								p.append(text + System.lineSeparator());
						}
					}

					sb.append(p.toString());
				}
			}
		}

		return sb.toString();
	}

	public List<Page> createChildPages(Document html, String urlPattern) {
		String stringLink;
		Elements listLinks = html.select("a[href]");
		List<Page> childPages = new ArrayList<Page>();
		for (Element tempLink : listLinks) {
			stringLink = tempLink.attr("abs:href");
			if (stringLink.endsWith("/")) {
				stringLink = stringLink.substring(0, stringLink.length() - 1);
			}
			if (stringLink.length() > 0 && stringLink.matches(urlPattern)) {
				Page page = new Page(currentLevel + 1, stringLink);

				if (!childPages.contains(page) && !page.getUrl().equals(currenUrl)) {
					childPages.add(page);
				}
			}
		}

		return childPages;
	}
}
