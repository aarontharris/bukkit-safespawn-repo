package com.ath.bukkit.safespawn;

import java.util.Date;

import org.json.simple.JSONObject;

import com.ath.bukkit.safespawn.data.PlayerData;

public class TestSafeSpawn {

	public static void main( String args[] ) {
		System.out.println( "Test PlayerData " + ( testPlayerData() ? "Pass" : "Fail" ) );
	}

	private static boolean testPlayerData() {
		try {
			PlayerData empty1 = new PlayerData();
			empty1.toJson();

			PlayerData empty2 = new PlayerData();
			empty2.fromJson( new JSONObject() );

			PlayerData data = new PlayerData();
			data.setName( "angryBits" );
			data.setFirstLogin( new Date() );
			data.setLastLogin( new Date() );
			data.setTimesLoggedIn( 15 );

			JSONObject json = data.toJson();

			PlayerData copy = new PlayerData();
			copy.fromJson( json );

			if ( !data.getName().equals( copy.getName() ) ) {
				System.out.println( "PlayerData.name fail" );
				return false;
			}
			if ( !data.getFirstLogin().equals( copy.getFirstLogin() ) ) {
				System.out.println( "PlayerData.firstLogin fail" );
				return false;
			}
			if ( !data.getLastLogin().equals( copy.getLastLogin() ) ) {
				System.out.println( "PlayerData.lastLogin fail" );
				return false;
			}
			if ( data.getTimesLoggedIn() != copy.getTimesLoggedIn() ) {
				System.out.println( "PlayerData.timesLoggedIn fail" );
				return false;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
