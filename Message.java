import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * A class which encapsulates all messages transferred through a
 * {@link Communicator}.  This class encapsulates both the data to
 * send/receive and the address to/from which the address is
 * sent/received.
 *
 * @see Communicator
 */
public class Message implements Serializable
{
	//  The address associated with this Message.
	private InetSocketAddress address;

	private String message;

	public Message( String hostname, int port, String message )
	{
		address = new InetSocketAddress( hostname, port );
		this.message = message;
	}

	/**
	 * Get the address associated with this <code>Message</code>.
	 */
	public InetSocketAddress getAddress()
	{
		return address;
	}

	public String getMessage()
	{
		return message;
	}
}
