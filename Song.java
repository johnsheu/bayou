public class Song {
	private String title;
	private String url;
	
	public String getTitle() {
		return title;
	}
	public Song setTitle(String songTitle) {
		title = songTitle;
		return this;
	}
	public String getURL() {
		return url;
	}
	public Song setURL(String songURL) {
		url = songURL;
		return this;
	}
	public Song(String songTitle, String songURL) {
		title = songTitle;
		url = songURL;
	}
}
