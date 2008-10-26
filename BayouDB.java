import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.SortedSet;


public class BayouDB
{
    private TreeSet<BayouWrite> writeLog;
    private HashMap playList;

    public BayouDB()
    {
	writeLog = new TreeSet<BayouWrite>();
	playList = new HashMap();
    }

    public HashMap rollBack( HashMap<ServerID, BigInteger> versionVector )
    {
	/*stub*/
	return new HashMap();
    }

    public void addWrite( BayouWrite write )
    {
	/*** update the playlist here ***/

	writeLog.add(write);
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
