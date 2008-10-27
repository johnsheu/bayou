import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class ClientMessage extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	public enum Type
	{
		REQUEST,
		UPDATE;
	}

	private Type messageType = null;

	private HashMap<ServerID, Long> messageVersionVector = null;

	private WriteID messageSequenceNumber = null;

	private HashMap messageDatabase = null;

	private LinkedList<WriteID> messageCommitNotifications = null;

	private LinkedList<BayouWrite> messageWrites = null;

	public ClientMessage()
	{
		super();
	}

	public void makeMessage( Type type, HashMap<ServerID, Long> vv,
		WriteID sn, HashMap db, LinkedList<WriteID> cn,
		LinkedList<BayouWrite> mw )
	{
		messageType = type;
		messageVersionVector = vv;
		messageSequenceNumber = sn;
		messageDatabase = db;
		messageCommitNotifications = cn;
		messageWrites = mw;
	}

	public Type getType()
	{
		return messageType;
	}

	public HashMap<ServerID, Long> getVersionVector()
	{
		return messageVersionVector;
	}

	public WriteID getSequenceNumber()
	{
		return messageSequenceNumber;
	}

	public HashMap getDatabase()
	{
		return messageDatabase;
	}

	public LinkedList<WriteID> getCommitNotifications()
	{
		return messageCommitNotifications;
	}

	public LinkedList<BayouWrite> getWrites()
	{
		return messageWrites;
	}
}

