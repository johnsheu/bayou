import java.util.ArrayList;
import java.util.Map;
import java.util.Random; 
import java.net.InetSocketAddress;

public class BayouServer<K, V>
{
	private static final long serialVersionUID = 1L;

	private class BayouServerComm extends Thread
	{
		public BayouServerComm()
		{

		}

		public void run()
		{
			while ( !isInterrupted() )
			{
				try
				{
					Message message = communicator.readMessageBlocking();
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
								reply.makeMessage(
									ManagerMessage.Type.ADDRESSES_DUMP, null,
									null, addresses );
								communicator.sendMessage( reply );
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
					else if ( message instanceof BayouAERequest )
					{
						BayouAERequest msg = (BayouAERequest)message;
						BayouAEResponse<K, V> reply = database.getUpdates( msg );
						reply.setAddress( msg.getAddress() );
						communicator.sendMessage( reply );
					}
					else if ( message instanceof BayouAEResponse<?, ?> )
					{
						BayouAEResponse<K, V> msg = (BayouAEResponse<K, V>)message;
						database.applyUpdates( msg );
					}
				}
				catch ( InterruptedException ex )
				{
					return;
				}
			}
		}
	}
	
	private class BayouServerActor extends Thread
	{
		Random random = null;
		
		public BayouServerActor()
		{
			random = new Random();
		}
		
		public void run()
		{
			while ( !isInterrupted() )
			{
				synchronized( addresses )
				{
					BayouAERequest message = database.makeRequest();
					message.setAddress( addresses.get( random.nextInt( addresses.size() ) ) );
					communicator.sendMessage( message );
				}
				
				try
				{
					sleep( sleepTime );
				}
				catch ( InterruptedException ex )
				{
					return;
				}
			}
		}
	}

	private Communicator communicator = null;

	private BayouServerComm serverComm = null;
	
	private BayouServerActor serverAct = null;

	private BayouDB<K, V> database = null;

	private boolean performUpdates = true;

	private boolean performAntiEntropy = true;

	private ArrayList<InetSocketAddress> addresses = null;

	private ServerID serverID = null;
	
	private long sleepTime = 100L;

	public BayouServer( int port )
	{
		database = new BayouDB<K, V>();
		communicator = new Communicator( port );
		addresses = new ArrayList<InetSocketAddress>();
	}

	public void start()
	{
		synchronized ( communicator )
		{
			if ( serverComm != null )
				return;

			communicator.start();

			serverComm = new BayouServerComm();
			serverComm.start();
			
			serverAct = new BayouServerActor();
			serverAct.start();
		}
	}

	public void stop()
	{
		synchronized ( communicator )
		{
			if ( serverComm == null )
				return;

			serverComm.interrupt();
			serverAct.interrupt();

			communicator.stop();

			try
			{
				serverComm.join();
			}
			catch ( InterruptedException ex )
			{

			}
			
			try
			{
				serverAct.join();
			}
			catch ( InterruptedException ex )
			{
				
			}
		}
	}

	public boolean isStarted()
	{
		synchronized ( communicator )
		{
			return serverComm != null;
		}
	}

	/*
	 * START Bayou API
	 */

	public void create()
	{
		synchronized ( database )
		{
			if ( serverID != null )
				return;
			//  Do stuff
		}
	}

	public void retire()
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return;
			//  Do stuff
		}
	}

	public boolean isCreated()
	{
		synchronized ( database )
		{
			return serverID != null;
		}
	}

	public void add( K key, V value )
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return;
			if ( !performUpdates )
				return;
			BayouWrite<K, V> write = new BayouWrite<K, V>(
				key, value, BayouWrite.Type.ADD,
				new WriteID( database.getAcceptStamp(), serverID ) );
			database.addWrite( write );
		}
	}

	public void edit( K key, V value )
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return;
			if ( !performUpdates )
				return;
			BayouWrite<K, V> write = new BayouWrite<K, V>(
				key, value, BayouWrite.Type.EDIT,
				new WriteID( database.getAcceptStamp(), serverID ) );
			database.addWrite( write );
		}
	}

	public V get( K key )
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return null;
			return database.getMap().get( key );
		}
	}

	public Map<K, V> getAll()
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return null;
			return database.getMap();
		}
	}

	public void remove( K key )
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return;
			if ( !performUpdates )
				return;
			BayouWrite<K, V> write = new BayouWrite<K, V>(
				key, null, BayouWrite.Type.DELETE,
				new WriteID( database.getAcceptStamp(), serverID ) );
			database.addWrite( write );
		}
	}

	/*
	 * END Bayou API
	 */

	public int size()
	{
		synchronized ( database )
		{
			if ( serverID == null )
				return 0;
			return database.getMap().size();
		}
	}

	public ArrayList<InetSocketAddress> getAddressList()
	{
		synchronized ( addresses )
		{
			return (ArrayList<InetSocketAddress>)( addresses.clone() );
		}
	}

	public void setAddressList( ArrayList<InetSocketAddress> addresses )
	{
		synchronized ( this.addresses )
		{
			this.addresses = (ArrayList<InetSocketAddress>)( addresses.clone() );
		}
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

