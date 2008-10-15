import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.*;
import java.io.*;


public class BayouServer
{
    public static void main (String args[])
    {
	if( args.length < 1)
	    System.out.println("AAAHH222!!!");
	BayouServer bs = new BayouServer();
	bs.start(args[0], args);
    }

    public void start(String serverType, String[] args)
    {
	Socket cs;
	ServerSocket ss;
	try{
	    if (serverType.equals("server"))
	    {
			Communicator c = new Communicator( 3006 );
			c.start();
			int i = 0;
			while ( true )
			{
				Thread.currentThread().sleep( 1000 );
				Message message = new Message( "localhost", 3007, "This is message " + i );
				c.sendMessage( message );
				i += 1;
			}
		}
/*
		ss = new ServerSocket(3007);
		System.out.println("In SERVER");
		cs= ss.accept();
		ObjectOutputStream os = new ObjectOutputStream(cs.getOutputStream());
		int i = 0;
		while(true)
		{
		    Thread.currentThread().sleep(5000);
		    os.writeObject("Hello " + i);
		    i++;
		}
*/

		else if (serverType.equals("client"))
	    {
			Communicator c = new Communicator( 3007 );
			c.start();
			int i = 0;
			while ( true )
			{
				Thread.currentThread().sleep( 250 );
				Message message = c.readMessage();
				if ( message != null )
					System.out.println( message.getMessage() );
			}
/*
		System.out.println("IN CLIENT");
		cs = new Socket();
		    
		cs.connect(new InetSocketAddress("davit.cs.utexas.edu", 3007));
		ObjectInputStream os = new ObjectInputStream(cs.getInputStream());
		Object o;
		while((o=os.readObject()) != null)
	        {
		    System.out.println((String)(o.toString()));
		    o = null;
		}
*/
	    }
		else if ( serverType.equals( "chat" ) )
		{
			int port = Integer.parseInt( args[2] );
			Communicator c = new Communicator( port );
			c.start();
			String line;
			String host = args[1];
			BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
			while ( ( line = reader.readLine() ) != null )
			{
				Message message = new Message( host, port, line );
				c.sendMessage( message );
				while ( ( message = c.readMessage() ) != null )
					System.out.println( "# " + message.getMessage() );
			}
		}
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }
}
