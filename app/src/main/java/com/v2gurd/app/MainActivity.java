package com.v2gurd.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int BG = Color.rgb(5, 8, 13);
    private static final int CARD = Color.rgb(12, 18, 27);
    private static final int CARD_2 = Color.rgb(16, 23, 34);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(155, 166, 182);
    private static final int CYAN = Color.rgb(38, 224, 201);
    private static final int PURPLE = Color.rgb(177, 107, 255);

    private LinearLayout content;
    private TextView title;

    // بعداً شماره کارت واقعی را اینجا قرار می‌دهیم
    private static final String CARD_NUMBER = "0000 0000 0000 0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        buildApp();
        showHome();
    }

    private TextView text(
            String value,
            float size,
            int color
    ) {

        TextView tv = new TextView(this);

        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);

        tv.setGravity(
                Gravity.RIGHT |
                Gravity.CENTER_VERTICAL
        );

        tv.setPadding(
                18,
                10,
                18,
                10
        );

        tv.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        return tv;
    }

    private Button button(String value) {

        Button button = new Button(this);

        button.setText(value);
        button.setTextSize(14);
        button.setTextColor(Color.BLACK);

        button.setAllCaps(false);

        button.setBackgroundColor(CYAN);

        button.setPadding(
                10,
                2,
                10,
                2
        );

        return button;
    }

    private LinearLayout card() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                10,
                10,
                10,
                10
        );

        card.setBackgroundColor(CARD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                10,
                7,
                10,
                7
        );

        card.setLayoutParams(params);

        card.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        return card;
    }

    private void buildApp() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(BG);

        root.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        // =========================
        // HEADER
        // =========================

        LinearLayout header =
                new LinearLayout(this);

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        header.setPadding(
                12,
                8,
                12,
                4
        );

        title = text(
                "V2GURD",
                25,
                CYAN
        );

        title.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        64,
                        1
                )
        );

        TextView online =
                text(
                        "● آنلاین",
                        13,
                        CYAN
                );

        online.setGravity(
                Gravity.CENTER
        );

        header.addView(
                online,
                new LinearLayout.LayoutParams(
                        95,
                        64
                )
        );

        root.addView(header);

        // =========================
        // CONTENT
        // =========================

        ScrollView scroll =
                new ScrollView(this);

        content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                4,
                4,
                4,
                30
        );

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        // =========================
        // BOTTOM NAVIGATION
        // =========================

        LinearLayout navigation =
                new LinearLayout(this);

        navigation.setGravity(
                Gravity.CENTER
        );

        navigation.setBackgroundColor(
                Color.rgb(9, 13, 20)
        );

        String[] tabs = {
                "خانه",
                "فروشگاه",
                "سرویس‌ها",
                "حساب"
        };

        for (String tab : tabs) {

            Button navButton =
                    new Button(this);

            navButton.setText(tab);
            navButton.setTextColor(WHITE);
            navButton.setTextSize(13);
            navButton.setAllCaps(false);

            navButton.setBackgroundColor(
                    Color.TRANSPARENT
            );

            navigation.addView(
                    navButton,
                    new LinearLayout.LayoutParams(
                            0,
                            64,
                            1
                    )
            );

            if (tab.equals("خانه")) {
                navButton.setOnClickListener(
                        v -> showHome()
                );
            }

            if (tab.equals("فروشگاه")) {
                navButton.setOnClickListener(
                        v -> showStore()
                );
            }

            if (tab.equals("سرویس‌ها")) {
                navButton.setOnClickListener(
                        v -> showServices()
                );
            }

            if (tab.equals("حساب")) {
                navButton.setOnClickListener(
                        v -> showAccount()
                );
            }
        }

        root.addView(navigation);

        setContentView(root);
    }

    private void clearPage(String heading) {

        content.removeAllViews();

        title.setText("V2GURD");

        TextView headingView =
                text(
                        heading,
                        23,
                        WHITE
                );

        headingView.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        content.addView(
                headingView
        );
    }

    // =========================
    // HOME
    // =========================

    private void showHome() {

        clearPage("داشبورد");

        LinearLayout status =
                card();

        status.addView(
                text(
                        "وضعیت اتصال",
                        14,
                        MUTED
                )
        );

        status.addView(
                text(
                        "● متصل نیست",
                        25,
                        WHITE
                )
        );

        status.addView(
                text(
                        "هنوز سرویسی برای اتصال فعال نشده است.",
                        14,
                        MUTED
                )
        );

        content.addView(status);

        LinearLayout welcome =
                card();

        welcome.addView(
                text(
                        "V2GURD",
                        31,
                        CYAN
                )
        );

        welcome.addView(
                text(
                        "اینترنت امن، سریع و پایدار",
                        18,
                        WHITE
                )
        );

        welcome.addView(
                text(
                        "مدیریت سرویس، خرید و پشتیبانی در یک اپ سبک.",
                        14,
                        MUTED
                )
        );

        content.addView(welcome);

        LinearLayout quick =
                card();

        quick.addView(
                text(
                        "دسترسی سریع",
                        17,
                        WHITE
                )
        );

        Button shop =
                button(
                        "مشاهده فروشگاه"
                );

        shop.setOnClickListener(
                v -> showStore()
        );

        quick.addView(shop);

        content.addView(quick);
    }

    // =========================
    // STORE
    // =========================

    private void showStore() {

        clearPage("فروشگاه");

        addProduct(
                "WireGuard",
                "سرویس سریع و پایدار WireGuard",
                "خرید سرویس"
        );

        addProduct(
                "V2Box",
                "پلن‌های مختلف V2Box",
                "خرید سرویس"
        );

        addProduct(
                "JumpJump",
                "سرویس IP ثابت و اتصال اختصاصی",
                "خرید سرویس"
        );
    }

    private void addProduct(
            String name,
            String description,
            String action
    ) {

        LinearLayout product =
                card();

        product.addView(
                text(
                        name,
                        21,
                        WHITE
                )
        );

        product.addView(
                text(
                        description,
                        14,
                        MUTED
                )
        );

        Button buy =
                button(action);

        buy.setOnClickListener(
                v -> showPayment(name)
        );

        product.addView(buy);

        content.addView(product);
    }

    // =========================
    // PAYMENT
    // =========================

    private void showPayment(
            String product
    ) {

        clearPage("پرداخت");

        LinearLayout order =
                card();

        order.addView(
                text(
                        "محصول انتخاب‌شده",
                        14,
                        MUTED
                )
        );

        order.addView(
                text(
                        product,
                        24,
                        WHITE
                )
        );

        order.addView(
                text(
                        "قیمت: بعداً تنظیم می‌شود",
                        14,
                        MUTED
                )
        );

        content.addView(order);

        LinearLayout payment =
                card();

        payment.addView(
                text(
                        "پرداخت کارت‌به‌کارت",
                        19,
                        WHITE
                )
        );

        TextView cardNumber =
                text(
                        CARD_NUMBER,
                        22,
                        CYAN
                );

        cardNumber.setGravity(
                Gravity.CENTER
        );

        payment.addView(cardNumber);

        Button copy =
                button(
                        "کپی شماره کارت"
                );

        copy.setOnClickListener(
                v -> {

                    ClipboardManager manager =
                            (ClipboardManager)
                                    getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                    );

                    manager.setPrimaryClip(
                            ClipData.newPlainText(
                                    "شماره کارت",
                                    CARD_NUMBER
                            )
                    );

                    Toast.makeText(
                            this,
                            "شماره کارت کپی شد",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        payment.addView(copy);

        Button paid =
                button(
                        "پرداخت کردم"
                );

        paid.setOnClickListener(
                v -> showSupport(product)
        );

        payment.addView(paid);

        content.addView(payment);
    }

    // =========================
    // SUPPORT
    // =========================

    private void showSupport(
            String product
    ) {

        clearPage("ثبت پرداخت");

        LinearLayout box =
                card();

        box.addView(
                text(
                        "درخواست پرداخت آماده شد",
                        22,
                        CYAN
                )
        );

        box.addView(
                text(
                        "محصول: " + product,
                        16,
                        WHITE
                )
        );

        box.addView(
                text(
                        "برای تأیید سفارش، اطلاعات پرداخت را برای پشتیبانی ارسال کنید.",
                        14,
                        MUTED
                )
        );

        Button telegram =
                button(
                        "ارتباط با پشتیبانی تلگرام"
                );

        telegram.setOnClickListener(
                v -> openTelegram()
        );

        box.addView(telegram);

        content.addView(box);
    }

    private void openTelegram() {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    "https://t.me/Vtwoguard"
                            )
                    );

            startActivity(intent);

        } catch (Exception ignored) {

        }
    }

    // =========================
    // SERVICES
    // =========================

    private void showServices() {

        clearPage("سرویس‌های من");

        LinearLayout box =
                card();

        box.addView(
                text(
                        "هنوز سرویسی اضافه نشده",
                        21,
                        WHITE
                )
        );

        box.addView(
                text(
                        "بعد از خرید، سرویس‌های فعال اینجا نمایش داده می‌شوند.",
                        14,
                        MUTED
                )
        );

        content.addView(box);
    }

    // =========================
    // ACCOUNT
    // =========================

    private void showAccount() {

        clearPage("حساب کاربری");

        LinearLayout box =
                card();

        box.addView(
                text(
                        "حساب V2GURD",
                        21,
                        WHITE
                )
        );

        box.addView(
                text(
                        "نسخه اول بدون ورود و دیتابیس طراحی شده است.",
                        14,
                        MUTED
                )
        );

        Button support =
                button(
                        "پشتیبانی V2GURD"
                );

        support.setOnClickListener(
                v -> openTelegram()
        );

        box.addView(support);

        content.addView(box);
    }
}
