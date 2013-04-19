package com.ath.bukkit.safespawn.magic;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import com.ath.bukkit.safespawn.Const;

public class MagicWords {

	//
	// DEFINITIONS
	//

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
	}



	// FIXME: move into a magic word to world item manager
	public static enum ActivatorType {
		INVALID( null, -1 ),
		EMPTY( "non", 0 ),
		BONE( "bon", Material.BONE.getId() );

		private static final Map<String, ActivatorType> wordToType = new HashMap<String, ActivatorType>();
		@SuppressWarnings( "unused" )
		private int materialId;
		private String word;
		static {
			for ( ActivatorType t : ActivatorType.values() ) {
				wordToType.put( t.word, t );
			}
		}

		ActivatorType( String word, int materialId ) {
			this.word = word;
			this.materialId = materialId;
		}

		public static ActivatorType getByWord( String word ) {
			if ( word != null ) {
				ActivatorType type = wordToType.get( word );
				if ( type != null ) {
					return type;
				}
			}
			return ActivatorType.INVALID;
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
