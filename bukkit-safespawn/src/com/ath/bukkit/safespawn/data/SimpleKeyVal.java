package com.ath.bukkit.safespawn.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;


@Entity
@Table( name = "SimpleKeyVal" )
public class SimpleKeyVal implements Persisted {
	public static final String KEY = "key";
	public static final String val = "value";

	public static final String[] SCHEMA = {
			"create table SimpleKeyVal" +
					"(" +
					"key TEXT," +
					"value TEXT," +
					"id INTEGER primary key autoincrement" +
					");",
			"CREATE INDEX SimpleKeyVal_key_INDEX ON SimpleKeyVal (key);" };

	@Override
	public String[] getSchema() {
		return SCHEMA;
	}

	@Id
	private int id;

	@NotEmpty
	@Column( name = "key", unique = true )
	private String key;

	@NotNull
	@Column( name = "value" )
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
