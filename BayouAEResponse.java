import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class BayouAEResponse<K, V> extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private HashMap<K, V> newDB = null;

	private HashMap<ServerID, Long> newOV = null;

	private long newOSN = -1;

	private LinkedList<WriteID> newCommits = null;

	private LinkedList<BayouWrite<K, V>> newWrites = null;

	public BayouAEResponse()
	{

	}

	private HashMap<K, V> getDatabase()
	{
		return newDB;
	}

	private HashMap<ServerID, Long> getOmittedVector()
	{
		return newOV;
	}

	private long getOSN()
	{
		return newOSN;
	}

	private LinkedList<WriteID> getCommits()
	{
		return newCommits;
	}

	private LinkedList<BayouWrite<K, V>> getWrites()
	{
		return newWrites;
	}

	void addDatabase( HashMap<K, V> db )
	{
		newDB = db;
	}

	void addOmmittedVector( HashMap<ServerID, Long> ov )
	{
		newOV = ov;
	}

	void addOSN( long osn )
	{
		newOSN = osn;
	}

	void addCommitNotification( WriteID commit )
	{
		if ( newCommits == null )
			newCommits = new LinkedList<WriteID>();
		newCommits.add( commit );
	}

	void addWrite( BayouWrite<K, V> write )
	{
		if ( newWrites == null )
			newWrites = new LinkedList<BayouWrite<K, V>>();
		newWrites.add( write );
	}
}

