package com.ath.bukkit.safespawn.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;

import com.ath.bukkit.safespawn.SafeSpawnPlugin;

public class LocationSerializer implements JSONSerializable {

	// This is the serializer for Location, so we don't use JSUtl.put/getLocation
	private transient Location location;

	public LocationSerializer( Location location ) {
		this.location = location;
	}

	public LocationSerializer() {
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public JSONObject toJson() throws Exception {
		JSUtl js = new JSUtl( new JSONObject() );
		if ( location != null && location.getWorld() != null ) {
			js.put( "world", location.getWorld().getName() );
			js.put( "x", location.getX() );
			js.put( "y", location.getY() );
			js.put( "z", location.getZ() );
			js.put( "yaw", location.getYaw() );
			js.put( "pitch", location.getPitch() );
		}
		return js.getJSON();
	}

	@Override
	public void fromJson( JSONObject json ) throws Exception {
		JSUtl js = new JSUtl( json );
		String worldName = js.getString( "world", null );
		World world = SafeSpawnPlugin.instance().getServer().getWorld( worldName ); // FIXME: need injection
		if ( world != null ) {
			Double x = js.getDouble( "x", 0.0 );
			Double y = js.getDouble( "y", 0.0 );
			Double z = js.getDouble( "z", 0.0 );
			Float yaw = js.getFloat( "yaw", 0.0f );
			Float pitch = js.getFloat( "pitch", 0.0f );
			this.location = new Location( world, x, y, z, yaw, pitch );
		}
	}

}
