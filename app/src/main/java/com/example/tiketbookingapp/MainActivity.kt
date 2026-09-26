package com.example.tiketbookingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale


// ============================================================
// KONSTANTA
// ============================================================

const val HARGA_TIKET_DEFAULT = 40000
const val MAKS_TIKET = 10


// ============================================================
// WARNA
// ============================================================

val Ink = Color(0xFF0E1116)
val LatarAtas = Color(0xFF1E1250)
val LatarBawah = Color(0xFF0A0916)
val Cream = Color(0xFFFFF4E0)
val CreamGelap = Color(0xFFEADFC8)
val CokelatMuda = Color(0xFF8A7F68)
val Coral = Color(0xFFFF5F6D)
val Violet = Color(0xFF7C3AED)
val Lime = Color(0xFFD4FF3F)
val AbuGelap = Color(0xFFA7A3C2)

val HijauStatus = Color(0xFF35A853)
val MerahStatus = Color(0xFFE63946)
val BiruStatus = Color(0xFF3B82F6)

val TinggiBagianAtas = 140.dp
val RadiusLekuk = 14.dp


// ============================================================
// MAIN ACTIVITY
// ============================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParentPemesananTiket()
        }
    }
}


// ============================================================
// PARENT
// STATE DITARUH DI SINI = STATE HOISTING
// ============================================================

@Composable
fun ParentPemesananTiket() {

    // ========================================================
    // STATE YANG DIKELOLA PARENT
    // ========================================================

    // 1. Harga tiket
    var hargaTiket by rememberSaveable {
        mutableIntStateOf(HARGA_TIKET_DEFAULT)
    }

    // 2. Jumlah tiket
    var jumlahTiket by rememberSaveable {
        mutableIntStateOf(1)
    }

    // 3. Nama pembeli tiket
    var namaPembeli by rememberSaveable {
        mutableStateOf("")
    }


    // ========================================================
    // STATE UNTUK STATUS PEMESANAN
    // ========================================================

    var statusPesanan by rememberSaveable {
        mutableStateOf("Silakan pesan tiket")
    }

    var orderTrigger by rememberSaveable {
        mutableIntStateOf(0)
    }


    // ========================================================
    // LAUNCHED EFFECT
    // ========================================================

    LaunchedEffect(orderTrigger) {

        // Jangan menjalankan apa-apa ketika aplikasi pertama
        // kali dibuka.
        if (orderTrigger == 0) return@LaunchedEffect

        // Jika nama kosong
        if (namaPembeli.trim().isEmpty()) {

            statusPesanan = "Nama masih kosong"

        } else {

            // Tampilkan status proses
            statusPesanan = "Memproses pesanan..."

            // Tunggu 5 detik
            delay(5000)

            // Setelah 5 detik
            statusPesanan = "Tiket telah dipesan"
        }
    }


    // ========================================================
    // KIRIM STATE DARI PARENT KE CHILD
    // ========================================================

    HalamanTiket(
        hargaTiket = hargaTiket,
        jumlahTiket = jumlahTiket,
        namaPembeli = namaPembeli,
        statusPesanan = statusPesanan,

        // Callback untuk mengubah jumlah
        onJumlahChange = {
            jumlahTiket = it
        },

        // Callback untuk mengubah nama
        onNamaChange = {
            namaPembeli = it
        },

        // Tombol pesan
        onPesanClick = {
            orderTrigger++
        },

        // Reset
        onReset = {
            jumlahTiket = 1
            namaPembeli = ""
            statusPesanan = "Silakan pesan tiket"
        }
    )
}


// ============================================================
// CHILD / UI HALAMAN TIKET
// ============================================================

