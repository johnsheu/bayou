import java.io.Serializable;

public class ServerID implements Comparable<ServerID>, Serializable
{
	private static final long serialVersionUID = 1L;

	private Long acceptStamp;

	private ServerID serverID;

	//  Cache hashcode, to avoid recursive computation
	//  Note that this relies on ServerIDs having no mutator methods
	private int hashCode;

	public ServerID( ServerID serverID, Long acceptStamp )
	{
		this.serverID = serverID;
		this.acceptStamp = acceptStamp;
		this.hashCode = serverID != null ? serverID.hashCode() * 31 + acceptStamp.hashCode()
			: acceptStamp.hashCode();
	}

	public boolean equals( Object o )
	{
		if ( this == o )
			return true;
		if ( o == null )
			return false;

		ServerID other = (ServerID)o;
		if ( !this.acceptStamp.equals( other.acceptStamp ) )
			return false;
		if ( this.serverID == null )
			return other.serverID == null;
		if ( other.serverID == null )
			return false;
		if ( this.hashCode() != other.hashCode() )
			return false;
		return this.serverID.equals( other.serverID );
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
				return this.acceptStamp.compareTo( other.acceptStamp );
			else
				return -1;
		}
		else if ( other.serverID == null )
			return 1;

		int result = other.serverID.compareTo( this.serverID );
		if ( result != 0 )
			return result;
		else
			return this.acceptStamp.compareTo( other.acceptStamp );
	}

	public String toString()
	{
		return "<" + acceptStamp + ", " + serverID + ">";
	}
}

