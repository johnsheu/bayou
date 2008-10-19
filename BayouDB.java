import java.math.BigInteger;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.ArrayList;


public class BayouDB
{
    private TreeSet<BayouWrite> writeLog;

    private HashMap playList;

    public HashMap rollBack( HashMap<ServerID, BigInteger> versionVector )
    {
	/*stub*/
	return new HashMap();
    }

    public void addWrite( BayouWrite write )
    {
	/*** update the playlist here ***/

	writeLog.add(write);
    }

    public void updateCNS( BayouWrite write, BigInteger CSN )
    {
	/*stub*/
	
    }

    public ArrayList getUpdateList( HashMap<ServerID, BigInteger> versionVector )
    {
	/*stub*/
	return new ArrayList();
    }

}
