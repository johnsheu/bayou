import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.SortedSet;


public class BayouDB
{
    private TreeSet<BayouWrite> writeLog;
    private Playlist playlist;
    private boolean modified;
    
    
    public BayouDB()
    {
	writeLog = new TreeSet<BayouWrite>();
	playlist = new Playlist();
	modified = true;
    }

    public HashMap rollBack( HashMap<ServerID, BigInteger> versionVector )
    {
	/*stub*/
    modified = true;
	return new HashMap();
    }

    public void addWrite( BayouWrite write )
    {
	/*** update the playlist here ***/
    
	writeLog.add(write);
    modified = true;
    }

    public void updateCSN( WriteID wid, Long csn )
    {
	BayouWrite bw1 = new BayouWrite( new BayouData(), BayouWrite.Type.EDIT, wid );
	if( writeLog.contains( bw1 ))
	{
	    BayouWrite bw2 = writeLog.floor( bw1 );
	    writeLog.remove( bw2 );
	    bw2.getWriteID().setCSN( csn );
	    writeLog.add( bw2 );
	}
	else
	    System.out.println( "ZOMG YOU SHOULD NOT BE HERE" );
	modified = true;
    }

    public TreeSet<BayouWrite> getUpdateList( HashMap<ServerID, Long> sendVersionVector, Long sendCSN, HashMap<ServerID, Long> recvVersionVector, Long recvCSN )
    {
	/*stub*/
        /*Note: Changes in the returned set are reflected in this set*/

        Iterator<ServerID> servers = sendVersionVector.keySet().iterator();

        TreeSet<BayouWrite> updates = new TreeSet<BayouWrite>();

        //KAREN - handle case with CSN
        if( sendCSN.compareTo( recvCSN ) > 0 )
	{
	    
	}

        WriteID firstUncommittedWID = new WriteID( new Long( 0 ), new ServerID( null, new Long( 0 )));
        BayouWrite firstUncommitted = new BayouWrite( new BayouData(), BayouWrite.Type.EDIT, firstUncommittedWID );
        TreeSet<BayouWrite> uncommittedWrites = new TreeSet<BayouWrite>( writeLog.tailSet( firstUncommitted ));

	BayouWrite write;
	Long writeAcceptStamp;
	Long sendAcceptStamp;
	Long recvAcceptStamp;
	ServerID writeServer;
	ServerID server;
	Iterator<BayouWrite> writes;
	boolean aboveRange;
        while( servers.hasNext() )
	{
		server = servers.next();

		aboveRange = true;
		writes = uncommittedWrites.descendingIterator();
		while( writes.hasNext() && aboveRange )
		{
		    write = writes.next();
		    writeAcceptStamp = write.getWriteID().getAcceptStamp();
		    sendAcceptStamp  = sendVersionVector.get( server );
		    recvAcceptStamp  = recvVersionVector.get( server );
		    
		    if( writeAcceptStamp > recvAcceptStamp )
		    {
			writeServer = write.getWriteID().getServerID() ;
			if( writeAcceptStamp < sendAcceptStamp && server.equals( writeServer ))
			{
			    updates.add( write );
			    aboveRange = false;
			}
		    }
		    else
			aboveRange = false;
		}

	}
        return updates;
    }
    
    public Playlist getPlaylist(){
    	if(modified)
    		renderPlaylist();
    	return playlist;
    }

    //Renders a new Playlist from the current writeLog. 
    //WILL NOT WORK YET.  SEE PROBLEMS BELOW.
    private void renderPlaylist() {
    	playlist = new Playlist();
    	
    	//This probably won't iterate in the right order.  Need a list, I assume.
    	for(BayouWrite write : writeLog){
    		write = (BayouWrite<Song>) write; //Why doesn't this fix the need to cast each object from getData()?
    		switch(write.getType()){
    		case ADD: 
    			Song songToAdd = (Song) write.getData(); //Is it okay to cast all "data" objects to songs?  I'm just assuming that, that's what they are...
    			playlist.put(songToAdd.getName(), songToAdd.getURL());
    			break;
    		case EDIT: 
    			Song songToEdit = (Song) write.getData();
    			playlist.put(songToEdit.getName(), songToEdit.getURL());
    			break;
    		case DELETE: 
    			Song songToRemove = (Song) write.getData();
    			playlist.remove(songToRemove.getName());
    			break;
    		default: break; //Type is CREATE or RETIRE, which don't matter here.
    		}
    	}
    	
    	modified = false;
    }

	/*** methods for testing purposes only ***/
    public void printTreeSet()
    {
	BayouWrite bw;
	Long bi;
	int i;

	System.out.println( "Tree Set is: " );
	System.out.println( "\n\n" );
	Iterator it = writeLog.iterator();
	while( it.hasNext() )
	{
	    bw = (BayouWrite)it.next();
	    bi = bw.getWriteID().getCSN();
	    if( bi == null )
		i = -1;
	    else 
		i = bi.intValue();

	    int as;
	    Long las = bw.getWriteID().getAcceptStamp();
	    if( las == null )
		as = -1;
	    else
		as = las.intValue();
		
	    System.out.println( "CSN is " + i );
	    System.out.println( "ServerID is " + bw.getWriteID().getServerID() );
	    System.out.println( "Accept Stamp is " + as );
	    System.out.println();
	}

	System.out.println( "Done with Tree Set\n\n\n\n" );	    
    }
}
