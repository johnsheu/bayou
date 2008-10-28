import java.net.InetSocketAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ChatClient
{
	Communicator communicator = null;

	InetSocketAddress address = null;

	public void run( String[] args )
	{
		if ( args.length != 2 )
		{
			System.out.print( "usage: java ChatClient <port> <host:port>\n" );
			System.exit( 0 );
		}

		int port = -1;
		try
		{
			port = Integer.parseInt( args[0] );
			String[] host = args[1].split( ":" );
			address = new InetSocketAddress( host[0], Integer.parseInt( host[1] ) );
		}
		catch ( NumberFormatException ex )
		{
			ex.printStackTrace();
			System.exit( 1 );
		}

		communicator = new Communicator( port );
		communicator.start();

		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		System.out.print( "# " );
		try
		{
			String line = null;
			while ( ( line = reader.readLine() ) != null )
			{
				if ( !line.trim().equals( "" ) )
				{
					TextMessage message = new TextMessage( line );
					message.setAddress( address );
					communicator.sendMessage( message );
				}
				Message message = null;
				while ( ( message = communicator.readMessage() ) != null )
				{
					System.out.println( message );
				}
				System.out.print( "# " );
			}
		}
		catch ( IOException ex )
		{
			ex.printStackTrace();
			return;
		}	
	}

	public static void main( String[] args )
	{
		new ChatClient().run( args );
	}
}

