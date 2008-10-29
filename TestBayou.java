import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

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
		testGetUpdates();
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


	public void testGetUpdates()
	{
		BayouDB<String, String> db1 = new BayouDB<String, String>();
		BayouDB<String, String> db2 = new BayouDB<String, String>();

		ServerID sid1 = new ServerID( null, 1L ),
			 sid2 = new ServerID( sid1, 2L ),
		         sid3 = new ServerID( sid1, 3L ),
			 sid4 = new ServerID( sid1, 4L ),
			 sid5 = new ServerID( sid1, 5L );
		    

		WriteID wid1  = new WriteID( 1L, 1L, sid1 ),
			wid2  = new WriteID( 2L, 2L, sid1 ),
			wid3  = new WriteID( 3L, 3L, sid1 ),
			wid4  = new WriteID( 4L, 1L, sid3 ),
			wid5  = new WriteID( 5L, 1L, sid4 ),
			wid6  = new WriteID( 6L, 2L, sid2 ),
			wid7  = new WriteID( 7L, 2L, sid4 ),

			wid8  = new WriteID( 1L, sid1 ),
			wid9  = new WriteID( 2L, sid1 ),
			wid10 = new WriteID( 3L, sid1 ),
			wid11 = new WriteID( 3L, sid3 ),
			wid12 = new WriteID( 3L, sid4 ),
			wid13 = new WriteID( 4L, sid2 ),
			wid14 = new WriteID( 4L, sid3 ),
			wid15 = new WriteID( 5L, sid2 );


		BayouAEResponse resp1 = new BayouAEResponse();
		BayouAEResponse resp2 = new BayouAEResponse();

		resp1.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid1  ));
		resp1.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid2  ));
		resp1.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid3  ));

		/*
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid4  ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid5  ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid6  ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid7  ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid8  ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid9  ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid10 ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid11 ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid12 ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid13 ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid14 ));
		resp.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid15 ));
		*/

		//db1.setTruncate( 6L );
		db1.applyUpdates( resp1 );

		HashMap<ServerID, Long> versionVector = new HashMap<ServerID, Long>();
		
		versionVector.put( sid1, 0L );
		versionVector.put( sid2, 0L );
		versionVector.put( sid3, 0L );
		versionVector.put( sid4, 0L );
		
		BayouAERequest request = new BayouAERequest( -1L, -1L, versionVector );

		resp1 = db1.getUpdates( request );

		printResponse( resp1 );


		db2.setPrimary( true );
		resp2.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid8  ));
		resp2.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid9  ));
		resp2.addWrite( new BayouWrite<String, String> ("", "", BayouWrite.Type.EDIT, wid10 ));
		db2.applyUpdates( resp2 );

		resp2 = db2.getUpdates( request );

		printResponse( resp2 );

	}

	private void printResponse( BayouAEResponse resp )
	{
		System.out.println( "\n\n" );
		System.out.println( "NEW RESPONSE" );

		//Print out writes
		LinkedList<BayouWrite<String, String>> writeList = resp.getWrites();

		if( writeList == null )
		    System.out.println( "No writes sent." );
		else
		{
			Iterator<BayouWrite<String, String>> writes = writeList.iterator();
			WriteID wid;

			System.out.println( "Writes sent: " );
		


			System.out.println();
			while( writes.hasNext() )
			    {
				wid = writes.next().getWriteID();
				System.out.println( wid );
			    }
		}
		System.out.println();

		//Print out commit notifications
		LinkedList<WriteID> noteList = resp.getCommitNotifications();
		if( noteList == null )
			System.out.println( "No Commit Notifications." );
		else
		{
			System.out.println( "Commit Notifications: " );
			Iterator<WriteID> notes = noteList.iterator();

			while( notes.hasNext() )
			{
			    System.out.println( notes.next() );
			}

			System.out.println();		
		}

		System.out.println();

		//Print out database
		HashMap<String, String> map = resp.getDatabase();

		if( map != null )
			System.out.println( "Database sent is: \n" + map); 
		else
			System.out.println( "No database sent." );
		System.out.println();

		//Print out OSN
		long OSN = resp.getOSN();
		if( OSN == -1 )
		    System.out.println( "No OSN sent." );
		else
		    System.out.println( "OSN is: " + OSN );
		System.out.println();

		//Print out omitted version vector
		HashMap<ServerID, Long> omap = resp.getOmittedVector();
		if( omap == null )
		    System.out.println( "No omitted version vector sent." );
		else
		    System.out.println( "Omitted version vector is:\n" + omap );
		System.out.println();
	}
}
