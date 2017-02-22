package webcrawler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.logging.Level;
import java.util.logging.Logger;

import webcrawler.crawling.CrawlingProfile;
import webcrawler.crawling.PageProcessor;
import webcrawler.pattern.HtmlPattern;
import webcrawler.pattern.Pattern;
import webcrawler.queue.Page;
import webcrawler.queue.QueueManager;
import webcrawler.sqlite.Database;


/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class WebCrawler {


	private final static java.util.regex.Pattern FILTERS = java.util.regex.Pattern.compile(
			".*(\\.(css|js|bmp|gif|jpeg" 
					+ "|png|tiff|mid|mp2|mp3|mp4" 
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf" 
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private final Pattern urlPattern;
	private final List<HtmlPattern> htmlPattern;
	private final int MAX_LEVEL;
	private int depth = 1;
	private int crawledPages = 0;
	private String outputFilepath;
	private String domain;
	private Database database;

	public WebCrawler(Pattern urlPattern, CrawlingProfile crawlingProfile, int maxNivel, String outputFilepah){
		this.urlPattern = urlPattern;
		this.htmlPattern = crawlingProfile.getPatterns();
		this.MAX_LEVEL = maxNivel;
		this.outputFilepath = outputFilepah;
		this.domain = crawlingProfile.getSite();
		this.database = Database.getInstance();

		if(outputFilepah == null || maxNivel < 1)
			throw new NullPointerException();
	}

	private boolean shouldVisit(String url){
		try {
			return !database.contains(url) || 
					!FILTERS.matcher(url).matches() && 
					(url.startsWith("http://" + domain) || url.startsWith("https://" + domain));
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void crawl(String startUrl){
		QueueManager.push(new Page(depth, startUrl));

		Page page = null;

		do{

			try {
				page = QueueManager.pull();
			} catch (InterruptedException e) {
				Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
			}

			if(page == null || !shouldVisit(page.getUrl())){
				continue; // Try next
			}
			
			depth = page.getDeeph();

			String text = new PageProcessor(
							depth,
							page.getUrl(),
							urlPattern,
							htmlPattern,
							null).process();
			try {
				write(text);
				crawledPages++;
				database.insert(page.getUrl(), depth);
			} catch (IOException e) {
				Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
			} catch (SQLException e) {
				Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
			}

			if(QueueManager.isEmpty())
				try {
					QueueManager.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
				}

		}while(!QueueManager.isEmpty() && depth <= MAX_LEVEL);
	}

	private void write(String text) throws IOException{

		Writer writer = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(
								outputFilepath,
								true
								),
						"UTF-8"
						)
				);

		if(text != null && !text.isEmpty()){
			writer.write(text);
		}

		writer.flush();
		writer.close();
	}

	public void printResults(){
		System.out.println("-> nivel maximo: " + depth);
		System.out.println("-> paginas baixadas: " + crawledPages);
	}
}
