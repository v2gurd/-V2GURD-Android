import React, { useState, useMemo } from "react";
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
  Modal,
  TextInput,
  Linking,
  Image,
} from "react-native";

/* ============ THEME (از CSS سایت) ============ */
const C = {
  primary: "#1d6fe8",
  primaryDark: "#1450b8",
  primarySoft: "#e6f0ff",
  rose: "#e11d48",
  roseSoft: "#ffe9ee",
  ink: "#0f1c33",
  muted: "#5d6e8c",
  bg: "#eef4ff",
  surface: "#ffffff",
  surface2: "#f6f9ff",
  border: "#dbe6f7",
  borderStrong: "#c7d7f0",
  cyan: "#22d3ee",
  heroA: "#0a1836",
  heroB: "#123a85",
  danger: "#c74848",
};

// اگر فونت Vazirmatn رو با expo-font لود کردی، اینو بکن "Vazirmatn"
const FONT = undefined;

const TELEGRAM = "https://t.me/Vtwoguard";
const CARD_NUMBER = "6219861841635526";

const IMG = {
  wireguard:
    "https://play-lh.googleusercontent.com/ggElzO1YS8fsVg5Y-lcsIV0slvKhKYiwHazffdKoAX-mLngJpiqoLXJ44gG2W05AypfyM6opCLcZQs4TU1Fd",
  v2box:
    "https://play-lh.googleusercontent.com/qQpPZLi7FtvauCecbDnEbFe8JScW1fOP-EuIkUorFDsFFTa3luf3w48PKXjnaBf3kb4QNFJYBTtWxhZctGy1xyA",
  jumpjump:
    "https://play-lh.googleusercontent.com/bTiMcJCnGw0QTnhHxwHffOUYvha9BUz5FhK7JAogvO8P-J_qKiJaqnyDSO1WswuqSMFxHDlu9tE0nwicfYLqWA",
};

/* ============ DATA (از سایت) ============ */
const fa = (v) => String(v).replace(/\d/g, (d) => "۰۱۲۳۴۵۶۷۸۹"[d]);
const money = (v) => fa(Number(v || 0).toLocaleString("en-US"));

const WG = [
  { id: "wg10", gb: 10, price: 149000, special: true },
  { id: "wg20", gb: 20, price: 279000 },
  { id: "wg40", gb: 40, price: 499000 },
  { id: "wg60", gb: 60, price: 699000 },
  { id: "wg100", gb: 100, price: 999000 },
].map((p) => ({
  ...p,
  icon: "🟣",
  title: `وایرگارد ${fa(p.gb)} گیگ`,
  type: "تک‌کاربره • ۳۰ روزه",
  badge: p.special ? "پیشنهاد ویژه" : null,
  features: ["تک‌کاربره و تک‌سرور", "IP ثابت", "سرعت و پایداری بالا", "اعتبار ۳۰ روزه", "پشتیبانی سریع"],
}));

const JJ = [
  { id: "jj1", devices: 1, months: 1, price: 700000 },
  { id: "jj2", devices: 2, months: 1, price: 999000 },
  { id: "jj6", devices: 2, months: 6, price: 4600000, special: true, status: "پیشنهاد ویژه" },
  { id: "jj12", devices: 2, months: 12, price: 5500000, special: true, status: "بهترین ارزش خرید" },
].map((p) => ({
  ...p,
  icon: "🚀",
  title: "JumpJump",
  type: `${fa(p.devices)} دستگاه • ${fa(p.months)} ماه`,
  badge: p.status || null,
  features: [`${fa(p.devices)} دستگاه`, `${fa(p.months)} ماه اعتبار`, "سرویس چند دستگاه", "پشتیبانی V2GURD", "مناسب استفاده روزمره"],
}));

const GB_OPTIONS = [50, 100, 150, 200, 250, 300, 400, 500];

const V2BOX = [
  { id: "economicPlan", type: "اقتصادی", icon: "💠", title: "سرور اقتصادی", status: "موجود", perGB: 2000, desc: ["مناسب مصرف روزمره", "قیمت اقتصادی", "حجم قابل انتخاب"] },
  { id: "normalPlan", type: "معمولی", icon: "⚡", title: "سرور معمولی", status: "موجود", perGB: 1500, desc: ["سرعت مناسب", "اتصال پایدار", "مناسب استفاده روزمره"] },
  { id: "cdnPlan", type: "CDN", icon: "🚀", title: "CDN", status: "ویژه", perGB: 1500, special: true, desc: ["اتصال سریع", "مناسب مصرف بالا", "مسیر بهینه"] },
  { id: "tunnelPlan", type: "Tunnel", icon: "🔗", title: "Tunnel", status: "موجود", perGB: 2500, desc: ["مسیر تونلی", "مناسب اتصال پایدار", "سرویس پرسرعت"] },
  { id: "proPlan", type: "حرفه‌ای", icon: "👑", title: "سرور حرفه‌ای", status: "حرفه‌ای", perGB: 3000, special: true, desc: ["مناسب مصرف سنگین", "سرعت بالا", "سرویس حرفه‌ای V2GURD"] },
];

const CATEGORIES = [
  { id: "wireguard", icon: "🟣", title: "WireGuard", desc: "تک‌کاربره • IP ثابت • سرعت بالا", image: IMG.wireguard },
  { id: "v2box", icon: "📦", title: "V2Box", desc: "CDN • حرفه‌ای • اقتصادی • Tunnel", image: IMG.v2box },
  { id: "jumpjump", icon: "🚀", title: "JumpJump", desc: "سرویس چند دستگاه", image: IMG.jumpjump },
];

