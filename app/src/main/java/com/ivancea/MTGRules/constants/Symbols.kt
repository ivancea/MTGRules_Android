package com.ivancea.MTGRules.constants

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.core.content.res.ResourcesCompat
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.RulesSource
import java.time.LocalDate

object Symbols {
    @JvmStatic
    val drawablesBySymbol = mapOf(
        "H" to R.drawable.symbol_phyrexian,
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
        "6" to R.drawable.symbol_6,
        "8" to R.drawable.symbol_8,
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
        "C/W" to R.drawable.symbol_cw,
        "C/U" to R.drawable.symbol_cu,
        "C/B" to R.drawable.symbol_cb,
        "C/R" to R.drawable.symbol_cr,
        "C/G" to R.drawable.symbol_cg,
        "2/W" to R.drawable.symbol_2w,
        "2/U" to R.drawable.symbol_2u,
        "2/B" to R.drawable.symbol_2b,
        "2/R" to R.drawable.symbol_2r,
        "2/G" to R.drawable.symbol_2g,
        "P" to R.drawable.symbol_pawprint,
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

    val drawablesBySymbolBefore_2024_08_02 = drawablesBySymbol.mapKeys {
        when (it.key) {
            // Pawprint symbol added
            "P" -> "-"
            // Phyrexian colorless symbol changed
            "H" -> "P"
            else -> it.key
        }
    }

    // TODO: Require a rules version to get the symbols map

    /**
     * A map of inline content to be passed to the [Text] composable.
     * It will allow for the use of symbols in the rules.
     *
     * The [RulesSource] parameter is used as some symbols may change between rules versions.
     */
    @JvmStatic
    fun makeSymbolsMap(rulesSource: RulesSource?, context: Context, lineHeight: TextUnit): Map<String, InlineTextContent> {
        var currentDrawablesBySymbol = if (rulesSource != null && rulesSource.date < LocalDate.of(2024, 8, 2)) {
            drawablesBySymbolBefore_2024_08_02
        } else {
            drawablesBySymbol
        }

        return currentDrawablesBySymbol.mapValues { (_, resource) ->
            val drawable = ResourcesCompat.getDrawable(context.resources, resource, context.theme)
            val height = if (lineHeight.isUnspecified) {
                TextUnit(16f, TextUnitType.Sp)
            } else {
                lineHeight
            }
            val width =
                height.times(drawable!!.intrinsicWidth.toDouble() / drawable.intrinsicHeight.toDouble())

            InlineTextContent(
                Placeholder(width, height, PlaceholderVerticalAlign.TextCenter)
            ) {
                Image(
                    painterResource(resource),
                    contentDescription = it,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(20.dp))
                        .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(2.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}