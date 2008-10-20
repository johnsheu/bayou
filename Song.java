import java.io.Serializable;

public class Song implements Serializable, Comparable<Song>
{
	private String song;

	private String url;

	public Song()
	{
		song = "";
		url = "";
	}

	public Song( String song, String url )
	{
		this.song = song;
		this.url = url;
	}

	public void setSong( String song )
	{
		this.song = song;
	}

	public String getSong()
	{
		return song;
	}

	public void setURL( String url )
	{
		this.url = url;
	}

	public String getURL()
	{
		return url;
	}

	/*
	 * Trust me.  The equals(), hashCode(), and compareTo()
	 * implementations will make sense when we start using them.
	 */
	public boolean equals( Object o )
	{
		return this.compareTo( (Song)o ) == 0;
	}

	public int hashCode()
	{
		return song.hashCode();
	}

	public int compareTo( Song song )
	{
		int result = this.song.compareTo( song.song );
		if ( result != 0 )
			return result;

		if ( this.url.equals( "*" ) || song.url.equals( "*" ) )
			return 0;

		return this.url.compareTo( song.url );
	}
}

