import java.io.Serializable;

public class WriteID implements Comparable<WriteID>, Serializable
{
	private static final long serialVersionUID = 1L;

	private long acceptStamp = Long.MAX_VALUE;

	private ServerID serverID = null;

	private long CSN = Long.MAX_VALUE;

	public WriteID( long CSN )
	{
		this.CSN = CSN;
		acceptStamp = Long.MAX_VALUE;
		serverID = null;
	}

	public WriteID( long acceptStamp, ServerID server )
	{
		CSN = Long.MAX_VALUE;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public WriteID( long CSN, long acceptStamp, ServerID server )
	{
		this.CSN = csn;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public void setCSN( long CSN )
	{
		this.CSN = CSN;
		acceptStamp = Long.MAX_VALUE;
		serverID = null;
	}

	public void setAcceptStamp( long acceptStamp, ServerID server )
	{
		CSN = Long.MAX_VALUE;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public void setCommitNotification( long CSN, long acceptStamp,
		ServerID server )
	{
		this.CSN = CSN;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public long getCSN()
	{
		return CSN;
	}

	public long getAcceptStamp()
	{
		return acceptStamp;
	}

	public ServerID getServerID()
	{
		return serverID;
	}

	public boolean isCommitted()
	{
		return CSN != Long.MAX_VALUE;
	}

	public boolean equals( Object o )
	{
		if ( this == o )
			return true;
		if ( o == null )
			return false;

		WriteID other = (WriteID)o;
		if ( this.serverID == null )
			return other.serverID == null &&
				this.CSN == other.CSN;
		if ( other.serverID == null )
			return false;
		return this.acceptStamp == other.acceptStamp &&
			this.serverID.equals( other.serverID );
	}

	public int hashCode()
	{
		if ( serverID == null )
			return ( (int)CSN ^ (int)( CSN >> 32 ) );
		else
			return serverID.hashCode() * 31 +
				( (int)acceptStamp ^ (int)( acceptStamp >> 32 ) );
	}

	public int compareTo( WriteID other )
	{
		if ( this == other )
			return 0;

		if ( this.serverID == null )
		{
			if ( other.serverID == null )
			{
				//  Both are committed writes
				if ( this.CSN == other.CSN )
					return 0;
				else if ( this.CSN < other.CSN )
					return -1;
				return 1;
			}
			else
				//  This is a committed write, other isn't
				return -1;
		}
		else if ( other.serverID == null )
			//  This isn't a committed write, other is
			return 1;

		//  Both are uncommitted writes
		if ( this.acceptStamp == other.acceptStamp )
			return this.serverID.compareTo( other.serverID );
		else if ( this.acceptStamp < other.acceptStamp )
			return -1;
		return 1;
	}

	public String toString()
	{
		return "<#:" + ( CSN != Long.MAX_VALUE ? CSN : ( acceptStamp + " , S:" +
			serverID ) ) + ">";
	}
}

