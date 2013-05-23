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

	/** overwrite */
	public static void setAccess( Block b, Set<String> playerNames ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.setAccess( playerNames );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}


	/** never null */
	public static Set<String> getAccess( BlockData bd ) {
		try {
			if ( bd != null )
				return bd.getAccess();
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

	public static boolean canModify( BlockData bd, Player player ) {
		try {
			if ( hasOwner( bd ) ) {
				if ( isOwner( bd, player ) ) {
					return true;
				}
			}
			return true;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/** does this user have the ability to use this block - includes owner, not the same as canModify */
	public static boolean canAccess( BlockData bd, Player player ) {
		try {
			if ( hasOwner( bd ) ) {
				if ( isOwner( bd, player ) ) {
					return true;
				}
				Set<String> access = getAccess( bd );
				if ( access.isEmpty() || access.contains( player.getName() ) ) {
					return true;
				}
				return false;
			}
			return true;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static void grantAccess( Block b, Player player ) {
		grantAccess( b, player.getName() );
	}

	public static void grantAccess( Block b, String playerName ) {
		try {
			BlockData bd = BlockData.attain( b );
			Set<String> access = getAccess( bd );
			if ( !access.contains( playerName ) ) {
				access.add( playerName );
				setAccess( b, access );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void clearAccess( Block b ) {
		try {
			BlockData bd = BlockData.attain( b );
			bd.setAccess( null );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

}
