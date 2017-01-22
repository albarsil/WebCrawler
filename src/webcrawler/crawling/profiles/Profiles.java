package webcrawler.crawling.profiles;

import webcrawler.crawling.CrawlingProfile;

public enum Profiles {
	GLOBO(1, new GloboProfile());

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
			return GLOBO;

		default:
			return null;
		}
	}

}
