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
	String up1 = "", up2 = "";
	BayouWrite.Type writeType = BayouWrite.Type.EDIT;

	ServerID sid1 = new ServerID( null, 1L );
	ServerID sid2 = new ServerID( sid1, 2L );
	ServerID sid3 = new ServerID( sid1, 3L );
	ServerID sid4 = new ServerID( sid2, 2L );
	ServerID sid5 = new ServerID( sid2, 3L );

	WriteID csn1 = new WriteID( 1L, 4L, sid1 );
	WriteID csn2 = new WriteID( 2L, 5L, sid2 );

	WriteID as1 = new WriteID( 1L, sid2 );
	WriteID as2 = new WriteID( 2L, sid2 );
	WriteID as3 = new WriteID( 1L, sid3 );
	WriteID as4 = new WriteID( 2L, sid3 );

	BayouWrite<String,String>
		b1 = new BayouWrite<String,String>( up1, up2, writeType, csn1 ),
		b2 = new BayouWrite<String,String>( up1, up2, writeType, csn2 ),
		b3 = new BayouWrite<String,String>( up1, up2, writeType, as1 ),
		b4 = new BayouWrite<String,String>( up1, up2, writeType, as2 ),
		b5 = new BayouWrite<String,String>( up1, up2, writeType, as3 ),
		b6 = new BayouWrite<String,String>( up1, up2, writeType, as4 );

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
    }
}
