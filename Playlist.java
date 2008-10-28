import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Playlist extends HashMap<String, String> {

	public Playlist( Map<String, String> map )
	{
		super( map );
	}
	
	public Song getSong(String songTitle){
		return new Song(songTitle, get(songTitle));
	}
	
	public Song getRandomSong(Random rn){
		Map.Entry<String, String> e = entrySet().toArray((Map.Entry<String, String>[]) null)[rn.nextInt(entrySet().size())];
		return new Song(e.getKey(), e.getValue());
	}
}
