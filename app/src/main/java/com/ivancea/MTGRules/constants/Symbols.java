package com.ivancea.MTGRules.constants;

import androidx.annotation.Nullable;

import com.ivancea.MTGRules.R;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Symbols {
	@Nullable
	public static Integer getDrawableId(String symbol) {
		switch (symbol) {
			// Basic mana
			case "W": return R.drawable.symbol_w;
			case "U": return R.drawable.symbol_u;
			case "B": return R.drawable.symbol_b;
			case "R": return R.drawable.symbol_r;
			case "G": return R.drawable.symbol_g;

			// Colorless mana
			case "C": return R.drawable.symbol_c;

			// Generic mana
			case "X": return R.drawable.symbol_x;
			case "0": return R.drawable.symbol_0;
			case "1": return R.drawable.symbol_1;
			case "2": return R.drawable.symbol_2;
			case "3": return R.drawable.symbol_3;
			case "4": return R.drawable.symbol_4;
			case "5": return R.drawable.symbol_5;

			// Hybrid mana
			case "W/U": return R.drawable.symbol_wu;
			case "W/B": return R.drawable.symbol_wb;
			case "U/B": return R.drawable.symbol_ub;
			case "U/R": return R.drawable.symbol_ur;
			case "B/R": return R.drawable.symbol_br;
			case "B/G": return R.drawable.symbol_bg;
			case "R/G": return R.drawable.symbol_rg;
			case "R/W": return R.drawable.symbol_rw;
			case "G/W": return R.drawable.symbol_gw;
			case "G/U": return R.drawable.symbol_gu;

			// Monocolored hybrid mana
			case "2/W": return R.drawable.symbol_2w;
			case "2/U": return R.drawable.symbol_2u;
			case "2/B": return R.drawable.symbol_2b;
			case "2/R": return R.drawable.symbol_2r;
			case "2/G": return R.drawable.symbol_2g;

			// Phyrexian mana
			case "P": return R.drawable.symbol_p;
			case "W/P": return R.drawable.symbol_wp;
			case "U/P": return R.drawable.symbol_up;
			case "B/P": return R.drawable.symbol_bp;
			case "R/P": return R.drawable.symbol_rp;
			case "G/P": return R.drawable.symbol_gp;

			// Phyrexian hybrid mana
			case "G/W/P": return R.drawable.symbol_gwp;
			case "G/U/P": return R.drawable.symbol_gup;
			case "R/G/P": return R.drawable.symbol_rgp;
			case "R/W/P": return R.drawable.symbol_rwp;
			case "B/R/P": return R.drawable.symbol_brp;
			case "B/G/P": return R.drawable.symbol_bgp;
			case "U/B/P": return R.drawable.symbol_ubp;
			case "U/R/P": return R.drawable.symbol_urp;
			case "W/B/P": return R.drawable.symbol_wbp;
			case "W/U/P": return R.drawable.symbol_wup;

			// Snow mana
			case "S": return R.drawable.symbol_s;

			// Energy
			case "E": return R.drawable.symbol_e;

			// Tap
			case "T": return R.drawable.symbol_t;

			// Untap
			case "Q": return R.drawable.symbol_q;

			// Planeswalker
			case "PW": return R.drawable.symbol_pw;

			// Chaos
			case "CHAOS": return R.drawable.symbol_chaos;

			// Ticket
			case "TK": return R.drawable.symbol_tk;
		}
		
		return null;
	}
}
