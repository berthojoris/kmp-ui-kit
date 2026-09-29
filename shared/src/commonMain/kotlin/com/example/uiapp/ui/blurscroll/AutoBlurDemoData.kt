package com.example.uiapp.ui.blurscroll

enum class DemoStatus { PAID, PENDING, REFUND }

data class DemoItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val amount: String,
    val status: DemoStatus,
)

val DemoTransactions: List<DemoItem> = List(30) { index ->
    val n = index + 1
    DemoItem(
        id = n,
        title = "Transaksi #INV-${2000 + n}",
        subtitle = "12 Sep 2026 \u00B7 09:${(10 + n % 45).toString().padStart(2, '0')}",
        amount = "Rp ${(250 + n * 37) * 1000}",
        status = when (n % 3) {
            0 -> DemoStatus.PAID
            1 -> DemoStatus.PENDING
            else -> DemoStatus.REFUND
        },
    )
}
