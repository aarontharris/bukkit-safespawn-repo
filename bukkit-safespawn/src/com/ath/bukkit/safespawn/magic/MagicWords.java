package com.ath.bukkit.safespawn.magic;

import java.util.HashMap;

import org.bukkit.Material;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.Log;

public class MagicWords {

	//
	// DEFINITIONS
	//

	// COMMAND WORDS

	public static enum MagicCommand {
		Ordinary( "un" ), //
		Teleport( "teles" ), //
		Manifest( "spitus" ), //
		Lock( "colloportus" ), //
		Unlock( "alohomora" ), //
		Levitate( "wingardium leviosa" ), //
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
		WOOD_OAK( "ud", Material.WOOD ),


		// EGGS
		EGG_ZOMBIE( "uzooba", null ), // SPAWN_EGGs have no material

		EGG_RANDOM( "uras", null ),

		RANDOM_OBJECT( "chans", null ),

		// Phrases
		SYMPATHY( "sympios", null ),

		; //


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
			if ( this.equals( INVALID ) ) {
				return false;
			}
			if ( this.equals( AGNOSTIC ) ) {
				return true;
			}
			if ( this.equals( NOTHING ) ) {
				return magicWord == null;
			}
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
				Log.error( e );
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
				Log.error( e );
			}
			return MagicWord.INVALID;
		}


		public String getWord() {
			return word;
		}
	}

	//
	// HELPER FUNCTIONS
	//

	public static MagicWord readMagicWordFromLine( String line ) {
		return readMagicWordFromLine( line, Const.MW_separator, 0 );
	}

	/**
	 * @param line
	 * @param delimiter - what separates the words in the line
	 * @param wordIndex - first word in the line would be 0, second would be 1 ...
	 * @return
	 */
	public static MagicWord readMagicWordFromLine( String line, String delimiter, int wordIndex ) {
		try {
			if ( line != null && !line.isEmpty() ) {
				String parts[] = line.split( delimiter );
				if ( parts.length >= ( wordIndex + 1 ) ) {
					return MagicWord.findItemByWord( parts[wordIndex] );
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return MagicWord.INVALID;
	}

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
