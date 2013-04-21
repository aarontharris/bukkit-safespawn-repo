package com.ath.bukkit.safespawn.data;


//@Entity
//@Table( name = "DBTest" )
public class SimpleKeyVal {

	// @Id
	private int id;

	// @NotEmpty
	private String key;

	private String value;

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey( String key ) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue( String value ) {
		this.value = value;
	}

}
