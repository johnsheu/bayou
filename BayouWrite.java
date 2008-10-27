import java.io.Serializable;

public class BayouWrite<K, V> implements Comparable<BayouWrite>, Serializable
{
	public static final long serialVersionUID = 1L;

	enum Type
	{
		ADD,
		EDIT,
		DELETE,
		CREATE,
		RETIRE;
	}

	private K writeKey;

	private V writeValue;

	private Type writeType;

	private WriteID writeID;

	public BayouWrite( K key, V value, Type type, WriteID id )
	{
		this.writeKey = key;
		this.writeValue = value;
		this.writeType = type;
		this.writeID = id;
	}

	public K getKey()
	{
		return writeKey;
	}

	public void setKey( K key )
	{
		this.writeKey = key;
	}

	public V getValue()
	{
		return writeValue;
	}

	public void setValue( V value )
	{
		this.writeValue = value;
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

	public boolean equals( Object o )
	{
		if ( this == o )
			return true;
		if ( o == null )
			return false;

		BayouWrite<K, V> other = (BayouWrite<K, V>)o;
		return this.writeID.equals( other.writeID );
	}

	public int hashCode()
	{
		return writeID.hashCode();
	}

	public int compareTo( BayouWrite other )
	{
		return this.writeID.compareTo( other.writeID );
	}

	public String toString()
	{
		return "<T:" + writeType + ", #:" + writeID + ", K:" +
			writeKey + ", V:" + writeValue + ">";
	}
}

