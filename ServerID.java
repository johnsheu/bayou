import java.io.Serializable;

public class ServerID implements Comparable<ServerID>, Serializable
{
	private static final long serialVersionUID = 1L;

	private long acceptStamp;

	private ServerID serverID;

	//  Cache hashcode, to avoid recursive computation
	//  Note that this relies on ServerIDs having no mutator methods
	private int hashCode;

	public ServerID( ServerID serverID, long acceptStamp )
	{
		this.serverID = serverID;
		this.acceptStamp = acceptStamp;
		this.hashCode = ( (int)acceptStamp ^ (int)( acceptStamp >> 32 ) );
		if ( serverID != null )
			this.hashCode += serverID.hashCode() * 31;
	}

	public boolean equals( Object o )
	{
		if ( this == o )
			return true;
		if ( o == null )
			return false;

		ServerID other = (ServerID)o;
		if ( this.acceptStamp != other.acceptStamp )
			return false;
		if ( this.serverID == null )
			return other.serverID == null;
		if ( other.serverID == null )
			return false;
		if ( this.hashCode() != other.hashCode() )
			return false;
		return this.serverID.equals( other.serverID );
	}

	public long getAcceptStamp()
	{
		return acceptStamp;
	}

	public int hashCode()
	{
		return hashCode;
	}

	public int compareTo( ServerID other )
	{
		if ( this == other )
			return 0;

		//  Can't forget the case when both SIDs are null
		if ( this.serverID == null )
		{
			if ( other.serverID == null )
				if ( this.acceptStamp == other.acceptStamp )
					return 0;
				else if ( this.acceptStamp < other.acceptStamp )
					return -1;
				else
					return 1;
			else
				return -1;
		}
		else if ( other.serverID == null )
			return 1;

		int result = other.serverID.compareTo( this.serverID );
		if ( result != 0 )
			return result;
		else
			if ( this.acceptStamp == other.acceptStamp )
				return 0;
			else if ( this.acceptStamp < other.acceptStamp )
				return -1;
			else
				return 1;
	}

	public String toString()
	{
		return "<" + acceptStamp + ", " + serverID + ">";
	}
}

