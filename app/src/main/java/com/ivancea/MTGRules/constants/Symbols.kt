package com.ivancea.MTGRules.constants

import com.ivancea.MTGRules.R

object Symbols {
    val drawablesBySymbol = mapOf(
        "W" to R.drawable.symbol_w,
        "U" to R.drawable.symbol_u,
        "B" to R.drawable.symbol_b,
        "R" to R.drawable.symbol_r,
        "G" to R.drawable.symbol_g,
        "C" to R.drawable.symbol_c,
        "X" to R.drawable.symbol_x,
        "0" to R.drawable.symbol_0,
        "1" to R.drawable.symbol_1,
        "2" to R.drawable.symbol_2,
        "3" to R.drawable.symbol_3,
        "4" to R.drawable.symbol_4,
        "5" to R.drawable.symbol_5,
        "W/U" to R.drawable.symbol_wu,
        "W/B" to R.drawable.symbol_wb,
        "U/B" to R.drawable.symbol_ub,
        "U/R" to R.drawable.symbol_ur,
        "B/R" to R.drawable.symbol_br,
        "B/G" to R.drawable.symbol_bg,
        "R/G" to R.drawable.symbol_rg,
        "R/W" to R.drawable.symbol_rw,
        "G/W" to R.drawable.symbol_gw,
        "G/U" to R.drawable.symbol_gu,
        "2/W" to R.drawable.symbol_2w,
        "2/U" to R.drawable.symbol_2u,
        "2/B" to R.drawable.symbol_2b,
        "2/R" to R.drawable.symbol_2r,
        "2/G" to R.drawable.symbol_2g,
        "P" to R.drawable.symbol_p,
        "W/P" to R.drawable.symbol_wp,
        "U/P" to R.drawable.symbol_up,
        "B/P" to R.drawable.symbol_bp,
        "R/P" to R.drawable.symbol_rp,
        "G/P" to R.drawable.symbol_gp,
        "G/W/P" to R.drawable.symbol_gwp,
        "G/U/P" to R.drawable.symbol_gup,
        "R/G/P" to R.drawable.symbol_rgp,
        "R/W/P" to R.drawable.symbol_rwp,
        "B/R/P" to R.drawable.symbol_brp,
        "B/G/P" to R.drawable.symbol_bgp,
        "U/B/P" to R.drawable.symbol_ubp,
        "U/R/P" to R.drawable.symbol_urp,
        "W/B/P" to R.drawable.symbol_wbp,
        "W/U/P" to R.drawable.symbol_wup,
        "S" to R.drawable.symbol_s,
        "E" to R.drawable.symbol_e,
        "T" to R.drawable.symbol_t,
        "Q" to R.drawable.symbol_q,
        "PW" to R.drawable.symbol_pw,
        "CHAOS" to R.drawable.symbol_chaos,
        "TK" to R.drawable.symbol_tk,
    )

    @JvmStatic
    fun getDrawableId(symbol: String): Int? {
        return drawablesBySymbol[symbol]
    }

}