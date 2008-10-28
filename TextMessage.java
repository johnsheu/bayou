import java.io.Serializable;

public class TextMessage extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String message = null;

	public TextMessage()
	{
		super();
		message = "";
	}

	public TextMessage( String text )
	{
		super();
		message = text;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String text )
	{
		message = text;
	}

	public String toString()
	{
		return message;
	}
}

