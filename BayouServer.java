import java.util.ArrayList;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.io.Serializable;

public class BayouServer<Data extends Comparable<Data> & Serializable>
	extends Thread
{
	private ServerID serverID;
	private Long acceptStampCounter;
	private Long largestKnownCSN;
	private HashMap<ServerID, Long> versionVector;
	private HashMap<ServerID, Long> omittedVector;

	private Communicator communicator;

	private Database database;

	private boolean performUpdates = true;

	private boolean performAntiEntropy = true;

	private ArrayList<InetSocketAddress> addresses =
		new ArrayList<InetSocketAddress>();

	public BayouServer( int port )
	{
		communicator = new Communicator( port );
		communicator.start();
	}

	public void add( Object o )
	{

	}

	public void edit( Object o )
	{

	}

	public void remove( Object o )
	{
		
	}

	public void run()
	{
		while ( true )
		{
			try
			{
				sleep( 100 );
			}
			catch ( InterruptedException ex )
			{
				//  Just ignore it
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

		BayouServer<Song> server = new BayouServer<Song>( port );
		server.start();
		try
		{
			server.join();
		}
		catch ( InterruptedException ex )
		{
			//  Just ignore it
		}
	}
}

