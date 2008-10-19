import java.io.Serializable;

public class BayouWrite implements Comparable<BayouWrite>, Serializable
{
	enum Type
	{
		ADD,
		EDIT,
		DELETE,
		CREATE,
		RETIRE;
	}

	private BayouData writeData;

	private Type writeType;

	private WriteID writeID;

	public BayouWrite( BayouData data, Type type, WriteID id )
	{
		this.writeData = data;
		this.writeType = type;
		this.writeID = id;
	}

	public BayouData getData()
	{
		return writeData;
	}

	public void setData( BayouData data )
	{
		this.writeData = data;
	}

	public Type getType()
	{
		return writeType;
	}

	public void setType( Type type )
	{
		this.writeType = type;
	}

	public WriteID getWriteID()
	{
		return writeID;
	}

	public void setWriteID( WriteID id )
	{
		this.writeID = id;
	}

	public int compareTo( BayouWrite other )
	{
		return writeID.compareTo( other.writeID );
	}

	public String toString()
	{
		return "<T: " + writeType + ", #: " + writeID + ", D: " +
			writeData + ">";
	}
}

