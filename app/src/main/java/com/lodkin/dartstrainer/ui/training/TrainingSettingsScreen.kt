package com.lodkin.dartstrainer.ui.training

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodkin.dartstrainer.data.training.TrainingBlocksStorage
import com.lodkin.dartstrainer.data.training.TrainingBotLevelStorage
import com.lodkin.dartstrainer.data.training.TrainingRepository
import com.lodkin.dartstrainer.data.training.TrainingStateStorage
import com.lodkin.dartstrainer.theme.Accent
import com.lodkin.dartstrainer.theme.ErrorColor
import com.lodkin.dartstrainer.theme.GoldAccent
import com.lodkin.dartstrainer.theme.TileBg
import com.lodkin.dartstrainer.theme.TileBgDark
import kotlinx.coroutines.launch

@Composable
fun TrainingSettingsScreen(
    repository: TrainingRepository,
    onBack: () -> Unit,
    onAfterReset: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var resetDone by remember { mutableStateOf(false) }
    
    var showInDevelopmentDialog by remember { mutableStateOf(false) }
    var inDevelopmentTitle by remember { mutableStateOf("") }

    // При загрузке экрана принудительно отключаем блоки
    LaunchedEffect(Unit) {
        TrainingBlocksStorage.setWarmupEnabled(context, false)
        TrainingBlocksStorage.setPsychologyEnabled(context, false)
        TrainingBlocksStorage.setStrengthEnabled(context, false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(TileBgDark)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("← Назад", color = Accent, fontSize = 15.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Настройки тренировки",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "БЛОКИ ТРЕНИРОВКИ",
                color = Accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "В этой версии доступна только основная часть. Остальные блоки появятся в следующих обновлениях.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))

            // Разминка (в разработке)
            DisabledBlockRow(
                icon = "🔥",
                title = "Разминка",
                subtitle = "Появится в следующем обновлении",
                onClick = { inDevelopmentTitle = "Разминка"; showInDevelopmentDialog = true }
            )
            Spacer(Modifier.height(10.dp))

            // Основная часть (всегда включена)
            DisabledBlockRow(
                icon = "🎯",
                title = "Основная часть",
                subtitle = "Это суть тренировки, её нельзя отключить",
                onClick = { }
            )
            Spacer(Modifier.height(10.dp))

            // Психология (в разработке)
            DisabledBlockRow(
                icon = "🧠",
                title = "Психология",
                subtitle = "Появится в следующем обновлении",
                onClick = { inDevelopmentTitle = "Психология"; showInDevelopmentDialog = true }
            )
            Spacer(Modifier.height(10.dp))

            // Силовая (в разработке)
            DisabledBlockRow(
                icon = "💪",
                title = "Силовая",
                subtitle = "Появится в следующем обновлении",
                onClick = { inDevelopmentTitle = "Силовая"; showInDevelopmentDialog = true }
            )

            Spacer(Modifier.height(28.dp))

            Text(
                "СБРОС ДАННЫХ ТРЕНИРОВКИ",
                color = Accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Начать тренировочный прогресс с чистого листа. " +
                        "Игры и статистика «Просто поиграть» не тронутся.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))

            if (resetDone) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Accent.copy(alpha = 0.12f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ Данные тренировок сброшены", color = Accent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TileBgDark)
                        .clickable { showResetDialog = true }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Сбросить тренировочные данные", color = ErrorColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = TileBgDark,
            title = { Text("Сбросить данные тренировки?", color = GoldAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Будут удалены все тренировочные сессии и результаты.", color = Color.White, fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    scope.launch {
                        repository.clearAllTrainingData()
                        TrainingStateStorage.clear(context)
                        TrainingBotLevelStorage.clear(context)
                        resetDone = true
                        onAfterReset()
                    }
                }) { Text("СБРОСИТЬ", color = ErrorColor, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Отмена", color = Accent) } }
        )
    }
    
    if (showInDevelopmentDialog) {
        AlertDialog(
            onDismissRequest = { showInDevelopmentDialog = false },
            containerColor = TileBgDark,
            confirmButton = { TextButton(onClick = { showInDevelopmentDialog = false }) { Text("Понятно", color = Accent, fontWeight = FontWeight.Bold) } },
            title = { Text("В разработке", color = GoldAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Раздел «$inDevelopmentTitle» появится в следующем обновлении приложения. Следите за новостями!", color = Color.White, fontSize = 14.sp) }
        )
    }
}

// --- НОВАЯ ФУНКЦИЯ: Всегда рисует неактивный вид (серый) ---
@Composable
private fun DisabledBlockRow(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TileBgDark)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
        }
        Spacer(Modifier.width(8.dp))

        // ВСЕГДА рисуем серый выключенный переключатель
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp, start = 2.dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.CenterStart)
            )
        }
    }
}
