import java.io.Serializable;

public class ServerID implements Comparable<ServerID>, Serializable
{
	private Long acceptStamp;

	private ServerID sid;

	public ServerID( ServerID sid, Long acceptStamp )
	{
		this.sid = sid;
		this.acceptStamp = acceptStamp;
	}

	public boolean equals( ServerID otherServer )
	{
		if ( otherServer == null )
			return false;
		if ( sid == otherServer.sid &&
			acceptStamp.equals( otherServer.acceptStamp ) )
			return true;
		return false;
	}

	public int compareTo( ServerID otherID )
	{
		if ( this == otherID )
			return 0;

		//  Can't forget the case when both SIDs are null
		if ( this.sid == null )
		{
			if ( otherID.sid == null )
				return 0;
			else
				return -1;
		}
		else if ( otherID.sid == null )
			return 1;

		int result = otherID.sid.compareTo( this.sid );
		if ( result == 0 )
			return this.acceptStamp.compareTo( otherID.acceptStamp );
		else
			return result;
	}

	public String toString()
	{
		return "<" + acceptStamp + ", " + sid + ">";
	}
}

