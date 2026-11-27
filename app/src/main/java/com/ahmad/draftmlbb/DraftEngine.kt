package com.ahmad.draftmlbb

import kotlin.math.exp

object DraftEngine {

    data class Suggestion(val hero: Hero, val score: Double, val reasons: List<String>)

    // Hero h kuat lawan siapa dalam opp
    private fun good(h: Hero, opp: List<Hero>) =
        opp.filter { it.id in h.strongAgainst || h.id in it.weakAgainst }

    // Hero h lemah lawan siapa dalam opp
    private fun bad(h: Hero, opp: List<Hero>) =
        opp.filter { it.id in h.weakAgainst || h.id in it.strongAgainst }

    private fun hasMagic(team: List<Hero>) = team.any { it.damage == "magic" || it.damage == "mixed" }
    private fun hasFrontline(team: List<Hero>) = team.any { it.tank >= 3 }

    /** Bonus kalau hero h melengkapkan apa yang team belum ada. */
    private fun fit(h: Hero, team: List<Hero>): Pair<Double, List<String>> {
        var bonus = 0.0
        val reasons = mutableListOf<String>()
        if (!hasFrontline(team) && h.tank >= 3) {
            bonus += 2.5; reasons += "Tambah frontline"
        }
        if (!hasMagic(team) && (h.damage == "magic" || h.damage == "mixed")) {
            bonus += 2.0; reasons += "Cover magic damage"
        }
        if (team.sumOf { it.cc } < 3 && h.cc >= 2) {
            bonus += 1.5; reasons += "Tambah CC"
        }
        if (team.none { "Marksman" in it.roles } && "Marksman" in h.roles) {
            bonus += 1.5; reasons += "Belum ada Marksman"
        }
        return bonus to reasons
    }

    fun suggestPicks(
        pool: List<Hero>,
        allies: List<Hero>,
        enemies: List<Hero>,
        limit: Int = 5,
    ): List<Suggestion> = pool.map { h ->
        val g = good(h, enemies)
        val b = bad(h, enemies)
        val (fitBonus, fitReasons) = fit(h, allies)
        val reasons = mutableListOf<String>()
        if (g.isNotEmpty()) reasons += "Counter " + g.joinToString { it.name }
        if (b.isNotEmpty()) reasons += "Lemah lawan " + b.joinToString { it.name }
        reasons += fitReasons
        if (reasons.isEmpty()) reasons += "Win rate asas ${"%.1f".format(h.wr)}%"
        Suggestion(h, (h.wr - 50) + 3.0 * g.size - 3.0 * b.size + fitBonus, reasons)
    }.sortedByDescending { it.score }.take(limit)

    /** Hero yang musuh patut/akan pick untuk lawan team kita = cadangan ban. */
    fun suggestBans(pool: List<Hero>, allies: List<Hero>, limit: Int = 5): List<Suggestion> =
        pool.map { h ->
            val g = good(h, allies)
            val reasons = mutableListOf<String>()
            if (g.isNotEmpty()) reasons += "Counter team kau: " + g.joinToString { it.name }
            if (h.ban >= 10.0) reasons += "Ban rate ${"%.1f".format(h.ban)}%"
            if (h.wr >= 52.0) reasons += "Win rate ${"%.1f".format(h.wr)}%"
            if (reasons.isEmpty()) reasons += "Win rate ${"%.1f".format(h.wr)}%"
            Suggestion(h, (h.wr - 50) + h.ban / 10.0 + 3.0 * g.size, reasons)
        }.sortedByDescending { it.score }.take(limit)

    /** Hero dalam pool yang counter satu hero musuh. */
    fun counterFor(enemy: Hero, pool: List<Hero>, limit: Int = 3): List<Hero> =
        pool.filter { good(it, listOf(enemy)).isNotEmpty() }
            .sortedByDescending { it.wr }
            .take(limit)

    private fun compBonus(team: List<Hero>): Double {
        var s = 0.0
        s += if (hasFrontline(team)) 2.0 else -2.0
        s += if (hasMagic(team)) 1.5 else -1.5
        s += if (team.sumOf { it.cc } >= 3) 1.5 else -1.5
        return s
    }

    private fun strength(team: List<Hero>, opp: List<Hero>): Double {
        if (team.isEmpty()) return 0.0
        var s = 0.0
        for (h in team) s += (h.wr - 50) + 2.0 * good(h, opp).size - 2.0 * bad(h, opp).size
        s = s / team.size * 5
        if (team.size >= 3) s += compBonus(team)
        return s
    }

    /** Peratus menang (ally, enemy). Anggaran ringkas, bukan ramalan sebenar. */
    fun winPrediction(allies: List<Hero>, enemies: List<Hero>): Pair<Double, Double> {
        if (allies.isEmpty() && enemies.isEmpty()) return 50.0 to 50.0
        val d = strength(allies, enemies) - strength(enemies, allies)
        val a = 100.0 / (1.0 + exp(-d / 8.0))
        return a to (100.0 - a)
    }

    /** Analisis potensi komposisi satu team. */
    fun analyze(team: List<Hero>): List<String> {
        if (team.isEmpty()) return listOf("Belum ada hero.")
        val out = mutableListOf<String>()
        val phys = team.count { it.damage == "physical" }
        val mag = team.count { it.damage == "magic" }
        val mix = team.count { it.damage == "mixed" }
        val unknown = team.count { it.damage == "unknown" }
        out += "Damage: physical $phys, magic $mag, mixed $mix" +
            if (unknown > 0) " ($unknown hero belum ada data)" else ""
        out += if (hasFrontline(team)) "Frontline: ada" else "Frontline: belum ada"
        out += "CC total: ${team.sumOf { it.cc }}"
        if (team.size >= 2) {
            val e = team.sumOf { it.early }
            val l = team.sumOf { it.late }
            out += when {
                e - l >= 2 -> "Power spike: awal (kena tekan sebelum lewat)"
                l - e >= 2 -> "Power spike: lewat (selamat dulu, scale kemudian)"
                else -> "Power spike: seimbang"
            }
        }
        if (team.size >= 3) {
            val missing = listOf("Tank", "Mage", "Marksman").filter { r -> team.none { r in it.roles } }
            if (missing.isNotEmpty()) out += "Role belum ada: " + missing.joinToString()
            if (mag == 0 && mix == 0) out += "Risiko: semua physical, senang bina armor"
            if (phys == 0) out += "Risiko: semua magic, senang bina magic resist"
        }
        return out
    }
}
