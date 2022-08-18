package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applozic.mobicomkit.broadcast.ConnectivityReceiver;
import com.applozic.mobicomkit.feed.GroupInfoUpdate;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.ChannelMetadata;

import java.util.HashMap;
import java.util.Map;

public class EditChannelDescriptionActivity extends AppCompatActivity {
    private static final String TAG = "EditChannelDescriptionActivity";
    ActionBar mActionBar;
    GroupInfoUpdate groupInfoUpdate;
    private EditText editTextChannelDescription;
    private AlCustomizationSettings alCustomizationSettings;
    private ConnectivityReceiver connectivityReceiver;

    private void loadCustomizationFile() {
        String jsonString = FileUtils.loadSettingsJsonFile(getApplicationContext());
        if (!TextUtils.isEmpty(jsonString)) {
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString, AlCustomizationSettings.class);
        } else {
            alCustomizationSettings = new AlCustomizationSettings();
        }
    }

    private void setActionBarColorFromCustomizationFile() {
        if (alCustomizationSettings == null) {
            return;
        }
        if(!TextUtils.isEmpty(alCustomizationSettings.getThemeColorPrimary()) && !TextUtils.isEmpty(alCustomizationSettings.getThemeColorPrimaryDark())){
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(alCustomizationSettings.getThemeColorPrimary())));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(alCustomizationSettings.getThemeColorPrimaryDark()));
            }
        }
    }

    private GroupInfoUpdate getExistingGroupInfoDataFromActivityIntent() {
        if (getIntent().getExtras() != null) {
            String groupInfoJson = getIntent().getExtras().getString(ChannelInfoActivity.GROUP_UPDTAE_INFO);
            return (GroupInfoUpdate) GsonUtils.getObjectFromJson(groupInfoJson, GroupInfoUpdate.class);
        }
        return null;
    }

    private @NonNull String getExistingChannelDescriptionFrom(GroupInfoUpdate groupInfoUpdate) {
        final String EMPTY_STRING = "";
        if(groupInfoUpdate == null) {
            return EMPTY_STRING;
        }
        return ChannelMetadata.getChannelDescriptionFrom(groupInfoUpdate.getMetadata());
    }

    private void updateGroupInfoUpdateObjectsChannelDescription(String channelDescription) {
        final String EMPTY_STRING = "";
        Map<String, String> metadata = groupInfoUpdate.getMetadata();
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        if(channelDescription == null) {
            channelDescription = EMPTY_STRING;
        }
        metadata.put(ChannelMetadata.AL_CHANNEL_DESCRIPTION, channelDescription);
        groupInfoUpdate.setMetadata(metadata);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_channel_description_layout);

        editTextChannelDescription = (EditText) findViewById(R.id.editTextNewChannelDescription);

        groupInfoUpdate = getExistingGroupInfoDataFromActivityIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getString(R.string.update_channel_title_description));

        loadCustomizationFile();
        setActionBarColorFromCustomizationFile();

        final String existingChannelDescription = getExistingChannelDescriptionFrom(groupInfoUpdate);

        editTextChannelDescription.setText(existingChannelDescription);

        Button buttonOk = (Button) findViewById(R.id.buttonChannelDescriptionOk);
        Button buttonCancel = (Button) findViewById(R.id.buttonChannelDescriptionCancel);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextString = editTextChannelDescription.getText().toString().trim();
                if (editTextString != null && !editTextString.equals(existingChannelDescription)) {
                    updateGroupInfoUpdateObjectsChannelDescription(editTextString);

                    Intent intent = new Intent();
                    intent.putExtra(ChannelInfoActivity.GROUP_UPDTAE_INFO, GsonUtils.getJsonFromObject(groupInfoUpdate, GroupInfoUpdate.class));
                    setResult(RESULT_OK, intent);

                    if (editTextString.trim().length() == 0) {
                        Toast.makeText(EditChannelDescriptionActivity.this, getString(R.string.channel_description_will_be_removed), Toast.LENGTH_SHORT).show();
                    }
                }
                EditChannelDescriptionActivity.this.finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditChannelDescriptionActivity.this.finish();
            }
        });

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (connectivityReceiver != null) {
                unregisterReceiver(connectivityReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
