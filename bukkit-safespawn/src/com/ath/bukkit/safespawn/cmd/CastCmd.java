package com.ath.bukkit.safespawn.cmd;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
		} catch ( Exception e ) {
			Log.error( e );
		}
		return true;
	}

	private static void doTest( World w, Player p ) {
		try {
			// p.setLevel( 10 );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	// /cast charge
	protected boolean doCharge( Player player, Command cmd, String label, String[] args ) {
		try {
			Block block = F.getTargetWallSign( player );

			// distance or no chest fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}

			Log.line( "%s.doCharge", player.getName() );
			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				if ( F.isOwnedWallSign( block.getLocation(), block.getType() ) == null ) { // if not owned
					Log.line( "%s.doCharge - magic allowed", player.getName() );
					Blocks.setMagical( block, true );
					Blocks.setOwner( block, player );
					Log.line( "%s successfully cast %s", player.getName(), F.joinSpace( args ) );
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

	// /cast lock
	protected boolean doLock( Player player, Command cmd, String label, String[] args ) {
		try {
			Block block = F.getTargetWallSign( player );

			// distance or no chest fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}


			// initial lock, self access only
			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				BlockData bd = BlockData.attain( block );
				if ( Blocks.canWrite( bd, player ) ) {
					Blocks.grantWriteAccess( block, player );
					Blocks.setMagical( block, true );
					Blocks.setOwner( block, player );
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
			Block block = F.getTargetWallSign( player );
			// distance or no chest fail
			if ( block == null ) {
				player.sendMessage( "You're too far from a Wall Sign" );
				return false;
			}

			if ( F.isMagicAllowed( block.getLocation(), block.getType() ) ) {
				BlockData bd = BlockData.attain( block );
				if ( Blocks.canWrite( bd, player ) ) {
					Blocks.clearReadWriteAccess( block );
					Blocks.setOwner( block, null );
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
			List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );
			Block block = blocks.get( 1 );

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
						Set<String> access = Blocks.getWriteAccess( bd );
						access.addAll( newAccess );
						Blocks.setWriteAccess( block, access );
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
			List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );
			Block block = blocks.get( 1 );

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
						Set<String> writes = Blocks.getWriteAccess( bd );
						writes.removeAll( access );
						Blocks.setWriteAccess( block, writes );
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
			Log.line( player.getName() + ".doAccess" );

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
				Log.line( "is magical" );
				if ( Blocks.isOwner( bd, player ) ) {
					Set<String> access = Blocks.getWriteAccess( bd );
					Log.line( "has write" );
					player.sendMessage( "Owner: " + ( bd.getOwner() == null ? "none" : bd.getOwner() ) );
					if ( access.isEmpty() ) {
						player.sendMessage( "Write: none" );
					} else {
						player.sendMessage( "Write: " + F.joinSpace( access ) );
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
