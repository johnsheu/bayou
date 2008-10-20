import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class BayouClient{

    private BayouServer m_server;

    public static void main(String[] args){
    	System.out.println("WELCOME...");
    	try {Thread.sleep(600);} catch (InterruptedException e) {}
    	System.out.println("TO BUFFALO BAYOU");
    	try {Thread.sleep(800);} catch (InterruptedException e) {}
    	
    	Scanner m_scanner = new Scanner(System.in);
    	System.out.print("Enter port number: ");
    	BayouClient client = new BayouClient(m_scanner.nextInt());
    	m_scanner.nextLine();
    	
    	while(true){
	    	System.out.print("Enter number: \n" +
	    			"1: Add Song \n" +
	    			"1: Modify Song \n" +
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

    //TODO :: Retire server and exit system.  Use different exit statuses and messages if retire fails.  Implement on BayouServer.
    private void exit() {
    	System.exit(0);
	}

	private void listSongs() {
    	final String SEPARATOR = "\n---------------";
    	System.out.println(SEPARATOR);
    	for(Map.Entry<String, String> ent : getPlaylist().entrySet())
    		System.out.println("Title: " + ent.getKey() + "\nURL:" + ent.getValue() + SEPARATOR);
    }

	private void removePrompt(Scanner m_scanner) {
		m_scanner.nextLine(); //Advance past any leftover input.
		System.out.print("Enter title: ");
		removeSong(m_scanner.nextLine());
	}

	private void addPrompt(Scanner m_scanner) {
		m_scanner.nextLine(); //Advance past any leftover input.
		Song m_song = new Song("", "");
		System.out.print("Enter title: ");
		m_song.setTitle(m_scanner.nextLine());
		System.out.print("Enter URL: ");
		m_song.setURL(m_scanner.nextLine());
		addSong(m_song);
	}

	public void addSong(Song song){}
    public void removeSong(String title){}
    public void removeSong(Song song){removeSong(song.getTitle());}
    public void modifySong(Song song){}

    //TODO :: Implement on BayouServer
	protected Playlist getPlaylist(){
		return new Playlist();
    }

    public BayouClient(int port){
    	m_server = new BayouServer(port);
    }
    
    public BayouClient(BayouServer server){
    	m_server = server;
    }    
}
