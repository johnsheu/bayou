import java.io.Serializable;

public class WriteID implements Comparable<WriteID>, Serializable
{
	private static final long serialVersionUID = 1L;

	private long acceptStamp = Long.MAX_VALUE;

	private ServerID serverID = null;

	private long CSN = Long.MAX_VALUE;

	public WriteID( long acceptStamp, ServerID server )
	{
		CSN = Long.MAX_VALUE;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public WriteID( long CSN, long acceptStamp, ServerID server )
	{
		this.CSN = CSN;
		this.acceptStamp = acceptStamp;
		serverID = server;
	}

	public void setCSN( long CSN )
	{
		this.CSN = CSN;
	}

	public void setAcceptStamp( long acceptStamp )
	{
		this.acceptStamp = acceptStamp;
	}
	
	public void setServerID( ServerID server )
	{
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
		return this.CSN == other.CSN &&
			this.acceptStamp == other.acceptStamp &&
			this.serverID.equals( other.serverID );
	}

	public int hashCode()
	{
		if ( CSN != Long.MAX_VALUE )
			return ( (int)CSN ^ (int)( CSN >> 32 ) );
		else
			return serverID.hashCode() * 31 +
				( (int)acceptStamp ^ (int)( acceptStamp >> 32 ) );
	}

	public int compareTo( WriteID other )
	{
		if ( this == other )
			return 0;
		
		if ( this.CSN != other.CSN )
		{
			if ( this.CSN < other.CSN )
				return -1;
			else
				return 1;
		}

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

