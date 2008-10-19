import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class BayouManager
{
	private Communicator communicator;

	private HashMap<String, InetSocketAddress> aliases =
		new HashMap<String, InetSocketAddress>();

	public BayouManager( int port )
	{
		communicator = new Communicator( port );
		communicator.start();
	}

	private InetSocketAddress getAddress( String host, String port )
	{
		if ( port == null )
		{
			return aliases.get( host );
		}
		else
		{
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

	public void commandLine( String command )
	{
		String[] args = command.split( "\\s+" );
		if ( args.length == 0 )
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
			if ( args.length != 4 )
			{
				System.out.print( "alias <host> <port> <alias>\n" );
				return;
			}
			int port = 0;
			try
			{
				port = Integer.parseInt( args[2] );
			}
			catch ( NumberFormatException ex )
			{
				ex.printStackTrace();
				return;
			}
			InetSocketAddress address =
				new InetSocketAddress( args[1], port );
			aliases.put( args[3], address );
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
		else if ( args[0].equalsIgnoreCase( "get_talking" ) )
		{
			InetSocketAddress address = null;
			if ( args.length == 2 )
				address = getAddress( args[1], null );
			else if ( args.length == 3 )
				address = getAddress( args[1], args[2] );
			else
			{
				System.out.print( "get_talking <host> <port>\n" );
				System.out.print( "get_talking <alias>\n" );
				return;
			}

			if ( address == null )
			{
				System.out.print( "error reading address\n" );
				return;
			}

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_TALKING,
				null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_talking" ) )
		{
			InetSocketAddress address = null;
			boolean value = true;
			if ( args.length == 3 )
			{
				address = getAddress( args[1], null );
				value = Boolean.parseBoolean( args[2] );
			}
			else if ( args.length == 4 )
			{
				address = getAddress( args[1], args[2] );
				value = Boolean.parseBoolean( args[3] );
			}
			else
			{
				System.out.print( "set_talking <host> <port> <\"true\"|\"false\">\n" );
				System.out.print( "set_talking <alias> <\"true\"|\"false\">\n" );
				return;
			}

			if ( address == null )
			{
				System.out.print( "error reading address\n" );
				return;
			}

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_TALKING,
				null, value, null );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "get_updating" ) )
		{
			InetSocketAddress address = null;
			if ( args.length == 2 )
				address = getAddress( args[1], null );
			else if ( args.length == 3 )
				address = getAddress( args[1], args[2] );
			else
			{
				System.out.print( "get_updating <host> <port>\n" );
				System.out.print( "get_updating <alias>\n" );
				return;
			}

			if ( address == null )
			{
				System.out.print( "error reading address\n" );
				return;
			}

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.GET_UPDATING,
				null, null, null );
			Message reply = getMessageReply( message );
			System.out.print( reply.toString() + '\n' );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "set_updating" ) )
		{
			InetSocketAddress address = null;
			boolean value = true;
			if ( args.length == 3 )
			{
				address = getAddress( args[1], null );
				value = Boolean.parseBoolean( args[2] );
			}
			else if ( args.length == 4 )
			{
				address = getAddress( args[1], args[2] );
				value = Boolean.parseBoolean( args[3] );
			}
			else
			{
				System.out.print( "set_updating <host> <port> <\"true\"|\"false\">\n" );
				System.out.print( "set_updating <alias> <\"true\"|\"false\">\n" );
				return;
			}

			if ( address == null )
			{
				System.out.print( "error reading address\n" );
				return;
			}

			ManagerMessage message = new ManagerMessage();
			message.setAddress( address );
			message.makeMessage( ManagerMessage.Type.SET_UPDATING,
				null, value, null );
			communicator.sendMessage( message );
			return;
		}
		else if ( args[0].equalsIgnoreCase( "quit" ) ||
			args[0].equalsIgnoreCase( "exit" ) )
		{
			System.exit( 0 );
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
					System.out.print( line + '\n' );
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
			while ( ( line = reader.readLine() ) != null )
			{
				manager.commandLine( line );
			}
		}
		catch ( IOException ex )
		{
			ex.printStackTrace();
		}
	}
}

