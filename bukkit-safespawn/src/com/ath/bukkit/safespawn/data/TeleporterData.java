package com.ath.bukkit.safespawn.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TeleporterData implements JSONSerializable {

	List<Teleporter> teleporters;

	@SuppressWarnings( "unchecked" )
	@Override
	public JSONObject toJson() throws Exception {
		JSUtl js = new JSUtl( new JSONObject() );
		JSONArray jsTeles = new JSONArray();
		for ( Teleporter tele : teleporters ) {
			jsTeles.add( tele.toJson() );
		}
		js.put( "teleporters", jsTeles );
		return js.getJSON();
	}

	@Override
	public void fromJson( JSONObject json ) throws Exception {
		teleporters = new ArrayList<Teleporter>();
		JSUtl js = new JSUtl( json );
		JSONArray jsTeles = js.getJSONArray( "teleporters", null );
		if ( jsTeles != null ) {
			for ( Object o : jsTeles ) {
				JSONObject jsTele = (JSONObject) o;
				Teleporter tp = new Teleporter();
				tp.fromJson( jsTele );
				if ( tp.getLocation() != null && tp.getLocation().getWorld() != null ) {
					teleporters.add( tp );
				}
			}
		}
	}
}
