import java.util.Map;
import java.util.Scanner;

public class BayouClient{

    private BayouServer m_server;
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
	    	default: System.out.println("Invalid option."); break;}}}
    
	//TODO :: Retire server and exit system.  Use different exit statuses and messages if retire fails.  Implement on BayouServer.
    private void exit() {
    	m_scanner.close();
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

	public void addSong(Song song){}
    public void removeSong(String title){}
    public void removeSong(Song song){removeSong(song.getTitle());}
    public void modifySong(Song song){}

    //TODO :: Implement on BayouServer
	protected Playlist getPlaylist(){
		return new Playlist();}

    public BayouClient(int port, Scanner inputReader){
    	m_server = new BayouServer(port);
    	m_scanner = inputReader;}
    
    public BayouClient(BayouServer server){
    	m_server = server;}}
