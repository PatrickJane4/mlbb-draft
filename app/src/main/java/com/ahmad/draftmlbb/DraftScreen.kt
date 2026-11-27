package com.ahmad.draftmlbb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val BAN_SLOTS = 6
private val AllyColor = Color(0xFF4DA3FF)
private val EnemyColor = Color(0xFFFF5A6A)
private val BanColor = Color(0xFFB0B0B0)

@Composable
fun DraftScreen(heroes: List<Hero>) {
    val allies = remember { mutableStateListOf<String?>(null, null, null, null, null) }
    val enemies = remember { mutableStateListOf<String?>(null, null, null, null, null) }
    val bans = remember { mutableStateListOf<String?>().apply { repeat(BAN_SLOTS) { add(null) } } }
    var target by remember { mutableStateOf<Pair<String, Int>?>(null) }

    val byId = remember(heroes) { heroes.associateBy { it.id } }
    val allyHeroes = allies.mapNotNull { id -> id?.let { byId[it] } }
    val enemyHeroes = enemies.mapNotNull { id -> id?.let { byId[it] } }
    val used = (allies + enemies + bans).filterNotNull().toSet()
    val pool = heroes.filter { it.id !in used }

    val (allyWin, enemyWin) = DraftEngine.winPrediction(allyHeroes, enemyHeroes)

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Draft Pick", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // Win prediction
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Anggaran peluang menang", fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth()) {
                    Text("Kau ${"%.0f".format(allyWin)}%", color = AllyColor, fontWeight = FontWeight.Bold)
                    Box(Modifier.weight(1f))
                    Text("Musuh ${"%.0f".format(enemyWin)}%", color = EnemyColor, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(7.dp))) {
                    Box(Modifier.weight(allyWin.toFloat().coerceAtLeast(1f)).fillMaxHeight().background(AllyColor))
                    Box(Modifier.weight(enemyWin.toFloat().coerceAtLeast(1f)).fillMaxHeight().background(EnemyColor))
                }
            }
        }

        // Slots
        SlotSection("Team kau", allies, byId, AllyColor) { target = "ally" to it }
        SlotSection("Musuh", enemies, byId, EnemyColor) { target = "enemy" to it }
        SlotSection("Ban", bans, byId, BanColor) { target = "ban" to it }

        Button(onClick = {
            allies.indices.forEach { allies[it] = null }
            enemies.indices.forEach { enemies[it] = null }
            bans.indices.forEach { bans[it] = null }
        }) { Text("Reset draft") }

        // Suggested picks
        if (allyHeroes.size < 5) {
            Section("Cadangan pick untuk kau") {
                val picks = DraftEngine.suggestPicks(pool, allyHeroes, enemyHeroes)
                picks.forEach { SuggestionCard(it, showBuild = true) }
            }
        }

        // Counter per enemy
        if (enemyHeroes.isNotEmpty()) {
            Section("Counter untuk setiap musuh") {
                enemyHeroes.forEach { e ->
                    val c = DraftEngine.counterFor(e, pool)
                    Text(
                        "${e.name}: " + if (c.isEmpty()) "tiada counter jelas dalam pool" else c.joinToString { it.name },
                        fontSize = 14.sp,
                    )
                }
            }
        }

        // Ban suggestions
        Section("Cadangan ban") {
            DraftEngine.suggestBans(pool, allyHeroes).forEach { SuggestionCard(it, showBuild = false) }
        }

        // Analysis
        Section("Potensi komposisi: team kau") {
            DraftEngine.analyze(allyHeroes).forEach { Text("• $it", fontSize = 14.sp) }
        }
        Section("Potensi komposisi: musuh") {
            DraftEngine.analyze(enemyHeroes).forEach { Text("• $it", fontSize = 14.sp) }
        }

        Text(
            "Win/pick/ban rate dan counter dari mobilelegends.com/rank (counter dipadankan dari gambar, sedikit mungkin silap). Role, CC dan build kebanyakan hero belum diisi.",
            fontSize = 11.sp,
            color = Color.Gray,
        )
    }

    target?.let { (kind, index) ->
        val list = when (kind) {
            "ally" -> allies
            "enemy" -> enemies
            else -> bans
        }
        HeroPicker(
            pool = pool,
            hasCurrent = list[index] != null,
            onPick = { list[index] = it.id; target = null },
            onClear = { list[index] = null; target = null },
            onDismiss = { target = null },
        )
    }
}

@Composable
private fun SlotSection(
    title: String,
    list: List<String?>,
    byId: Map<String, Hero>,
    color: Color,
    onSlot: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, color = color)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            list.forEachIndexed { i, id ->
                Slot(id?.let { byId[it] }, color, Modifier.weight(1f)) { onSlot(i) }
            }
        }
    }
}

@Composable
private fun Slot(hero: Hero?, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.18f))
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            hero?.name ?: "+",
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun SuggestionCard(s: DraftEngine.Suggestion, showBuild: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(s.hero.name, fontWeight = FontWeight.Bold)
            Text("  ${s.hero.roles.joinToString("/")}", fontSize = 12.sp, color = Color.Gray)
            Box(Modifier.weight(1f))
            Text("skor ${"%+.1f".format(s.score)}", fontSize = 12.sp, color = Color.Gray)
        }
        Text(s.reasons.joinToString(" · "), fontSize = 13.sp)
        if (showBuild) Text("Build: " + s.hero.build.joinToString(" > "), fontSize = 12.sp, color = Color.Gray)
        if (showBuild && s.hero.note.isNotBlank()) Text(s.hero.note, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun HeroPicker(
    pool: List<Hero>,
    hasCurrent: Boolean,
    onPick: (Hero) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    var q by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih hero") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } },
        dismissButton = { TextButton(onClick = onClear, enabled = hasCurrent) { Text("Kosongkan slot") } },
        text = {
            Column {
                OutlinedTextField(
                    value = q,
                    onValueChange = { q = it },
                    singleLine = true,
                    label = { Text("Cari") },
                )
                LazyColumn(Modifier.heightIn(max = 360.dp)) {
                    items(pool.filter { it.name.contains(q, ignoreCase = true) }, key = { it.id }) { h ->
                        Text(
                            "${h.name}  ·  ${h.roles.joinToString("/")}",
                            Modifier.fillMaxWidth().clickable { onPick(h) }.padding(vertical = 10.dp),
                        )
                    }
                }
            }
        },
    )
}
