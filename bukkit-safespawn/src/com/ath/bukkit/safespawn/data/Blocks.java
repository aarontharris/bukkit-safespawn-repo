package com.ath.bukkit.safespawn.data;

import java.util.Collections;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Log;

public class Blocks {

	public static boolean isMagical( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getBoolean( "magical", false );
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static void setMagical( Block b, boolean enabled ) {
		try {
			BlockData.attain( b ).putBoolean( "magical", enabled );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void grantReadWriteAccess( Block b, List<String> playerNames ) {
		try {
			grantReadAccess( b, playerNames );
			grantWriteAccess( b, playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void grantReadAccess( Block b, List<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.putStringArray( "read_access", playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void grantWriteAccess( Block b, List<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.putStringArray( "write_access", playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static List<String> getReadAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getStringArray( "read_access" );
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptyList();
	}

	public static List<String> getWriteAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getStringArray( "write_access" );
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptyList();
	}

	/** do not use in a loop */
	public static boolean hasReadAccess( BlockData bd, Player player ) {
		try {
			List<String> access = getReadAccess( bd );
			if ( access.size() == 0 ) {
				return true;
			} else if ( access.contains( player.getName() ) ) {
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/** do not use in a loop */
	public static boolean hasWriteAccess( BlockData bd, Player player ) {
		try {
			List<String> access = getWriteAccess( bd );
			if ( access.size() == 0 ) {
				return true;
			} else if ( access.contains( player.getName() ) ) {
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static void grantReadAccess( Block b, Player player ) {
		try {
			BlockData bd = BlockData.attain( b );
			List<String> access = getReadAccess( bd );
			if ( !access.contains( player.getName() ) ) {
				access.add( player.getName() );
				grantReadAccess( b, access );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void grantWriteAccess( Block b, Player player ) {
		try {
			BlockData bd = BlockData.attain( b );
			List<String> access = getWriteAccess( bd );
			if ( !access.contains( player.getName() ) ) {
				access.add( player.getName() );
				grantWriteAccess( b, access );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
