package com.hmediaplayer.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * H Media Player typography preferences.
 *
 * Call applyTree(rootView) after a screen is inflated.
 * Call apply(textView) in RecyclerView/ListView adapter onBind methods
 * for dynamically-created song rows.
 *
 * Put .ttf/.otf files in app/src/main/assets/fonts/.
 */
public final class HmpTypography {
    private static final String PREFS = "hmp_typography";
    private static final String KEY_FONT = "font";
    private static final String KEY_SIZE = "size_sp";
    private static final String DEFAULT_FONT = "System Default";
    private static final float DEFAULT_SIZE_SP = 16f;
    public static final String MEDIEVAL_NAME = "HMP Medieval";
    private static final String MEDIEVAL_FILE = "HMPMedieval-Regular.ttf";
    private static final WeakHashMap<TextView, Float> BASE_SIZES = new WeakHashMap<>();

    private HmpTypography() {}

    public static String[] getFontChoices(Context context) {
        List<String> names = new ArrayList<>();
        names.add(MEDIEVAL_NAME);
        names.add(DEFAULT_FONT);
        names.add("Serif");
        names.add("Sans Serif");
        names.add("Monospace");
        names.add("Serif Mono");
        names.add("Condensed");
        names.add("Light");
        names.add("Medium");
        names.add("Black");
        names.add("Casual");
        names.add("Cursive");

        try {
            String[] files = context.getAssets().list("fonts");
            if (files != null) {
                for (String file : files) {
                    String lower = file.toLowerCase();
                    if (lower.endsWith(".ttf") || lower.endsWith(".otf")) {
                        if (!MEDIEVAL_FILE.equals(file)) names.add(file);
                    }
                }
            }
        } catch (Exception ignored) {}
        return names.toArray(new String[0]);
    }

    public static String getSelectedFont(Context context) {
        return prefs(context).getString(KEY_FONT, MEDIEVAL_NAME);
    }

    public static float getTextSizeSp(Context context) {
        return prefs(context).getFloat(KEY_SIZE, DEFAULT_SIZE_SP);
    }

    public static void setFont(Context context, String fontName) {
        prefs(context).edit().putString(KEY_FONT, fontName).apply();
    }

    public static void setTextSizeSp(Context context, float sizeSp) {
        float safe = Math.max(10f, Math.min(32f, sizeSp));
        prefs(context).edit().putFloat(KEY_SIZE, safe).apply();
    }

    public static void apply(TextView view) {
        if (view == null) return;
        Context context = view.getContext();

        Float saved;
        synchronized (BASE_SIZES) {
            saved = BASE_SIZES.get(view);
            if (saved == null) {
                saved = view.getTextSize() / context.getResources()
                        .getDisplayMetrics().scaledDensity;
                BASE_SIZES.put(view, saved);
            }
        }
        float baseSp = saved;

        float chosen = getTextSizeSp(context);
        // Scale relative to the user's chosen base size, preserving each view's hierarchy.
        float scaled = baseSp * (chosen / DEFAULT_SIZE_SP);
        view.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, scaled);

        Typeface typeface = resolveTypeface(context, getSelectedFont(context));
        if (typeface != null) {
            view.setTypeface(typeface);
        }
    }

    public static void applyTree(View root) {
        if (root == null) return;
        if (root instanceof TextView) {
            apply((TextView) root);
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyTree(group.getChildAt(i));
            }
        }
    }

    private static Typeface resolveTypeface(Context context, String name) {
        if (name == null || DEFAULT_FONT.equals(name)) return Typeface.DEFAULT;
        if ("Serif".equals(name)) return Typeface.SERIF;
        if ("Sans Serif".equals(name)) return Typeface.SANS_SERIF;
        if ("Monospace".equals(name)) return Typeface.MONOSPACE;
        if ("Serif Mono".equals(name)) return Typeface.create("serif-monospace", Typeface.NORMAL);
        if ("Condensed".equals(name)) return Typeface.create("sans-serif-condensed", Typeface.NORMAL);
        if ("Light".equals(name)) return Typeface.create("sans-serif-light", Typeface.NORMAL);
        if ("Medium".equals(name)) return Typeface.create("sans-serif-medium", Typeface.NORMAL);
        if ("Black".equals(name)) return Typeface.create("sans-serif-black", Typeface.NORMAL);
        if ("Casual".equals(name)) return Typeface.create("casual", Typeface.NORMAL);
        if ("Cursive".equals(name)) return Typeface.create("cursive", Typeface.NORMAL);

        if (MEDIEVAL_NAME.equals(name)) name = MEDIEVAL_FILE;
        try {
            return Typeface.createFromAsset(context.getAssets(), "fonts/" + name);
        } catch (Exception ignored) {
            return Typeface.DEFAULT;
        }
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
