import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.io.Serializable;

public class BayouDB<K, V> implements Serializable
{
    private HashMap<K, V> writeData;
	private LinkedList<BayouWrite<K, V>> writeLog;
	private LinkedList<BayouWrite<K, V>> undoLog;

	private long acceptStamp;
	private HashMap<ServerID, Long> versionVector;
	private HashMap<ServerID, Long> omittedVector;
	private long CSN;
	private long OSN;

	private boolean modified;
    
	public BayouDB()
	{
		writeData = new HashMap<K, V>();
		writeLog = new LinkedList<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		acceptStamp = 0L;
		modified = true;
	}

	public BayouDB( int initialCapacity )
	{
		writeData = new HashMap<K, V>( initialCapacity );
		writeLog = new LinkedList<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		acceptStamp = 0L;
		modified = true;
	}

	public BayouDB( int initialCapacity, float loadFactor )
	{
		writeData = new HashMap<K, V>( initialCapacity, loadFactor );
		writeLog = new LinkedList<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		acceptStamp = 0L;
		modified = true;
	}

	public BayouAEResponse<K, V> getUpdates( BayouAERequest request )
	{
		BayouAEResponse<K, V> response = new BayouAEResponse<K, V>();
	/*stub*/
        /*Note: Changes in the returned set are reflected in this set*/

        Iterator<ServerID> servers = sendVersionVector.keySet().iterator();

        TreeSet<BayouWrite> updates = new TreeSet<BayouWrite>();

        //KAREN - handle case with CSN
        if( sendCSN.compareTo( recvCSN ) > 0 )
	{
	    
	}

        WriteID firstUncommittedWID = new WriteID( new Long( 0 ), new ServerID( null, new Long( 0 )));
        BayouWrite firstUncommitted = new BayouWrite( "", "", BayouWrite.Type.EDIT, firstUncommittedWID );
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

	public synchronized void applyUpdates( BayouAEResponse<K, V> updates )
	{
		HashMap<K, V> db = updates.getDatabase();
		if ( db != null )
		{
			writeData = db;
			omittedVector = updates.getOmittedVector();
			OSN = updates.getOSN();
		}

		LinkedList<WriteID> cn = updates.getCommitNotifications();
		if ( cn != null )
		{
			Collections.sort( cn );
			ListIterator<BayouWrite<K, V>> iter = writeLog.listIterator();
			Iterator<WriteID> citer = cn.iterator();
			LinkedList<BayouWrite<K, V>> list = new LinkedList<BayouWrite<K, V>>();
outer:
			while ( citer.hasNext() )
			{
				WriteID cid = citer.next();
				while ( iter.hasNext() )
				{
					BayouWrite<K, V> item = iter.next();
					WriteID id = item.getWriteID();
					if ( id.getAcceptStamp() == cid.getAcceptStamp() &&
						id.getServerID().equals( cid.getServerID() ) )
					{
						id.setCSN( cid.getCSN() );
						iter.remove();
						list.add( item );
						continue outer;
					}
				}
				break;
			}

			iter = writeLog.listIterator();
			Iterator<BayouWrite<K, V>> liter = list.iterator();
outer:
			while ( liter.hasNext() )
			{
				BayouWrite<K, V> commit = liter.next();
				while ( iter.hasNext() )
				{
					BayouWrite<K, V> item = iter.next();
					if ( item.compareTo( commit ) <= 0 )
						continue;
					iter.add( item );
					iter.set( commit );
					continue outer;
				}
				break;
			}
		}

		LinkedList<BayouWrite<K, V>> w = updates.getWrites();
		if ( w != null )
		{
			ListIterator<BayouWrite<K, V>> iter = writeLog.listIterator();
			Iterator<BayouWrite<K, V>> witer = w.iterator();
outer:
			while ( witer.hasNext() )
			{
				BayouWrite<K, V> write = witer.next();
				while ( iter.hasNext() )
				{
					BayouWrite<K, V> item = iter.next();
					if ( item.compareTo( write ) <= 0 )
						continue;
					iter.add( write );
					iter.set( write );
					continue outer;
				}
				break;
			}
		}
	}

	public synchronized void addWrite( BayouWrite<K, V> write )
	{
		writeLog.add( write );
		acceptStamp += 1L;
		//  Do write-apply heuristics here
	}

	public long getAcceptStamp()
	{
		return acceptStamp;
	}

	public synchronized HashMap<K, V> getMap()
	{
		HashMap<K, V> entries = (HashMap<K, V>)writeData.clone();
		Iterator<BayouWrite<K, V>> iter = writeLog.iterator();
		while ( iter.hasNext() )
		{
			applyWrite( iter.next(), entries );
			//  Uncomment to get the apply-writes-on-render semantics
			//iter.remove();
		}
		return entries;
	}

	private boolean applyWrite( BayouWrite<K, V> write, HashMap<K, V> data )
	{
		switch ( write.getType() )
		{
			case ADD:
				if ( data.containsKey( write.getKey() ) )
					return false;
				data.put( write.getKey(), write.getValue() );
				break;
			case EDIT:
				if ( !data.containsKey( write.getKey() ) )
					return false;
				data.put( write.getKey(), write.getValue() );
				break;
			case DELETE:
				if ( !data.containsKey( write.getKey() ) )
					return false;
				data.remove( write.getKey() );
				break;
			default:
				return false;
		}
		return true;
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


    public void updateCSN( WriteID wid, Long csn )
    {
	BayouWrite bw1 = new BayouWrite( "", "", BayouWrite.Type.EDIT, wid );
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

}
