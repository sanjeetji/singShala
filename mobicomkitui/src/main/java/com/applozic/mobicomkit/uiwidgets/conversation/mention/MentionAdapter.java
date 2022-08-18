package com.applozic.mobicomkit.uiwidgets.conversation.mention;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applozic.mobicomkit.api.mention.Mention;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Default adapter for displaying mention in {@link MentionAutoCompleteTextView}.
 */
public class MentionAdapter extends ArrayAdapter<Mention> {
    public MentionAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public MentionAdapter(@NonNull Context context) {
        this(context, R.layout.layout_mention_item, R.id.mention_user_id);
    }

    private Filter filter;
    private final List<Mention> mentions = new ArrayList<>();

    public List<Mention> getMentions() {
        return mentions;
    }

    @Override
    public void add(@Nullable Mention object) {
        super.add(object);
        mentions.add(object);
    }

    @Override
    public void addAll(@NonNull Collection<? extends Mention> collection) {
        super.addAll(collection);
        mentions.addAll(collection);
    }

    @Override
    public final void addAll(Mention... items) {
        super.addAll(items);
        Collections.addAll(mentions, items);
    }

    @Override
    public void remove(@Nullable Mention object) {
        super.remove(object);
        mentions.remove(object);
    }

    @Override
    public void clear() {
        super.clear();
        mentions.clear();
    }

    @NonNull
    public CharSequence convertToString(Mention object) {
        Spannable spannable = new SpannableString(object.getMentionIdentifier());
        if (!TextUtils.isEmpty(spannable)) {
            spannable.setSpan(new MentionAutoCompleteTextView.MentionClickableSpan(), 0, spannable.length(), 0);
        }
        return spannable;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MentionFilter();
        }
        return filter;
    }

    @SuppressWarnings("unchecked")
    private class MentionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            if (constraint == Utils.EMPTY_STRING) { //empty will show all items, null will show none
                results.values = mentions;
                results.count = mentions.size();
                return results;
            }
            final List<Mention> filteredItems = new ArrayList<>();
            for (final Mention item : mentions) {
                if (item.getDisplayNameOrUserId()
                        .toLowerCase(Locale.getDefault())
                        .contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                    filteredItems.add(item);
                }
            }
            results.values = filteredItems;
            results.count = filteredItems.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values instanceof List) {
                final List<Mention> list = (List<Mention>) results.values;
                if (results.count > 0) {
                    MentionAdapter.super.clear();
                    for (final Mention object : list) {
                        MentionAdapter.super.add(object);
                    }
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return convertToString((Mention) resultValue);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_mention_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Mention item = getItem(position);
        if (item != null) {
            holder.userIdView.setText(item.getUserId());

            final CharSequence displayName = item.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                holder.displayNameView.setText(displayName);
            } else {
                holder.displayNameView.setText(item.getUserId());
            }

            final String profileImageUrl = item.getProfileImage();
            if (!TextUtils.isEmpty(profileImageUrl)) {
                Glide.with(getContext()).load(profileImageUrl).into(holder.profileImage);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        private final ImageView profileImage;
        private final TextView userIdView;
        private final TextView displayNameView;

        ViewHolder(View itemView) {
            profileImage = itemView.findViewById(R.id.mention_profile_image);
            userIdView = itemView.findViewById(R.id.mention_user_id);
            displayNameView = itemView.findViewById(R.id.mention_display_name);
        }
    }
}