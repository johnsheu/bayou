import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class BayouClient{

    protected BayouServer<String, String> m_server;
    protected Scanner m_scanner;
    private final String SEPARATOR = "---------------";
    
    public static void main(String[] args){
    	printWelcomeMessage();
    	Scanner m_scanner = new Scanner(System.in);
    	boolean primary = false;
    	
    	int port = -1;
    	
    	if(args.length < 1)
    	{
    		System.out.print("Enter port number: ");
    		port = -1;
    		try{port = m_scanner.nextInt();}
	    	catch(java.util.InputMismatchException e){
	    		System.out.println("Invalid port number.  Exiting.");
	    		System.exit(1);}
	    	finally{m_scanner.nextLine();} //Advance past any leftover input.
    	}
    	else
    		port = Integer.parseInt(args[0]);
    	
    	try
    	{
    		ServerSocket s = ServerSocketChannel.open().socket();
    		s.bind(new InetSocketAddress(port));
    		s.close();
    	}
    	catch(IOException e)
    	{ 
    		System.out.println("Port probably already in use.  Exiting.");
    		System.exit(1);
    	}
    	
    	if(args.length < 2)
    	{
	    	System.out.println("Are you the first (primary) node in the system? (y/n)");
	    	primary = m_scanner.nextLine().contains("y");
    	}
    	else
    		primary = Boolean.parseBoolean(args[1]);
    	
		BayouClient client = new BayouClient(port, m_scanner, primary);
		client.startUI();
    	}

	protected static void printWelcomeMessage() {
		System.out.println("WELCOME...");
    	try {Thread.sleep(600);} catch (InterruptedException e) {}
    	System.out.println("TO BUFFALO BAYOU");
    	try {Thread.sleep(800);} catch (InterruptedException e) {}}

	protected void startUI() {
    	while(true){
	    	System.out.print(
	    			SEPARATOR + "\n" +
	    			"0 - Join a Network \n" +
	    			"1 - Add Song \n" +
	    			"2 - Modify Song \n" +
	    			"3 - Remove Song By Name \n" +
	    			"4 - List Songs \n" +
	    			"5 - Go Online \n" +
	    			"6 - Go Offline \n" +
	    			"7 - Test Menu \n" +
	    			"8 - Exit \n" +
	    			"Enter number: ");
	    	
	    	outer:
	    	switch(getMenuChoice()){
	    	case 0:
	    		if(!m_server.isStarted()) {
	    			System.out.println("Server must be started to join a network.");
	    			break outer;
	    		}
	    		appendAddressListPrompt();
	    		m_server.create(); 
	    		break;
	    	case 1: addSong(new Song(prompt(m_scanner, "title"), prompt(m_scanner, "URL"))); break;
	    	case 2: modifySong(new Song(prompt(m_scanner, "title"), prompt(m_scanner, "URL"))); break;
	    	case 3: removeSong(prompt(m_scanner, "title")); break;
	    	case 4: listSongs(); break;
	    	case 5: goOnline(); break;
	    	case 6: goOffline(); break;
	    	case 7: testMenu(); break;
	    	case 8: exit(); break;
	    	//case 0: System.out.println("You can't do this yet.  Sorry!"); break;
	    	default: System.out.println("Invalid option."); break;}}}

	protected void appendAddressListPrompt() {
		ArrayList<InetSocketAddress> list = m_server.getAddressList();
		list.add(addressPrompt());
		m_server.setAddressList(list);
	}
    
	private InetSocketAddress addressPrompt() {
		return new InetSocketAddress(prompt(m_scanner, "hostname"), Integer.parseInt(prompt(m_scanner, "port")));
	}

	protected void testMenu(){
		outer:
		while(true){
	    	System.out.print(
	    			SEPARATOR + "\n" +
	    			"1 - Force Anti-Entropy \n" +
	    			"2 - View Address List \n" +
	    			"3 - Add to Address List \n" +
	    			"4 - Back to Main Menu \n" +
	    			"Enter number: ");
	    	
	    	switch(getMenuChoice()){
	    	case 1: m_server.forceAntiEntropy(addressPrompt()); break;
	    	case 2: System.out.println(m_server.getAddressList()); break;
	    	case 3: appendAddressListPrompt(); break;
	    	case 4: break outer;
	    	default: System.out.println("Invalid option."); break;}
		}
	}

	protected int getMenuChoice() {
		int switch_val = -1;
		try{switch_val = m_scanner.nextInt();}
		catch(java.util.InputMismatchException e){}
		finally{m_scanner.nextLine();} //Advance past any leftover input.
		return switch_val;
	}
	
    protected void goOffline() {
		m_server.stop();
		System.out.println("You're in offline mode.");
	}

	protected void goOnline() {
		m_server.start();
		System.out.println("You're in online mode.");
	}

	protected void exit() {
    	System.out.println("Attempting to retire from the system.");
    	m_server.retire();
    	m_scanner.close();
    	System.out.println("You have left the system.");
    	System.out.println("Goodbye, we'll miss you.");
    	System.exit(0);}

	protected void listSongs() {
		System.out.println(SEPARATOR);
    	for(Map.Entry<String, String> ent : getPlaylist().entrySet()){
    		System.out.println("Title: " + ent.getKey() + "\nURL:" + ent.getValue() + "\n" + SEPARATOR);
    	}
    }
	
	protected String prompt(Scanner m_scanner, String promptText){
		System.out.print("Enter " + promptText + ": ");
		return m_scanner.nextLine();}

	public void addSong(Song song){m_server.add(song.getName(), song.getURL());}
    public void removeSong(String title){m_server.remove(title);}
    public void removeSong(Song song){removeSong(song.getName());}
    public void modifySong(Song song){m_server.edit(song.getName(), song.getURL());}

	protected Playlist getPlaylist()
	{
		Map<String, String> m = m_server.getAll();
		if( m != null )
			return new Playlist( m_server.getAll() );
		else
			return new Playlist();
	}

    public BayouClient(int port, Scanner inputReader, boolean primary){
    	m_server = new BayouServer<String, String>(port, primary);
    	goOnline();
    	//m_server.setAddressList(new ArrayList<InetSocketAddress>(Arrays.asList(new InetSocketAddress[]{new InetSocketAddress("localhost", port)})));
    	m_scanner = inputReader;}
    
    public BayouClient(BayouServer<String, String> server){
    	m_server = server;
    	goOnline();}}
