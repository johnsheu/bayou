import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Playlist extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	public Playlist()
	{
		super();
	}

	public Playlist( Map<String, String> map )
	{
		super( map );
	}
	
	public Song getSong(String songTitle)
	{
		return new Song(songTitle, get(songTitle));
	}
	
	public Song getRandomSong(Random rn)
	{
		if(entrySet().size() == 0)
			return null;
		Map.Entry<String, String>[] a = new Map.Entry[0];
		Map.Entry<String, String> e = entrySet().toArray(a)[rn.nextInt(entrySet().size())];
		return new Song(e.getKey(), e.getValue());
	}
}
