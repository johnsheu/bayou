import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class BayouAEResponse<K, V> extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ServerID newID = null;

	private HashMap<K, V> newDB = null;

	private HashMap<ServerID, Long> newOV = null;

	private long newOSN = -1;

	private LinkedList<WriteID> newCommits = null;

	private LinkedList<BayouWrite<K, V>> newWrites = null;

	public BayouAEResponse()
	{

	}

	public HashMap<K, V> getDatabase()
	{
		return newDB;
	}

	public HashMap<ServerID, Long> getOmittedVector()
	{
		return newOV;
	}

	public long getOSN()
	{
		return newOSN;
	}

	public LinkedList<WriteID> getCommitNotifications()
	{
		return newCommits;
	}

	public LinkedList<BayouWrite<K, V>> getWrites()
	{
		return newWrites;
	}

	public ServerID getServerID()
	{
		return newID;
	}

	public void addDatabase( HashMap<K, V> db )
	{
		newDB = db;
	}

	public void addOmittedVector( HashMap<ServerID, Long> ov )
	{
		newOV = ov;
	}

	public void addOSN( long osn )
	{
		newOSN = osn;
	}

	public void addCommitNotification( WriteID commit )
	{
		if ( newCommits == null )
			newCommits = new LinkedList<WriteID>();
		newCommits.add( commit );
	}

	public void addWrite( BayouWrite<K, V> write )
	{
		if ( newWrites == null )
			newWrites = new LinkedList<BayouWrite<K, V>>();
		newWrites.add( write );
	}

	public void addServerID( ServerID id )
	{
		newID = id;
	}
}

