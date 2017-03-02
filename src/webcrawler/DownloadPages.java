package webcrawler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import webcrawler.sqlite.Database;

/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class DownloadPages {

	private static void printHelp(){
		System.out.println("Deve-se passar somente 2 parametros!");
		System.out.println("1 - A profundidade maxima desejada");
		System.out.println("2 - O endereço para iniciar o crawling");
	}

	private static boolean isNumber(String value) {  
		try {  
			Integer.parseInt(value);  
			return true;  
		} catch (NumberFormatException e) {  
			return false;  
		}  
	}


	private static void validateArgs(String[] args){
		switch (args.length) {
		case 2:
			if(!isNumber(args[0])){
				printHelp();
				System.exit(0);
			}

			break;
		default:
			printHelp();
			System.exit(0);
			return;
		}
	}



	public static void main(String[] args) {
		
		validateArgs(args);
		
		Instant initialTime = Instant.now();

		int depth = Integer.valueOf(args[0]);
		
		if(!Pattern.compile("(https|http).*\\.com").matcher(args[1]).matches())
			throw new IllegalArgumentException("Check start url address");
		
		String domain = args[1].split("\\.")[1];

		String mypath = ""; // Can set a custom folder
		String folderPath = mypath + "resultados"; // Create the website folder
		String filepath = folderPath + "/resultados-" + domain + ".txt"; // The file inside website folder

		new File(folderPath).mkdirs();
		File file = new File(filepath);

		if(file.exists())
			file.delete();
		else{
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		try {
			Database.getInstance().clear();
		} catch (SQLException e) {
			Logger.getLogger(DownloadPages.class.getName()).log(Level.SEVERE, null, e);
		}

		WebCrawler webCrawler = new WebCrawler(domain, depth, filepath);
		webCrawler.crawl(args[1]);

		System.out.println();
		System.out.println("WebCrawler finalizado!");
		System.out.println("Status finais: ");
		webCrawler.printResults();
		System.out.println("-> tempo total: " + Duration.between(initialTime, Instant.now()).toMinutes() + " minutos");
		System.out.println();
	}
}
