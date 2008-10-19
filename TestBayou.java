import java.math.BigInteger;
import java.util.ArrayList;

public class TestBayou
{
    public static void main(String[] args)
    {
	TestBayou tb = new TestBayou();
	tb.start();
    }

    public void start()
    {
	testServerID();
	testBayouWrite();
    }

    public void testServerID()
    {
	ServerID sid1 = new ServerID( null, new BigInteger( "1" ));
	ServerID sid2 = new ServerID( sid1, new BigInteger( "2" ));
	ServerID sid3 = new ServerID( sid1, new BigInteger( "2" ));
	ServerID sid4 = new ServerID( sid1, new BigInteger( "3" ));
	ServerID sid5 = new ServerID( sid2, new BigInteger( "2" ));

	int result;
	result = sid1.compareTo( sid1 );
	if( result != 0 )
	    System.out.println( "Exception ServerID 1.  Result is " + result );
	result = sid1.compareTo( sid2 );
	if( result != -1 )
	    System.out.println( "Exception ServerID 2.  Result is: " + result );
	result = sid2.compareTo( sid1 );
	if( result != 1 )
	    System.out.println( "Exception ServerID 3.  Result is: " + result );
	result = sid2.compareTo( sid3 );
	if( result != 0 )
	    System.out.println( "Exception ServerID 4.  Result is: " + result );
	result = sid3.compareTo( sid2 );
	if( result != 0 )
	    System.out.println( "Exception ServerID 5.  Result is: " + result );
	result = sid3.compareTo( sid4 );
	if( result != -1 )
	    System.out.println( "Exception ServerID 6.  Result is: " + result );
	result = sid4.compareTo( sid3 );
	if( result != 1 )
	    System.out.println( "Exception ServerID 7.  Result is: " + result );
	result = sid4.compareTo( sid5 );
	if( result != 1 )
	    System.out.println( "Exception ServerID 8.  Result is: " + result );
	result = sid5.compareTo( sid4 );
	if( result != -1 )
	    System.out.println( "Exception ServerID 9.  Result is: " + result );
    }


    public void testBayouWrite()
    {
	ArrayList updates = new ArrayList();
	String writeType = "update";

	BigInteger as1 = new BigInteger( "1" );
	BigInteger as2 = new BigInteger( "2" );
	BigInteger as3 = new BigInteger( "3" );

	BigInteger csn1 = new BigInteger( "1" );
	BigInteger csn2 = new BigInteger( "2" );
	BigInteger csn3 = new BigInteger( "3" );

	ServerID sid1 = new ServerID( null, new BigInteger("1") );
	ServerID sid2 = new ServerID( sid1, new BigInteger("2") );
	ServerID sid3 = new ServerID( sid1, new BigInteger("3") );
	ServerID sid4 = new ServerID( sid2, new BigInteger("2") );
	ServerID sid5 = new ServerID( sid2, new BigInteger("3") );
	ServerID sid6 = new ServerID( sid2, new BigInteger("4") );


	BayouWrite b1 = new BayouWrite( updates, as1, sid1, writeType, csn1);
	BayouWrite b2 = new BayouWrite( updates, as1, sid1, writeType, csn1);
	BayouWrite b3 = new BayouWrite( updates, as1, sid1, writeType, csn2);	
	BayouWrite b4 = new BayouWrite( updates, as2, sid1, writeType, csn2);
	BayouWrite b5 = new BayouWrite( updates, as2, sid2, writeType, csn2);
	BayouWrite b6 = new BayouWrite( updates, as2, sid1, writeType, csn2);
	BayouWrite b7 = new BayouWrite( updates, as3, sid2, writeType, csn1);

	int result;
	result = b1.compareTo( b1 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite  1.  Result is: " + result );
	result = b1.compareTo( b2 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite  2.  Result is: " + result );
	result = b1.compareTo( b3 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  3.  Result is: " + result );
	result = b3.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite  4.  Result is: " + result );
	result = b3.compareTo( b4 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  5.  Result is: " + result );
	result = b4.compareTo( b3 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite  6.  Result is: " + result );
	result = b4.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  7.  Result is: " + result );
	result = b5.compareTo( b4 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite  8.  Result is: " + result );
	result = b5.compareTo( b6 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite  9.  Result is: " + result );
	result = b6.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 10.  Result is: " + result );
	result = b7.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 11.  Result is: " + result );
	result = b6.compareTo( b7 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 12.  Result is: " + result );

	b1 = new BayouWrite( updates, as1, sid1, writeType, csn1);
	b2 = new BayouWrite( updates, as1, sid1, writeType );
	b3 = new BayouWrite( updates, as1, sid2, writeType, csn2);	
	b4 = new BayouWrite( updates, as2, sid1, writeType, csn2);
	b5 = new BayouWrite( updates, as1, sid2, writeType, csn2);

	result = b1.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 13.  Result is: " + result );
	result = b2.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 14.  Result is: " + result );
	result = b2.compareTo( b3 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 15.  Result is: " + result );
	result = b3.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 16.  Result is: " + result );
	result = b2.compareTo( b4 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 17.  Result is: " + result );
	result = b4.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 18.  Result is: " + result );

	b1 = new BayouWrite( updates, as1, sid1, writeType );
	b2 = new BayouWrite( updates, as1, sid2, writeType );
	b3 = new BayouWrite( updates, as2, sid1, writeType );	

	result = b1.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 19.  Result is: " + result );
	result = b2.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 20.  Result is: " + result );
	result = b3.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 21.  Result is: " + result );
	result = b2.compareTo( b3 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 22.  Result is: " + result );
	result = b1.compareTo( b1 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite 21.  Result is: " + result );

	
	/*
	BigInteger commitSN2 = new BigInteger( "2" );


	BigInteger csn = bw1.getCSN();
	if( csn != null )
	    System.out.println( "Exception 1" );
	bw1.setCSN( commitSN1 );
	csn = bw1.getCSN();
	if( csn.intValue() != 1 )
	    System.out.println( "Exception 2" );

	
	BayouWrite bw2 = new BayouWrite( updates1, acceptStamp1, serverID1, writeType1 , commitSN2 );
	
	int result = bw1.compareTo( bw2 );
	System.out.println(result);
	if ( result > -1 )
	    System.out.println( "Exception 3" );
	result = bw2.compareTo( bw1 );
	if ( result < 1 )
	    System.out.println( "Exception 4" );

	bw1 = new BayouWrite( updates1, acceptStamp1, serverID1, writeType1 );
	bw2 = new BayouWrite( updates1, new BigInteger("2"), serverID1, writeType1 );
	result = bw1.compareTo( bw2 );
	if ( result > -1 )
	    System.out.println( "Exception 5" );
	result = bw2.compareTo( bw1 );
	if ( result < 1 )
	    System.out.println( "Exception 6" );

	bw1 = new BayouWrite( updates1, acceptStamp1, serverID1, writeType1 );
	bw2 = new BayouWrite( updates1, new BigInteger("2"), serverID2, writeType1 );
	if ( result != 0 )
	    System.out.println( "Exception 5" );
	result = bw2.compareTo( bw1 );
	if ( result != 0 )
	    System.out.println( "Exception 6" );
	

	*/
	
	/*

	public BayouWrite( ArrayList updates, BigInteger acceptStamp, ServerID serverID, String writeType );
        public BayouWrite( ArrayList updates, BigInteger acceptStamp, ServerID serverID, String writeType, BigInteger commitSN );
	getCNS();
	getAcceptStamp();
	getServerID();
	setCNS();
	compareTo();
	*/
    }
}
