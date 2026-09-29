package com.example.uiapp.ui.commerce

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class CommerceSubTab(val title: String) {
    PRODUCT("Produk"),
    CHECKOUT("Checkout"),
    PAYMENT("Pembayaran"),
    WALLET("Dompet"),
}

private data class CartItem(val id: Int, val name: String, val price: Long, var qty: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommerceLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(CommerceSubTab.PRODUCT) }

    val cartItems = remember {
        mutableStateListOf(
            CartItem(1, "Minimalist Linen Overshirt", 450000, 1),
            CartItem(2, "Leather Minimalist Wallet", 250000, 2),
        )
    }
    var showCartSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Commerce & Transaksi",
                    subtitle = "Produk, Keranjang, Checkout, Dompet",
                    onBack = onBack,
                    action = {
                        Surface(
                            onClick = { showCartSheet = true },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primary,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text("Keranjang", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp),
                                ) {
                                    Text(
                                        text = cartItems.sumOf { it.qty }.toString(),
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surface)
                        .border(BorderStroke(1.dp, palette.border))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CommerceSubTab.entries.forEach { tab ->
                        val isSelected = activeSubTab == tab
                        Surface(
                            onClick = { activeSubTab = tab },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) palette.primary else palette.surfaceMuted,
                            border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                        ) {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else palette.textPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (activeSubTab) {
                        CommerceSubTab.PRODUCT -> ProductCardView(palette, onAddToCart = {
                            cartItems.add(CartItem(cartItems.size + 1, "Classic Wool Knit Jacket", 599000, 1))
                            showCartSheet = true
                        })
                        CommerceSubTab.CHECKOUT -> CheckoutStepperView(palette)
                        CommerceSubTab.PAYMENT -> PaymentMethodView(palette)
                        CommerceSubTab.WALLET -> DigitalWalletView(palette)
                    }
                }
            }
        }

        // Shopping Cart Bottom Sheet
        AnimatedVisibility(
            visible = showCartSheet,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showCartSheet = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Keranjang Belanja", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                            Text("Tutup ✕", fontSize = 12.sp, color = palette.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showCartSheet = false })
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        cartItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                    Text("Rp ${item.price.toString().reversed().chunked(3).joinToString(".").reversed()}", fontSize = 12.sp, color = palette.textSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        onClick = {
                                            if (item.qty > 1) {
                                                cartItems[index] = item.copy(qty = item.qty - 1)
                                            } else {
                                                cartItems.removeAt(index)
                                            }
                                        },
                                        shape = CircleShape,
                                        color = palette.surfaceMuted,
                                        border = BorderStroke(1.dp, palette.border),
                                    ) {
                                        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                                            Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(item.qty.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Surface(
                                        onClick = { cartItems[index] = item.copy(qty = item.qty + 1) },
                                        shape = CircleShape,
                                        color = palette.primary,
                                    ) {
                                        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                                            Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        val totalAmount = cartItems.sumOf { it.price * it.qty }
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Pesanan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                            Text("Rp ${totalAmount.toString().reversed().chunked(3).joinToString(".").reversed()}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = palette.primary)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            onClick = {
                                showCartSheet = false
                                activeSubTab = CommerceSubTab.CHECKOUT
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = palette.primary,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                                Text("Lanjut ke Checkout", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCardView(
    palette: com.example.uiapp.theme.AppPalette,
    onAddToCart: () -> Unit,
) {
    var selectedSize by remember { mutableStateOf("M") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    val colors = listOf(Color(0xFF0A332C), Color(0xFF1E293B), Color(0xFF94A3B8), Color(0xFFB45309))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Product Mock Image Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✦ LUXURY ATELIER ✦", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Signature Overshirt", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                Text("Classic Minimalist Overshirt", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = palette.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Rp 450.000", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = palette.primary)
                Spacer(modifier = Modifier.height(12.dp))

                // Size Selector
                Text("Ukuran:", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("S", "M", "L", "XL").forEach { size ->
                        val isSelected = selectedSize == size
                        Surface(
                            onClick = { selectedSize = size },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) palette.primary else palette.surfaceMuted,
                            border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                        ) {
                            Text(
                                text = size,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else palette.textPrimary,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Color Selector
                Text("Pilihan Warna:", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEachIndexed { index, c ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(BorderStroke(if (selectedColorIndex == index) 2.dp else 1.dp, if (selectedColorIndex == index) palette.primary else palette.border), CircleShape)
                                .clickable { selectedColorIndex = index },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(12.dp),
                    color = palette.primary,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                        Text("+ Tambah ke Keranjang", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutStepperView(palette: com.example.uiapp.theme.AppPalette) {
    var checkoutStep by remember { mutableIntStateOf(1) }
    var promoCode by remember { mutableStateOf("DISC20") }
    var isPromoApplied by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Step progress header
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                listOf("1. Alamat", "2. Pengiriman", "3. Pembayaran").forEachIndexed { i, stepTitle ->
                    val isDone = i <= checkoutStep
                    Text(
                        text = stepTitle,
                        fontSize = 11.sp,
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDone) palette.primary else palette.textMuted,
                    )
                }
            }
        }

        // Voucher / Coupon Validator Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Voucher / Kode Promo", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = promoCode,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                    Surface(
                        onClick = { isPromoApplied = !isPromoApplied },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPromoApplied) palette.success else palette.primary,
                    ) {
                        Text(
                            text = if (isPromoApplied) "Terpasang ✓" else "Gunakan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                }
                if (isPromoApplied) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Hemat potongan Rp 50.000 untuk transaksi ini.", fontSize = 11.sp, color = palette.success)
                }
            }
        }

        Surface(
            onClick = {
                if (checkoutStep < 2) checkoutStep++ else checkoutStep = 0
            },
            shape = RoundedCornerShape(12.dp),
            color = palette.primary,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (checkoutStep < 2) "Lanjut Langkah Berikutnya →" else "Konfirmasi & Bayar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodView(palette: com.example.uiapp.theme.AppPalette) {
    var selectedMethod by remember { mutableStateOf("Credit Card") }
    val methods = listOf(
        "Credit Card (Visa / Mastercard)" to "•••• •••• •••• 4242",
        "Apple Pay / Google Pay" to "One-touch secure checkout",
        "Transfer Bank Virtual Account" to "BCA, Mandiri, BNI, BRI",
        "Digital Wallet (GoPay/OVO/ShopeePay)" to "Saldo terhubung",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Metode Pembayaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
        Text("Pilih saluran pembayaran aman", fontSize = 12.sp, color = palette.textMuted)

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                methods.forEachIndexed { index, (name, detail) ->
                    val isSelected = selectedMethod.startsWith(name.take(10))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = name }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = palette.textPrimary)
                            Text(detail, fontSize = 11.sp, color = palette.textMuted)
                        }
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) palette.primary else Color.Transparent)
                                .border(BorderStroke(1.dp, if (isSelected) palette.primary else palette.border), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (index < methods.size - 1) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    }
                }
            }
        }
    }
}

@Composable
private fun DigitalWalletView(palette: com.example.uiapp.theme.AppPalette) {
    var balance by remember { mutableStateOf(2450000L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Digital Wallet Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.primary,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("DOMPET DIGITAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                    Text("ACTIVE PASS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text("Saldo Aktif", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                Text(
                    text = "Rp ${balance.toString().reversed().chunked(3).joinToString(".").reversed()}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        onClick = { balance += 100000 },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f),
                    ) {
                        Text("+ Top Up Rp 100k", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
        }

        // Mutation History
        Text("RIWAYAT MUTASI TERBARU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
        val history = listOf(
            Triple("Pembayaran di Atelier Store", "- Rp 450.000", "Hari ini"),
            Triple("Top Up Bank Transfer", "+ Rp 1.000.000", "Kemarin"),
            Triple("Cashback Promo KMP", "+ Rp 50.000", "2 hari lalu"),
        )
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                history.forEachIndexed { index, (desc, amount, date) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(desc, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Text(date, fontSize = 10.sp, color = palette.textMuted)
                        }
                        Text(
                            text = amount,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (amount.startsWith("+")) palette.success else palette.textPrimary,
                        )
                    }
                    if (index < history.size - 1) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    }
                }
            }
        }
    }
}
