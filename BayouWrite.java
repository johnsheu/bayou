import java.math.BigInteger;
import java.util.ArrayList;

public class BayouWrite implements Comparable
{
    private ArrayList updates;
    private BigInteger acceptStamp;
    private ServerID serverID;
    private BigInteger commitSN;
    private String writeType;

    public BayouWrite( ArrayList updates, BigInteger acceptStamp, ServerID serverID, String writeType )
    {
	this.updates = updates;
	this.acceptStamp = acceptStamp;
	this.serverID = serverID;
	this.writeType = writeType;
    }

    public BayouWrite( ArrayList updates, BigInteger acceptStamp, ServerID serverID, String writeType, BigInteger commitSN )
    {
	this(updates, acceptStamp, serverID, writeType );
	this.commitSN = commitSN;
    }

    public boolean hasCSN()
    {
	if( commitSN != null )
	    return true;
	return false;
    }

    public BigInteger getCSN()
    {
	return commitSN;
    }

    public BigInteger getAcceptStamp()
    {
	return acceptStamp;
    }

    public ServerID getServerID()
    {
	return serverID;
    }

    public void setCSN( BigInteger csn)
    {
	this.commitSN= csn;
    }

    public int compareTo( Object write )
    {
	BayouWrite otherWrite = (BayouWrite) write;
	if( this == otherWrite )
	    return 0;

	if( otherWrite.commitSN == null && this.commitSN != null)
	    return -1;
	if( otherWrite.commitSN != null && this.commitSN == null)
	    return 1;
	if( otherWrite.commitSN != null && this.commitSN != null)
	    return this.commitSN.compareTo( otherWrite.commitSN);


	int result = acceptStamp.compareTo(otherWrite.getAcceptStamp());
	if( result != 0 )
	    return result;
	
	return this.serverID.compareTo(otherWrite.serverID);	
    }
}
