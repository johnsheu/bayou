import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

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
		//  The port to send to
		private int port = -1;

		public SenderThread( int port )
		{
			this.port = port;

			//  Java VM should not wait for thread to die naturally
			setDaemon( true );
		}

		public void run()
		{
			while ( !isInterrupted() )
			{
				Message message = null;

				//  Wait for message to send
				try
				{
					message = senderQueue.take();
				}
				catch ( InterruptedException ex )
				{
					return;
				}

				//  Try sending it
				Socket socket = new Socket();
				try
				{
					socket.connect( message.getAddress() );
					ObjectOutputStream oos =
						new ObjectOutputStream( socket.getOutputStream() );
					message.setAddress( socket.getLocalAddress(), port );
					oos.writeObject( message );
				}
				catch ( IOException ex )
				{
					//  ex.printStackTrace();
					ErrorMessage emessage = new ErrorMessage();
					emessage.setAddress( message.getAddress() );
					emessage.setError( ex.toString() );
					receiverQueue.offer( emessage );
				}
				finally
				{
					try
					{
						socket.close();
					}
					catch ( IOException ex )
					{
						//  ex.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * Helper class that handles threaded message receiving
	 */
	private class ReceiverThread extends Thread
	{
		//  The port to listen on
		private int port = -1;

		public ReceiverThread( int port )
		{
			this.port = port;

			//  Java VM should not wait for thread to die naturally
			setDaemon( true );
		}

		public void run()
		{
			//  Socket setup
			ServerSocketChannel sschannel = null;
			try
			{
				sschannel = ServerSocketChannel.open();
				sschannel.configureBlocking( true );
				sschannel.socket().bind( new InetSocketAddress( port ) );
			}
			catch ( IOException ex )
			{
				ex.printStackTrace();
				return;
			}

			while ( !isInterrupted() )
			{
				Socket socket = null;

				//  Wait for connection
				try
				{
					socket = sschannel.accept().socket();
				}
				catch ( ClosedByInterruptException ex )
				{
					return;
				}
				catch ( IOException ex )
				{
					ex.printStackTrace();
					continue;
				}

				//  Pull data off connection
				try
				{
					ObjectInputStream oos =
						new ObjectInputStream( socket.getInputStream() );
					Object o = oos.readObject();
					receiverQueue.offer( (Message)o );
				}
				catch ( IOException ex )
				{
					//  ex.printStackTrace();
				}
				catch ( ClassNotFoundException ex )
				{
					ex.printStackTrace();
				}
				finally
				{
					try
					{
						socket.close();
					}
					catch ( IOException ex )
					{
						//  ex.printStackTrace();
					}
				}
			}
		}
	}

	//  The port to listen on
	int port = -1;

	//  Message receive queue
	LinkedBlockingQueue<Message> receiverQueue = null;

	//  Message send queue
	LinkedBlockingQueue<Message> senderQueue = null;

	//  Message receive thread
	ReceiverThread receiver = null;

	//  Message sender thread
	SenderThread sender = null;

	public Communicator( int port )
	{
		this.port = port;
		receiverQueue = new LinkedBlockingQueue<Message>();
		senderQueue = new LinkedBlockingQueue<Message>();
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
	 * Read a message using this <code>Communicator</code>, blocking until
	 * a message is available.  The message is read and removed from the
	 * message queue, blocking if empty.
	 *
	 * @return  the {@link Message} at the front of t, blocking if empty.)
	 * 
	 * @throws InterruptedException if interrupted while waiting
	 */
	public Message readMessageBlocking() throws InterruptedException
	{
		return receiverQueue.take();
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
	public synchronized void start()
	{
		if ( receiver != null )
			return;

		receiver = new ReceiverThread( port );
		sender = new SenderThread( port );

		receiver.start();
		sender.start();
	}

	/**
	 * Stop the asynchronous I/O threads for this <code>Communicator</code>.
	 * While stopped, no I/O will occur, but message received up to this
	 * point can still be retrieved from the queue.
	 */ 
	public synchronized void stop()
	{
		if ( receiver == null )
			return;

		receiver.interrupt();
		sender.interrupt();

		try
		{
			receiver.join();
		}
		catch ( InterruptedException ex )
		{

		}

		try
		{
			sender.join();
		}
		catch ( InterruptedException ex )
		{

		}
	}

	/**
	 * Tests if this <code>Communicator</code> is performing I/O.
	 *
	 * @return  <code>true</code> if the I/O threads are running,
	 *          <code>false</code> otherwise
	 */
	public synchronized boolean isStarted()
	{
		return receiver != null;
	}
}

