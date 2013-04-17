package com.ath.bukkit.safespawn.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class BaseDao {

	// public static final Gson gson = new GsonBuilder() .setFieldNamingPolicy( FieldNamingPolicy.UPPER_CAMEL_CASE ) .setDateFormat( "yyyy-MM-dd'T'HH:mm:ss Z" )
	// .create();

	private JavaPlugin plugin;

	public BaseDao( JavaPlugin plugin ) {
		this.plugin = plugin;
	}

	public JSONObject read( String filename ) throws Exception {
		if ( filename != null ) {
			JSONParser parser = new JSONParser();
			String path = getPlugin().getDataFolder().getAbsolutePath() + File.separator + filename;
			File file = new File( path );
			if ( file.exists() ) {
				JSONObject json = (JSONObject) parser.parse( new FileReader( file ) );
				return json;
			}
		}
		return new JSONObject();
	}

	public void write( String data, String filename ) throws Exception {
		if ( data != null ) {
			String path = getPlugin().getDataFolder().getAbsolutePath() + File.separator + filename;
			OutputStream stream = new FileOutputStream( path );
			stream.write( data.getBytes() );
			stream.flush();
			stream.close();
		}
	}

	protected JavaPlugin getPlugin() {
		return plugin;
	}
}
