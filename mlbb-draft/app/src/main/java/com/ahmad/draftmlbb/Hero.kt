package com.ahmad.draftmlbb

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * damage: "physical" | "magic" | "mixed"
 * cc / tank / early / late: skala 0-3
 * wr: win rate asas (%), strong/weak: id hero lain
 */
data class Hero(
    val id: String,
    val name: String,
    val roles: List<String>,
    val damage: String,
    val cc: Int,
    val tank: Int,
    val early: Int,
    val late: Int,
    val wr: Double,
    val pick: Double,
    val ban: Double,
    val strongAgainst: List<String>,
    val weakAgainst: List<String>,
    val build: List<String>,
    val note: String,
)

object HeroRepository {
    fun load(context: Context): List<Hero> {
        val text = context.assets.open("heroes.json").bufferedReader().use { it.readText() }
        val arr = JSONObject(text).getJSONArray("heroes")
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Hero(
                id = o.getString("id"),
                name = o.getString("name"),
                roles = o.getJSONArray("roles").toList(),
                damage = o.getString("damage"),
                cc = o.getInt("cc"),
                tank = o.getInt("tank"),
                early = o.getInt("early"),
                late = o.getInt("late"),
                wr = o.getDouble("wr"),
                pick = o.optDouble("pick", 0.0),
                ban = o.optDouble("ban", 0.0),
                strongAgainst = o.getJSONArray("strong").toList(),
                weakAgainst = o.getJSONArray("weak").toList(),
                build = o.getJSONArray("build").toList(),
                note = o.optString("note", ""),
            )
        }.sortedBy { it.name }
    }

    private fun JSONArray.toList(): List<String> = (0 until length()).map { getString(it) }
}
