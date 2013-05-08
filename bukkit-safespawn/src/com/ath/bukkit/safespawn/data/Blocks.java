package com.ath.bukkit.safespawn.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.Log;

public class Blocks {

	private static final String WRITE_ACCESS = "write_access";
	private static final String READ_ACCESS = "read_access";

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

	public static void grantReadWriteAccess( Block b, Collection<String> playerNames ) {
		try {
			setReadAccess( b, playerNames );
			setWriteAccess( b, playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** overwrite */
	public static void setReadAccess( Block b, Collection<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.putStringArray( READ_ACCESS, new ArrayList<String>( playerNames ) );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** overwrite */
	public static void setWriteAccess( Block b, Collection<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.putStringArray( WRITE_ACCESS, new ArrayList<String>( playerNames ) );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** never null */
	public static Set<String> getReadAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getStringSet( READ_ACCESS );
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	/** never null */
	public static Set<String> getWriteAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getStringSet( WRITE_ACCESS );
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	/** do not use in a loop */
	public static boolean canRead( BlockData bd, Player player ) {
		try {
			Log.line( "canRead( %s, %s )", bd.toString(), player.getName() );
			if ( canWrite( bd, player ) ) {
				Log.line( "canRead( %s, %s ) - canWrite", bd.toString(), player.getName() );
				return true;
			}
			Set<String> rAccess = getReadAccess( bd );
			if ( rAccess.contains( player.getName() ) ) {
				Log.line( "canRead( %s, %s ) - read.contains", bd.toString(), player.getName() );
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		Log.line( "canRead( %s, %s ) - no", bd.toString(), player.getName() );
		return false;
	}

	/** do not use in a loop */
	public static boolean canWrite( BlockData bd, Player player ) {
		try {
			Log.line( "canWrite( %s, %s )", bd.toString(), player.getName() );
			Set<String> access = getWriteAccess( bd );
			return hasWriteAccess( bd, access, player );
		} catch ( Exception e ) {
			Log.error( e );
		}
		Log.line( "canWrite( %s, %s ) - no", bd.toString(), player.getName() );
		return false;
	}

	/** Loop safe */
	public static boolean hasWriteAccess( BlockData bd, Set<String> access, Player player ) {
		try {
			Log.line( "hasWriteAccess( %s, %s )", bd.toString(), player.getName() );
			if ( player.hasPermission( Const.PERM_skeleton_key ) ) {
				Log.line( "hasWriteAccess( %s, %s ) - skeleton key", bd.toString(), player.getName() );
				player.sendMessage( "Accessed with Skeleton Key" );
				return true;
			}

			if ( access.isEmpty() ) {
				Log.line( "hasWriteAccess( %s, %s ) - access is empty", bd.toString(), player.getName() );
				return true;
			} else if ( access.contains( player.getName() ) ) {
				Log.line( "hasWriteAccess( %s, %s ) - access.contains", bd.toString(), player.getName() );
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		Log.line( "hasWriteAccess( %s, %s ) - no", bd.toString(), player.getName() );
		return false;
	}

	public static void grantReadAccess( Block b, Player player ) {
		try {
			BlockData bd = BlockData.attain( b );
			Set<String> access = getReadAccess( bd );
			if ( !access.contains( player.getName() ) ) {
				access.add( player.getName() );
				setReadAccess( b, access );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void grantWriteAccess( Block b, Player player ) {
		grantWriteAccess( b, player.getName() );
	}

	public static void grantWriteAccess( Block b, String playerName ) {
		try {
			BlockData bd = BlockData.attain( b );
			Set<String> access = getWriteAccess( bd );
			if ( !access.contains( playerName ) ) {
				access.add( playerName );
				setWriteAccess( b, access );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void clearReadWriteAccess( Block b ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.removeKey( READ_ACCESS );
			bd.removeKey( WRITE_ACCESS );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
