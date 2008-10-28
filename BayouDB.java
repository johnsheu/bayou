import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.Serializable;

public class BayouDB<K, V> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private HashMap<K, V> writeData;
	private TreeSet<BayouWrite<K, V>> writeLog;
	private LinkedList<BayouWrite<K, V>> undoLog;

	private long acceptStamp;
	private HashMap<ServerID, Long> versionVector;
	private HashMap<ServerID, Long> omittedVector;
	private long CSN;
	private long OSN;
	
	//For caching current rendering of the playlist/map.
	private boolean caching = false;
	private HashMap<K, V> renderedData = null;
	private boolean modified = true;
    
	public BayouDB()
	{
		writeData = new HashMap<K, V>();
		writeLog = new TreeSet<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		versionVector = new HashMap<ServerID, Long>();
		omittedVector = new HashMap<ServerID, Long>();
		acceptStamp = 0L;
	}
	
	public BayouDB( boolean useCaching )
	{
		this();
		caching = useCaching;
	}

	public BayouAEResponse<K, V> getUpdates( BayouAERequest request )
	{
		BayouAEResponse<K, V> response = new BayouAEResponse<K, V>();
		return response;
/*
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

			writes = writeLog.iterator();

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
*/
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

			Iterator<BayouWrite<K, V>> iter = writeLog.iterator();
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
			Iterator<WriteID> citer = cn.iterator();
			while ( citer.hasNext() )
			{
				WriteID cid = citer.next();
				BayouWrite<K, V> item = new BayouWrite<K, V>( null, null,
					BayouWrite.Type.ADD,
					new WriteID( cid.getAcceptStamp(), cid.getServerID() ) );
				item = writeLog.ceiling( item );
				writeLog.remove( item );
				CSN = cid.getCSN();
				item.getWriteID().setCSN( CSN );
				writeLog.add( item );
			}
		}

		//  Unknown writes
		LinkedList<BayouWrite<K, V>> w = updates.getWrites();
		if ( w != null )
		{
			Iterator<BayouWrite<K, V>> witer = w.iterator();
			while ( witer.hasNext() )
				writeLog.add( witer.next() );
		}
	}
	
	public synchronized BayouAERequest makeRequest()
	{
		return new BayouAERequest( OSN, CSN, versionVector );
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
		if(modified || renderedData == null || !caching)
			renderData();
		
		return renderedData;
	}

	private void renderData() {
		renderedData = new HashMap<K, V>(writeData);
		
		for(BayouWrite<K, V> write : writeLog)
			applyWrite( write, renderedData );
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
	
	private BayouWrite<K, V> flipWrite( BayouWrite<K, V> write, HashMap<K, V> data )
	{
		BayouWrite<K, V> flip = null;
		switch( write.getType() )
		{
			case ADD:
				flip = new BayouWrite<K, V>( write.getKey(), write.getValue(),
					BayouWrite.Type.DELETE, write.getWriteID() );
				break;
			case EDIT:
				flip = new BayouWrite<K, V>( write.getKey(), data.get( write.getKey() ),
					BayouWrite.Type.EDIT, write.getWriteID() );
				break;
			case DELETE:
				flip = new BayouWrite<K, V>( write.getKey(), data.get( write.getKey() ),
					BayouWrite.Type.ADD, write.getWriteID() );
				break;
			default:
				break;
		}
		return flip;
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
		
		db.addWrite( new BayouWrite<String, String>(
			"song4", "http://www.example.4.com", BayouWrite.Type.EDIT,
			new WriteID( db.getAcceptStamp(), self ) ) );
		
		BayouAEResponse<String, String> response = new BayouAEResponse<String, String>();
		
		response.addCommitNotification( new WriteID( 0L, 0L, self ) );
		
		response.addCommitNotification( new WriteID( 1L, 1L, self ) );
		
		response.addCommitNotification( new WriteID( 2L, 2L, self ) );
		
		response.addWrite( new BayouWrite<String, String>( "song5", "http://www.example5.com",
			BayouWrite.Type.ADD, new WriteID( 3L, 0L, other ) ) );
		
		response.addWrite( new BayouWrite<String, String>( "song3", "", BayouWrite.Type.DELETE,
			new WriteID( 4L, other ) ) );

		response.addWrite( new BayouWrite<String, String>( "song4", "http://www.example4.com",
			BayouWrite.Type.ADD, new WriteID( 5L, other ) ) );
		
		db.applyUpdates( response );
		
		db.dump();
		System.out.print( "MAP: " + db.getMap().toString() + '\n' );
	}

	public boolean isCaching() {
		return caching;
	}

	public void setCaching(boolean caching) {
		this.caching = caching;
	}
}
