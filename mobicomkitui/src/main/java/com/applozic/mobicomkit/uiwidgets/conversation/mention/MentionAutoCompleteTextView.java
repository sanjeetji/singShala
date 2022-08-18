package com.applozic.mobicomkit.uiwidgets.conversation.mention;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.QwertyKeyListener;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

import com.applozic.mobicomkit.api.conversation.MentionMetadataModel;
import com.applozic.mobicomkit.api.mention.Mention;
import com.applozic.mobicomkit.api.mention.MentionHelper;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This EditText supports 'Mentions' and uses the {@link CharTokenizer} class to identify them.
 * Refer to {@link MentionHelper}.
 */
public class MentionAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    private static final String TAG = "MentionAutoCompleteView";

    private MentionAdapter mentionAdapter;
    @Nullable private OnClickListener mentionClickListener;

    private CharTokenizer charTokenizer;

    private final ArrayList<MentionMetadataModel> mentionMetadataModels = new ArrayList<>();

    public MentionAutoCompleteTextView(Context context) {
        super(context);
    }

    public MentionAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MentionAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initMentions(MentionAdapter mentionAdapter) {
        if (isMentionEnabled()) {
            addTextChangedListener(textWatcher);
            setTokenizer(new CharTokenizer());
            setMentionAdapter(mentionAdapter);
            setAdapter(mentionAdapter);

            recreateMentionsUiAndData();
        }
    }

    public boolean isMentionEnabled() {
        String jsonString = FileUtils.loadSettingsJsonFile(getContext().getApplicationContext());
        AlCustomizationSettings alCustomizationSettings;
        if (!TextUtils.isEmpty(jsonString)) {
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString, AlCustomizationSettings.class);
        } else {
            alCustomizationSettings = new AlCustomizationSettings();
        }

        if (alCustomizationSettings == null) {
            return true; //enabled by default
        }

        return !alCustomizationSettings.isDisableMentions();
    }

    public ArrayList<MentionMetadataModel> getMentionMetadata() {
        return mentionMetadataModels;
    }

    /**
     * Analogous/wrapper of/for {@link #getText()}.
     * This method modifies the text according to what is required by mentions and returns it along with the mention metadata.
     *
     * @return MentionPair
     */
    public MentionHelper.MentionPair getMentionPair() {
        return getMentionPairForMessageSend(this);
    }

    @Override
    public void setTokenizer(Tokenizer tokenizer) {
        super.setTokenizer(tokenizer);
        if(tokenizer instanceof CharTokenizer) {
            charTokenizer = (CharTokenizer) tokenizer;
        }
    }

    @Override
    public int getThreshold() {
        return 0;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (charTokenizer != null && charTokenizer.findTokenStart(text, getSelectionEnd()) <= CharTokenizer.TOKEN_NOT_FOUND) {
            dismissDropDown();

            Filter f = getFilter();
            if (f != null) {
                f.filter(null);
            }
        } else {
            super.performFiltering(text, keyCode);
        }
    }

    @Override
    public boolean enoughToFilter() {
        Editable text = getText();

        int end = getSelectionEnd();
        if (end < 0 || charTokenizer == null) {
            return false;
        }

        int start = charTokenizer.findTokenStart(text, end);

        if (start <= CharTokenizer.TOKEN_NOT_FOUND) {
            return false;
        }

        return end - start >= getThreshold();
    }

    @Override
    public void performValidation() {
        Validator v = getValidator();

        if (v == null || charTokenizer == null) {
            return;
        }

        Editable e = getText();
        int i = getText().length();
        while (i > 0) {
            int start = charTokenizer.findTokenStart(e, i);

            if (start <= CharTokenizer.TOKEN_NOT_FOUND) {
                break;
            }

            int end = charTokenizer.findTokenEnd(e, start);

            CharSequence sub = e.subSequence(start, end);
            if (TextUtils.isEmpty(sub)) {
                e.replace(start, i, "");
            } else if (!v.isValid(sub)) {
                e.replace(start, i,
                        charTokenizer.terminateToken(v.fixText(sub)));
            }

            i = start;
        }
    }

    @Override
    protected void replaceText(CharSequence text) {
        clearComposingText();

        int end = getSelectionEnd();
        int start = charTokenizer.findTokenStart(getText(), end);

        if (start <= CharTokenizer.TOKEN_NOT_FOUND) {
            return;
        }

        Editable editable = getText();
        String original = ""; //to get rid of the entire mention text on DEL press

        QwertyKeyListener.markAsReplaced(editable, start, end, original);
        editable.replace(start, end, charTokenizer.terminateToken(text));
    }

    private MentionHelper.MentionPair getMentionPairForMessageSend(MentionAutoCompleteTextView mentionAutoCompleteTextView) {
        ArrayList<MentionMetadataModel> notServerSendReadyMentionMetadataList = mentionAutoCompleteTextView.getMentionMetadata(); //items in this list must have identifierStart
        String notServerSendReadyMessageString = mentionAutoCompleteTextView.getText().toString();

        return MentionHelper.getServerSendReadyMentionPair(notServerSendReadyMessageString, notServerSendReadyMentionMetadataList);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final TextWatcher textWatcher = new TextWatcher() {
        String oldText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                return;
            }
            recreateMentionsUiAndData();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                return;
            }

            if (TextUtils.isEmpty(s)) {
                mentionMetadataModels.clear();
            }

            oldText = s.toString();
        }
    };

    @Nullable
    public MentionAdapter getMentionAdapter() {
        return mentionAdapter;
    }

    public void setMentionAdapter(@Nullable MentionAdapter adapter) {
        mentionAdapter = adapter;
    }

    @NonNull
    public Pattern getMentionPattern() {
        return MentionHelper.MENTION_PATTERN;
    }

    public void setOnMentionClickListener(@Nullable OnClickListener listener) {
        mentionClickListener = listener;
    }

    private void resetMentionSpans(Spannable spannable) {
        if(spannable == null) {
            return;
        }

        for (final Object span : spannable.getSpans(0, spannable.length(), CharacterStyle.class)) {
            if(span instanceof MentionClickableSpan) {
                spannable.removeSpan(span);
            }
        }
    }

    private void resetMentionsData() {
        if (mentionMetadataModels == null) {
            return;
        }

        mentionMetadataModels.clear();
    }

    private void recreateMentionsUiAndData() {
        final Spannable spannable = getText();

        resetMentionSpans(spannable);
        resetMentionsData();
        recreateMentionsSpanAndData(spannable, getMentionPattern());
    }

    private void recreateMentionsSpanAndData(Spannable spannable, Pattern pattern) {
        try {
            final Matcher matcher = pattern.matcher(spannable);
            while (matcher.find()) {
                final int start = matcher.start() + 1; //the character index after mention token start char (i.e. '@')
                final int end = matcher.end();
                String possibleMention = matcher.group(1);

                for (Mention mention : mentionAdapter.getMentions()) {
                    if (mention.getMentionIdentifier().equals(possibleMention)) {
                        MentionMetadataModel mentionMetadataModel = new MentionMetadataModel();
                        mentionMetadataModel.userId = mention.getUserId().toString();
                        if (mention.getDisplayName() != null && !mention.getDisplayName().equals(Utils.EMPTY_STRING)) {
                            mentionMetadataModel.displayName = mention.getDisplayName().toString();
                        }
                        int[] indices = new int[2];
                        indices[0] = start; //for sorting
                        mentionMetadataModel.indices = indices;
                        mentionMetadataModels.add(mentionMetadataModel);

                        final MentionClickableSpan span = new MentionClickableSpan();
                        spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (span instanceof MentionClickableSpan) {
                            span.userId = mention.getUserId();
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static class CharTokenizer implements Tokenizer {
        public static final int TOKEN_NOT_FOUND = -1;
        public static final char TOKEN = '@';
        public static final char DELIMITER = ' ';

        @Override
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            if (TextUtils.isEmpty(text)) {
                return TOKEN_NOT_FOUND;
            }

            while (i > 0 && text.charAt(i - 1) != TOKEN) {
                i--;
            }

            if (i <= 0 && text.charAt(0) != TOKEN) {
                return TOKEN_NOT_FOUND;
            }

            return i;
        }

        @Override
        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == TOKEN) {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        @Override
        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == DELIMITER) {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == TOKEN) {
                return text;
            } else {
                if (text instanceof Spanned) {
                    final Spannable spannable = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, spannable, 0);
                    return spannable;
                } else {
                    return text + " ";
                }
            }
        }
    }

    public static class MentionClickableSpan extends StyleSpan {
        CharSequence userId;

        public MentionClickableSpan() {
            super(Typeface.BOLD_ITALIC);
        }
    }

    interface OnClickListener {
        void onClick(@NonNull MentionAutoCompleteTextView view, @NonNull CharSequence text);
    }
}