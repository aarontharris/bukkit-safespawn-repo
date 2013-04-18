package com.ath.bukkit.safespawn.data;

import org.bukkit.Location;
import org.json.simple.JSONObject;

public class Teleporter implements JSONSerializable {

	private Location location;
	private Location destination;

	public Teleporter() {
	}

	@Override
	public JSONObject toJson() throws Exception {
		JSUtl js = new JSUtl( new JSONObject() );
		js.put( "location", location );
		js.put( "destination", destination );
		return js.getJSON();
	}

	@Override
	public void fromJson( JSONObject json ) throws Exception {
		JSUtl js = new JSUtl( json );
		this.location = js.getLocation( "location", null );
		this.destination = js.getLocation( "destination", null );
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation( Location location ) {
		this.location = location;
	}

	public Location getDestination() {
		return destination;
	}

	public void setDestination( Location destination ) {
		this.destination = destination;
	}

}
