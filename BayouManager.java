import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class BayouManager
{
	private Communicator communicator;

	private HashMap<String, InetSocketAddress> aliases;

	public BayouManager( int port )
	{
		aliases = new HashMap<String, InetSocketAddress>();
		communicator = new Communicator( port );
		communicator.start();
	}

	private InetSocketAddress getAddress( String address )
	{
		int index = address.indexOf( ':' );
		if ( index == -1 )
		{
			InetSocketAddress ret = aliases.get( address );
			if ( ret == null )
				System.out.print( "error: alias \"" + address +
					"\" not found\n" );
			return ret;
		}
		else
		{
			String host = address.substring( 0, index );
			String port = address.substring( index + 1 );
			int nport = 0;
			try
			{
				nport = Integer.parseInt( port );
			}
			catch ( NumberFormatException ex )
			{
				ex.printStackTrace();
				return null;
			}
			return new InetSocketAddress( host, nport );
		}
	}

	private Message getMessageReply( Message message )
	{
		InetSocketAddress address = message.getAddress();
		communicator.sendMessage( message );
		long timeout = System.currentTimeMillis() + 5000L;
		while ( System.currentTimeMillis() < timeout )
		{
			Message reply;
			while ( ( reply = communicator.readMessage() ) != null )
			{
				if ( !address.equals( reply.getAddress() ) )
					continue;
				return reply;
			}
			try
			{
				Thread.currentThread().sleep( 100 );
			}
			catch ( InterruptedException ex )
			{

			}
		}
		ErrorMessage emessage = new ErrorMessage();
		emessage.setAddress( address );
		emessage.setError( "getMessageReply timed out" );
		return emessage;
	}

	/*
	 * Execute a command line.
	 */
	public void commandLine( String command )
	{
		String[] args = command.split( "\\s+" );

		if ( args.length == 0 )
			return;

		if ( args[0].length() != 0 && args[0].charAt( 0 ) == '#' )
			//  Comment
			return;

		if ( args[0].equalsIgnoreCase( "create" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "create <port>\n" );
				return;
			}
			int port = 0;
			try
			{
				port = Integer.parseInt( args[1] );
			}
			catch ( NumberFormatException ex )
			{
				ex.printStackTrace();
				return;
			}
			BayouServer server = new BayouServer( port );
			server.start();
			return;
		}
		else if ( args[0].equalsIgnoreCase( "alias" ) )
		{
			if ( args.length != 3 )
			{
				System.out.print( "alias <host:port> <alias>\n" );
				return;
			}
			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			aliases.put( args[2], address );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "unalias" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "unalias <alias>\n" );
				return;
			}
			aliases.remove( args[1] );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "sleep" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "sleep <time>\n" );
				return;
			}
			float time = 0.0f;
			try
			{
				time = Float.parseFloat( args[1] );
			}
			catch ( NumberFormatException ex )
			{
				ex.printStackTrace();
				return;
			}

			long stime = (long)( time * 1000.0f );
			try
			{
				Thread.currentThread().sleep( stime );
			}
			catch ( InterruptedException ex )
			{

			}
			return;
		}
		else if ( args[0].equalsIgnoreCase( "get_talking" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "get_talking <host:port>|<alias>\n" );
				return;
			}

			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_TALKING,
				null, null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_talking" ) )
		{
			if ( args.length != 3 )
			{
				System.out.print( "set_talking <host:port>|<alias> <\"true\"|\"false\">\n" );
				return;
			}

			boolean value = Boolean.parseBoolean( args[2] );
			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_TALKING,
				null, value, null, null );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "get_updating" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "get_updating <host:port>|<alias>\n" );
				return;
			}

			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_UPDATING,
				null, null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_updating" ) )
		{
			if ( args.length != 3 )
			{
				System.out.print( "set_updating <host:port>|<alias> <\"true\"|\"false\">\n" );
				return;
			}

			boolean value = Boolean.parseBoolean( args[2] );
			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_UPDATING,
				null, value, null, null );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "get_sleeptime" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "get_sleeptime <host:port>|<alias>\n" );
				return;
			}

			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_SLEEPTIME,
				null, null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_sleeptime" ) )
		{
			if ( args.length != 3 )
			{
				System.out.print( "set_sleeptime <host:port>|<alias> <time>\n" );
				return;
			}

			long value = Long.parseLong( args[2] );
			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_SLEEPTIME,
				null, null, value, null );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "get_caching" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "get_caching <host:port>|<alias>\n" );
				return;
			}

			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_CACHING,
				null, null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_caching" ) )
		{
			if ( args.length != 3 )
			{
				System.out.print( "set_caching <host:port>|<alias> <\"true\"|\"false\">\n" );
				return;
			}

			boolean value = Boolean.parseBoolean( args[2] );
			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_CACHING,
				null, value, null, null );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "get_addresses" ) )
		{
			if ( args.length != 2 )
			{
				System.out.print( "get_addresses <host:port>|<alias>\n" );
				return;
			}

			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_ADDRESSES,
				null, null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_addresses" ) )
		{
			if ( args.length < 2 )
			{
				System.out.print( "set_addresses <host:port>|<alias> [<host1:port1>|<alias1> <host2:port2>|<alias2> ...]" );
				return;
			}

			InetSocketAddress address = getAddress( args[1] );
			if ( address == null )
				return;

			ArrayList<InetSocketAddress> addresses =
				new ArrayList<InetSocketAddress>();
			for ( int i = 2; i < args.length; i += 1 )
			{
				InetSocketAddress addr = getAddress( args[i] );
				if ( addr == null )
					continue;
				addresses.add( addr );
			}

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_ADDRESSES,
				null, null, null, addresses );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "quit" ) ||
			args[0].equalsIgnoreCase( "exit" ) )
		{
			System.exit( 0 );
		}
		else
		{
			System.out.print( "Commands:\n" );
			System.out.print( " alias\n" );
			System.out.print( " create\n" );
			System.out.print( " exit\n" );
			System.out.print( " get_addresses\n" );
			System.out.print( " get_caching\n" );
			System.out.print( " get_primary\n" );
			System.out.print( " get_sleeptime\n" );
			System.out.print( " get_talking\n" );
			System.out.print( " get_updating\n" );
			System.out.print( " quit\n" );
			System.out.print( " set_addresses\n" );
			System.out.print( " set_caching\n" );
			System.out.print( " set_primary\n" );
			System.out.print( " set_sleeptime\n" );
			System.out.print( " set_talking\n" );
			System.out.print( " set_updating\n" );
			System.out.print( " unalias\n" );
			System.out.print( "Call a command with no arguments for usage info\n" );
			return;
		}
	}

	public static void main( String[] args )
	{
		if ( args.length < 1 || args.length > 2 )
		{
			System.out.print( "usage: java BayouManager <port> [cmdfile]\n" );
			System.out.print( "       <port> - port number to listen on\n" );
			System.out.print( "       [cmdfile] - (optional) file of commands to run before stdin\n\n" );
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

		BayouManager manager = new BayouManager( port );

		if ( args.length == 2 )
		{
			try
			{
				BufferedReader reader = new BufferedReader( 
					new FileReader( args[1] ) );
				String line;
				while ( ( line = reader.readLine() ) != null )
				{
					System.out.print( "> " + line + '\n' );
					manager.commandLine( line );
				}
			}
			catch ( FileNotFoundException ex )
			{
				ex.printStackTrace();
			}
			catch ( IOException ex )
			{
				ex.printStackTrace();
			}
		}

		BufferedReader reader = new BufferedReader( 
			new InputStreamReader( System.in ) );
		String line;
		try
		{
			System.out.print( "> " );
			while ( ( line = reader.readLine() ) != null )
			{
				manager.commandLine( line );
				System.out.print( "> " );
			}
		}
		catch ( IOException ex )
		{
			ex.printStackTrace();
		}
	}
}

