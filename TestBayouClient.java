import java.util.Random;
import java.util.Scanner;

public class TestBayouClient extends BayouClient
{
	private final Random RN;
	
	public static void main(String[] args) 
	{
		if(args.length!=4){
			System.out.println("Usage: java TestBayouClient (int port) (boolean primary) (int sleepTimeInMilliseconds) (int numberOfRandomActions) (boolean deterministic)");
			System.exit(1);
		}
		Integer port = Integer.parseInt(args[0]);
		boolean primary = Boolean.parseBoolean(args[1]);
		int sleepTime = Integer.parseInt(args[2]);
		int actions = Integer.parseInt(args[3]);
		boolean deterministic = Boolean.parseBoolean(args[4]);
		
		TestBayouClient client = new TestBayouClient(port, primary, deterministic);
		client.breakOurSystem(sleepTime, actions);
		System.exit(0);
	}

	public TestBayouClient(int port, boolean primary, boolean deterministic)
	{
		super(port, new Scanner(System.in), primary);
		if(deterministic) 
			RN = new Random(314159265);
		else 
			RN = new Random();
	}

	/**
	 * Creates a random song and adds it to the playlist.
	 */
	private void addRandomSong()
	{
		addSong(createRandomSong());
	}

	/**
	 * Removes a random song from the playlist, or tries to remove a randomly generated song..
	 */
	private void removeRandomSong()
	{
		if(RN.nextBoolean())
			removeSong(createRandomSong());
		else
			removeSong(getPlaylist().getRandomSong(RN));
	}

	/**
	 * Modifies URL of randomly chosen song from playlist, or modifies a nonexistent song (should return error flag).
	 */
	private void randomModifyRandomSong()
	{    	
		if(RN.nextBoolean())
			modifySong(getPlaylist().getRandomSong(RN).setURL(createRandomURL())); //Modify existing song.
		else
			modifySong(createRandomSong()); //Modify song that probably doesn't exist.
	}

	private Song createRandomSong() 
	{
		return new Song(createRandomTitle(), createRandomURL());
	}

	private String createRandomTitle()
	{
		String title = "" + RN.nextInt();
		switch (RN.nextInt(6)) 
		{
			case 0: title += " Reasons I Love You"; break; //Typical.
			case 1: title += " Problems and malloc Ain't One (Java Ed.)"; break; //Garbage collection is nice sometimes.
			case 2: title += " Problems and Node Failure Ain't One"; break; //That's why we're here.
			case 3: title += " Red Balloons"; break; //99 is so overdone.
			case 4: title = "Telemann's Symphony No. " + title; break; //Suitable for the most prolific classical composer.
			case 5: title = "Fur Elise"; break;  //For deliberate collisions.
		}
		return title;
	}

	private String createRandomURL()
	{
		return "http://www.cs.utexas.edu/~lorenzo/music/" + RN.nextInt() + ".mp3";
	}

	public void breakOurSystem(int sleepTime, int actions)
	{
		testUseCases();
		stressTest(sleepTime, actions);
	}

	private void testUseCases() 
	{
		//add same song twice
		Song m_song = createRandomSong();
		addSong(m_song);
		addSong(m_song);
		assert getPlaylist().containsKey(m_song);
		
		//add song, remove it
		m_song = createRandomSong();
		addSong(m_song);
		removeSong(m_song);
		assert !getPlaylist().containsKey(m_song);
		
		//add song, change it
		addSong(m_song);
		m_song.setURL("http://www.newurl.com/");
		modifySong(m_song);
		assert getPlaylist().get(m_song.getName()).equals(m_song.getURL());
		
		//remove nonexistent song
		m_song = createRandomSong();
		removeSong(m_song);
		assert !getPlaylist().containsKey(m_song.getName());
		
		//change nonexistent song (should return error flag)
		m_song = createRandomSong();
		modifySong(m_song);
		assert !getPlaylist().containsKey(m_song.getName());
	}

	private void stressTest(int sleepTime, int actions)
	{
		int i = 0;
		//Random testing.
		while(++i < actions)
		{
			switch (RN.nextInt(3))
			{
				case 0: addRandomSong(); break;
				case 1: randomModifyRandomSong(); break;
				case 2: removeRandomSong(); break;
			}
			
		try {Thread.sleep(sleepTime);}
			catch (InterruptedException e) {}
		
		}
		
		listSongs();
	}
}
