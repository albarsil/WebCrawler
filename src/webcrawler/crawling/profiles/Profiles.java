package webcrawler.crawling.profiles;

import webcrawler.crawling.CrawlingProfile;

/**
 * @author  Allan de Barcelos Silva <albarsil@gmail.com>
 *
 */
public enum Profiles {
	WIKIPEDIA(1, new WikipediaProfile()),
	BBC(2, new BBCProfile()),
	GLOBO(3, new GloboProfile());

	int num;
	CrawlingProfile profile;

	Profiles(int num, CrawlingProfile profile) {
		this.num = num;
		this.profile = profile;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public CrawlingProfile getProfile() {
		return profile;
	}
	public void setProfile(CrawlingProfile profile) {
		this.profile = profile;
	}
	
	public static Profiles from(int num){
		switch (num) {
		case 1:
			return WIKIPEDIA;
		case 2:
			return BBC;
		case 3:
			return GLOBO;

		default:
			return null;
		}
	}

}
