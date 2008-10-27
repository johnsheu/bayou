import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class BayouAERequest extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ServerID recvID = null;

	private long recvOSN = -1;

	private HashMap<ServerID, Long> recvVV = null;

	public BayouAERequest( ServerID ID, long OSN,
		HashMap<ServerID, Long> VV )
	{
		recvID = ID;
		recvOSN = OSN;
		recvVV = VV;
	}

	public ServerID getRecvID()
	{
		return recvID;
	}

	public long getRecvOSN()
	{
		return recvOSN;
	}

	public HashMap<ServerID, Long> getRecvVV()
	{
		return recvVV;
	}
}

