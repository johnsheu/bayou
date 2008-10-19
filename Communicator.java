import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A class which handles asynchronous socket I/O.  This class sends and
 * receives {@link Message} objects in a non-blocking, FIFO manner.
 *
 * @see Message
 */
public class Communicator
{
	/*
	 * Helper class that handles threaded message sending
	 */
	private class SenderThread extends Thread
	{
		private volatile boolean active = false;

		private int port = -1;

		public SenderThread( int port )
		{
			//  Java VM should not wait for thread to die naturally
			this.port = port;
			setDaemon( true );
		}

		//  Set active state
		public synchronized void setActive( boolean active )
		{
			if ( this.active )
			{
				if ( active )
					return;
				this.active = false;
			}
			else
			{
				if ( !active )
					return;
				this.active = true;
				start();
			}
		}

		public synchronized boolean isActive()
		{
			return active;
		}

		public void run()
		{
			while ( active )
			{
				//  Send all messages in queue
				while ( !senderQueue.isEmpty() )
				{
					Message message = senderQueue.poll();
					try
					{
						Socket socket = new Socket();
						socket.connect( message.getAddress() );
						ObjectOutputStream oos =
							new ObjectOutputStream( socket.getOutputStream() );
						message.setAddress( socket.getLocalAddress(), port );
						oos.writeObject( message );
					}
					catch ( IOException ex )
					{
						ex.printStackTrace();
						ErrorMessage emessage = new ErrorMessage();
						emessage.setAddress( message.getAddress() );
						emessage.setError( ex.toString() );
						receiverQueue.offer( emessage );
					}
				}

				//  Sleep until next poll or interruption
				try
				{
					sleep( 50 );
				}
				catch ( InterruptedException ex )
				{
					//  No stack trace here, normal operation
				}
			}
		}
	}

	/*
	 * Helper class that handles threaded message receiving
	 */
	private class ReceiverThread extends Thread
	{
		private volatile boolean active = false;

		private ServerSocket ssocket = null;

		public ReceiverThread( int port )
		{
			//  Java VM should not wait for thread to die naturally
			setDaemon( true );

			try
			{
				ssocket = new ServerSocket( port );
				//  100-millisecond polling
				ssocket.setSoTimeout( 50 );
			}
			catch ( IOException ex )
			{
				ex.printStackTrace();
			}
		}

		//  Set active state
		public synchronized void setActive( boolean active )
		{
			if ( this.active )
			{
				if ( active )
					return;
				this.active = false;
			}
			else
			{
				if ( !active )
					return;
				this.active = true;
				start();
			}
		}

		public synchronized boolean isActive()
		{
			return active;
		}

		public void run()
		{
			while( active )
			{
				try
				{
					Socket socket = ssocket.accept();
					if ( socket != null )
					{
						ObjectInputStream oos =
							new ObjectInputStream( socket.getInputStream() );
						Object o = oos.readObject();
						Message m = (Message)o;
						receiverQueue.offer( (Message)o );
					}
				}
				catch ( SocketTimeoutException ex )
				{
					//  No stack trace, normal operation
				}
				catch ( IOException ex )
				{
					ex.printStackTrace();
				}
				catch ( ClassNotFoundException ex )
				{
					ex.printStackTrace();
				}
			}
		}
	}

	//  Message receive queue
	ConcurrentLinkedQueue<Message> receiverQueue = null;

	//  Message send queue
	ConcurrentLinkedQueue<Message> senderQueue = null;

	//  Message receive thread
	ReceiverThread receiver = null;

	//  Message sender thread
	SenderThread sender = null;

	public Communicator( int port )
	{
		receiverQueue = new ConcurrentLinkedQueue<Message>();
		senderQueue = new ConcurrentLinkedQueue<Message>();
		receiver = new ReceiverThread( port );
		sender = new SenderThread( port );
	}

	/**
	 * Send a message using this <code>Communicator</code>.  The message
	 * is put into the sender queue and sent asynchronously
	 *
	 * @param message  the {@link Message} object to send
	 *
	 * @see Message
	 */
	public void sendMessage( Message message )
	{
		senderQueue.offer( message );
		//  Poke the thread to send immediately
		sender.interrupt();
	}

	/**
	 * Read a message using this <code>Communicator</code>.  The message
	 * is read and removed from the message queue.  If the queue is empty,
	 * it returns <code>null</code>.
	 *
	 * @return  the {@link Message} at the front of the receive queue, or
	 *          <code>null</code> if the queue is empty
	 */
	public Message readMessage()
	{
		return receiverQueue.poll();
	}

	/**
	 * Peek at a message using this <code>Communicator</code>.  The message
	 * is read from the message queue, but not removed.  If the queue is
	 * empty, it returns <code>null</code>.
	 *
	 * @return  the {@link Message} at the front of the receive queue, or
	 *          <code>null</code> if the queue is empty
	 */
	public Message peekMessage()
	{
		return receiverQueue.peek();
	}

	/**
	 * Start the asynchronous I/O threads for this <code>Communicator</code>.
	 */
	public void start()
	{
		receiver.setActive( true );
		sender.setActive( true );
	}

	/**
	 * Stop the asynchronous I/O threads for this <code>Communicator</code>.
	 * While stopped, no I/O will occur, but message received up to this
	 * point can still be retrieved from the queue.
	 */ 
	public void stop()
	{
		receiver.setActive( false );
		sender.setActive( false );
	}

	/**
	 * Tests if this <code>Communicator</code> is performing I/O.
	 *
	 * @return  <code>true</code> if the I/O threads are running,
	 *          <code>false</code> otherwise
	 */
	public boolean isStarted()
	{
		return receiver.isActive();
	}
}

