import java.io.Serializable;
import java.util.HashMap;

public class BayouAERequest extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ServerID senderID = null;

	private long recvOSN = -1;

	private long recvCSN = -1;

	private HashMap<ServerID, Long> recvVV = null;

	private boolean isCreate = false;

	public BayouAERequest( long OSN, long CSN, HashMap<ServerID, Long> VV )
	{
		recvOSN = OSN;
		recvCSN = CSN;
		recvVV = VV;
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
	
	public void setCreate( boolean create )
	{
		isCreate = create;
	}

	public boolean isCreate()
	{
		return isCreate;
	}
	
	public void setSenderID( ServerID id )
	{
		senderID = id;
	}
	
	public ServerID getSenderID()
	{
		return senderID;
	}
}

