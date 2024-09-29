package com.rmitsubayashi.pennantmanager.ui.util

import com.rmitsubayashi.pennantmanager.R

object TeamStyleMapper {

    // only part match so we can match team names like "楽天（キャッチャー威圧感禁止）"
    fun map(teamName: String) : Int {
        for ((keywords, style) in styleMap) {
            for (keyword in keywords) {
                if (teamName.contains(keyword)) {
                    return style
                }
            }
        }

        return R.style.Theme_PawapuroPennantManager
    }

    private val swallowsKeywords = listOf("ヤクルト","スワローズ")
    private val baystarsKeywords = listOf("横浜", "DeNA", "ベイスターズ")
    private val giantsKeywords = listOf("巨人", "読売", "ジャイアンツ")
    private val carpKeywords = listOf("広島", "カープ")
    private val dragonsKeywords = listOf("中日", "ドラゴンズ")
    private val tigersKeywords = listOf("阪神", "タイガーズ")
    private val eaglesKeywords = listOf("楽天", "イーグルズ")
    private val buffalosKeywords = listOf("オリックス", "バッファローズ")
    private val lionsKeywords = listOf("西武", "ライオンズ")
    private val fightersKeywords = listOf("日本ハム", "ファイターズ")
    private val marinesKeywords = listOf("ロッテ", "マリーンズ")
    private val hawksKeywords = listOf("ソフトバンク", "ホークス")

    private val styleMap = mapOf(
        dragonsKeywords to R.style.ChunichiDragons,
        giantsKeywords to R.style.YomiuriGiants,
        tigersKeywords to R.style.HanshinTigers,
        carpKeywords to R.style.ToyoCarp,
        baystarsKeywords to R.style.DenaBaystars,
        swallowsKeywords to R.style.YakultSwallows,
        hawksKeywords to R.style.SoftbankHawks,
        buffalosKeywords to R.style.OrixBuffalos,
        fightersKeywords to R.style.NipponHamFighters,
        eaglesKeywords to R.style.RakutenEagles,
        lionsKeywords to R.style.SeibuLions,
        marinesKeywords to R.style.LotteMarines
    )
}