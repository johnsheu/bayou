import java.util.Map;
import java.util.Scanner;

public class BayouClient{

    private BayouServer<String, String> m_server;
    private Scanner m_scanner;
    
    public static void main(String[] args){
    	printWelcomeMessage();
    	Scanner m_scanner = new Scanner(System.in);
    	
    	System.out.print("Enter port number: ");
    	int port = -1;
    	try{port = m_scanner.nextInt();}
    	catch(java.util.InputMismatchException e){
    		System.out.println("Invalid port number.  Exiting.");
    		System.exit(1);}
    	finally{m_scanner.nextLine();} //Advance past any leftover input.
    	
    	BayouClient client = new BayouClient(port, m_scanner);
    	client.startUI();}

	private static void printWelcomeMessage() {
		System.out.println("WELCOME...");
    	try {Thread.sleep(600);} catch (InterruptedException e) {}
    	System.out.println("TO BUFFALO BAYOU");
    	try {Thread.sleep(800);} catch (InterruptedException e) {}}

	private void startUI() {
    	while(true){
	    	System.out.print(
	    			"0 - Connect to a Network Manually \n" +
	    			"1 - Add Song \n" +
	    			"2 - Modify Song \n" +
	    			"3 - Remove Song By Name \n" +
	    			"4 - List Songs \n" +
	    			"5 - Exit \n" +
	    			"Enter number: ");
	    	
	    	int switch_val = -1;
	    	try{switch_val = m_scanner.nextInt();}
	    	catch(java.util.InputMismatchException e){}
	    	finally{m_scanner.nextLine();} //Advance past any leftover input.
	    	
	    	switch(switch_val){
	    	case 1: addSong(new Song(prompt(m_scanner, "title"), prompt(m_scanner, "URL"))); break;
	    	case 2: modifySong(new Song(prompt(m_scanner, "title"), prompt(m_scanner, "URL"))); break;
	    	case 3: removeSong(prompt(m_scanner, "title")); break;
	    	case 4: listSongs(); break;
	    	case 5: exit(); break;
	    	case 0: System.out.println("You can't do this yet.  Sorry!"); break;
	    	default: System.out.println("Invalid option."); break;}}}
    
    private void exit() {
    	System.out.println("Attempting to retire from the system.");
    	m_server.retire();
    	m_scanner.close();
    	System.out.println("You have left the system.");
    	System.out.println("Goodbye, we'll miss you.");
    	System.exit(0);}

	private void listSongs() {
    	final String SEPARATOR = "\n---------------";
    	System.out.println(SEPARATOR);
    	for(Map.Entry<String, String> ent : getPlaylist().entrySet())
    		System.out.println("Title: " + ent.getKey() + "\nURL:" + ent.getValue() + SEPARATOR);}
		
	private String prompt(Scanner m_scanner, String promptText){
		System.out.print("Enter " + promptText + ": ");
		return m_scanner.nextLine();}

	public void addSong(Song song){m_server.add(song.getName(), song.getURL());}
    public void removeSong(String title){m_server.remove(title);}
    public void removeSong(Song song){removeSong(song.getName());}
    public void modifySong(Song song){m_server.edit(song.getName(), song.getURL());}

    //TODO :: Implement on BayouServer
	protected Playlist getPlaylist(){
		return new Playlist(m_server.getAll());}

    public BayouClient(int port, Scanner inputReader){
    	m_server = new BayouServer<String, String>(port);
    	m_server.start();
    	m_server.create();
    	m_scanner = inputReader;}
    
    public BayouClient(BayouServer<String, String> server){
    	m_server = server;
    	if(!m_server.isStarted())
    		m_server.start();
    	if(!m_server.isCreated())
    		m_server.create();}}
