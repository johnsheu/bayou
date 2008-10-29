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
									null, null, null );
								communicator.sendMessage( msg );
								break;
							}
							case GET_STATUS:
							{
								TextMessage reply = new TextMessage();
								reply.setMessage(
									"CREATED:   " + isCreated() + '\n' +
									"RETIRED:   " + isRetired() + '\n' +
									"TALKING:   " + performAntiEntropy + '\n' +
									"UPDATING:  " + performUpdates + '\n' +
									"SLEEPTIME: " + sleepTime + '\n' +
									"CACHING:   " + database.isCaching() + '\n' +
									"PRIMARY    " + database.isPrimary() + '\n' );
								communicator.sendMessage( reply );
								break;
							}
							case SET_TALKING:
							{
								performAntiEntropy =
									msg.getBoolean().booleanValue();
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
									null, null, addresses );
								communicator.sendMessage( reply );
								break;
							}
							case SET_ADDRESSES:
							{
								addresses = msg.getAddresses();
								break;
							}
							case SET_SLEEPTIME:
							{
								sleepTime = msg.getLong();
								break;
							}
							case SET_CACHING:
							{
								database.setCaching( msg.getBoolean().booleanValue() );
								break;
							}
							case SET_PRIMARY:
							{
								database.setPrimary( msg.getBoolean().booleanValue() );
								break;
							}
							case CREATE:
							{
								create( -1L );
								break;
							}
							case RETIRE:
							{
								retire( -1L );
								BayouServer.this.stop();
								break;
							}
						}
					}
					else if ( message instanceof ErrorMessage )
					{
						ErrorMessage msg = (ErrorMessage)message;
						synchronized ( addresses )
						{
							addresses.remove( msg.getAddress() );
						}
					}
					else if ( state != ServerState.RETIRED )
					{
						synchronized ( database )
						{
							if ( state != ServerState.CREATING &&
								message instanceof BayouAERequest )
							{
								BayouAERequest msg = (BayouAERequest)message;
								BayouAEResponse<K, V> reply = database.getUpdates( msg );
								if ( msg.isCreate() && state == ServerState.CREATING )
									reply.addServerID( new ServerID( serverID,
										database.getAcceptStamp() + 1 ) );
								reply.setAddress( msg.getAddress() );
								communicator.sendMessage( reply );
								if ( state == ServerState.RETIRING )
								{
									state = ServerState.RETIRED;
									database.notifyAll();
								}
							}
							else if ( state != ServerState.RETIRING &&
								message instanceof BayouAEResponse<?, ?> )
							{
								BayouAEResponse<K, V> msg = (BayouAEResponse<K, V>)message;
								if ( state == ServerState.CREATING &&
									msg.getServerID() != null )
								{
									serverID = msg.getServerID();
									database.setAcceptStamp(
										msg.getServerID().getAcceptStamp() );
									state = ServerState.CREATED;
									database.notifyAll();
								}
								
								database.applyUpdates( msg );
							}
						}
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
		public BayouServerActor()
		{

		}
		
		public void run()
		{
			while ( !isInterrupted() )
			{
				try
				{
					synchronized ( database )
					{
						while ( state == ServerState.RETIRED ||
							state == ServerState.CREATING )
							database.wait();
					}

					InetSocketAddress address = getRandomAddress();
					if ( address != null )
					{
						BayouAERequest message = database.makeRequest();
						message.setAddress( address );
						communicator.sendMessage( message );
					}
				
					sleep( sleepTime );
				}
				catch ( InterruptedException ex )
				{
					return;
				}
			}
		}
	}

	private enum ServerState
	{
		RETIRED,
		CREATING,
		CREATED,
		RETIRING;
	}

	private Communicator communicator = null;

	private BayouServerComm serverComm = null;
	
	private BayouServerActor serverAct = null;

	private BayouDB<K, V> database = null;

	private boolean performUpdates = true;

	private boolean performAntiEntropy = true;

	private ArrayList<InetSocketAddress> addresses = null;

	private ServerID serverID = null;

	private ServerState state = ServerState.RETIRED;
	
	private long sleepTime = 100L;

	Random random = null;

	public BayouServer( int port )
	{
		random = new Random();
		database = new BayouDB<K, V>();
		communicator = new Communicator( port );
		addresses = new ArrayList<InetSocketAddress>();
	}

	public BayouServer( int port, boolean isprimary )
	{
		this( port );
		if ( isprimary )
		{
			database.setPrimary( true );
			serverID = new ServerID( null, 0L );
			state = ServerState.CREATED;
		}
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
		create( 0L );
	}

	public void create( long timeout )
	{
		synchronized ( database )
		{
			if ( state == ServerState.CREATED )
				return;

			if ( state == ServerState.RETIRING )
				retire( timeout );

			if ( state == ServerState.RETIRING )
				return;

			//  State now either RETIRED, or CREATING
			if ( state == ServerState.RETIRED )
			{
				InetSocketAddress address = getRandomAddress();
				if ( address == null )
					return;
				state = ServerState.CREATING;
				BayouAERequest request = database.makeRequest();
				request.setCreate( true );
				request.setAddress( address );
				communicator.sendMessage( request );
			}

			if ( timeout < 0 )
				return;

			try
			{
				while ( state != ServerState.CREATED )
					database.wait( timeout );
			}
			catch ( InterruptedException ex )
			{

			}
		}
	}

	public void retire()
	{
		retire( 0L );
	}

	public void retire( long timeout )
	{
		synchronized ( database )
		{
			if ( database.isPrimary() || state == ServerState.RETIRED )
				return;

			if ( state == ServerState.CREATING )
				create( timeout );

			if ( state == ServerState.CREATING )
				return;

			//  State now either CREATED, or RETIRING
			if ( state == ServerState.CREATED )
			{
				state = ServerState.RETIRING;
				BayouWrite<K, V> write = new BayouWrite<K, V>(
					null, null, BayouWrite.Type.DELETE,
					new WriteID( database.getAcceptStamp(), serverID ) );
				database.addWrite( write );
			}

			if ( timeout < 0 )
				return;

			try
			{
				while ( state != ServerState.RETIRED )
					database.wait( timeout );
			}
			catch ( InterruptedException ex )
			{

			}
		}
	}

	public boolean isCreated()
	{
		synchronized ( database )
		{
			return state == ServerState.CREATED;
		}
	}

	public boolean isRetired()
	{
		synchronized ( database )
		{
			return state == ServerState.RETIRED;
		}
	}

	public void add( K key, V value )
	{
		synchronized ( database )
		{
			if ( state != ServerState.CREATED )
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
			if ( state != ServerState.CREATED )
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
			if ( state != ServerState.CREATED )
				return null;
			return database.getMap().get( key );
		}
	}

	public Map<K, V> getAll()
	{
		synchronized ( database )
		{
			if ( state != ServerState.CREATED )
				return null;
			return database.getMap();
		}
	}

	public void remove( K key )
	{
		synchronized ( database )
		{
			if ( state != ServerState.CREATED )
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
			if ( state != ServerState.CREATED )
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

	public InetSocketAddress getRandomAddress()
	{
		synchronized ( addresses )
		{
			if ( addresses.size() != 0 )
				return addresses.get( random.nextInt( addresses.size() ) );
		}
		return null;
	}

	public String dump()
	{
		return database.dump();
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

