package com.hmediaplayer.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Simple typography settings dialog.
 * Usage: HmpTypographySettings.show(this, findViewById(android.R.id.content));
 */
public final class HmpTypographySettings {
    private HmpTypographySettings() {}

    public static void show(Context context, ViewGroup screenRoot) {
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * context.getResources().getDisplayMetrics().density);
        panel.setPadding(pad, pad, pad, pad);

        TextView fontLabel = new TextView(context);
        fontLabel.setText("Font style");
        panel.addView(fontLabel);

        String[] fonts = HmpTypography.getFontChoices(context);
        Spinner fontSpinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, fonts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);
        String selected = HmpTypography.getSelectedFont(context);
        for (int i = 0; i < fonts.length; i++) {
            if (fonts[i].equals(selected)) {
                fontSpinner.setSelection(i);
                break;
            }
        }
        panel.addView(fontSpinner);

        TextView sizeLabel = new TextView(context);
        sizeLabel.setText("Font size: " + Math.round(HmpTypography.getTextSizeSp(context)) + " sp");
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = pad;
        panel.addView(sizeLabel, labelParams);

        SeekBar sizeSeek = new SeekBar(context);
        sizeSeek.setMax(22); // 10sp to 32sp
        sizeSeek.setProgress(Math.round(HmpTypography.getTextSizeSp(context)) - 10);
        panel.addView(sizeSeek);

        sizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeLabel.setText("Font size: " + (progress + 10) + " sp");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new AlertDialog.Builder(context)
                .setTitle("H Media Player Typography")
                .setView(panel)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Reset", (dialog, which) -> {
                    HmpTypography.setFont(context, "System Default");
                    HmpTypography.setTextSizeSp(context, 16f);
                    HmpTypography.applyTree(screenRoot);
                })
                .setPositiveButton("Apply", (dialog, which) -> {
                    HmpTypography.setFont(context, (String) fontSpinner.getSelectedItem());
                    HmpTypography.setTextSizeSp(context, sizeSeek.getProgress() + 10f);
                    HmpTypography.applyTree(screenRoot);
                })
                .show();
    }
}
