package webcrawler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import webcrawler.crawling.CrawlingProfile;
import webcrawler.crawling.PageProcessor;
import webcrawler.pattern.HtmlPattern;
import webcrawler.pattern.Pattern;
import webcrawler.queue.Page;
import webcrawler.queue.QueueManager;


/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class WebCrawler {

	private final Pattern urlPattern;
	private final List<HtmlPattern> htmlPattern;
	private final int MAX_LEVEL;
	private int depth = 1;
	private int crawledPages = 0;
	private List<String> visitedPages;
	private boolean isDone;
	private String outputFilepath;
	private String domain;

	public WebCrawler(Pattern urlPattern, CrawlingProfile crawlingProfile, int maxNivel, String outputFilepah){
		this.urlPattern = urlPattern;
		this.htmlPattern = crawlingProfile.getPatterns();
		this.MAX_LEVEL = maxNivel;
		this.visitedPages = new ArrayList<>();
		this.outputFilepath = outputFilepah;
		this.domain = crawlingProfile.getSite();

		if(outputFilepah == null || maxNivel < 1)
			throw new NullPointerException();
	}

	public void crawl(String startUrl){
		QueueManager.push(new Page(depth, startUrl));


		//create thread pool
		ExecutorService crawlService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Page page = null;

		do{

			try {
				page = QueueManager.pull();
			} catch (InterruptedException e) {
				Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
			}

			if(page == null){
				System.out.println("!!! - Can not get a Page from QueueManager!");
				continue; // Try next
			}
			else if(visitedPages.contains(page.getUrl()) || !belongsTo(page.getUrl(), domain)){ // Only pages inside the domain
				continue; // Try next
			}
			else{
				visitedPages.add(page.getUrl());
			}

			depth = page.getDeeph();

			Future<String> future = crawlService.submit(
					new PageProcessor(
							depth,
							page.getUrl(),
							urlPattern,
							htmlPattern,
							null)
					);


			while(!future.isDone());
			try {
				write(future.get());
				crawledPages++;
			} catch (IOException | InterruptedException | ExecutionException e) {
				Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
			}

			if(QueueManager.isEmpty())
				try {
					QueueManager.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
				}

		}while(!QueueManager.isEmpty() && depth <= MAX_LEVEL);

		//shut down when the last url have been added to the pool
		crawlService.shutdown();

		try {
			//wait until all threads have ended
			crawlService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, e);
		}

		isDone = crawlService.isTerminated();
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
		System.out.println("-> paginas visitadas: " + visitedPages.size());
	}

	private boolean belongsTo(String url, String site){
		return url.startsWith("http://" + site) || url.startsWith("https://" + site);
	}

	public boolean isDone(){
		return isDone;
	}
}
