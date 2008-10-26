import java.io.Serializable;

public class Song implements Serializable, Comparable<Song>
{
	private String name;

	private String url;

	public Song()
	{
		name = "";
		url = "";
	}

	public Song( String name, String url )
	{
		this.name = name;
		this.url = url;
	}

	public Song setName( String song )
	{
		this.name = song;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Song setURL( String url )
	{
		this.url = url;
		return this;
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
		return name.hashCode();
	}

	public int compareTo( Song song )
	{
		int result = this.name.compareTo( song.name );
		if ( result != 0 )
			return result;

		if ( this.url.equals( "*" ) || song.url.equals( "*" ) ) //Allows for a "wildcard" as a URL.
			return 0;

		return this.url.compareTo( song.url );
	}
}

