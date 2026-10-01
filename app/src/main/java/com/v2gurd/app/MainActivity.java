package com.v2gurd.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // ---------- Theme (از CSS سایت) ----------
    private val PRIMARY = Color.parseColor("#1d6fe8")
    private val PRIMARY_SOFT = Color.parseColor("#e6f0ff")
    private val ROSE = Color.parseColor("#e11d48")
    private val INK = Color.parseColor("#0f1c33")
    private val MUTED = Color.parseColor("#5d6e8c")
    private val BG = Color.parseColor("#eef4ff")
    private val BORDER = Color.parseColor("#dbe6f7")
    private val SURFACE2 = Color.parseColor("#f6f9ff")
    private val CYAN = Color.parseColor("#22d3ee")
    private val HERO_A = Color.parseColor("#0a1836")
    private val HERO_B = Color.parseColor("#123a85")

    private val TELEGRAM = "https://t.me/Vtwoguard"
    private val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
    private val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

    // ---------- Data ----------
    data class Product(
        val id: String,
        val title: String,
        val type: String,
        val price: Long,
        val features: List<String>,
        val badge: String? = null,
        val special: Boolean = false,
        val icon: String
    )

    data class CartItem(val id: String, val title: String, val price: Long, var qty: Int)

    private val cart = LinkedHashMap<String, CartItem>()

    private val wireguard: List<Product> by lazy {
        listOf(10 to 149000L, 20 to 279000L, 40 to 499000L, 60 to 699000L, 100 to 999000L).map { (gb, price) ->
            Product(
                id = "wg$gb",
                title = "وایرگارد ${fa(gb)} گیگ",
                type = "تک‌کاربره • ۳۰ روزه",
                price = price,
                features = listOf("تک‌کاربره و تک‌سرور", "IP ثابت", "سرعت و پایداری بالا", "اعتبار ۳۰ روزه", "پشتیبانی سریع"),
                badge = if (gb == 10) "پیشنهاد ویژه" else null,
                special = gb == 10,
                icon = "🟣"
            )
        }
    }

    private val jumpjump: List<Product> by lazy {
        listOf(
            Triple(1, 1, 700000L), Triple(2, 1, 999000L),
            Triple(2, 6, 4600000L), Triple(2, 12, 5500000L)
        ).map { (devices, months, price) ->
            val badge = when (months) { 6 -> "پیشنهاد ویژه"; 12 -> "بهترین ارزش خرید"; else -> null }
            Product(
                id = "jj$months-$devices",
                title = "JumpJump",
                type = "${fa(devices)} دستگاه • ${fa(months)} ماه",
                price = price,
                features = listOf("${fa(devices)} دستگاه", "${fa(months)} ماه اعتبار", "سرویس چند دستگاه", "پشتیبانی V2GURD", "مناسب استفاده روزمره"),
                badge = badge,
                special = badge != null,
                icon = "🚀"
            )
        }
    }

    data class V2Plan(val id: String, val type: String, val icon: String, val title: String,
                      val status: String, val perGb: Long, val special: Boolean, val desc: List<String>)

    private val v2box = listOf(
        V2Plan("eco", "اقتصادی", "💠", "سرور اقتصادی", "موجود", 2000, false, listOf("مناسب مصرف روزمره", "قیمت اقتصادی", "حجم قابل انتخاب")),
        V2Plan("normal", "معمولی", "⚡", "سرور معمولی", "موجود", 1500, false, listOf("سرعت مناسب", "اتصال پایدار", "مناسب استفاده روزمره")),
        V2Plan("cdn", "CDN", "🚀", "CDN", "ویژه", 1500, true, listOf("اتصال سریع", "مناسب مصرف بالا", "مسیر بهینه")),
        V2Plan("tunnel", "Tunnel", "🔗", "Tunnel", "موجود", 2500, false, listOf("مسیر تونلی", "مناسب اتصال پایدار", "سرویس پرسرعت")),
        V2Plan("pro", "حرفه‌ای", "👑", "سرور حرفه‌ای", "حرفه‌ای", 3000, true, listOf("مناسب مصرف سنگین", "سرعت بالا", "سرویس حرفه‌ای V2GURD"))
    )

    private val gbOptions = listOf(50, 100, 150, 200, 250, 300, 400, 500)

    // ---------- Views ----------
    private lateinit var content: LinearLayout
    private lateinit var scroll: ScrollView
    private lateinit var cartBtn: TextView
    private val navItems = LinkedHashMap<String, TextView>()
    private var currentPage = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(BG)
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        root.fitsSystemWindows = true

        root.addView(buildHeader(), lp(MATCH, dp(62)))

        scroll = ScrollView(this)
        scroll.isVerticalScrollBarEnabled = false
        content = vbox(16)
        scroll.addView(content, ViewGroup.LayoutParams(MATCH, WRAP))
        root.addView(scroll, LinearLayout.LayoutParams(MATCH, 0, 1f))

        root.addView(buildNav(), lp(MATCH, dp(62)))

        setContentView(root)
        show("home")
    }

    // ---------- Helpers ----------
    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun fa(n: Number): String {
        val s = String.format(Locale.US, "%,d", n.toLong())
        return s.map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")
    }

    private fun shape(fill: Int, r: Int, stroke: Int = 0, sw: Int = 0): GradientDrawable {
        val d = GradientDrawable()
        d.setColor(fill)
        d.cornerRadius = dp(r).toFloat()
        if (sw > 0) d.setStroke(dp(sw), stroke)
        return d
    }

    private fun darkShape(r: Int): GradientDrawable {
        val d = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(HERO_A, HERO_B))
        d.cornerRadius = dp(r).toFloat()
        return d
    }

    private fun lp(w: Int, h: Int, top: Int = 0, weight: Float = 0f): LinearLayout.LayoutParams {
        val p = if (weight > 0f) LinearLayout.LayoutParams(w, h, weight) else LinearLayout.LayoutParams(w, h)
        p.topMargin = dp(top)
        return p
    }

    private fun text(t: String, sp: Float, color: Int, bold: Boolean = false): TextView {
        val tv = TextView(this)
        tv.text = t
        tv.textSize = sp
        tv.setTextColor(color)
        if (bold) tv.setTypeface(null, Typeface.BOLD)
        tv.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        tv.setLineSpacing(0f, 1.25f)
        return tv
    }

    private fun vbox(pad: Int = 0): LinearLayout {
        val l = LinearLayout(this)
        l.orientation = LinearLayout.VERTICAL
        l.setPadding(dp(pad), dp(pad), dp(pad), dp(pad))
        return l
    }

    private fun hbox(): LinearLayout {
        val l = LinearLayout(this)
        l.orientation = LinearLayout.HORIZONTAL
        l.gravity = Gravity.CENTER_VERTICAL
        return l
    }

    private fun btn(label: String, bg: Int, fg: Int, onClick: () -> Unit): TextView {
        val b = text(label, 14f, fg, true)
        b.gravity = Gravity.CENTER
        b.textAlignment = View.TEXT_ALIGNMENT_CENTER
        b.background = shape(bg, 10)
        b.setPadding(dp(16), dp(12), dp(16), dp(12))
        b.setOnClickListener { onClick() }
        return b
    }

    private fun outlineBtn(label: String, onClick: () -> Unit): TextView {
        val b = btn(label, Color.WHITE, Color.parseColor("#1450b8"), onClick)
        b.background = shape(Color.WHITE, 10, Color.parseColor("#c7d7f0"), 1)
        return b
    }

    private fun card(): LinearLayout {
        val c = vbox(16)
        c.background = shape(Color.WHITE, 20, BORDER, 1)
        return c
    }

    private fun emojiBox(emoji: String, size: Int, color: Int): TextView {
        val t = TextView(this)
        t.text = emoji
        t.textSize = 20f
        t.gravity = Gravity.CENTER
        t.background = shape(color, 13)
        t.layoutParams = LinearLayout.LayoutParams(dp(size), dp(size))
        return t
    }

    private fun add(view: View, top: Int = 14) {
        content.addView(view, lp(MATCH, WRAP, top))
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    // ---------- Header & Nav ----------
    private fun buildHeader(): View {
        val h = hbox()
        h.setBackgroundColor(Color.WHITE)
        h.setPadding(dp(16), 0, dp(16), 0)

        val mark = text("V2", 15f, Color.WHITE, true)
        mark.gravity = Gravity.CENTER
        mark.textAlignment = View.TEXT_ALIGNMENT_CENTER
        mark.background = shape(PRIMARY, 11)
        h.addView(mark, LinearLayout.LayoutParams(dp(36), dp(36)))

        val name = SpannableString("V2GURD")
        name.setSpan(ForegroundColorSpan(PRIMARY), 2, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val brand = text("", 20f, INK, true)
        brand.text = name
        val bp = LinearLayout.LayoutParams(0, WRAP, 1f)
        bp.marginStart = dp(10)
        h.addView(brand, bp)

        cartBtn = text("🛒 ۰", 14f, INK, true)
        cartBtn.gravity = Gravity.CENTER
        cartBtn.textAlignment = View.TEXT_ALIGNMENT_CENTER
        cartBtn.background = shape(Color.WHITE, 11, BORDER, 1)
        cartBtn.setPadding(dp(12), dp(8), dp(12), dp(8))
        cartBtn.setOnClickListener { showCart() }
        h.addView(cartBtn, LinearLayout.LayoutParams(WRAP, dp(42)))

        val wrap = vbox()
        wrap.addView(h, LinearLayout.LayoutParams(MATCH, dp(61)))
        val line = View(this)
        line.setBackgroundColor(BORDER)
        wrap.addView(line, LinearLayout.LayoutParams(MATCH, dp(1)))
        return wrap
    }

    private fun buildNav(): View {
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.setBackgroundColor(Color.WHITE)
        val items = listOf(
            Triple("home", "⌂", "خانه"),
            Triple("wireguard", "🟣", "WireGuard"),
            Triple("v2box", "📦", "V2Box"),
            Triple("jumpjump", "🚀", "JumpJump"),
            Triple("support", "💬", "پشتیبانی")
        )
        for ((id, icon, label) in items) {
            val t = TextView(this)
            t.text = "$icon\n$label"
            t.textSize = 10f
            t.gravity = Gravity.CENTER
            t.setTextColor(MUTED)
            t.setOnClickListener { show(id) }
            navItems[id] = t
            bar.addView(t, LinearLayout.LayoutParams(0, MATCH, 1f))
        }
        return bar
    }

    private fun updateNav() {
        for ((id, t) in navItems) t.setTextColor(if (id == currentPage) PRIMARY else MUTED)
    }

    private fun show(page: String) {
        currentPage = page
        content.removeAllViews()
        when (page) {
            "home" -> renderHome()
            "wireguard" -> renderCategory("سرویس تک‌کاربره", "🟣 وایرگارد", "اتصال امن و پایدار با IP ثابت و اعتبار ۳۰ روزه.") { wireguard.forEach { add(productCard(it)) } }
            "jumpjump" -> renderCategory("سرویس چند دستگاه", "🚀 JumpJump", "مناسب استفاده همزمان روی چند دستگاه.") { jumpjump.forEach { add(productCard(it)) } }
            "v2box" -> renderCategory("سرویس حجمی", "📦 V2Box", "سرویس‌های اقتصادی، CDN، Tunnel و حرفه‌ای با حجم قابل انتخاب.") { v2box.forEach { add(v2boxCard(it)) } }
            "support" -> renderSupport()
        }
        updateNav()
        scroll.post { scroll.scrollTo(0, 0) }
    }

    // ---------- Home ----------
    private fun renderHome() {
        // Hero
        val eyebrow = text("● فروشگاه رسمی V2GURD", 11f, Color.parseColor("#1450b8"), true)
        eyebrow.background = shape(PRIMARY_SOFT, 30)
        eyebrow.setPadding(dp(12), dp(6), dp(12), dp(6))
        content.addView(eyebrow, LinearLayout.LayoutParams(WRAP, WRAP))

        val title = SpannableString("اینترنت امن،\nسریع و پایدار")
        val start = title.indexOf("سریع")
        title.setSpan(ForegroundColorSpan(PRIMARY), start, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val h1 = text("", 32f, INK, true)
        h1.text = title
        h1.setLineSpacing(0f, 1.3f)
        add(h1, 14)

        add(text("سرویس موردنیازت را انتخاب کن، حجم مناسب را مشخص کن و سفارش خودت را به‌سادگی ثبت کن.", 13f, MUTED), 10)

        val row = hbox()
        val b1 = btn("مشاهده سرویس‌ها", PRIMARY, Color.WHITE) { show("wireguard") }
        val b2 = outlineBtn("🛒 سبد خرید") { showCart() }
        row.addView(b1, LinearLayout.LayoutParams(0, WRAP, 1f))
        val p2 = LinearLayout.LayoutParams(0, WRAP, 1f)
        p2.marginStart = dp(10)
        row.addView(b2, p2)
        add(row, 18)
        add(btn("✈️ پشتیبانی تلگرام", INK, Color.WHITE) { openUrl(TELEGRAM) }, 10)

        // Network card
        val net = vbox(20)
        net.background = darkShape(26)
        val head = hbox()
        head.addView(text("وضعیت شبکه V2GURD", 15f, Color.WHITE, true), LinearLayout.LayoutParams(0, WRAP, 1f))
        val live = text("● آنلاین", 10f, Color.parseColor("#7fe7ff"), true)
        live.background = shape(Color.parseColor("#1f3d63"), 20, CYAN, 1)
        live.setPadding(dp(10), dp(4), dp(10), dp(4))
        head.addView(live)
        net.addView(head)

        val route = hbox()
        val nodes = listOf("📱\nدستگاه شما", "🛡️\nV2GURD", "🌐\nمقصد")
        nodes.forEachIndexed { i, label ->
            if (i > 0) {
                val a = text("←", 16f, CYAN, true)
                a.setPadding(dp(6), 0, dp(6), 0)
                route.addView(a)
            }
            val n = text(label, 10f, Color.parseColor("#c5d6f2"))
            n.gravity = Gravity.CENTER
            n.textAlignment = View.TEXT_ALIGNMENT_CENTER
            n.background = shape(Color.parseColor("#14305f"), 13, Color.parseColor("#3d6bb3"), 1)
            n.setPadding(dp(4), dp(12), dp(4), dp(12))
            route.addView(n, LinearLayout.LayoutParams(0, WRAP, 1f))
        }
        net.addView(route, lp(MATCH, WRAP, 16))

        val stats = hbox()
        listOf("۳۸ms" to "Ping", "۰٪" to "Packet Loss", "پایدار" to "Route").forEachIndexed { i, (v, l) ->
            val s = text("$v\n$l", 11f, Color.parseColor("#9bb8e6"))
            s.gravity = Gravity.CENTER
            s.textAlignment = View.TEXT_ALIGNMENT_CENTER
            s.background = shape(Color.parseColor("#14305f"), 11, Color.parseColor("#3d6bb3"), 1)
            s.setPadding(dp(4), dp(10), dp(4), dp(10))
            val p = LinearLayout.LayoutParams(0, WRAP, 1f)
            if (i > 0) p.marginStart = dp(8)
            stats.addView(s, p)
        }
        net.addView(stats, lp(MATCH, WRAP, 12))
        add(net, 22)

        // Trust strip
        val trust = vbox()
        trust.background = shape(Color.WHITE, 18, BORDER, 1)
        val items = listOf(
            "⚡" to ("سرعت بالا" to "مسیرهای بهینه"), "🛡️" to ("امنیت" to "اتصال محافظت‌شده"),
            "◉" to ("پایداری" to "اتصال مناسب و پایدار"), "۲۴" to ("پشتیبانی ۲۴/۷" to "از طریق تلگرام")
        )
        for (r in 0..1) {
            val line = hbox()
            for (c in 0..1) {
                val (icon, ts) = items[r * 2 + c]
                val cell = hbox()
                cell.setPadding(dp(12), dp(14), dp(12), dp(14))
                val ic = text(icon, 15f, PRIMARY, true)
                ic.gravity = Gravity.CENTER
                ic.textAlignment = View.TEXT_ALIGNMENT_CENTER
                ic.background = shape(PRIMARY_SOFT, 11)
                cell.addView(ic, LinearLayout.LayoutParams(dp(38), dp(38)))
                val tv = vbox()
                tv.addView(text(ts.first, 12f, INK, true))
                tv.addView(text(ts.second, 10f, MUTED))
                val tp = LinearLayout.LayoutParams(0, WRAP, 1f)
                tp.marginStart = dp(10)
                cell.addView(tv, tp)
                line.addView(cell, LinearLayout.LayoutParams(0, WRAP, 1f))
            }
            trust.addView(line)
        }
        add(trust, 22)

        // Featured
        val feat = vbox(22)
        feat.background = darkShape(26)
        feat.addView(text("پیشنهاد ویژه فروشگاه", 10f, Color.parseColor("#7fe7ff"), true))
        feat.addView(text("🟣 وایرگارد تک‌کاربره", 22f, Color.WHITE, true), lp(MATCH, WRAP, 6))
        feat.addView(text("سرویس تک‌کاربره با IP ثابت، سرعت و پایداری بالا و اعتبار ۳۰ روزه.", 12f, Color.parseColor("#c5d6f2")), lp(MATCH, WRAP, 8))
        feat.addView(text("شروع قیمت از ${fa(149000)} تومان", 18f, Color.WHITE, true), lp(MATCH, WRAP, 14))
        feat.addView(btn("افزودن به سبد", PRIMARY, Color.WHITE) { addToCart(wireguard[0].id, wireguard[0].title, wireguard[0].price) }, lp(MATCH, WRAP, 12))
        add(feat, 22)

        // Categories
        add(sectionTitle("دسته‌بندی سرویس‌ها", "سرویس مناسب خودت را انتخاب کن"), 28)
        listOf(
            Triple("wireguard", "🟣 WireGuard", "تک‌کاربره • IP ثابت • سرعت بالا"),
            Triple("v2box", "📦 V2Box", "CDN • حرفه‌ای • اقتصادی • Tunnel"),
            Triple("jumpjump", "🚀 JumpJump", "سرویس چند دستگاه")
        ).forEach { (id, t, d) ->
            val c = card()
            c.addView(text(t, 17f, INK, true))
            c.addView(text(d, 11f, MUTED), lp(MATCH, WRAP, 3))
            c.addView(text("مشاهده سرویس‌ها ←", 11f, PRIMARY, true), lp(MATCH, WRAP, 12))
            c.setOnClickListener { show(id) }
            add(c, 12)
        }

        // Steps
        add(sectionTitle("مراحل سفارش", "خرید در چهار مرحله"), 28)
        val steps = card()
        listOf(
            "۱  انتخاب سرویس" to "سرویس مناسب نیازت را انتخاب کن.",
            "۲  افزودن به سبد" to "پلن و حجم موردنظر را به سبد اضافه کن.",
            "۳  پرداخت" to "مبلغ نهایی را به کارت اعلام‌شده انتقال بده.",
            "۴  دریافت سرویس" to "رسید را در تلگرام ارسال کن."
        ).forEachIndexed { i, (t, d) ->
            steps.addView(text(t, 13f, INK, true), lp(MATCH, WRAP, if (i == 0) 0 else 12))
            steps.addView(text(d, 11f, MUTED), lp(MATCH, WRAP, 2))
        }
        add(steps, 12)
    }

    private fun sectionTitle(kicker: String, title: String): View {
        val v = vbox()
        v.addView(text(kicker, 11f, PRIMARY, true))
        v.addView(text(title, 21f, INK, true), lp(MATCH, WRAP, 2))
        return v
    }

    // ---------- Category pages ----------
    private fun renderCategory(tag: String, title: String, desc: String, products: () -> Unit) {
        val hero = card()
        hero.addView(text(tag, 11f, Color.parseColor("#1450b8"), true))
        hero.addView(text(title, 28f, INK, true), lp(MATCH, WRAP, 8))
        hero.addView(text(desc, 12f, MUTED), lp(MATCH, WRAP, 6))
        content.addView(hero, lp(MATCH, WRAP, 0))
        products()
    }

    private fun cardHeader(p: Product): View {
        val top = hbox()
        top.addView(emojiBox(p.icon, 46, if (p.special) ROSE else PRIMARY))
        val tv = vbox()
        tv.addView(text(p.title, 15f, INK, true))
        tv.addView(text(p.type, 10f, MUTED))
        val tp = LinearLayout.LayoutParams(0, WRAP, 1f)
        tp.marginStart = dp(12)
        top.addView(tv, tp)
        if (p.badge != null) {
            val b = text(p.badge, 9f, Color.WHITE, true)
            b.background = shape(ROSE, 20)
            b.setPadding(dp(9), dp(4), dp(9), dp(4))
            top.addView(b)
        }
        return top
    }

    private fun featureList(items: List<String>): View {
        val box = vbox()
        items.forEachIndexed { i, f ->
            box.addView(text("✓  $f", 11f, Color.parseColor("#596865")), lp(MATCH, WRAP, if (i == 0) 0 else 7))
        }
        return box
    }

    private fun divider(): View {
        val v = View(this)
        v.setBackgroundColor(BORDER)
        return v
    }

    private fun productCard(p: Product): View {
        val c = card()
        if (p.special) c.background = shape(Color.parseColor("#fff8fa"), 20, Color.parseColor("#ffb3c2"), 1)
        c.addView(cardHeader(p))
        c.addView(divider(), lp(MATCH, dp(1), 14))
        c.addView(featureList(p.features), lp(MATCH, WRAP, 12))
        c.addView(divider(), lp(MATCH, dp(1), 12))
        c.addView(text("قیمت نهایی", 10f, MUTED), lp(MATCH, WRAP, 12))
        c.addView(text("${fa(p.price)} تومان", 21f, if (p.special) ROSE else PRIMARY, true))
        c.addView(
            btn("افزودن به سبد", if (p.special) ROSE else PRIMARY, Color.WHITE) { addToCart(p.id, "${p.title} — ${p.type}", p.price) },
            lp(MATCH, WRAP, 12)
        )
        return c
    }

    private fun v2boxCard(p: V2Plan): View {
        val c = card()
        if (p.special) c.background = shape(Color.parseColor("#fff8fa"), 20, Color.parseColor("#ffb3c2"), 1)
        c.addView(cardHeader(Product(p.id, p.title, "${p.type} • ${p.status}", 0, emptyList(), if (p.special) p.status else null, p.special, p.icon)))
        c.addView(divider(), lp(MATCH, dp(1), 14))
        c.addView(featureList(p.desc), lp(MATCH, WRAP, 12))

        var selected = 50
        val priceTv = text("${fa(selected * p.perGb)} تومان", 18f, if (p.special) ROSE else PRIMARY, true)

        val box = vbox(10)
        box.background = shape(SURFACE2, 12, BORDER, 1)
        box.addView(text("انتخاب حجم (گیگ)", 10f, MUTED))

        val chipRow = hbox()
        val chips = ArrayList<Pair<Int, TextView>>()
        fun paint() {
            chips.forEach { (gb, tv) ->
                val on = gb == selected
                tv.background = shape(if (on) PRIMARY else Color.WHITE, 9, Color.parseColor("#c7d7f0"), 1)
                tv.setTextColor(if (on) Color.WHITE else INK)
            }
            priceTv.text = "${fa(selected * p.perGb)} تومان"
        }
        gbOptions.forEach { gb ->
            val tv = text(fa(gb), 12f, INK, true)
            tv.gravity = Gravity.CENTER
            tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tv.setPadding(dp(14), dp(7), dp(14), dp(7))
            tv.setOnClickListener { selected = gb; paint() }
            val cp = LinearLayout.LayoutParams(WRAP, WRAP)
            cp.marginEnd = dp(6)
            chipRow.addView(tv, cp)
            chips.add(gb to tv)
        }
        paint()
        val hs = HorizontalScrollView(this)
        hs.isHorizontalScrollBarEnabled = false
        hs.addView(chipRow)
        box.addView(hs, lp(MATCH, WRAP, 8))
        box.addView(text("قیمت هر گیگ: ${fa(p.perGb)} تومان", 10f, MUTED), lp(MATCH, WRAP, 10))
        box.addView(priceTv, lp(MATCH, WRAP, 2))
        c.addView(box, lp(MATCH, WRAP, 12))

        c.addView(
            btn("افزودن به سبد", if (p.special) ROSE else PRIMARY, Color.WHITE) {
                addToCart("v2box_${p.id}_$selected", "📦 ${p.type} — ${fa(selected)} گیگ", selected * p.perGb)
            },
            lp(MATCH, WRAP, 12)
        )
        return c
    }

    // ---------- Support ----------
    private fun renderSupport() {
        val c = card()
        c.addView(text("پشتیبانی", 11f, Color.parseColor("#1450b8"), true))
        c.addView(text("💬 درخواست پشتیبانی", 24f, INK, true), lp(MATCH, WRAP, 8))
        c.addView(text("مشکل یا سؤالی دارید؟ از طریق تلگرام با پشتیبانی ۲۴ ساعته در ارتباط باشید.", 12f, MUTED), lp(MATCH, WRAP, 8))
        c.addView(btn("ورود مستقیم به پشتیبانی", PRIMARY, Color.WHITE) { openUrl(TELEGRAM) }, lp(MATCH, WRAP, 16))
        content.addView(c, lp(MATCH, WRAP, 0))
    }

    // ---------- Cart ----------
    private fun addToCart(id: String, title: String, price: Long) {
        val ex = cart[id]
        if (ex != null) ex.qty++ else cart[id] = CartItem(id, title, price, 1)
        cartBtn.text = "🛒 ${fa(cart.values.sumOf { it.qty })}"
        toast("محصول به سبد خرید اضافه شد")
    }

    private fun showCart() {
        if (cart.isEmpty()) {
            toast("سبد خرید خالی است")
            return
        }
        val total = cart.values.sumOf { it.price * it.qty }
        val lines = cart.values.joinToString("\n\n") { "• ${it.title} × ${fa(it.qty)}\n   ${fa(it.price * it.qty)} تومان" }
        val msg = "$lines\n\nمبلغ نهایی: ${fa(total)} تومان"

        AlertDialog.Builder(this)
            .setTitle("🛒 سبد خرید")
            .setMessage(msg)
            .setPositiveButton("ارسال سفارش در تلگرام") { _, _ ->
                val body = "سلام V2GURD 👋\n\nمی‌خواهم سفارش خود را ثبت کنم.\n\n" +
                        cart.values.joinToString("\n") { "• ${it.title} × ${it.qty} — ${fa(it.price * it.qty)}" } +
                        "\n\nمبلغ نهایی: ${fa(total)} تومان"
                openUrl("$TELEGRAM?text=" + URLEncoder.encode(body, "UTF-8"))
            }
            .setNeutralButton("خالی کردن") { _, _ ->
                cart.clear()
                cartBtn.text = "🛒 ۰"
            }
            .setNegativeButton("بستن", null)
            .show()
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            toast("باز کردن لینک ممکن نشد")
        }
    }
}
