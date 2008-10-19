import java.math.BigInteger;

public class ServerID implements Comparable
{

    private ServerID sid;
    private BigInteger acceptStamp;
    private boolean isFirst;

    public ServerID( ServerID sid, BigInteger acceptStamp )
    {
	this.sid = sid;
	this.acceptStamp = acceptStamp;
    }

    public boolean equals(ServerID otherServer)
    {
	if( otherServer == null )
	    return false;
	if( sid == otherServer.sid && acceptStamp.equals(otherServer.acceptStamp) )
	    return true;

	return false;
    }

    public int compareTo( Object otherObject )
    {
	ServerID otherID = (ServerID) otherObject;

	if( this == otherID )
	    return 0;
	else if( this.sid == null && otherID.sid != null )
	    return -1;
	else if( this.sid != null && otherID.sid == null)
	    return 1;

	int result = otherID.sid.compareTo(this.sid);
	if( result == 0 )
	    return this.acceptStamp.compareTo(otherID.acceptStamp);
	else if( result < 0 )
	    return -1;
	else
	    return 1;
    }
}
