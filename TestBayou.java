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
	//	testBayouWriteCompareTo();
	testBayouWriteUpdateCSN();
    }

    public void testServerID()
    {
	ServerID sid1 = new ServerID( null, new Long( "1" ));
	ServerID sid2 = new ServerID( sid1, new Long( "2" ));
	ServerID sid3 = new ServerID( sid1, new Long( "2" ));
	ServerID sid4 = new ServerID( sid1, new Long( "3" ));
	ServerID sid5 = new ServerID( sid2, new Long( "2" ));

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

    /*
    public void testBayouWriteCompareTo()
    {
	BayoutData updates = new BayouData();
	String writeType = "update";

	Long as1 = new Long( "1" );
	Long as2 = new Long( "2" );
	Long as3 = new Long( "3" );

	Long csn1 = new Long( "1" );
	Long csn2 = new Long( "2" );
	Long csn3 = new Long( "3" );

	ServerID sid1 = new ServerID( null, new Long("1") );
	ServerID sid2 = new ServerID( sid1, new Long("2") );
	ServerID sid3 = new ServerID( sid1, new Long("3") );
	ServerID sid4 = new ServerID( sid2, new Long("2") );
	ServerID sid5 = new ServerID( sid2, new Long("3") );
	ServerID sid6 = new ServerID( sid2, new Long("4") );


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
    */

    public void testBayouWriteUpdateCSN()
    {
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
    }
}
