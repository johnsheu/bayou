import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.io.Serializable;

public class BayouDB<K, V> implements Serializable
{
	private static final long serialVersionUID = 1L;

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
		versionVector = new HashMap<ServerID, Long>();
		omittedVector = new HashMap<ServerID, Long>();
		acceptStamp = 0L;
		modified = true;
	}

	public BayouDB( int initialCapacity )
	{
		writeData = new HashMap<K, V>( initialCapacity );
		writeLog = new LinkedList<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		versionVector = new HashMap<ServerID, Long>();
		omittedVector = new HashMap<ServerID, Long>();
		acceptStamp = 0L;
		modified = true;
	}

	public BayouDB( int initialCapacity, float loadFactor )
	{
		writeData = new HashMap<K, V>( initialCapacity, loadFactor );
		writeLog = new LinkedList<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		versionVector = new HashMap<ServerID, Long>();
		omittedVector = new HashMap<ServerID, Long>();
		acceptStamp = 0L;
		modified = true;
	}

	public BayouAEResponse<K, V> getUpdates( BayouAERequest request )
	{
		BayouAEResponse<K, V> response = new BayouAEResponse<K, V>();

	        Iterator<ServerID> servers = versionVector.keySet().iterator();

		HashMap<ServerID, Long> recvVersionVector = request.getRecvVV();

		long recvCSN = request.getRecvCSN();


		Iterator<BayouWrite<K, V>> writes;

		BayouWrite<K, V> write;
		BayouWrite<K, V> tempWrite;

		ServerID writeServer;
		ServerID server;

		WriteID wid;

		long writeAcceptStamp;
		long sendAcceptStamp;
		long recvAcceptStamp;

		boolean outOfRange;


	        if( CSN > recvCSN )
		{
			outOfRange = false;

			writes = writeLog.listIterator();

			while( writes.hasNext() && !outOfRange)
			{
				write = writes.next();
				wid = write.getWriteID();
				
				if( wid.getCSN() <= recvCSN )
					outOfRange = true;

				else
				{
					writeAcceptStamp = wid.getAcceptStamp();
					recvAcceptStamp = recvVersionVector.get( wid.getServerID() );

					if( recvAcceptStamp > writeAcceptStamp )
						response.addWrite( write );
					else
						response.addCommitNotification( wid );
				}
			}
		}



	        while( servers.hasNext() )
		{
			server = servers.next();

			outOfRange = false;
			writes = writeLog.descendingIterator();
			while( writes.hasNext() && !outOfRange )
			{
				write = writes.next();

				if( write.getWriteID().isCommitted() )
					outOfRange = true;

				else
				{
				    
					writeAcceptStamp = write.getWriteID().getAcceptStamp();
					sendAcceptStamp  = versionVector.get( server );
					recvAcceptStamp  = recvVersionVector.get( server );
		    
					if( writeAcceptStamp > recvAcceptStamp )
					{
						writeServer = write.getWriteID().getServerID() ;
						if( writeAcceptStamp < sendAcceptStamp && server.equals( writeServer ))
						{
							response.addWrite( write );
							outOfRange = true;
						}
					}
					else
						outOfRange = true;
				}
			}

		}
	        return response;
	}

	public synchronized void applyUpdates( BayouAEResponse<K, V> updates )
	{
		//  Full DB transfer
		HashMap<K, V> db = updates.getDatabase();
		if ( db != null )
		{
			writeData = db;
			omittedVector = updates.getOmittedVector();
			OSN = updates.getOSN();

			ListIterator<BayouWrite<K, V>> iter = writeLog.listIterator();
			while ( iter.hasNext() )
			{
				BayouWrite<K, V> write = iter.next();
				WriteID id = write.getWriteID();
				if ( id.getAcceptStamp() <= omittedVector.get( id.getServerID() ) )
					iter.remove();
			}
		}

		//  Commit notifications
		LinkedList<WriteID> cn = updates.getCommitNotifications();
		if ( cn != null )
		{
			//  Remove the writes to make committed from the write log
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
						CSN = cid.getCSN();
						id.setCSN( CSN );
						iter.remove();
						list.add( item );
						continue outer;
					}
				}
				break;
			}

			//  Add them back in, now as committed writes
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
					WriteID id = commit.getWriteID();
					if ( id.isCommitted() )
						CSN = id.getCSN();
					else
						versionVector.put( id.getServerID(), id.getAcceptStamp() );
					iter.add( item );
					iter.set( commit );
					continue outer;
				}
				break;
			}
		}

		//  Unknown writes
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
		WriteID id = write.getWriteID();
		versionVector.put( id.getServerID(), id.getAcceptStamp() );
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
/*
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
*/
    }
    
    public void dump()
    {
    	System.out.print( "DB : " + writeData.toString() + '\n' );
    	System.out.print( "WL: " + writeLog.toString() + '\n' );
    	System.out.print( "UL: " + undoLog.toString() + '\n' );
    	System.out.print( "VV: " + versionVector.toString() + '\n' );
    	System.out.print( "OV: " + omittedVector.toString() + '\n' );
    	System.out.print( "AS :" + acceptStamp + " CSN: " + CSN + " OSN: " + OSN + '\n' );
    }

	public static void main( String[] args )
	{
		BayouDB<String, String> db = new BayouDB<String, String>();
		ServerID self = new ServerID( null, 1L );
		ServerID other = new ServerID( null, 2L );
		
		db.addWrite( new BayouWrite<String, String>(
			"song1", "http://www.example1.com", BayouWrite.Type.ADD,
			new WriteID( db.getAcceptStamp(), self ) ) );
		
		db.addWrite( new BayouWrite<String, String>(
			"song2", "http://www.example2.com", BayouWrite.Type.ADD,
			new WriteID( db.getAcceptStamp(), self ) ) );
		
		db.addWrite( new BayouWrite<String, String>(
			"song3", "http://www.example3.com", BayouWrite.Type.ADD,
			new WriteID( db.getAcceptStamp(), self ) ) );
		
		db.addWrite( new BayouWrite<String, String>(
			"song3", "http://www.3example.com", BayouWrite.Type.EDIT,
			new WriteID( db.getAcceptStamp(), self ) ) );;
		
		db.addWrite( new BayouWrite<String, String>(
			"song2", "", BayouWrite.Type.DELETE,
			new WriteID( db.getAcceptStamp(), self ) ) );
		
		db.addWrite( new BayouWrite<String, String>(
			"song3", "http://www.example.3.com", BayouWrite.Type.EDIT,
			new WriteID( db.getAcceptStamp(), self ) ) );
		
		BayouAEResponse<String, String> response = new BayouAEResponse<String, String>();
		response.addWrite( new BayouWrite<String, String>( "song3", "", BayouWrite.Type.DELETE,
			new WriteID( 4L, other ) ) );
		
		db.applyUpdates( response );
		
		db.dump();
		System.out.print( "MAP: " + db.getMap().toString() + '\n' );
	}
}
