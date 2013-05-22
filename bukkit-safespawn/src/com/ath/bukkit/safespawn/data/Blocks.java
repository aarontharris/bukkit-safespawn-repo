package com.ath.bukkit.safespawn.data;

import java.util.Collections;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Const;
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

	public static void grantReadWriteAccess( Block b, Set<String> playerNames ) {
		try {
			setReadAccess( b, playerNames );
			setWriteAccess( b, playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** overwrite */
	public static void setReadAccess( Block b, Set<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			// bd.putStringCollection( READ_ACCESS, playerNames );
			bd.setReadAccess( playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** overwrite */
	public static void setWriteAccess( Block b, Set<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			// bd.putStringCollection( WRITE_ACCESS, playerNames );
			bd.setWriteAccess( playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}


	/** never null */
	public static Set<String> getReadAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getReadAccess();
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	/** never null */
	public static Set<String> getWriteAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getWriteAccess();
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	public static void setOwner( Block b, Player p ) {
		try {
			BlockData bd = BlockData.attain( b );
			if ( p == null ) {
				bd.setOwner( null );
			} else {
				bd.setOwner( p.getName() );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static boolean isOwner( BlockData bd, Player p ) {
		try {
			if ( p.getName().equals( bd.getOwner() ) ) {
				return true;
			}
			if ( p.hasPermission( Const.PERM_skeleton_key ) ) {
				p.sendMessage( "Used Skeleton Key" );
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static boolean hasOwner( BlockData bd ) {
		try {
			if ( bd.getOwner() != null ) {
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/** do not use in a loop */
	public static boolean canRead( BlockData bd, Player player ) {
		try {
			if ( canWrite( bd, player ) ) {
				return true;
			}
			if ( getReadAccess( bd ).contains( player.getName() ) ) {
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/** do not use in a loop */
	public static boolean canWrite( BlockData bd, Player player ) {
		try {
			if ( isOwner( bd, player ) ) {
				return true;
			}
			if ( getWriteAccess( bd ).contains( player.getName() ) ) {
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
			bd.setReadAccess( null );
			bd.setWriteAccess( null );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
