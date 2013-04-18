package com.ath.bukkit.safespawn.data.magicsign;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.ath.bukkit.safespawn.SafeSpawnPlugin;

public class SignReader {

	public static enum MagicSignType {
		Ordinary( "un" ), //
		Teleporter( "teles" ), //
		;

		private String id;

		private MagicSignType( String id ) {
			this.id = id;
		}

		public static MagicSignType getById( String id ) {
			for ( MagicSignType type : MagicSignType.values() ) {
				if ( type.id.equals( id ) ) {
					return type;
				}
			}
			return MagicSignType.Ordinary;
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
			ActivatorType type = wordToType.get( word );
			if ( type != null ) {
				return type;
			}
			return ActivatorType.INVALID;
		}
	}

	public static MagicSign readSign( Sign sign ) {
		MagicSign out = null;
		MagicSignType type = parseSignType( sign );
		switch ( type ) {
		case Teleporter:
			out = new TeleporterSign();
			out.setType( MagicSignType.Teleporter );
			break;
		default:
			out = new OrdinarySign();
			out.setType( MagicSignType.Ordinary );
			break;
		}
		return out;
	}

	private static MagicSignType parseSignType( Sign sign ) {
		try {
			String line = sign.getLine( 0 );
			if ( line != null && !line.isEmpty() ) {
				String parts[] = line.split( " " );
				if ( parts != null && parts.length > 0 ) {
					String id = parts[0];
					if ( id != null ) {
						return MagicSignType.getById( id );
					}
				}
			}
		} catch ( Exception e ) {
			SafeSpawnPlugin.logError( e ); // TODO mute me
		}
		return MagicSignType.Ordinary;
	}

}