const TRUST = [
  { icon: "⚡", t: "سرعت بالا", s: "مسیرهای بهینه" },
  { icon: "🛡️", t: "امنیت", s: "اتصال محافظت‌شده" },
  { icon: "◉", t: "پایداری", s: "اتصال مناسب و پایدار" },
  { icon: "۲۴", t: "پشتیبانی ۲۴/۷", s: "از طریق تلگرام" },
];

/* ============ SMALL UI ============ */
function Btn({ title, onPress, variant = "primary", style, small }) {
  const v = {
    primary: { bg: C.primary, edge: C.primaryDark, color: "#fff" },
    rose: { bg: C.rose, edge: "#9f1239", color: "#fff" },
    dark: { bg: C.ink, edge: "#050b18", color: "#fff" },
    outline: { bg: "#fff", edge: "#c2d4f2", color: C.primaryDark, border: C.borderStrong },
  }[variant];
  return (
    <TouchableOpacity
      activeOpacity={0.85}
      onPress={onPress}
      style={[
        s.btn,
        small && { paddingVertical: 8, paddingHorizontal: 12 },
        {
          backgroundColor: v.bg,
          borderBottomColor: v.edge,
          borderColor: v.border || v.bg,
        },
        style,
      ]}
    >
      <Text style={[s.btnText, { color: v.color }, small && { fontSize: 11 }]}>{title}</Text>
    </TouchableOpacity>
  );
}

function SectionHead({ kicker, title }) {
  return (
    <View style={{ marginBottom: 14 }}>
      <Text style={s.kicker}>{kicker}</Text>
      <Text style={s.sectionTitle}>{title}</Text>
    </View>
  );
}

function ProductCard({ p, onAdd }) {
  return (
    <View style={[s.card, p.special && s.cardSpecial]}>
      {p.badge ? (
        <View style={s.badge}>
          <Text style={s.badgeText}>{p.badge}</Text>
        </View>
      ) : null}

      <View style={s.pTop}>
        <View style={[s.pIcon, p.special && { backgroundColor: C.rose }]}>
          <Text style={{ fontSize: 20 }}>{p.icon}</Text>
        </View>
        <View style={{ flex: 1 }}>
          <Text style={s.pTitle}>{p.title}</Text>
          <Text style={s.pType}>{p.type}</Text>
        </View>
      </View>

      <View style={s.features}>
        {p.features.map((f) => (
          <View key={f} style={s.featRow}>
            <Text style={s.featText}>{f}</Text>
            <Text style={s.tick}>✓</Text>
          </View>
        ))}
      </View>

      <View style={s.priceRow}>
        <View>
          <Text style={s.priceLabel}>قیمت نهایی</Text>
          <Text style={[s.price, p.special && { color: C.rose }]}>
            {money(p.price)} <Text style={s.priceUnit}>تومان</Text>
          </Text>
        </View>
      </View>

      <Btn title="افزودن به سبد" onPress={() => onAdd(p)} variant={p.special ? "rose" : "primary"} />
    </View>
  );
}

function V2BoxCard({ p, onAdd }) {
  const [gb, setGb] = useState(50);
  return (
    <View style={[s.card, p.special && s.cardSpecial]}>
      {p.special ? (
        <View style={s.badge}>
          <Text style={s.badgeText}>{p.status}</Text>
        </View>
      ) : null}

      <View style={s.pTop}>
        <View style={[s.pIcon, p.special && { backgroundColor: C.rose }]}>
          <Text style={{ fontSize: 20 }}>{p.icon}</Text>
        </View>
        <View style={{ flex: 1 }}>
          <Text style={s.pTitle}>{p.title}</Text>
          <Text style={s.pType}>
            {p.type} • {p.status}
          </Text>
        </View>
      </View>

      <View style={s.features}>
        {p.desc.map((f) => (
          <View key={f} style={s.featRow}>
            <Text style={s.featText}>{f}</Text>
            <Text style={s.tick}>✓</Text>
          </View>
        ))}
      </View>

      <View style={s.volumeBox}>
        <Text style={s.priceLabel}>انتخاب حجم</Text>
        <View style={s.chips}>
          {GB_OPTIONS.map((g) => (
            <TouchableOpacity key={g} onPress={() => setGb(g)} style={[s.chip, gb === g && s.chipOn]}>
              <Text style={[s.chipText, gb === g && { color: "#fff" }]}>{fa(g)}</Text>
            </TouchableOpacity>
          ))}
        </View>
        <View style={s.volTotal}>
          <Text style={s.priceLabel}>قیمت هر گیگ: {money(p.perGB)}</Text>
          <Text style={s.volPrice}>{money(gb * p.perGB)}</Text>
        </View>
      </View>

      <Btn
        title="افزودن به سبد"
        variant={p.special ? "rose" : "primary"}
        onPress={() =>
          onAdd({
            id: `v2box_${p.id}_${gb}`,
            title: `📦 ${p.type} — ${fa(gb)} گیگ`,
            type: `${fa(gb)} گیگ • ${money(p.perGB)} تومان/گیگ`,
            price: gb * p.perGB,
          })
        }
      />
    </View>
  );
}

