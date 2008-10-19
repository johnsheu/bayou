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
	testBayouWriteCompareTo();
	testBayouWriteUpdateCSN();
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


    public void testBayouWriteCompareTo()
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
	    System.out.println( "Exception BayouWriteCompareTo  1.  Result is: " + result );
	result = b1.compareTo( b2 );
	if( result != 0)
	    System.out.println( "Exception BayouWriteCompareTo  2.  Result is: " + result );
	result = b1.compareTo( b3 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo  3.  Result is: " + result );
	result = b3.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo  4.  Result is: " + result );
	result = b3.compareTo( b4 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo  5.  Result is: " + result );
	result = b4.compareTo( b3 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo  6.  Result is: " + result );
	result = b4.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo  7.  Result is: " + result );
	result = b5.compareTo( b4 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo  8.  Result is: " + result );
	result = b5.compareTo( b6 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo  9.  Result is: " + result );
	result = b6.compareTo( b5 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 10.  Result is: " + result );
	result = b7.compareTo( b6 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 11.  Result is: " + result );
	result = b6.compareTo( b7 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo 12.  Result is: " + result );

	b1 = new BayouWrite( updates, as1, sid1, writeType, csn1);
	b2 = new BayouWrite( updates, as1, sid1, writeType );
	b3 = new BayouWrite( updates, as1, sid2, writeType, csn2);	
	b4 = new BayouWrite( updates, as2, sid1, writeType, csn2);
	b5 = new BayouWrite( updates, as1, sid2, writeType, csn2);

	result = b1.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 13.  Result is: " + result );
	result = b2.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo 14.  Result is: " + result );
	result = b2.compareTo( b3 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo 15.  Result is: " + result );
	result = b3.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 16.  Result is: " + result );
	result = b2.compareTo( b4 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo 17.  Result is: " + result );
	result = b4.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 18.  Result is: " + result );

	b1 = new BayouWrite( updates, as1, sid1, writeType );
	b2 = new BayouWrite( updates, as1, sid2, writeType );
	b3 = new BayouWrite( updates, as2, sid1, writeType );	

	result = b1.compareTo( b2 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 19.  Result is: " + result );
	result = b2.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo 20.  Result is: " + result );
	result = b3.compareTo( b1 );
	if( result != 1)
	    System.out.println( "Exception BayouWriteCompareTo 21.  Result is: " + result );
	result = b2.compareTo( b3 );
	if( result != -1)
	    System.out.println( "Exception BayouWriteCompareTo 22.  Result is: " + result );
	result = b1.compareTo( b1 );
	if( result != 0)
	    System.out.println( "Exception BayouWriteCompareTo 23.  Result is: " + result );

	ArrayList updates1   = new ArrayList();
	ArrayList updates2   = new ArrayList();
	String    writeType1 = "update";
	String    writeType2 = "add";
	
	b1 = new BayouWrite( updates1, as1, sid1, writeType1, csn1 );
	b2 = new BayouWrite( updates2, as1, sid1, writeType2, csn1 );

	result = b1.compareTo( b2 );
	if( result != 0)
	    System.out.println( "Exception BayouWriteCompareTo 24.  Result is: " + result );
	result = b2.compareTo( b1 );
	if( result != 0)
	    System.out.println( "Exception BayouWriteCompareTo 25.  Result is: " + result );
    }

    public void testBayouWriteUpdateCSN()
    {
	BayouDB db = new BayouDB();

	ArrayList updates = new ArrayList();
	String writeType = "update";

	BigInteger as1 = new BigInteger( "1" );
	BigInteger as2 = new BigInteger( "2" );
	BigInteger as3 = new BigInteger( "3" );

	BigInteger csn1 = new BigInteger( "1" );
	BigInteger csn2 = new BigInteger( "2" );
	BigInteger csn3 = new BigInteger( "3" );
	BigInteger csn4 = new BigInteger( "4" );
	BigInteger csn5 = new BigInteger( "5" );
	BigInteger csn6 = new BigInteger( "6" );
	BigInteger csn7 = new BigInteger( "7" );

	ServerID sid1 = new ServerID( null, new BigInteger("1") );
	ServerID sid2 = new ServerID( sid1, new BigInteger("2") );
	ServerID sid3 = new ServerID( sid1, new BigInteger("3") );

	BayouWrite b1 = new BayouWrite( updates, as1, sid1, writeType, csn1);
	BayouWrite b2 = new BayouWrite( updates, as2, sid1, writeType, csn2);	
	BayouWrite b3 = new BayouWrite( updates, as1, sid2, writeType, csn3);
	BayouWrite b5 = new BayouWrite( updates, as2, sid2, writeType, csn5);
	BayouWrite b6 = new BayouWrite( updates, as3, sid1, writeType );
	BayouWrite b7 = new BayouWrite( updates, as3, sid2, writeType, csn7);

	db.addWrite( b1 );
	db.addWrite( b2 );
	db.addWrite( b3 );
	db.addWrite( b7 );
	db.addWrite( b5 );
	db.addWrite( b6 );

	db.printTreeSet();

	db.updateCSN( sid1, as2, csn4);

	db.printTreeSet();
    }
}