@Composable
fun HalamanTiket(
    hargaTiket: Int,
    jumlahTiket: Int,
    namaPembeli: String,
    statusPesanan: String,

    onJumlahChange: (Int) -> Unit,
    onNamaChange: (String) -> Unit,
    onPesanClick: () -> Unit,
    onReset: () -> Unit
) {

    // Total dihitung dari state parent
    val total = hargaTiket * jumlahTiket

    // Animasi total pembayaran
    val totalAnimasi by animateIntAsState(
        targetValue = total,
        animationSpec = tween(durationMillis = 400),
        label = "total"
    )


    // ========================================================
    // TAMPILAN UTAMA
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LatarAtas,
                        LatarBawah
                    )
                )
            )
    ) {

        // ====================================================
        // BAGIAN ATAS
        // ====================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 20.dp
                    )
            ) {

                Text(
                    text = "E-TICKET",
                    color = Lime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Pemesanan Tiket",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Pesan tiket dengan mudah!",
                    color = AbuGelap,
                    fontSize = 15.sp
                )
            }


            // =================================================
            // TICKET CARD
            // =================================================

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(
                        TicketShape(
                            24.dp,
                            RadiusLekuk,
                            TinggiBagianAtas + 1.dp
                        )
                    )
                    .background(Cream)
            ) {

                // =============================================
                // BAGIAN HARGA
                // =============================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TinggiBagianAtas)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Coral,
                                    Violet
                                )
                            )
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 24.dp)
                    ) {

                        Text(
                            text = "Harga Tiket",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = formatRupiah(hargaTiket),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "per tiket",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = "ADMIT\nONE",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 30.dp)
                    )
                }


                // Garis putus-putus
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = RadiusLekuk + 8.dp)
                        .height(2.dp)
                ) {

                    drawLine(
                        color = Color(0xFFCDBF9F),
                        start = Offset(
                            0f,
                            size.height / 2
                        ),
                        end = Offset(
                            size.width,
                            size.height / 2
                        ),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(16f, 14f)
                        )
                    )
                }


                // =============================================
                // FORM
                // =============================================

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    // -----------------------------------------
                    // NAMA PEMBELI
                    // -----------------------------------------

                    Text(
                        text = "Nama Pembeli",
                        color = CokelatMuda,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = namaPembeli,
                        onValueChange = onNamaChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Masukkan nama Anda"
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    // -----------------------------------------
                    // JUMLAH TIKET
                    // -----------------------------------------

                    Text(
                        text = "Jumlah Tiket",
                        color = CokelatMuda,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(50)
                            )
                            .background(CreamGelap)
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        StepBtn(
                            label = "−",
                            aktif = jumlahTiket > 1
                        ) {
                            if (jumlahTiket > 1) {
                                onJumlahChange(jumlahTiket - 1)
                            }
                        }


                        AnimatedContent(
                            targetState = jumlahTiket,
                            modifier = Modifier.weight(1f),
                            transitionSpec = {

                                if (targetState > initialState) {

                                    (
                                            slideInVertically { it } +
                                                    fadeIn()
                                            ).togetherWith(
                                            slideOutVertically { -it } +
                                                    fadeOut()
                                        )

                                } else {

                                    (
                                            slideInVertically { -it } +
                                                    fadeIn()
                                            ).togetherWith(
                                            slideOutVertically { it } +
                                                    fadeOut()
                                        )
                                }
                            },
                            label = "jumlah"
                        ) { angka ->

                            Text(
                                text = "$angka",
                                color = Ink,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }


                        StepBtn(
                            label = "+",
                            aktif = jumlahTiket < MAKS_TIKET
                        ) {

                            if (jumlahTiket < MAKS_TIKET) {
                                onJumlahChange(jumlahTiket + 1)
                            }
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // -----------------------------------------
                    // INDIKATOR JUMLAH TIKET
                    // -----------------------------------------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {

                        for (i in 1..MAKS_TIKET) {

                            Segmen(
                                aktif = i <= jumlahTiket,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )


                    Text(
                        text = "Maksimal $MAKS_TIKET tiket per pesanan",
                        color = CokelatMuda,
                        fontSize = 12.sp
                    )


                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )


                    // -----------------------------------------
                    // TOMBOL PESAN
                    // -----------------------------------------

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Coral,
                                        Violet
                                    )
                                )
                            )
                            .clickable {
                                onPesanClick()
                            }
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Pesan Tiket",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // -----------------------------------------
                    // STATUS
                    // -----------------------------------------

                    StatusPesanan(
                        status = statusPesanan
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }


        // ====================================================
        // TOTAL BAYAR
        // ====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Lime,
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .navigationBarsPadding()
                .padding(
                    horizontal = 24.dp,
                    vertical = 22.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "TOTAL BAYAR",
                    color = Ink.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = formatRupiah(totalAnimasi),
                    color = Ink,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "$jumlahTiket × ${formatRupiah(hargaTiket)}",
                    color = Ink.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
            }


            // RESET
            Row(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(50)
                    )
                    .background(Ink)
                    .clickable {
                        onReset()
                    }
                    .padding(
                        horizontal = 18.dp,
                        vertical = 14.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Lime
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "RESET",
                    color = Lime,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}