/* ============ APP ============ */
export default function App() {
  const [page, setPage] = useState("home");
  const [cart, setCart] = useState([]);
  const [cartOpen, setCartOpen] = useState(false);
  const [coupon, setCoupon] = useState("");
  const [discount, setDiscount] = useState(0);
  const [toast, setToast] = useState("");

  const showToast = (m) => {
    setToast(m);
    setTimeout(() => setToast(""), 1800);
  };

  const addToCart = (p) => {
    setCart((prev) => {
      const ex = prev.find((i) => i.id === p.id);
      if (ex) return prev.map((i) => (i.id === p.id ? { ...i, qty: i.qty + 1 } : i));
      return [...prev, { id: p.id, title: p.title, type: p.type, price: p.price, qty: 1 }];
    });
    showToast("محصول به سبد خرید اضافه شد");
  };

  const changeQty = (id, d) =>
    setCart((prev) =>
      prev.map((i) => (i.id === id ? { ...i, qty: i.qty + d } : i)).filter((i) => i.qty > 0)
    );

  const count = useMemo(() => cart.reduce((a, i) => a + i.qty, 0), [cart]);
  const subtotal = useMemo(() => cart.reduce((a, i) => a + i.price * i.qty, 0), [cart]);
  const discAmount = (subtotal * discount) / 100;
  const total = Math.max(0, subtotal - discAmount);

  const applyCoupon = () => {
    if (coupon.trim().toUpperCase() === "V2G10") {
      setDiscount(10);
      showToast("کد تخفیف ۱۰٪ فعال شد");
    } else {
      setDiscount(0);
      showToast("کد تخفیف معتبر نیست");
    }
  };

  const sendReceipt = () => {
    const items = cart.map((i) => `• ${i.title} × ${i.qty} — ${money(i.price * i.qty)}`).join("\n");
    const msg = `سلام V2GURD 👋\n\nمی‌خواهم سفارش خود را ثبت کنم.\n\nمحصولات:\n${items}\n\nمبلغ نهایی:\n${money(total)} تومان`;
    Linking.openURL(`${TELEGRAM}?text=${encodeURIComponent(msg)}`);
  };

  const renderHome = () => (
    <>
      {/* Hero */}
      <View style={s.hero}>
        <View style={s.eyebrow}>
          <View style={s.eyebrowDot} />
          <Text style={s.eyebrowText}>فروشگاه رسمی V2GURD</Text>
        </View>
        <Text style={s.h1}>
          اینترنت امن،{"\n"}
          <Text style={{ color: C.primary }}>سریع و پایدار</Text>
        </Text>
        <Text style={s.heroDesc}>
          سرویس موردنیازت را انتخاب کن، حجم مناسب را مشخص کن و سفارش خودت را به‌سادگی ثبت کن.
        </Text>
        <View style={s.heroBtns}>
          <Btn title="مشاهده سرویس‌ها" onPress={() => setPage("wireguard")} style={{ flex: 1 }} />
          <Btn title="🛒 سبد خرید" onPress={() => setCartOpen(true)} variant="outline" style={{ flex: 1 }} />
        </View>
        <Btn title="✈️ پشتیبانی تلگرام" onPress={() => Linking.openURL(TELEGRAM)} variant="dark" style={{ marginTop: 10 }} />
      </View>

      {/* Network card */}
      <View style={s.netCard}>
        <View style={s.netHead}>
          <View style={s.live}>
            <View style={s.liveDot} />
            <Text style={s.liveText}>آنلاین</Text>
          </View>
          <Text style={s.netTitle}>وضعیت شبکه V2GURD</Text>
        </View>
        <View style={s.netBox}>
          <Text style={s.netSub}>مسیر اتصال فعلی</Text>
          <View style={s.route}>
            {[
              ["📱", "دستگاه شما"],
              ["🛡️", "V2GURD"],
              ["🌐", "مقصد"],
            ].map(([i, t], idx) => (
              <React.Fragment key={t}>
                {idx > 0 && <Text style={s.arrow}>←</Text>}
                <View style={s.node}>
                  <Text style={{ fontSize: 16 }}>{i}</Text>
                  <Text style={s.nodeText}>{t}</Text>
                </View>
              </React.Fragment>
            ))}
          </View>
          <View style={s.stats}>
            {[
              ["۳۸ms", "Ping"],
              ["۰٪", "Packet Loss"],
              ["پایدار", "Route"],
            ].map(([v, l]) => (
              <View key={l} style={s.stat}>
                <Text style={s.statV}>{v}</Text>
                <Text style={s.statL}>{l}</Text>
              </View>
            ))}
          </View>
        </View>
      </View>

      {/* Trust */}
      <View style={s.trust}>
        {TRUST.map((t) => (
          <View key={t.t} style={s.trustItem}>
            <View style={s.trustIcon}>
              <Text style={{ fontSize: 15, color: C.primary }}>{t.icon}</Text>
            </View>
            <View style={{ flex: 1 }}>
              <Text style={s.trustT}>{t.t}</Text>
              <Text style={s.trustS}>{t.s}</Text>
            </View>
          </View>
        ))}
      </View>

      {/* Featured */}
      <View style={s.featured}>
        <Text style={s.featLabel}>پیشنهاد ویژه فروشگاه</Text>
        <Text style={s.featTitle}>🟣 وایرگارد تک‌کاربره</Text>
        <Text style={s.featDesc}>
          سرویس تک‌کاربره با IP ثابت، سرعت و پایداری بالا و اعتبار ۳۰ روزه.
        </Text>
        <View style={s.featTags}>
          {["تک‌کاربره", "IP ثابت", "۳۰ روز اعتبار", "پشتیبانی سریع"].map((t) => (
            <View key={t} style={s.featTag}>
              <Text style={s.featTagText}>{t}</Text>
            </View>
          ))}
        </View>
        <Text style={s.featFrom}>شروع قیمت از</Text>
        <Text style={s.featPrice}>۱۴۹,۰۰۰ تومان</Text>
        <Btn title="افزودن به سبد" onPress={() => addToCart(WG[0])} />
      </View>

      {/* Categories */}
      <SectionHead kicker="دسته‌بندی سرویس‌ها" title="سرویس مناسب خودت را انتخاب کن" />
      {CATEGORIES.map((c) => (
        <TouchableOpacity key={c.id} activeOpacity={0.9} style={s.catCard} onPress={() => setPage(c.id)}>
          <Image source={{ uri: c.image }} style={s.catImg} resizeMode="cover" />
          <View style={{ padding: 16 }}>
            <View style={s.catIcon}>
              <Text style={{ fontSize: 18 }}>{c.icon}</Text>
            </View>
            <Text style={s.catTitle}>{c.title}</Text>
            <Text style={s.catDesc}>{c.desc}</Text>
            <View style={s.catFoot}>
              <Text style={s.catFootText}>←</Text>
              <Text style={s.catFootText}>مشاهده سرویس‌ها</Text>
            </View>
          </View>
        </TouchableOpacity>
      ))}

      {/* Steps */}
      <SectionHead kicker="مراحل سفارش" title="خرید در چهار مرحله" />
      <View style={s.steps}>
        {[
          ["🛍️", "انتخاب سرویس", "سرویس مناسب نیازت را انتخاب کن."],
          ["🛒", "افزودن به سبد", "پلن و حجم موردنظر را به سبد اضافه کن."],
          ["💳", "پرداخت", "مبلغ نهایی را به کارت اعلام‌شده انتقال بده."],
          ["✈️", "دریافت سرویس", "رسید را در تلگرام ارسال کن."],
        ].map(([i, t, d]) => (
          <View key={t} style={s.step}>
            <Text style={{ fontSize: 22, marginBottom: 6 }}>{i}</Text>
            <Text style={s.stepT}>{t}</Text>
            <Text style={s.stepD}>{d}</Text>
          </View>
        ))}
      </View>
    </>
  );

  const renderCategoryPage = (id) => {
    const meta = {
      wireguard: { tag: "سرویس تک‌کاربره", h: "🟣 وایرگارد", p: "اتصال امن و پایدار با IP ثابت و اعتبار ۳۰ روزه.", img: IMG.wireguard },
      v2box: { tag: "سرویس حجمی", h: "📦 V2Box", p: "سرویس‌های اقتصادی، CDN، Tunnel و حرفه‌ای با حجم قابل انتخاب.", img: IMG.v2box },
      jumpjump: { tag: "سرویس چند دستگاه", h: "🚀 JumpJump", p: "مناسب استفاده همزمان روی چند دستگاه.", img: IMG.jumpjump },
    }[id];
    return (
      <>
        <View style={s.innerHero}>
          <View style={s.eyebrow}>
            <View style={s.eyebrowDot} />
            <Text style={s.eyebrowText}>{meta.tag}</Text>
          </View>
          <Text style={s.innerH1}>{meta.h}</Text>
          <Text style={s.innerP}>{meta.p}</Text>
          <Image source={{ uri: meta.img }} style={s.innerImg} resizeMode="cover" />
        </View>
        {id === "wireguard" && WG.map((p) => <ProductCard key={p.id} p={p} onAdd={addToCart} />)}
        {id === "jumpjump" && JJ.map((p) => <ProductCard key={p.id} p={p} onAdd={addToCart} />)}
        {id === "v2box" && V2BOX.map((p) => <V2BoxCard key={p.id} p={p} onAdd={addToCart} />)}
      </>
    );
  };

  const renderSupport = () => (
    <View style={s.supportBox}>
      <Text style={{ fontSize: 56, textAlign: "center" }}>💬</Text>
      <Text style={s.supportH}>درخواست پشتیبانی</Text>
      <Text style={s.supportP}>مشکل یا سؤالی دارید؟ از طریق تلگرام با پشتیبانی ۲۴ ساعته در ارتباط باشید.</Text>
      <Btn title="ورود مستقیم به پشتیبانی" onPress={() => Linking.openURL(TELEGRAM)} />
    </View>
  );

  return (
    <SafeAreaView style={s.container}>
      <StatusBar barStyle="dark-content" backgroundColor="#fff" />

      {/* Header */}
      <View style={s.header}>
        <TouchableOpacity style={s.iconBtn} onPress={() => setCartOpen(true)}>
          <Text style={{ fontSize: 18 }}>🛒</Text>
          {count > 0 && (
            <View style={s.cartCount}>
              <Text style={s.cartCountText}>{fa(count)}</Text>
            </View>
          )}
        </TouchableOpacity>
        <TouchableOpacity style={s.brand} onPress={() => setPage("home")}>
          <Text style={s.brandText}>
            V2<Text style={{ color: C.primary }}>GURD</Text>
          </Text>
          <View style={s.brandMark}>
            <Text style={s.brandMarkText}>V2</Text>
          </View>
        </TouchableOpacity>
      </View>

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={s.content}>
        {page === "home" && renderHome()}
        {["wireguard", "v2box", "jumpjump"].includes(page) && renderCategoryPage(page)}
        {page === "support" && renderSupport()}
        <View style={{ height: 90 }} />
      </ScrollView>

      {toast ? (
        <View style={s.toast}>
          <Text style={s.toastText}>{toast}</Text>
        </View>
      ) : null}

      {/* Bottom nav */}
      <View style={s.nav}>
        {[
          ["home", "⌂", "خانه"],
          ["wireguard", "🟣", "WireGuard"],
          ["v2box", "📦", "V2Box"],
          ["jumpjump", "🚀", "JumpJump"],
          ["support", "💬", "پشتیبانی"],
        ].map(([id, ic, label]) => (
          <TouchableOpacity key={id} style={s.navItem} onPress={() => setPage(id)}>
            <Text style={[s.navIcon, page === id && { opacity: 1 }]}>{ic}</Text>
            <Text style={[s.navText, page === id && { color: C.primary }]}>{label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Cart */}
      <Modal visible={cartOpen} animationType="slide" onRequestClose={() => setCartOpen(false)}>
        <SafeAreaView style={{ flex: 1, backgroundColor: "#eaf2ff" }}>
          <View style={s.cartHead}>
            <TouchableOpacity onPress={() => setCartOpen(false)} style={s.cartClose}>
              <Text style={{ color: "#fff", fontSize: 18 }}>×</Text>
            </TouchableOpacity>
            <Text style={s.cartTitle}>🛒 سبد خرید</Text>
          </View>

          <ScrollView contentContainerStyle={{ padding: 15 }}>
            {cart.length === 0 ? (
              <View style={{ alignItems: "center", marginTop: 80 }}>
                <Text style={{ fontSize: 40 }}>🛒</Text>
                <Text style={s.cartEmpty}>سبد خرید خالی است</Text>
              </View>
            ) : (
              cart.map((i) => (
                <View key={i.id} style={s.cartItem}>
                  <View style={s.cartItemTop}>
                    <Text style={s.cartItemPrice}>{money(i.price)}</Text>
                    <View style={{ flex: 1 }}>
                      <Text style={s.cartItemName}>{i.title}</Text>
                      <Text style={s.cartItemMeta}>{i.type}</Text>
                    </View>
                  </View>
                  <View style={s.qtyRow}>
                    <TouchableOpacity onPress={() => changeQty(i.id, -i.qty)}>
                      <Text style={{ color: C.danger, fontSize: 11 }}>حذف محصول</Text>
                    </TouchableOpacity>
                    <View style={s.qtyCtl}>
                      <TouchableOpacity style={s.qtyBtn} onPress={() => changeQty(i.id, -1)}>
                        <Text style={s.qtyBtnText}>−</Text>
                      </TouchableOpacity>
                      <Text style={s.qty}>{fa(i.qty)}</Text>
                      <TouchableOpacity style={s.qtyBtn} onPress={() => changeQty(i.id, 1)}>
                        <Text style={s.qtyBtnText}>+</Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                </View>
              ))
            )}
          </ScrollView>

          <View style={s.cartFoot}>
            <View style={s.couponRow}>
              <TouchableOpacity style={s.couponBtn} onPress={applyCoupon}>
                <Text style={{ color: "#fff", fontSize: 11, fontWeight: "800" }}>اعمال</Text>
              </TouchableOpacity>
              <TextInput
                value={coupon}
                onChangeText={setCoupon}
                placeholder="کد تخفیف"
                placeholderTextColor="#8b9895"
                autoCapitalize="characters"
                style={s.couponInput}
              />
            </View>
            <View style={s.sumRow}>
              <Text style={s.sumV}>{money(discAmount)} تومان</Text>
              <Text style={s.sumL}>تخفیف</Text>
            </View>
            <View style={s.sumRow}>
              <Text style={s.sumV}>{money(subtotal)} تومان</Text>
              <Text style={s.sumL}>مبلغ سفارش</Text>
            </View>
            <View style={s.totalRow}>
              <Text style={s.totalV}>{money(total)} تومان</Text>
              <Text style={s.totalL}>مبلغ نهایی</Text>
            </View>
            <Text style={s.cardNote}>
              پرداخت با کارت‌به‌کارت: {CARD_NUMBER} (نامی — بانک بلو)
            </Text>
            <Btn
              title="ادامه و ارسال رسید در تلگرام"
              onPress={() => (cart.length ? sendReceipt() : showToast("سبد خرید شما خالی است"))}
              style={{ marginTop: 10 }}
            />
          </View>
        </SafeAreaView>
      </Modal>
    </SafeAreaView>
  );
}

/* ============ STYLES ============ */
const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: C.bg },
  content: { paddingHorizontal: 16, paddingTop: 14 },

  header: {
    height: 62,
    backgroundColor: "rgba(255,255,255,0.96)",
    borderBottomWidth: 1,
    borderBottomColor: C.border,
    flexDirection: "row-reverse",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
  },
  brand: { flexDirection: "row", alignItems: "center", gap: 10 },
  brandMark: {
    width: 36, height: 36, borderRadius: 11, backgroundColor: C.primary,
    alignItems: "center", justifyContent: "center",
  },
  brandMarkText: { color: "#fff", fontWeight: "900", fontSize: 15, fontFamily: FONT },
  brandText: { color: C.ink, fontSize: 20, fontWeight: "900", fontFamily: FONT },
  iconBtn: {
    width: 42, height: 42, borderRadius: 11, backgroundColor: "#fff",
    borderWidth: 1, borderColor: C.border, borderBottomWidth: 3, borderBottomColor: "#c2d4f2",
    alignItems: "center", justifyContent: "center",
  },
  cartCount: {
    position: "absolute", top: -6, left: -5, minWidth: 20, height: 20, paddingHorizontal: 5,
    borderRadius: 10, backgroundColor: C.rose, borderWidth: 2, borderColor: "#fff",
    alignItems: "center", justifyContent: "center",
  },
  cartCountText: { color: "#fff", fontSize: 10, fontWeight: "800" },

  btn: {
    borderRadius: 10, paddingVertical: 12, paddingHorizontal: 18, alignItems: "center",
    justifyContent: "center", borderWidth: 1, borderBottomWidth: 3,
  },
  btnText: { fontSize: 13, fontWeight: "800", fontFamily: FONT },

  eyebrow: {
    alignSelf: "flex-end", flexDirection: "row-reverse", alignItems: "center", gap: 8,
    backgroundColor: C.primarySoft, paddingVertical: 6, paddingHorizontal: 12,
    borderRadius: 30, marginBottom: 14,
  },
  eyebrowDot: { width: 7, height: 7, borderRadius: 4, backgroundColor: C.primary },
  eyebrowText: { color: C.primaryDark, fontSize: 11, fontWeight: "800", fontFamily: FONT },

  hero: { marginBottom: 18 },
  h1: { color: C.ink, fontSize: 36, lineHeight: 48, fontWeight: "900", textAlign: "right", fontFamily: FONT },
  heroDesc: { color: C.muted, fontSize: 13, lineHeight: 24, textAlign: "right", marginTop: 10, marginBottom: 18, fontFamily: FONT },
  heroBtns: { flexDirection: "row-reverse", gap: 10 },

  netCard: {
    backgroundColor: C.heroA, borderRadius: 28, padding: 20, marginBottom: 18,
    borderWidth: 1, borderColor: "rgba(96,165,250,0.35)",
  },
  netHead: { flexDirection: "row-reverse", justifyContent: "space-between", alignItems: "center", marginBottom: 16 },
  netTitle: { color: "#fff", fontSize: 15, fontWeight: "900", fontFamily: FONT },
  live: {
    flexDirection: "row-reverse", alignItems: "center", gap: 6, paddingVertical: 4, paddingHorizontal: 10,
    borderRadius: 20, borderWidth: 1, borderColor: "rgba(34,211,238,0.5)", backgroundColor: "rgba(34,211,238,0.12)",
  },
  liveDot: { width: 7, height: 7, borderRadius: 4, backgroundColor: C.cyan },
  liveText: { color: "#7fe7ff", fontSize: 10, fontWeight: "800", fontFamily: FONT },
  netBox: {
    backgroundColor: "rgba(255,255,255,0.05)", borderRadius: 16, padding: 14,
    borderWidth: 1, borderColor: "rgba(147,197,253,0.22)",
  },
  netSub: { color: "#9bb8e6", fontSize: 11, textAlign: "right", marginBottom: 12, fontFamily: FONT },
  route: { flexDirection: "row-reverse", alignItems: "center", gap: 6, marginBottom: 14 },
  node: {
    flex: 1, minHeight: 58, borderRadius: 13, alignItems: "center", justifyContent: "center",
    backgroundColor: "rgba(255,255,255,0.07)", borderWidth: 1, borderColor: "rgba(96,165,250,0.35)",
  },
  nodeText: { color: "#9bb8e6", fontSize: 9, marginTop: 2, fontFamily: FONT },
  arrow: { color: C.cyan, fontWeight: "900" },
  stats: { flexDirection: "row-reverse", gap: 8 },
  stat: {
    flex: 1, paddingVertical: 10, borderRadius: 11, alignItems: "center",
    backgroundColor: "rgba(255,255,255,0.07)", borderWidth: 1, borderColor: "rgba(96,165,250,0.28)",
  },
  statV: { color: "#7fe7ff", fontSize: 14, fontWeight: "900", fontFamily: FONT },
  statL: { color: "#9bb8e6", fontSize: 9, marginTop: 2, fontFamily: FONT },

  trust: {
    backgroundColor: "#fff", borderRadius: 18, borderWidth: 1, borderColor: C.border,
    flexDirection: "row-reverse", flexWrap: "wrap", marginBottom: 24, overflow: "hidden",
  },
  trustItem: {
    width: "50%", padding: 14, flexDirection: "row-reverse", alignItems: "center", gap: 10,
    borderWidth: 0.5, borderColor: C.border,
  },
  trustIcon: {
    width: 38, height: 38, borderRadius: 11, backgroundColor: C.primarySoft,
    alignItems: "center", justifyContent: "center",
  },
  trustT: { color: C.ink, fontSize: 12, fontWeight: "800", textAlign: "right", fontFamily: FONT },
  trustS: { color: C.muted, fontSize: 9, textAlign: "right", marginTop: 2, fontFamily: FONT },

  featured: { backgroundColor: C.heroB, borderRadius: 28, padding: 22, marginBottom: 26 },
  featLabel: { color: "#7fe7ff", fontSize: 10, fontWeight: "800", textAlign: "right", marginBottom: 6, fontFamily: FONT },
  featTitle: { color: "#fff", fontSize: 24, fontWeight: "900", textAlign: "right", marginBottom: 8, fontFamily: FONT },
  featDesc: { color: "#c5d6f2", fontSize: 12, lineHeight: 21, textAlign: "right", marginBottom: 14, fontFamily: FONT },
  featTags: { flexDirection: "row-reverse", flexWrap: "wrap", gap: 7, marginBottom: 16 },
  featTag: {
    paddingVertical: 4, paddingHorizontal: 10, borderRadius: 20,
    backgroundColor: "rgba(255,255,255,0.08)", borderWidth: 1, borderColor: "rgba(96,165,250,0.35)",
  },
  featTagText: { color: "#d8e6ff", fontSize: 10, fontFamily: FONT },
  featFrom: { color: "#8fb0e6", fontSize: 10, textAlign: "left", fontFamily: FONT },
  featPrice: { color: "#fff", fontSize: 28, fontWeight: "900", textAlign: "left", marginBottom: 12, fontFamily: FONT },

  kicker: { color: C.primary, fontSize: 11, fontWeight: "900", textAlign: "right", marginBottom: 4, fontFamily: FONT },
  sectionTitle: { color: C.ink, fontSize: 22, fontWeight: "900", textAlign: "right", fontFamily: FONT },

  catCard: {
    backgroundColor: "#fff", borderRadius: 20, borderWidth: 1, borderColor: C.border,
    overflow: "hidden", marginBottom: 14,
  },
  catImg: { width: "100%", height: 150, backgroundColor: "#f0f4f3" },
  catIcon: {
    width: 42, height: 42, borderRadius: 13, backgroundColor: C.primary,
    alignItems: "center", justifyContent: "center", alignSelf: "flex-end", marginBottom: 8,
  },
  catTitle: { color: C.ink, fontSize: 17, fontWeight: "900", textAlign: "right", fontFamily: FONT },
  catDesc: { color: C.muted, fontSize: 11, textAlign: "right", marginTop: 3, marginBottom: 12, fontFamily: FONT },
  catFoot: {
    flexDirection: "row-reverse", justifyContent: "space-between",
    borderTopWidth: 1, borderTopColor: C.border, paddingTop: 12,
  },
  catFootText: { color: C.primary, fontSize: 11, fontWeight: "800", fontFamily: FONT },

  steps: { flexDirection: "row-reverse", flexWrap: "wrap", gap: 10, marginTop: 4 },
  step: {
    width: "48%", backgroundColor: "#fff", borderRadius: 16, borderWidth: 1, borderColor: C.border,
    padding: 16,
  },
  stepT: { color: C.ink, fontSize: 13, fontWeight: "800", textAlign: "right", fontFamily: FONT },
  stepD: { color: C.muted, fontSize: 10, lineHeight: 17, textAlign: "right", marginTop: 4, fontFamily: FONT },

  innerHero: {
    backgroundColor: "#fff", borderRadius: 26, borderWidth: 1, borderColor: C.border,
    padding: 20, marginBottom: 16,
  },
  innerH1: { color: C.ink, fontSize: 30, fontWeight: "900", textAlign: "right", fontFamily: FONT },
  innerP: { color: C.muted, fontSize: 12, lineHeight: 21, textAlign: "right", marginTop: 6, marginBottom: 14, fontFamily: FONT },
  innerImg: { width: "100%", height: 150, borderRadius: 17, backgroundColor: "#f1f4f3" },

  card: {
    backgroundColor: "#fff", borderRadius: 20, borderWidth: 1, borderColor: "#d3e2f8",
    padding: 18, marginBottom: 14,
  },
  cardSpecial: { borderColor: "#ffb3c2", backgroundColor: "#fff8fa" },
  badge: {
    position: "absolute", top: 14, left: 14, backgroundColor: C.rose, borderRadius: 20,
    paddingVertical: 4, paddingHorizontal: 9, zIndex: 2,
  },
  badgeText: { color: "#fff", fontSize: 9, fontWeight: "900", fontFamily: FONT },
  pTop: { flexDirection: "row-reverse", alignItems: "center", gap: 12, marginBottom: 14, paddingLeft: 70 },
  pIcon: {
    width: 46, height: 46, borderRadius: 13, backgroundColor: C.primary,
    alignItems: "center", justifyContent: "center",
  },
  pTitle: { color: C.ink, fontSize: 15, fontWeight: "900", textAlign: "right", fontFamily: FONT },
  pType: { color: C.muted, fontSize: 10, textAlign: "right", marginTop: 2, fontFamily: FONT },
  features: {
    paddingVertical: 12, borderTopWidth: 1, borderBottomWidth: 1, borderColor: C.border,
    marginBottom: 14, gap: 7,
  },
  featRow: { flexDirection: "row-reverse", alignItems: "center", gap: 7 },
  featText: { color: "#596865", fontSize: 11, textAlign: "right", fontFamily: FONT },
  tick: { color: C.cyan, fontWeight: "900", fontSize: 12 },
  priceRow: { flexDirection: "row-reverse", marginBottom: 12 },
  priceLabel: { color: C.muted, fontSize: 10, textAlign: "right", fontFamily: FONT },
  price: { color: C.primary, fontSize: 21, fontWeight: "900", textAlign: "right", fontFamily: FONT },
  priceUnit: { color: C.muted, fontSize: 10, fontWeight: "400" },

  volumeBox: {
    backgroundColor: C.surface2, borderWidth: 1, borderColor: C.border,
    borderRadius: 12, padding: 10, marginBottom: 12,
  },
  chips: { flexDirection: "row-reverse", flexWrap: "wrap", gap: 6, marginTop: 8 },
  chip: {
    paddingVertical: 6, paddingHorizontal: 12, borderRadius: 9, backgroundColor: "#fff",
    borderWidth: 1, borderColor: C.borderStrong,
  },
  chipOn: { backgroundColor: C.primary, borderColor: C.primary },
  chipText: { color: C.ink, fontSize: 11, fontWeight: "700", fontFamily: FONT },
  volTotal: { flexDirection: "row-reverse", justifyContent: "space-between", alignItems: "center", marginTop: 10 },
  volPrice: { color: C.primary, fontSize: 15, fontWeight: "900", fontFamily: FONT },

  supportBox: {
    backgroundColor: "#fff", borderRadius: 24, borderWidth: 1, borderColor: "#cfe0f7",
    padding: 24, gap: 12,
  },
  supportH: { color: C.ink, fontSize: 24, fontWeight: "900", textAlign: "center", fontFamily: FONT },
  supportP: { color: C.muted, fontSize: 12, lineHeight: 21, textAlign: "center", marginBottom: 6, fontFamily: FONT },

  nav: {
    position: "absolute", bottom: 0, left: 0, right: 0, height: 64, backgroundColor: "rgba(255,255,255,0.97)",
    borderTopWidth: 1, borderTopColor: C.border, flexDirection: "row-reverse",
    justifyContent: "space-around", alignItems: "center",
  },
  navItem: { alignItems: "center", minWidth: 58 },
  navIcon: { fontSize: 18, opacity: 0.55, marginBottom: 1 },
  navText: { color: C.muted, fontSize: 9, fontWeight: "700", fontFamily: FONT },

  toast: {
    position: "absolute", bottom: 78, alignSelf: "center", backgroundColor: C.ink,
    borderRadius: 11, paddingVertical: 10, paddingHorizontal: 16,
  },
  toastText: { color: "#fff", fontSize: 11, fontFamily: FONT },

  cartHead: {
    height: 66, backgroundColor: C.heroA, flexDirection: "row-reverse", alignItems: "center",
    justifyContent: "space-between", paddingHorizontal: 18, borderBottomWidth: 3, borderBottomColor: C.cyan,
  },
  cartTitle: { color: "#fff", fontSize: 16, fontWeight: "900", fontFamily: FONT },
  cartClose: {
    width: 36, height: 36, borderRadius: 18, backgroundColor: "rgba(255,255,255,0.12)",
    borderWidth: 1, borderColor: "rgba(255,255,255,0.3)", alignItems: "center", justifyContent: "center",
  },
  cartEmpty: { color: C.muted, fontSize: 12, marginTop: 8, fontFamily: FONT },
  cartItem: {
    backgroundColor: "#fff", borderRadius: 14, borderWidth: 1, borderColor: "#cfe0f7",
    borderRightWidth: 4, borderRightColor: C.primary, padding: 12, marginBottom: 8,
  },
  cartItemTop: { flexDirection: "row-reverse", justifyContent: "space-between", gap: 10 },
  cartItemName: { color: C.ink, fontSize: 12, fontWeight: "800", textAlign: "right", fontFamily: FONT },
  cartItemMeta: { color: C.muted, fontSize: 10, textAlign: "right", marginTop: 2, fontFamily: FONT },
  cartItemPrice: { color: C.primary, fontSize: 12, fontWeight: "900", fontFamily: FONT },
  qtyRow: { flexDirection: "row-reverse", justifyContent: "space-between", alignItems: "center", marginTop: 10 },
  qtyCtl: { flexDirection: "row-reverse", alignItems: "center", gap: 8 },
  qtyBtn: {
    width: 28, height: 28, borderRadius: 9, backgroundColor: C.primary,
    alignItems: "center", justifyContent: "center", borderBottomWidth: 2, borderBottomColor: C.primaryDark,
  },
  qtyBtnText: { color: "#fff", fontWeight: "900", fontSize: 15 },
  qty: { color: C.primaryDark, fontWeight: "900", fontSize: 12, minWidth: 20, textAlign: "center" },

  cartFoot: {
    backgroundColor: "rgba(255,255,255,0.9)", borderTopWidth: 1, borderTopColor: "#cfe0f7", padding: 16,
  },
  couponRow: { flexDirection: "row", gap: 6, marginBottom: 10 },
  couponInput: {
    flex: 1, height: 40, borderWidth: 1.5, borderColor: C.borderStrong, borderRadius: 10,
    paddingHorizontal: 10, fontSize: 12, color: C.ink, backgroundColor: "#fff", textAlign: "left",
  },
  couponBtn: {
    paddingHorizontal: 16, borderRadius: 10, backgroundColor: C.rose, alignItems: "center",
    justifyContent: "center", borderBottomWidth: 3, borderBottomColor: "#9f1239",
  },
  sumRow: { flexDirection: "row-reverse", justifyContent: "space-between", marginBottom: 6 },
  sumL: { color: C.muted, fontSize: 11, fontFamily: FONT },
  sumV: { color: C.ink, fontSize: 11, fontWeight: "800", fontFamily: FONT },
  totalRow: {
    flexDirection: "row-reverse", justifyContent: "space-between", alignItems: "center",
    backgroundColor: C.heroA, borderRadius: 12, paddingVertical: 10, paddingHorizontal: 14, marginTop: 6,
  },
  totalL: { color: "#cfe6ff", fontSize: 12, fontWeight: "900", fontFamily: FONT },
  totalV: { color: "#7fe7ff", fontSize: 18, fontWeight: "900", fontFamily: FONT },
  cardNote: { color: C.muted, fontSize: 10, textAlign: "center", marginTop: 10, fontFamily: FONT },
});
