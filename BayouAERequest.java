import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class BayouAERequest extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ServerID recvID = null;

	private long recvOSN = -1;

	private long recvCSN = -1;

	private HashMap<ServerID, Long> recvVV = null;

	public BayouAERequest( ServerID ID, long OSN, long CSN,
		HashMap<ServerID, Long> VV )
	{
		recvID = ID;
		recvOSN = OSN;
		recvCSN = CSN;
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

	public long getRecvCSN()
	{
		return recvCSN;
	}

	public HashMap<ServerID, Long> getRecvVV()
	{
		return recvVV;
	}
}

