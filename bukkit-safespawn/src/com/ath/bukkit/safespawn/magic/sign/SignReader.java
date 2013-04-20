package com.ath.bukkit.safespawn.magic.sign;

import org.bukkit.block.Sign;

import com.ath.bukkit.safespawn.magic.MagicWords;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicCommand;

public class SignReader {

	public static MagicSign readSign( Sign sign ) {
		MagicSign out = null;
		MagicCommand type = parseSignType( sign );
		switch ( type ) {
		case Manifest:
			out = new ShrineSign();
			break;
		case Teleport:
			out = new TeleporterSign();
			break;
		default:
			out = new OrdinarySign();
			break;
		}
		return out;
	}

	private static MagicCommand parseSignType( Sign sign ) {
		try {
			return MagicWords.readCommandFromLine( sign.getLine( 0 ) );
		} catch ( Exception e ) {
			// mute
		}
		return MagicCommand.Ordinary;
	}

}
