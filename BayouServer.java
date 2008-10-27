import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.io.Serializable;

public class BayouServer<K, V>
{
	private static final long serialVersionUID = 1L;

	private class BayouServerThread extends Thread
	{
		public BayouServerThread()
		{

		}

		public void run()
		{
			while ( !isInterrupted() )
			{
				try
				{
					sleep( 100 );
				}
				catch ( InterruptedException ex )
				{
					return;
				}

				Message message;
				while ( ( message = communicator.readMessage() ) != null )
				{
					//  Do whatever the message requests us to do
					if ( message instanceof ManagerMessage )
					{
						/*
						 * These are the actions for ManagerMessage.  These are
						 * generally meta-actions useful for debugging.  A
						 * Bayou network will work happily on its own without
						 * any of these messages ever getting sent.
						 */
						ManagerMessage msg = (ManagerMessage)message;
						switch( msg.getType() )
						{
							case GET_DB:
							{
								ManagerMessage reply = new ManagerMessage();
								reply.setAddress( msg.getAddress() );
								msg.makeMessage(
									ManagerMessage.Type.DB_DUMP, database,
									null, null );
								communicator.sendMessage( msg );
								break;
							}
							case GET_TALKING:
							{
								ManagerMessage reply = new ManagerMessage();
								reply.setAddress( msg.getAddress() );
								msg.makeMessage(
									ManagerMessage.Type.IS_TALKING, null,
									performAntiEntropy, null );
								communicator.sendMessage( msg );
								break;
							}
							case SET_TALKING:
							{
								performAntiEntropy =
									msg.getBoolean().booleanValue();
								break;
							}
							case GET_UPDATING:
							{
								ManagerMessage reply = new ManagerMessage();
								reply.setAddress( msg.getAddress() );
								msg.makeMessage(
									ManagerMessage.Type.IS_UPDATING, null,
									performUpdates, null );
								communicator.sendMessage( msg );
								break;
							}
							case SET_UPDATING:
							{
								performUpdates =
									msg.getBoolean().booleanValue();
								break;
							}
							case GET_ADDRESSES:
							{
								ManagerMessage reply = new ManagerMessage();
								reply.setAddress( msg.getAddress() );
								msg.makeMessage(
									ManagerMessage.Type.ADDRESSES_DUMP, null,
									null, addresses );
								communicator.sendMessage( msg );
								break;
							}
							case SET_ADDRESSES:
							{
								addresses = msg.getAddresses();
								break;
							}
							case RETIRE:
							{
								break;
							}
						}
					}
					else if ( message instanceof ClientMessage )
					{

					}
				}
			}
		}
	}

	private ServerID serverID;
	private Long acceptStampCounter;
	private Long largestKnownCSN;
	private HashMap<ServerID, Long> omittedVector;

	private Communicator communicator;

	private BayouServerThread serverThread = null;

	private BayouDB<K, V> database;

	private boolean performUpdates = true;

	private boolean performAntiEntropy = true;

	private ArrayList<InetSocketAddress> addresses =
		new ArrayList<InetSocketAddress>();

	private HashMap<ServerID, WriteID> versionVector;
	
	public BayouServer( int port )
	{
		database = new BayouDB<K, V>();
		communicator = new Communicator( port );
	}

	public BayouServer( int port, int initialCapacity )
	{
		database = new BayouDB<K, V>( initialCapacity );
		communicator = new Communicator( port );
	}

	public BayouServer( int port, int initialCapacity, float loadFactor )
	{
		database = new BayouDB<K, V>( initialCapacity, loadFactor );
		communicator = new Communicator( port );
	}

	public synchronized void start()
	{
		if ( serverThread != null )
			return;

		communicator.start();

		serverThread = new BayouServerThread();
		serverThread.start();
	}

	public synchronized void stop()
	{
		if ( serverThread == null )
			return;

		serverThread.interrupt();

		communicator.stop();

		try
		{
			serverThread.join();
		}
		catch ( InterruptedException ex )
		{

		}
	}

	public synchronized boolean isStarted()
	{
		return serverThread != null;
	}

	public boolean add( K key, V value )
	{
		return false;
	}

	public V put( K key, V value )
	{
		return null;
	}

	public V get( K key )
	{
		return null;
	}

	public Map<K, V> getAll()
	{
		return null;
	}

	public int size()
	{
		return 0;
	}

	public boolean edit( K key, V value )
	{
		return false;
	}

	public V remove( K key )
	{
		return null;
	}

	void clear()
	{

	}

	public void retire()
	{

	}

	public List<InetSocketAddress> getAddressList()
	{
		return addresses;
	}

	public void setAddressList( List<InetSocketAddress> list )
	{
		addresses = new ArrayList<InetSocketAddress>( list );
	}


	public static void main( String[] args )
	{
		if ( args.length != 1 )
		{
			System.out.print( "usage: java BayouServer <port>\n" );
			System.out.print( "       <port> - port number to listen on\n\n" );
			System.exit( 0 );
		}

		int port = 0;
		try
		{
			port = Integer.parseInt( args[0] );
		}
		catch ( NumberFormatException ex )
		{
			ex.printStackTrace();
			System.exit( 1 );
		}

		BayouServer<String, String> server = new BayouServer<String, String>( port );
		server.start();
	}
}

