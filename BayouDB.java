import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.SortedSet;
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
	
	private boolean primary;
	
	//For caching current rendering of the playlist/map.
	private boolean caching;
	private HashMap<K, V> renderedData;
	private boolean modified;
	private long truncateLimit;
    
	public BayouDB()
	{
		writeData = new HashMap<K, V>();
		writeLog = new TreeSet<BayouWrite<K, V>>();
		undoLog = new LinkedList<BayouWrite<K, V>>();
		versionVector = new HashMap<ServerID, Long>();
		omittedVector = new HashMap<ServerID, Long>();
		renderedData = null;
		primary = false;
		modified = true;
		caching = false;
		acceptStamp = 0L;
		truncateLimit = 16L;
	}
	
	public BayouDB( boolean useCaching )
	{
		this();
		caching = useCaching;
	}
	
	public BayouDB( boolean useCaching, long truncate )
	{
		this( useCaching );
		truncateLimit = truncate;
	}

	public BayouAEResponse<K, V> getUpdates( BayouAERequest request )
	{
		BayouAEResponse<K, V> response = new BayouAEResponse<K, V>();

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

		if( OSN > recvCSN )
		{
		    //KAREN - rollback

		    response.addDatabase( writeData );
		    response.addOSN( OSN );

		    //KAREN - need to set version vector and CSN 
		    
		}

	        if( CSN > recvCSN )
		{
			outOfRange = false;

			writes = writeLog.iterator();

			long writeCSN;

			while( writes.hasNext() && !outOfRange )
			{
				write = writes.next();

				wid = write.getWriteID();

				writeCSN = wid.getCSN();

				if( writeCSN == CSN )
				    outOfRange = true;

				if( writeCSN > recvCSN )
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


	        Iterator<ServerID> servers = versionVector.keySet().iterator();

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
						if( server.equals( writeServer ))
						{
							response.addWrite( write );
						}
					}
					else
					{
						outOfRange = true;
						System.out.println( "KAREN IN ELSE" );
					}
				}
			}

		}
		return response;
	}

	public synchronized void applyUpdates( BayouAEResponse<K, V> updates )
	{
		modified = true;
		
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
		
		long oldCSN = CSN;

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
				CSN = Math.max( CSN, cid.getCSN() );
				item.getWriteID().setCSN( CSN );
				writeLog.add( item );
			}
		}

		//  Unknown writes
		LinkedList<BayouWrite<K, V>> w = updates.getWrites();
		if ( w != null )
		{
			Iterator<BayouWrite<K, V>> witer = w.iterator();
			
			if ( !primary )  //  Not the primary replica
			{
				while ( witer.hasNext() )
				{
					BayouWrite<K, V> write = witer.next();
					writeLog.add( write );
					if ( write.getWriteID().isCommitted() )
						CSN = Math.max( CSN, write.getWriteID().getCSN() );
					WriteID wid = write.getWriteID();
					Long as = versionVector.get( wid.getServerID() );
					if ( as == null || as.compareTo( wid.getAcceptStamp() ) < 0 )
						versionVector.put( wid.getServerID(), wid.getAcceptStamp() );
				}
			}
			else  //  Primary replica
			{
				while ( witer.hasNext() )
				{
					BayouWrite<K, V> write = witer.next();
					write.getWriteID().setCSN( CSN++ );
					WriteID wid = write.getWriteID();
					Long as = versionVector.get( wid.getServerID() );
					if ( as == null || as.compareTo( wid.getAcceptStamp() ) < 0 )
						versionVector.put( wid.getServerID(), wid.getAcceptStamp() );
					writeLog.add( write );
				}
			}
		}
		
		//  Truncate write log as per policy
		BayouWrite<K, V> from = new BayouWrite<K, V>( null, null, BayouWrite.Type.ADD,
			new WriteID( oldCSN + 1L, -1L, new ServerID( null, 0L ) ) );
		BayouWrite<K, V> to = new BayouWrite<K, V>( null, null, BayouWrite.Type.ADD,
				new WriteID( CSN + 1L, -1L, new ServerID( null, 0L ) ) );
		SortedSet<BayouWrite<K, V>> subset = writeLog.subSet( from, to );
		for ( BayouWrite<K,V> write : subset )
			applyWrite( write, writeData );
		
		if ( OSN < CSN - truncateLimit )
		{
			to.getWriteID().setCSN( CSN - truncateLimit );
			writeLog.headSet( to ).clear();
			OSN = CSN - truncateLimit;
		}
	}
	
	public synchronized BayouAERequest makeRequest()
	{
		return new BayouAERequest( OSN, CSN, versionVector );
	}

	public synchronized void addWrite( BayouWrite<K, V> write )
	{
		modified = true;
		if ( primary )
			write.getWriteID().setCSN( CSN++ );
		writeLog.add( write );
		acceptStamp += 1L;
		WriteID id = write.getWriteID();
		versionVector.put( id.getServerID(), id.getAcceptStamp() );
	}

	public long getAcceptStamp()
	{
		return acceptStamp;
	}

	public synchronized HashMap<K, V> getMap()
	{
		if ( modified || !caching )
			renderData();
		
		return renderedData;
	}

	private void renderData()
	{
		renderedData = new HashMap<K, V>( writeData );
		
		for ( BayouWrite<K, V> write : writeLog )
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
	
	public boolean isPrimary()
	{
		return primary;
	}
	
	public void setPrimary( boolean primary )
	{
		this.primary = primary;
	}

	public boolean isCaching()
	{
		return caching;
	}

	public void setCaching( boolean caching )
	{
		this.caching = caching;
	}
	
	public long getTruncate()
	{
		return truncateLimit;
	}
	
	public void setTruncate( long truncate )
	{
		if ( truncate < 0 )
			truncateLimit = 0;
		else
			truncateLimit = truncate;
	}
    
    public String dump()
    {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append( "DB : " + writeData.toString() + '\n' );
    	buffer.append( "WL: " + writeLog.toString() + '\n' );
    	buffer.append( "UL: " + undoLog.toString() + '\n' );
    	buffer.append( "VV: " + versionVector.toString() + '\n' );
    	buffer.append( "OV: " + omittedVector.toString() + '\n' );
    	buffer.append( "AS :" + acceptStamp + " CSN: " + CSN + " OSN: " + OSN + '\n' );
    	return buffer.toString();
    }

	public static void main( String[] args )
	{
		BayouDB<String, String> db = new BayouDB<String, String>();
		ServerID self = new ServerID( null, 1L );
		ServerID other = new ServerID( null, 2L );
		
		//db.setPrimary( true );
		
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
		
		System.out.print( db.dump() );
		System.out.print( "MAP: " + db.getMap().toString() + '\n' );
	}
}
