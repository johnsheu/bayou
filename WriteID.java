import java.io.Serializable;

public class WriteID implements Comparable<WriteID>, Serializable
{
	private Long acceptStamp;

	private ServerID serverID;

	private Long CSN;

	public WriteID( Long CSN )
	{
		this.CSN = CSN;
		acceptStamp = null;
		serverID = null;
	}

	public WriteID( Long acceptStamp, ServerID server )
	{
		CSN = null;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public void setCSN( Long CSN )
	{
		this.CSN = CSN;
		acceptStamp = null;
		serverID = null;
	}

	public Long getCSN()
	{
		if ( !isCommitted() )
			return null;
		return CSN;
	}

	public void setAcceptStamp( Long acceptStamp, ServerID server )
	{
		CSN = null;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public Long getAcceptStamp()
	{
		if ( isCommitted() )
			return null;
		return acceptStamp;
	}

	public ServerID getServerID()
	{
		return serverID;
	}

	public boolean isCommitted()
	{
		return serverID == null;
	}

	public int compareTo( WriteID other )
	{
		if ( this == other )
			return 0;

		if ( this.serverID == null )
		{
			if ( other.serverID == null )
				//  Both are committed writes
				return this.CSN.compareTo( other.CSN );
			else
				//  This is a committed write, other isn't
				return -1;
		}
		else if ( other.serverID == null )
			//  This isn't a committed write, other is
			return 1;

		//  Both are uncommitted writes
		int result = this.acceptStamp.compareTo( other.acceptStamp );
		if ( result != 0 )
			return result;
		
		return this.serverID.compareTo( other.serverID );
	}

	public String toString()
	{
		return "<#:" + ( CSN != null ? CSN : ( acceptStamp + " , S:" +
			serverID ) ) + ">";
	}
}

