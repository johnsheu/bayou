import java.io.Serializable;
import java.util.HashMap;

public class BayouAERequest extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private long recvOSN = -1;

	private long recvCSN = -1;

	private HashMap<ServerID, Long> recvVV = null;

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
}

