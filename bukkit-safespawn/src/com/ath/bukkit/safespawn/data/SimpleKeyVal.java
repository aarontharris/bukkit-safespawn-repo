package com.ath.bukkit.safespawn.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;


@Entity
@Table( name = "SimpleKeyVal" )
public class SimpleKeyVal {
	public static final String KEY = "key";
	public static final String val = "value";

	@Id
	private int id;

	@NotEmpty
	@Column( unique = true )
	private String key;

	@NotNull
	private String value;

	public SimpleKeyVal() {
	}
	
	public SimpleKeyVal( String key, String val ) {
		setKey( key );
		setValue( val );
	}

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
