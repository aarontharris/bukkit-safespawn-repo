package com.ath.bukkit.safespawn.cmd;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.BlockData;
import com.ath.bukkit.safespawn.data.Blocks;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicCast;
import com.ath.bukkit.safespawn.magic.sign.MagicSign;
import com.ath.bukkit.safespawn.magic.sign.SignReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CastCmd implements CommandExecutor {

	private SafeSpawn plugin;

	public CastCmd( SafeSpawn plugin ) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		try {
			Player player = plugin.getPlayerManager().getActivePlayerByName( sender.getName() );
			if ( player != null ) {
				Log.line( "CastCmd: %s, %s, %s", player.getName(), cmd.getName(), F.joinSpace( args ) );
				if ( player.hasPermission( Const.PERM_magic_cast ) ) {
					Log.line( "CastCmd: %s, %s, %s, has perm", player.getName(), cmd.getName(), F.joinSpace( args ) );
					if ( args.length > 0 ) {
						MagicCast castWord = MagicCast.Invalid;
						try {
							castWord = MagicCast.fromWord( args[0] );
						} catch ( Exception e ) {
							Log.error( e );
						}

						if ( player.getLevel() >= castWord.getMinLevel() ) {
							boolean success = false;
							switch ( castWord ) {
							case Charge:
								success = doCharge( player, cmd, label, args );
								break;
							case Discharge:
								success = doDischarge( player, cmd, label, args );
								break;
							case Lock:
								success = doLock( player, cmd, label, args );
								break;
							case Unlock:
								success = doUnlock( player, cmd, label, args );
								break;
							case Grant:
								success = doGrant( player, cmd, label, args );
								break;
							case Revoke:
								success = doRevoke( player, cmd, label, args );
								break;
							case Access:
								success = doAccess( player, cmd, label, args );
								break;
							case Protection:
								success = doProtection( player, cmd, label, args );
								break;
							case Levitate:
								success = doLevitate( player, cmd, label, args );
								break;
							case Time:
								success = doTime( player, cmd, label, args );
								break;
							case Debug:
								if ( player.hasPermission( Const.PERM_admin ) ) {
									success = doDebug( player, cmd, label, args );
								}
								break;
							default:
								break;
							}

							if ( success ) {
								if ( castWord.getLvlCost() > 0 ) {
									player.setLevel( player.getLevel() - castWord.getLvlCost() );
								}
								return true;
							} else {
								return false;
							}
						} else {
							player.sendMessage( "You're not high enough level to cast that." );
							return false;
						}
					}

					player.sendMessage( "Nothing happens... Perhaps you mispronounced the spell?" );
					return false;
				}
			} else {
				sender.sendMessage( "Something evil has happened... Your cast failed..." );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	private boolean doTime( Player player, Command cmd, String label, String[] args ) {
		player.sendMessage( "Time: " + player.getLocation().getWorld().getTime() );
		return true;
	}

	private boolean doDebug( Player player, Command cmd, String label, String[] args ) {
		try {
			if ( args.length < 2 ) {
				return false;
			}

			String subcmd = args[1];

			if ( "chunkblocks".equals( subcmd ) ) {
				for ( String chunkHash : SafeSpawn.instance().getBlockStore().getCachedChunks() ) {
					Log.line( "CHUNK: %s", chunkHash );
					for ( BlockData bd : SafeSpawn.instance().getBlockStore().getBlockDatasByChunk( chunkHash ) ) {
						Log.line( " - In Chunk BlockData: %s", bd );
					}
				}
			}
			else if ( "allblocks".equals( subcmd ) ) {
				Log.line( "ALL BLOCKS" );
				for ( BlockData bd : SafeSpawn.instance().getBlockStore().getCachedBlockDatas() ) {
					Log.line( " -      All BlockData: %s", bd );
				}
			}
			else if ( "testaction".equals( subcmd ) ) {
				Log.line( "TEST ACTION" );
				doTest( player.getWorld(), player );
			}
			else if ( "create_defuct".equals( subcmd ) ) {
				Log.line( "CREATE DEFUNCT" );
				doCreateDefunct( player );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return true;
	}

	private static void doCreateDefunct( Player p ) {
		Block b = p.getTargetBlock( null, 10 );
		BlockData bd = BlockData.get( b );
		if ( bd == null ) {
			bd = BlockData.newBlockData( b );
			Material mutateMaterial = ( Material.BEDROCK.getId() == bd.getBlockM() ? Material.DIAMOND_BLOCK : Material.BEDROCK );
			bd.setBlockM( mutateMaterial.getId() ); // change it so that it doesnt match whats in the world thus making it defunct
			bd.setHash( BlockData.toHash( b.getLocation(), mutateMaterial ) );
			SafeSpawn.instance().getBlockStore().dbSave( bd );
			SafeSpawn.instance().getBlockStore().cacheBlockData( bd );
			p.sendMessage( "Saved to DB: " + bd );
		} else {
			p.sendMessage( "That block already exists: " + bd );
		}
	}

	private static void doTest( World w, Player p ) {
		try {
			// p.setLevel( 10 );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	private void enhance( Block b, Player p ) {
		try {
			Blocks.setMagical( b, true );

			Sign state = F.blockToBlockSign( b );
			if ( state != null ) {
				MagicSign sign = SignReader.readSign( (Sign) state );
				sign.enhance( state, p );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	// /cast charge
	protected boolean doCharge( Player player, Command cmd, String label, String[] args ) {
		try {
			Log.line( "%s.doCharge", player.getName() );

			Block block = F.getTargetWallSign( player );

			// distance or no sign fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}

			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				if ( F.isOwnedWallSign( block.getLocation(), block.getType() ) == null ) { // if not owned
					enhance( block, player );
					Blocks.setOwner( block, player );
					player.sendMessage( MagicCast.Charge.getMagicWord() + " -- charge successful" );
					return true;
				}
			} else {
				player.sendMessage( "You cannot charge a sign that is placed on a block with gravity" );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	protected boolean doDischarge( Player player, Command cmd, String label, String[] args ) {
		try {
			Log.line( "%s.doDischarge", player.getName() );
			Block block = F.getTargetWallSign( player );

			// distance or no sign fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}

			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				if ( F.isOwnedWallSign( block.getLocation(), block.getType() ) != null ) { // if not owned
					BlockData bd = BlockData.get( block );
					if ( bd != null && Blocks.isOwner( bd, player ) ) {
						SafeSpawn.instance().getBlockStore().remove( bd );
						player.sendMessage( MagicCast.Discharge.getMagicWord() + " -- discharge successful" );
						return true;
					}
				}
			} else {
				player.sendMessage( "You cannot charge a sign that is placed on a block with gravity" );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// /cast lock
	protected boolean doLock( Player player, Command cmd, String label, String[] args ) {
		try {
			Log.line( "%s.doLock", player.getName() );
			Block block = F.getTargetWallSign( player );

			// distance or no sign fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}

			// initial lock, self access only
			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				BlockData bd = BlockData.attain( block );
				if ( Blocks.canModify( bd, player ) ) {
					Blocks.setOwner( block, player );
					Blocks.grantAccess( block, player );
					enhance( block, player );
					player.sendMessage( MagicCast.Lock.getMagicWord() + " -- lock successful" );
					return true;
				} else {
					player.sendMessage( "You don't have permission." );
				}
			} else {
				player.sendMessage( "You cannot lock a sign that is placed on a block with gravity" );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// /cast unlock
	protected boolean doUnlock( Player player, Command cmd, String label, String[] args ) {
		try {
			Log.line( "%s.doLock", player.getName() );
			Block block = F.getTargetWallSign( player );

			// distance or no sign fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}

			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				BlockData bd = BlockData.attain( block );
				if ( Blocks.canModify( bd, player ) ) {
					Blocks.clearAccess( block );
					player.sendMessage( MagicCast.Unlock.getMagicWord() + " -- unlock successful" );
					return true;
				} else {
					player.sendMessage( "You don't have permission." );
				}
			} else {
				player.sendMessage( "You cannot unlock a sign that is placed on a block with gravity" );
			}

		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// /cast grant playername1,playername2,playername3
	protected boolean doGrant( Player player, Command cmd, String label, String[] args ) {
		try {
			Log.line( "%s.doGrant", player.getName() );
			Block block = F.getTargetWallSign( player );

			if ( !Material.WALL_SIGN.equals( block.getType() ) ) {
				player.sendMessage( "What is it you're trying to access?" );
				return false;
			}

			if ( !F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				player.sendMessage( "You cannot grant access to a sign that is placed on a block with gravity" );
			}

			BlockData bd = BlockData.get( block );

			// verify usage
			if ( args.length < 2 ) {
				player.sendMessage( "Nothing happens... Try /cast grant playername" );
				return false;
			}

			// translate playernames
			Set<String> newAccess = null;
			if ( args[1].contains( "," ) ) {
				newAccess = Sets.newHashSet( args[1].split( "," ) );
			} else {
				newAccess = Sets.newHashSet( args[1] );
			}

			// check permissions
			if ( newAccess != null ) {
				if ( Blocks.isMagical( bd ) ) {
					if ( Blocks.isOwner( bd, player ) ) {
						Set<String> access = Blocks.getAccess( bd );
						access.addAll( newAccess );
						Blocks.setAccess( block, access );
						player.sendMessage( "Granted" );
						return true;
					} else {
						player.sendMessage( "You don't have permission." );
					}
				} else {
					player.sendMessage( "That is not a magical block" );
				}
			}

		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// /cast revoke playername1,playername2,playername3
	protected boolean doRevoke( Player player, Command cmd, String label, String[] args ) {
		try {
			Block block = F.getTargetWallSign( player );

			if ( !Material.WALL_SIGN.equals( block.getType() ) ) {
				player.sendMessage( "What is it you're trying to access?" );
				return false;
			}

			if ( !F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				player.sendMessage( "You cannot grant access to a sign that is placed on a block with gravity" );
			}

			BlockData bd = BlockData.get( block );

			// verify usage
			if ( args.length < 2 ) {
				player.sendMessage( "Nothing happens... Try /cast grant playername" );
				return false;
			}

			// translate playernames
			List<String> access = null;
			if ( args[1].contains( "," ) ) {
				access = Lists.newArrayList( args[1].split( "," ) );
			} else {
				access = Lists.newArrayList( args[1] );
			}

			// check permissions
			if ( access != null ) {
				if ( Blocks.isMagical( bd ) ) {
					if ( Blocks.isOwner( bd, player ) ) {
						Set<String> writes = Blocks.getAccess( bd );
						writes.removeAll( access );
						Blocks.setAccess( block, writes );
						return true;
					} else {
						player.sendMessage( "You don't have permission." );
					}
				} else {
					player.sendMessage( "That is not a magical block" );
				}
			}

		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// /cast access
	private boolean doAccess( Player player, Command cmd, String label, String[] args ) {
		try {
			Block block = F.getTargetWallSign( player );

			if ( block == null || !Material.WALL_SIGN.equals( block.getType() ) ) {
				player.sendMessage( "What is it you're trying to access?" );
				return false;
			}

			if ( !F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				player.sendMessage( "You cannot grant access to a sign that is placed on a block with gravity" );
			}

			BlockData bd = BlockData.get( block );
			if ( bd == null ) {
				player.sendMessage( "You've tried to access a non-magical " + block.getType() );
				return false;
			}

			if ( Blocks.isMagical( bd ) ) {
				if ( Blocks.isOwner( bd, player ) ) {
					Set<String> access = Blocks.getAccess( bd );
					player.sendMessage( "Owner: " + ( bd.getOwner() == null ? "none" : bd.getOwner() ) );
					if ( access.isEmpty() ) {
						player.sendMessage( "Access: open" );
					} else {
						player.sendMessage( "Access: " + F.joinSpace( access ) );
					}
					return true;
				} else {
					player.sendMessage( "You don't have permission." );
				}
			} else {
				player.sendMessage( "You've tried to access a non-magical " + block.getType() );
			}

		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	protected boolean doLevitate( Player player, Command cmd, String label, String[] args ) {
		try {
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	protected boolean doProtection( Player player, Command cmd, String label, String[] args ) {
		try {
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}
}
