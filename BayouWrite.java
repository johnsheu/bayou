import java.io.Serializable;

public class BayouWrite<Data extends Comparable<Data> & Serializable>
	implements Comparable<BayouWrite>, Serializable
{
	enum Type
	{
		ADD,
		EDIT,
		DELETE,
		CREATE,
		RETIRE;
	}

	private Data writeData;

	private Type writeType;

	private WriteID writeID;

	public BayouWrite( Data data, Type type, WriteID id )
	{
		this.writeData = data;
		this.writeType = type;
		this.writeID = id;
	}

	public Data getData()
	{
		return writeData;
	}

	public void setData( Data data )
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

