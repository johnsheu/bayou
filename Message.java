import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * A class which encapsulates all messages transferred through a
 * {@link Communicator}.  This abstract class includes only the addressing
 * portion of each message.
 *
 * @see Communicator
 */
public abstract class Message implements Serializable
{
	//  The address associated with this Message.
	private InetSocketAddress address = null;

	/**
	 * Default constructor.  This constructor adds no addressing
	 * information to the message.
	 */
	public Message()
	{
		
	}

	/**
	 * Constructor with address initializer.
	 *
	 * @param address  the {@link InetSocketAddress} address for this
	 *                 message
	 */
	public Message( InetSocketAddress address )
	{
		setAddress( address );
	}

	/**
	 * Constructor with address initializer.
	 *
	 * @param hostname  the {@link InetAddress} hostname for this message
	 * @param port      the port for this message
	 */
	public Message( InetAddress hostname, int port )
	{
		setAddress( hostname, port );
	}

	/**
	 * Constructor with address initializer.
	 *
	 * @param hostname  the <code>String</code> hostname for this message
	 * @param port      the port for this message
	 */
	public Message( String hostname, int port )
	{
		setAddress( hostname, port );
	}

	/**
	 * Get the address associated with this <code>Message</code>.
	 *
	 * @return  the {@link InetSocketAddress} address for this
	 *          message
	 */
	public InetSocketAddress getAddress()
	{
		return address;
	}

	/**
	 * Set the address associated with this <code>Message</code>.
	 *
	 * @param address  the {@link InetSocketAddress} address for this
	 *                 message
	 */
	public void setAddress( InetSocketAddress address )
	{
		this.address = address;
	}

	/**
	 * Set the address associated with this <code>Message</code>.
	 *
	 * @param hostname  the {@link InetAddress} hostname for this message
	 * @param port      the port for this message
	 */
	public void setAddress( InetAddress hostname, int port )
	{
		setAddress( new InetSocketAddress( hostname, port ) );
	}

	/**
	 * Set the address associated with this <code>Message</code>.
	 *
	 * @param hostname  the <code>String</code> hostname for this message
	 * @param port      the port for this message
	 */
	public void setAddress( String hostname, int port )
	{
		setAddress( new InetSocketAddress( hostname, port ) );
	}

	public String toString()
	{
		return "Message from <" + address + ">: ";
	}
}

