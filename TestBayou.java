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
	testBayouWriteUpdateCSN();
    }

    public void testServerID()
    {
	ServerID sid1 = new ServerID( null, 1L );
	ServerID sid2 = new ServerID( sid1, 2L );
	ServerID sid3 = new ServerID( sid1, 2L );
	ServerID sid4 = new ServerID( sid1, 3L );
	ServerID sid5 = new ServerID( sid2, 2L );

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
	Song update = new Song();
	BayouWrite.Type writeType = BayouWrite.Type.EDIT;

	ServerID sid1 = new ServerID( null, 1L );
	ServerID sid2 = new ServerID( sid1, 2L );
	ServerID sid3 = new ServerID( sid1, 3L );
	ServerID sid4 = new ServerID( sid2, 2L );
	ServerID sid5 = new ServerID( sid2, 3L );

	WriteID csn1 = new WriteID( 1L );
	WriteID csn2 = new WriteID( 2L );

	WriteID as1 = new WriteID( 1L, sid2 );
	WriteID as2 = new WriteID( 2L, sid2 );
	WriteID as3 = new WriteID( 1L, sid3 );
	WriteID as4 = new WriteID( 2L, sid3 );

	BayouWrite<Song> b1 = new BayouWrite<Song>( update, writeType, csn1 );
	BayouWrite<Song> b2 = new BayouWrite<Song>( update, writeType, csn2 );
	BayouWrite<Song> b3 = new BayouWrite<Song>( update, writeType, as1 );
	BayouWrite<Song> b4 = new BayouWrite<Song>( update, writeType, as2 );
	BayouWrite<Song> b5 = new BayouWrite<Song>( update, writeType, as3 );
	BayouWrite<Song> b6 = new BayouWrite<Song>( update, writeType, as4 );

	int result;
	result = b1.compareTo( b1 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite  1.  Result is: " + result );
	result = b1.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  2.  Result is: " + result );
	result = b1.compareTo( b3 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  3.  Result is: " + result );
	result = b1.compareTo( b4 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  4.  Result is: " + result );
	result = b1.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  5.  Result is: " + result );
	result = b1.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  6.  Result is: " + result );
	result = b2.compareTo( b2 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite  7.  Result is: " + result );
	result = b2.compareTo( b3 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  8.  Result is: " + result );
	result = b2.compareTo( b4 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite  9.  Result is: " + result );
	result = b2.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 10.  Result is: " + result );
	result = b2.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 11.  Result is: " + result );
	result = b3.compareTo( b3 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite 12.  Result is: " + result );
	result = b3.compareTo( b4 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 13.  Result is: " + result );
	result = b3.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 14.  Result is: " + result );
	result = b3.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 15.  Result is: " + result );
	result = b4.compareTo( b4 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite 16.  Result is: " + result );
	result = b4.compareTo( b5 );
	if( result != 1)
	    System.out.println( "Exception BayouWrite 17.  Result is: " + result );
	result = b4.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 18.  Result is: " + result );
	result = b5.compareTo( b5 );
	if( result != 0)
	    System.out.println( "Exception BayouWrite 19.  Result is: " + result );
	result = b5.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWrite 20.  Result is: " + result );
	result = b6.compareTo( b6 );
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

	
	BayouWrite bw2 = new BayouWrite( update1, acceptStamp1, serverID1, writeType1 , commitSN2 );
	
	int result = bw1.compareTo( bw2 );
	System.out.println(result);
	if ( result > -1 )
	    System.out.println( "Exception 3" );
	result = bw2.compareTo( bw1 );
	if ( result < 1 )
	    System.out.println( "Exception 4" );

	bw1 = new BayouWrite( update1, acceptStamp1, serverID1, writeType1 );
	bw2 = new BayouWrite( update1, new BigInteger("2"), serverID1, writeType1 );
	result = bw1.compareTo( bw2 );
	if ( result > -1 )
	    System.out.println( "Exception 5" );
	result = bw2.compareTo( bw1 );
	if ( result < 1 )
	    System.out.println( "Exception 6" );

	bw1 = new BayouWrite( update1, acceptStamp1, serverID1, writeType1 );
	bw2 = new BayouWrite( update1, new BigInteger("2"), serverID2, writeType1 );
	if ( result != 0 )
	    System.out.println( "Exception 5" );
	result = bw2.compareTo( bw1 );
	if ( result != 0 )
	    System.out.println( "Exception 6" );
	

	*/
	
	/*

	public BayouWrite( ArrayList update, BigInteger acceptStamp, ServerID serverID, String writeType );
        public BayouWrite( ArrayList update, BigInteger acceptStamp, ServerID serverID, String writeType, BigInteger commitSN );
	getCNS();
	getAcceptStamp();
	getServerID();
	setCNS();
	compareTo();
	*/
    }
	
	public void testBayouWriteUpdateCSN()
    {
	/*
	BayouDB db = new BayouDB();

	BayouData updates = new BayouData();
	BayouWrite.Type writeType = BayouWrite.Type.ADD;



	Long as1 = new Long( "1" );
	Long as2 = new Long( "2" );
	Long as3 = new Long( "3" );

	Long csn1 = new Long( "1" );
	Long csn2 = new Long( "2" );
	Long csn3 = new Long( "3" );
	Long csn4 = new Long( "4" );
	Long csn5 = new Long( "5" );
	Long csn6 = new Long( "6" );
	Long csn7 = new Long( "7" );

	ServerID sid1 = new ServerID( null, new Long("1") );
	ServerID sid2 = new ServerID( sid1, new Long("2") );
	ServerID sid3 = new ServerID( sid1, new Long("3") );

	WriteID wid1 = new WriteID( as1, sid1 );
	wid1.setCSN( csn1 );
	WriteID wid2 = new WriteID( as2, sid1 );
	wid2.setCSN( csn2 );
	WriteID wid3 = new WriteID( as1, sid2 );
	wid3.setCSN( csn3 );
	WriteID wid4 = new WriteID( as2, sid2);
	wid4.setCSN( csn5 );
	WriteID wid5 = new WriteID( as3, sid1 );
	WriteID wid6 = new WriteID( as3, sid2 );
	wid6.setCSN( csn7 );

	BayouWrite b1 = new BayouWrite( updates, writeType, wid1 );
	BayouWrite b2 = new BayouWrite( updates, writeType, wid2 );	
	BayouWrite b3 = new BayouWrite( updates, writeType, wid3 );
	BayouWrite b5 = new BayouWrite( updates, writeType, wid4 );
	BayouWrite b6 = new BayouWrite( updates, writeType, wid5 );
	BayouWrite b7 = new BayouWrite( updates, writeType, wid6 );

	db.addWrite( b1 );
	db.addWrite( b2 );
	db.addWrite( b3 );
	db.addWrite( b7 );
	db.addWrite( b5 );
	db.addWrite( b6 );

	db.printTreeSet();

	db.updateCSN( wid5, csn4 );

	db.printTreeSet();
	*/
    }
}
