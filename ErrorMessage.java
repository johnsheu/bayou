import java.io.Serializable;

/**
 * This class encapsulates an erroneous message transferred through a
 * {@link Communicator}.
 *
 * @see Communicator
 */
public class ErrorMessage extends Message implements Serializable
{
	//  Recommended for Serializable
	private static final long serialVersionUID = 1L;

	//  The error message.
	private String error;

	/**
	 * Default constructor.  This constructor initializes a blank error
	 * message.
	 */
	public ErrorMessage()
	{
		this.error = "";
	}

	/**
	 * Constructor with error initializer.
	 *
	 * @param error  the <code>String</code> error message for this
	 *               message
	 */
	public ErrorMessage( String error )
	{
		this.error = error;
	}

	/**
	 * Set the error associated with this <code>ErrorMessage</code>.
	 *
	 * @param error  the <code>String</code> error message for this
	 *               message
	 */
	public void setError( String error )
	{
		this.error = error;
	}

	/**
	 * Get the error associated with this <code>ErrorMessage</code>.
	 *
	 * @return  the <code>String</code> error message for this message
	 */
	public String getError()
	{
		return error;
	}

	public String toString()
	{
		return super.toString() + getError();
	}
}

