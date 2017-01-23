package webcrawler;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import webcrawler.crawling.CrawlingProfile;
import webcrawler.crawling.profiles.Profiles;
import webcrawler.pattern.Pattern;

/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public class DownloadPages {

	private static void printHelp(){
		System.out.println("Deve-se passar somente 2 parametros!");
		System.out.println("1 - A profundidade maxima desejada");
		
		StringBuilder message = new StringBuilder();
		
		for(Profiles profile : Profiles.values()){
			message.append(profile.getNum() + "- " + profile.getProfile().getName() + " ");
		}
		
		System.out.println(String.format("2 - O peril desejado (%s)", message.toString()));
	}

	private static boolean isNumber(String value) {  
		try {  
			Integer.parseInt(value);  
			return true;  
		} catch (NumberFormatException e) {  
			return false;  
		}  
	}

	private static String[] setDefaultProfile(String[] args){
		return new String[]{args[0], "1"};
	}

	private static void validateArgs(String[] args){
		switch (args.length) {
		case 1:
			if(!isNumber(args[0])){
				printHelp();
				System.exit(0);
			}

			break;
		case 2:
			if(!isNumber(args[0]) || !isNumber(args[1])){
				printHelp();
				System.exit(0);
			}

			int option = Integer.parseInt(args[1]);

			if(option > Profiles.values().length || option < 1){
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

		// Set default profile
		if(args.length == 1){
			args = setDefaultProfile(args);
		}

		Instant initialTime = Instant.now();

		CrawlingProfile cp = Profiles.from(Integer.parseInt(args[1])).getProfile();

		String startURL = "http://" + cp.getSite();
		Pattern urlPattern = new Pattern("http.*" + cp.getSite() + ".*");

		int depth = Integer.valueOf(args[0]);

		String mypath = ""; // Can set a custom folder
		String folderPath = mypath + "resultados-" + cp.getName() + "/"; // Create the website folder
		String filepath = folderPath + "/resultados.txt"; // The file inside website folder

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

		WebCrawler webCrawler = new WebCrawler(urlPattern, cp, depth, filepath);
		webCrawler.crawl(startURL);

		System.out.println();
		System.out.println("WebCrawler finalizado!");
		System.out.println("Status finais: ");
		webCrawler.printResults();
		System.out.println("-> tempo total: " + Duration.between(initialTime, Instant.now()).toMinutes() + " minutos");
		System.out.println();
	}
}