// ============================================================
// STATUS PESANAN
// ============================================================

@Composable
fun StatusPesanan(
    status: String
) {

    val isEmpty = status == "Nama masih kosong"
    val isProcessing = status == "Memproses pesanan..."
    val isSuccess = status == "Tiket telah dipesan"

    val background = when {

        isEmpty ->
            Color(0xFFFFE5E8)

        isProcessing ->
            Color(0xFFE3F0FF)

        isSuccess ->
            Color(0xFFE3F7E8)

        else ->
            Color(0xFFF2F4F7)
    }


    val textColor = when {

        isEmpty ->
            MerahStatus

        isProcessing ->
            BiruStatus

        isSuccess ->
            HijauStatus

        else ->
            CokelatMuda
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(background)
            .padding(
                horizontal = 14.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon status
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(textColor)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = "Status: $status",
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


// ============================================================
// TICKET SHAPE
// ============================================================

class TicketShape(
    private val sudut: Dp,
    private val radiusLekuk: Dp,
    private val posisiLekuk: Dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val sudutPx = with(density) {
            sudut.toPx()
        }

        val r = with(density) {
            radiusLekuk.toPx()
        }

        val y = with(density) {
            posisiLekuk.toPx()
        }


        val dasar = Path().apply {

            addRoundRect(
                RoundRect(
                    0f,
                    0f,
                    size.width,
                    size.height,
                    CornerRadius(sudutPx)
                )
            )
        }


        val lubang = Path().apply {

            addOval(
                Rect(
                    Offset(
                        -r,
                        y - r
                    ),
                    Size(
                        r * 2,
                        r * 2
                    )
                )
            )

            addOval(
                Rect(
                    Offset(
                        size.width - r,
                        y - r
                    ),
                    Size(
                        r * 2,
                        r * 2
                    )
                )
            )
        }


        return Outline.Generic(
            Path.combine(
                PathOperation.Difference,
                dasar,
                lubang
            )
        )
    }
}


// ============================================================
// SEGMENT INDIKATOR
// ============================================================

@Composable
fun Segmen(
    aktif: Boolean,
    modifier: Modifier = Modifier
) {

    val warna by animateColorAsState(
        targetValue = if (aktif) {
            Violet
        } else {
            CreamGelap
        },
        animationSpec = tween(250),
        label = "segmen"
    )


    Box(
        modifier = modifier
            .height(8.dp)
            .clip(
                RoundedCornerShape(50)
            )
            .background(warna)
    )
}


// ============================================================
// BUTTON + / -
// ============================================================

@Composable
fun StepBtn(
    label: String,
    aktif: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                if (aktif) {
                    Ink
                } else {
                    Color(0xFFD3C7AE)
                }
            )
            .clickable(
                enabled = aktif,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = label,
            color = if (aktif) {
                Cream
            } else {
                Color(0xFF9A8F78)
            },
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// ============================================================
// FORMAT RUPIAH
// ============================================================

fun formatRupiah(
    nilai: Int
): String {

    val format =
        NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("id-ID")
        )

    return "Rp" + format.format(nilai)
}