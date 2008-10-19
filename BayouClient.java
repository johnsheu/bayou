import java.util.*;

public class BayouClient{

    private final Random RN = new Random(314159265);
    private BayouServer m_server;

    public static void main(String[] args){
    	System.out.println("WELCOME...");
    	try {Thread.sleep(1500);} catch (InterruptedException e) {}
    	System.out.println("TO BUFFALO BAYOU");
    	
    	Scanner m_scanner = new Scanner(System.in);
    	System.out.print("Enter port number: ");
    	BayouClient client = new BayouClient(m_scanner.nextInt());
    	
    	while(true){
	    	System.out.print("Enter number: \n" +
	    			"1: Add Song (or modify if title exists) \n" +
	    			"2: Remove Song By Name \n" +
	    			"3: List Songs \n" +
	    			"4: Exit \n");
	    	switch(m_scanner.nextInt()){
	    	case 1: client.addPrompt(m_scanner); break;
	    	case 2: client.removePrompt(m_scanner); break;
	    	case 3: client.listSongs(); break;
	    	case 4: client.exit(); break;
	    	default: System.out.println("Invalid option."); break;
	    	}
    	}
    }

    private void exit() {
		//TODO :: Retire server and exit system.  Use different exit statuses and messages if retire fails.
    	System.exit(0);
	}

	private void listSongs() {
    	final String SEPARATOR = "\n---------------";
    	System.out.println(SEPARATOR);
    	for(Map.Entry<String, String> ent : getPlaylist().entrySet())
    		System.out.println("Title: " + ent.getKey() + "\nURL:" + ent.getValue() + SEPARATOR);
    }

	private void removePrompt(Scanner m_scanner) {
		System.out.print("Enter title: ");
		removeSong(m_scanner.nextLine());
	}

	private void addPrompt(Scanner m_scanner) {
		Song m_song = new Song("", "");
		System.out.print("Enter title: ");
		m_song.setTitle(m_scanner.nextLine());
		System.out.print("Enter URL: ");
		m_song.setURL(m_scanner.nextLine());
		addSong(m_song);
	}

	/**
     * Creates a random song and adds it to the playlist.
     */
    private void addRandomSong(){
    	addSong(createRandomSong());
    }

	/**
     * Removes a random song from the playlist, or tries to remove a randomly generated song..
     */
    private void removeRandomSong(){
    	if(RN.nextBoolean())
    		removeSong(createRandomSong());
    	else
    		removeSong(getRandomSong());
    		
    }

	private Song getRandomSong() {
		return null;
	}

	
	/**
	 * Modifies URL of randomly chosen song from playlist, or modifies a nonexistent song (should return error flag).
	 */
	private void randomModifyRandomSong(){    	
		if(RN.nextBoolean())
			modifySong(getPlaylist().getRandomSong(RN).setURL(createRandomURL())); //Modify existing song.
		else
			modifySong(createRandomSong()); //Modify song that probably doesn't exist.
	}
	
    private Song createRandomSong() {
    	return new Song(createRandomTitle(), createRandomURL());
	}

    private String createRandomTitle(){
		String title = "" + RN.nextInt();
		switch (RN.nextInt(6)) {
			case 0: title += " Reasons I Love You"; break; //Typical.
			case 1: title += " Problems and malloc Ain't One (Java Ed.)"; break; //Garbage collection is nice sometimes.
			case 2: title += " Problems and Node Failure Ain't One"; break; //That's why we're here.
			case 3: title += " Red Balloons"; break; //99 is so overdone.
			case 4: title = "Telemann's Symphony No. " + title; break; //Suitable for the most prolific classical composer.
			case 5: title = "Fur Elise"; break;  //For deliberate collisions.
			}
		return title;
    }

    private String createRandomURL(){
    	return "http://www.cs.utexas.edu/~lorenzo/music/" + RN.nextInt() + ".mp3";
    }

    public void addSong(Song song){}
    public void removeSong(String title){}
    public void removeSong(Song song){removeSong(song.getTitle());}
    public void modifySong(Song song){}

    public void breakOurSystem(){
    	testUseCases();
    	stressTest();
    }


    private void testUseCases() {
    	//Use cases:
    	//add same song twice
    	Song m_song = createRandomSong();
    	addSong(m_song);
    	addSong(m_song);
    	
    	//add song, remove it
    	m_song = createRandomSong();
    	addSong(m_song);
    	removeSong(m_song);
    	
    	//add song, change it
    	addSong(m_song);
    	m_song.setURL("http://www.newurl.com/");
    	modifySong(m_song);
    	
    	//remove nonexistent song
    	m_song = createRandomSong();
    	removeSong(m_song);
    	
    	//change nonexistent song (should return error flag)
    	m_song = createRandomSong();
    	modifySong(m_song);
	}
    
    //TODO :: Not very stressful yet.  Remove/lower sleep() time after basic functionality is okay.
    private void stressTest() {
    	//Random testing.
    	while(true){
    		switch (RN.nextInt(3)){
    		case 0: addRandomSong(); break;
    		case 1: randomModifyRandomSong(); break;
    		case 2: removeRandomSong(); break;
    		}
		try {Thread.sleep(500);} catch (InterruptedException e) {}
    	}
    }
    
	private Playlist getPlaylist(){
    	return new Playlist(); //TODO :: Implement
    }

    public BayouClient(int port){
    	m_server = new BayouServer(port); //TODO :: Add port number.
    }
    
    public BayouClient(BayouServer server){
    	m_server = server;
    }    
}
