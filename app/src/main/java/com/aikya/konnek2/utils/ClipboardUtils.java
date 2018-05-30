package com.aikya.konnek2.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;

public class ClipboardUtils {

    private static final String DEFAULT_LABEL = "q-municate_simple_text_clipboard";

    public static void copySimpleTextToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(DEFAULT_LABEL, text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    public static void pasteSimpleText(Context context, EditText pasteTextTv) {

        ClipboardManager clipboardManager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            CharSequence ptext = item.getText();
            pasteTextTv.setText(ptext);
        }
    }
}
