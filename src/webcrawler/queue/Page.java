package webcrawler.queue;

public class Page {

	int deeph;
	String url;
		
	public Page(int deeph, String url) {
		super();
		this.deeph = deeph;
		this.url = url;
	}
	
	public int getDeeph() {
		return deeph;
	}
	public void setDeeph(int deeph) {
		this.deeph = deeph;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
