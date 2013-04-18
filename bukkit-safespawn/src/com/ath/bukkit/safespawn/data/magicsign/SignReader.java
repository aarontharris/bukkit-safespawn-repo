package com.ath.bukkit.safespawn.data.magicsign;

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
				String parts[] = line.split( ":" );
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
