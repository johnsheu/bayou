import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * A class which encapsulates all messages involving a
 * {@link BayouManager}, transferred through a {@link Communicator}.
 * This may either be a message sent to, or received from, a
 * <code>BayouManager</code>.
 * <p>
 * Note that any message sent as a member of this class must not be
 * vital to the correctness of the Bayou protocol.  These messages are
 * intended to be "meta-messages" of sorts, useful in debugging and
 * status-checking.  Messages of the Bayou protocol itself should be
 * members of the {@link ClientMessage} class.
 *
 * @see Communicator
 * @see BayouManager
 * @see BayouServer
 * @see ClientMessage
 */
public class ManagerMessage extends Message implements Serializable
{
	//  Recommended for Serializable
	private static final long serialVersionUID = 1L;

	/**
	 * The <code>Enum</code> defining the possible types of
	 * <code>ManagerMessage</code> messages.
	 */
	public enum Type
	{
		NONE,
		//  The manager sends these kinds of messages
		GET_DB,
		GET_STATUS,
		SET_TALKING,
		SET_UPDATING,
		SET_SLEEPTIME,
		SET_CACHING,
		SET_PRIMARY,
		GET_ADDRESSES,
		SET_ADDRESSES,
		CREATE,
		RETIRE,
		//  The client sends these kinds of messages in reply
		DB_DUMP,
		STATUS,
		ADDRESSES_DUMP;
	}

	//  The type of this message
	private Type messageType = Type.NONE;

	//  The Database associated with this message, if any
	private BayouDB messageDatabase = null;

	//  The boolean value associated with this message, if any
	private Boolean messageBoolean = null;
	
	//  The long value associated with this message, if any
	private Long messageLong = 0L;

	//  The ArrayList of addresses associated with this message, if any
	private ArrayList<InetSocketAddress> messageAddresses = null;

	/**
	 * Default constructor.  This constructor adds no addressing, type,
	 * or data information to the message.
	 */
	public ManagerMessage()
	{
		super();
	}

	/**
	 * Set the type and data associated with this
	 * <code>ManagerMessage</code>.  Only one of <code>db</code>,
	 * <code>bool</code>, or <code>addresses</code> should be non-
	 * <code>null</code>.
	 *
	 * @param type       the {@link Type} of this message
	 * @param database   the {@link BayouDB} associated with this message
	 * @param bvalue     the <code>Boolean</code> value associated with
	 *                   this message
	 * @param lvalue     the <code>Long</code> value associated with
	 *                   this message
	 * @param addresses  the {@link ArrayList} of
	 *                   {@link InetSocketAddress} elements associated with
	 *                   this message
	 */
	public void makeMessage( Type type, BayouDB database, Boolean bvalue,
		Long lvalue, ArrayList<InetSocketAddress> addresses )
	{
		messageType = type;
		messageDatabase = database;
		messageBoolean = bvalue;
		messageLong = lvalue;
		messageAddresses = addresses;
	}

	/**
	 * Get the type of this <code>ManagerMessage</code>.
	 *
	 * @return  the {@link Type} of this message
	 */
	public Type getType()
	{
		return messageType;
	}

	/**
	 * Get the database associated with this <code>ManagerMessage</code>.
	 *
	 * @return  the {@link BayouDB} associated with this message, or
	 *          <code>null</code> if none
	 */
	public BayouDB getDatabase()
	{
		return messageDatabase;
	}

	/**
	 * Get the boolean value associated with this
	 * <code>ManagerMessage</code>.
	 *
	 * @return  the <code>Boolean</code> associated with this message, or
	 *          <code>null</code> if none
	 */
	public Boolean getBoolean()
	{
		return messageBoolean;
	}

	/**
	 * Get the long integer value associated with this
	 * <code>ManagerMessage</code>.
	 *
	 * @return  the <code>Long</code> associated with this message, or
	 *          <code>null</code> if none
	 */
	public Long getLong()
	{
		return messageLong;
	}

	/**
	 * Get the list of addresses associated with this
	 * <code>ManagerMessage</code>.
	 *
	 * @return  the {@link ArrayList} of {@link InetSocketAddress}
	 *          elements associated with this message, or <code>null</code>
	 *          if none
	 */
	public ArrayList<InetSocketAddress> getAddresses()
	{
		return messageAddresses;
	}

	public String toString()
	{
		if ( messageDatabase != null )
			return super.toString() + messageType + ":\n" + messageDatabase.dump() +
			   "MAP: " + messageDatabase.getMap().toString() + '\n';
		else if ( messageBoolean != null )
			return super.toString() + messageType + ": " + messageBoolean;
		else if ( messageLong != null )
			return super.toString() + messageType + ": " + messageLong;
		else if ( messageAddresses != null )
			return super.toString() + messageType + ": " + messageAddresses;
		else
			return super.toString() + messageType;
	}
}

