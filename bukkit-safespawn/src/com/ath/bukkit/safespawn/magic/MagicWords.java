package com.ath.bukkit.safespawn.magic;

import java.util.HashMap;

import org.bukkit.Material;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.SafeSpawn;

public class MagicWords {

	//
	// DEFINITIONS
	//

	// COMMAND WORDS

	public static enum MagicCommand {
		Ordinary( "un" ), //
		Teleport( "teles" ), //
		Manifest( "spitus" ), //
		;

		private String word;

		private MagicCommand( String word ) {
			this.word = word;
		}

		public static MagicCommand getByWord( String word ) {
			for ( MagicCommand type : MagicCommand.values() ) {
				if ( type.word.equals( word ) ) {
					return type;
				}
			}
			return MagicCommand.Ordinary;
		}

		public String getWord() {
			return word;
		}
	}



	// ITEM WORDS
	public static enum MagicWord {
		INVALID( null, null ), // bad
		NOTHING( "vod", null ), // must be empty
		AGNOSTIC( "non", null ), // agnostic, can be anything
		BONE( "bon", Material.BONE ),
		GOLD_SPADE( "g'spad", Material.GOLD_SPADE ),
		IRON_SPADE( "i'spad", Material.IRON_SPADE ),
		STONE_SPADE( "p'spad", Material.STONE_SPADE ),
		WOOD_SPADE( "u'spad", Material.WOOD_SPADE ),

		GOLD_BLOCK( "gal'b", Material.GOLD_BLOCK ),

		DIAMOND( "wb'f", Material.DIAMOND ),
		STONE( "poc", Material.STONE ),
		WOOD_OAK( "ud", Material.WOOD ), ;


		private String word;
		private Material mat;

		MagicWord( String word, Material mat ) {
			this.word = word;
			this.mat = mat;
		}

		/**
		 * True when, not equal but match requirements are met.<br>
		 * EX: BONE.matches( BONE ) == true.
		 * EX: BONE.matches( STONE ) == false.
		 * EX: AGNOSTIC.matches( BONE ) == true.
		 * EX: AGNOSTIC.matches( INVALID ) == true.
		 * EX: AGNOSTIC.matches( null ) == true.
		 * EX: *.matches( AGNOSTIC ) == false.
		 * EX: NOTHING.matches( null ) == true.
		 * EX: NOTHING.matches( !null ) == false.
		 * EX: INVALID.matches( * ) == false.
		 */
		public boolean matches( MagicWord magicWord ) {
			SafeSpawn.logLine( this + ".matches( " + magicWord + " )" );
			if ( this.equals( INVALID ) ) {
				SafeSpawn.logLine( this + ".matches( " + magicWord + " ) false" );
				return false;
			}
			if ( this.equals( AGNOSTIC ) ) {
				SafeSpawn.logLine( this + ".matches( " + magicWord + " ) true" );
				return true;
			}
			if ( this.equals( NOTHING ) ) {
				SafeSpawn.logLine( this + ".matches( " + magicWord + " ) " + ( magicWord == null ) );
				return magicWord == null;
			}
			SafeSpawn.logLine( this + ".matches( " + magicWord + " ) " + ( this.equals( magicWord ) ) );
			return this.equals( magicWord );
		}

		private static HashMap<String, MagicWord> nameMap = new HashMap<String, MagicWord>();
		private static HashMap<Material, MagicWord> matMap = new HashMap<Material, MagicWord>();

		static {
			for ( MagicWord itemWord : MagicWord.values() ) {
				nameMap.put( itemWord.word, itemWord );
				matMap.put( itemWord.mat, itemWord );
			}
		}

		public static MagicWord findItemByWord( String word ) {
			try {
				MagicWord out = nameMap.get( word );
				if ( out != null ) {
					return out;
				}
			} catch ( Exception e ) {
				SafeSpawn.logError( e );
			}
			return MagicWord.INVALID;
		}

		public static MagicWord findItemByMaterial( Material material ) {
			try {
				MagicWord out = matMap.get( material );
				if ( out != null ) {
					return out;
				}
			} catch ( Exception e ) {
				SafeSpawn.logError( e );
			}
			return MagicWord.INVALID;
		}
	}

	//
	// HELPER FUNCTIONS
	//

	/** must be the first or only word in the line */
	public static MagicCommand readCommandFromLine( String line ) {
		if ( line != null ) {
			try {
				String parts[] = line.split( Const.MW_separator );
				if ( parts.length > 0 ) {
					return MagicCommand.getByWord( parts[0] );
				}
			} catch ( Exception e ) {
				// mute
			}
		}
		return MagicCommand.Ordinary;
	}

	public static int readCoordinateFromLine( String line ) {
		if ( line.startsWith( "-" ) ) {
			return -Integer.decode( "0x" + line.substring( 1 ) );
		} else {
			return Integer.decode( "0x" + line );
		}
	}
}
