package com.applozic.mobicomkit.uiwidgets.people.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.user.AlUserSearchTask;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.AlGroupOfTwoCreateTask;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicomkit.listners.AttachmentFilteringListener;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComAttachmentSelectorActivity;
import com.applozic.mobicomkit.uiwidgets.people.channel.ChannelFragment;
import com.applozic.mobicomkit.uiwidgets.people.contact.AppContactFragment;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.OnContactsInteractionListener;
import com.applozic.mobicommons.people.SearchListFragment;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicommons.task.AlAsyncTask;
import com.applozic.mobicommons.task.AlTask;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MobiComKitPeopleActivity extends AppCompatActivity implements OnContactsInteractionListener,
        SearchView.OnQueryTextListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MobiComKitPeopleActivity";
    public static final String SHARED_TEXT = "SHARED_TEXT";
    public static final String FORWARD_MESSAGE = "forwardMessage";
    public static final String USER_ID_ARRAY = "userIdArray";
    private static final String CONTACT_ID = "contactId";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_NAME = "groupName";
    private static final String USER_ID = "userId";
    public static boolean isSearching = false;
    protected SearchView searchView;
    protected String searchTerm;
    ViewPager viewPager;
    TabLayout tabLayout;
    ActionBar actionBar;
    String[] userIdArray;
    AppContactFragment appContactFragment;
    ChannelFragment channelFragment;
    ViewPagerAdapter adapter;
    AlCustomizationSettings alCustomizationSettings;
    Intent intentExtra;
    String action, type;
    OnContactsInteractionListener onContactsInteractionListener;
    private SearchListFragment searchListFragment;
    private boolean isSearchResultView = false;

    public static void addFragment(FragmentActivity fragmentActivity, Fragment fragmentToAdd, String fragmentTag) {
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = supportFragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.layout_child_activity, fragmentToAdd,
                fragmentTag);

        if (supportFragmentManager.getBackStackEntryCount() > 1) {
            supportFragmentManager.popBackStack();
        }
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
        supportFragmentManager.executePendingTransactions();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MobiComUserPreference.getInstance(this).isLoggedIn()) {
            finish();
        }
        setContentView(R.layout.people_activity);
        String jsonString = FileUtils.loadSettingsJsonFile(getApplicationContext());
        if (!TextUtils.isEmpty(jsonString)) {
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString, AlCustomizationSettings.class);
        } else {
            alCustomizationSettings = new AlCustomizationSettings();
        }

        onContactsInteractionListener = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Set up the action bar.
        actionBar = getSupportActionBar();
        if (!TextUtils.isEmpty(alCustomizationSettings.getThemeColorPrimary()) && !TextUtils.isEmpty(alCustomizationSettings.getThemeColorPrimaryDark())) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(alCustomizationSettings.getThemeColorPrimary())));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(alCustomizationSettings.getThemeColorPrimaryDark()));
            }
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);


        intentExtra = getIntent();
        action = intentExtra.getAction();
        type = intentExtra.getType();

        if (getIntent().getExtras() != null) {
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                actionBar.setTitle(getString(R.string.send_message_to));
            } else {
                actionBar.setTitle(getString(R.string.search_title));
                userIdArray = getIntent().getStringArrayExtra(USER_ID_ARRAY);
            }
        } else {
            actionBar.setTitle(getString(R.string.search_title));
        }
        appContactFragment = new AppContactFragment(userIdArray);
        appContactFragment.setAlCustomizationSettings(alCustomizationSettings);
        channelFragment = new ChannelFragment();
        setSearchListFragment(appContactFragment);
        if (alCustomizationSettings.isGroupsSectionTabHidden() || ApplozicSetting.getInstance(this).isGroupsSectionTabHidden()) {
            addFragment(this, appContactFragment, "AppContactFragment");
        } else {
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            viewPager.setVisibility(View.VISIBLE);
            setupViewPager(viewPager);
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.addOnTabSelectedListener(this);
        }

        // This flag notes that the Activity is doing a search, and so the result will be
        // search results rather than all contacts. This prevents the Activity and Fragment
        // from trying to a search on search results.
        isSearchResultView = true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        if (Utils.hasICS()) {
            searchItem.collapseActionView();
        }
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(true);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {

    }

    public void startNewConversation(String contactNumber) {
        Intent intent = new Intent();
        intent.putExtra(USER_ID, contactNumber);
        finishActivity(intent);
    }


    @Override
    public void onGroupSelected(final Channel channel) {
        Intent intent = null;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (!ChannelService.getInstance(MobiComKitPeopleActivity.this).processIsUserPresentInChannel(channel.getKey())) {
                Toast.makeText(this, getString(R.string.unable_share_message), Toast.LENGTH_SHORT).show();
                return;
            }
            if ("text/plain".equals(type)) {
                intent = new Intent(this, ConversationActivity.class);
                intent.putExtra(GROUP_ID, channel.getKey());
                intent.putExtra(GROUP_NAME, channel.getName());
                intent.putExtra(ConversationUIService.DEFAULT_TEXT, intentExtra.getStringExtra(Intent.EXTRA_TEXT));
                startActivity(intent);
                finish();
            } else if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
                final Uri fileUri = (Uri) intentExtra.getParcelableExtra(Intent.EXTRA_STREAM);
                if (fileUri != null) {
                    long maxSize = alCustomizationSettings.getMaxAttachmentSizeAllowed() * 1024 * 1024;
                    if (FileUtils.isMaxUploadSizeReached(this, fileUri, maxSize)) {
                        Toast.makeText(this, getString(R.string.info_attachment_max_allowed_file_size), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (getApplicationContext() instanceof AttachmentFilteringListener) {
                        ((AttachmentFilteringListener) getApplicationContext()).onAttachmentSelected(this, fileUri, new AlCallback() {
                            @Override
                            public void onSuccess(Object response) {
                                processAttachmentUri(fileUri, null, channel);
                            }

                            @Override
                            public void onError(Object error) {
                                Utils.printLog(getApplicationContext(), TAG, "Error in file : " + GsonUtils.getJsonFromObject(error, Object.class));
                            }
                        });
                    } else {
                        processAttachmentUri(fileUri, null, channel);
                    }
                }
            }
        } else {
            intent = new Intent();
            intent.putExtra(GROUP_ID, channel.getKey());
            intent.putExtra(GROUP_NAME, channel.getName());
            finishActivity(intent);
        }
    }

    @Override
    public void onCustomContactSelected(final Contact contact) {
        Intent intent = null;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (contact.isBlocked()) {
                Toast.makeText(this, getString(R.string.user_is_blocked), Toast.LENGTH_SHORT).show();
                return;
            }
            if ("text/plain".equals(type)) {
                intent = new Intent(this, ConversationActivity.class);
                intent.putExtra(USER_ID, contact.getUserId());
                intent.putExtra(ConversationUIService.DEFAULT_TEXT, intentExtra.getStringExtra(Intent.EXTRA_TEXT));
                startActivity(intent);
                finish();
            } else if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
                final Uri fileUri = (Uri) intentExtra.getParcelableExtra(Intent.EXTRA_STREAM);
                long maxSize = alCustomizationSettings.getMaxAttachmentSizeAllowed() * 1024 * 1024;

                if (FileUtils.isMaxUploadSizeReached(this, fileUri, maxSize)) {
                    Toast.makeText(this, getString(R.string.info_attachment_max_allowed_file_size), Toast.LENGTH_LONG).show();
                    return;
                }

                if (getApplicationContext() instanceof AttachmentFilteringListener) {
                    ((AttachmentFilteringListener) getApplicationContext()).onAttachmentSelected(this, fileUri, new AlCallback() {
                        @Override
                        public void onSuccess(Object response) {
                            processAttachmentUri(fileUri, contact, null);
                        }

                        @Override
                        public void onError(Object error) {
                            Utils.printLog(getApplicationContext(), TAG, "Error in file : " + GsonUtils.getJsonFromObject(error, Object.class));
                        }
                    });
                } else {
                    processAttachmentUri(fileUri, contact, null);
                }
            }
        } else {
            if (ApplozicClient.getInstance(this).isStartGroupOfTwo()) {
                ProgressDialog progressDialog = ProgressDialog.show(this, "",
                        getString(R.string.please_wait_creating_group_of_two), true);

                AlTask.execute(new AlGroupOfTwoCreateTask(MobiComKitPeopleActivity.this, MobiComUserPreference.getInstance(this).getParentGroupKey(), contact, new AlGroupOfTwoCreateTask.ChannelCreateListener() {
                    @Override
                    public void onSuccess(@NonNull Channel channel) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (channel != null) {
                            Intent intent = new Intent(MobiComKitPeopleActivity.this, ConversationActivity.class);
                            intent.putExtra(ConversationUIService.GROUP_ID, channel.getKey());
                            intent.putExtra(ConversationUIService.GROUP_NAME, channel.getName());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }));
            } else {
                intent = new Intent();
                intent.putExtra(USER_ID, contact.getUserId());
                finishActivity(intent);
            }
        }
    }

    private void processAttachmentUri(Uri fileUri, Contact contact, Channel channel) {
        if (FileUtils.isContentScheme(fileUri)) {
            String mimeType = FileUtils.getMimeTypeByContentUriOrOther(this, fileUri);
            if (TextUtils.isEmpty(mimeType)) {
                this.finish();
            } else {
                AlTask.execute(new ShareAsyncTask(this, fileUri, contact, channel, mimeType));
            }
        } else {
            Intent intentImage = new Intent(this, MobiComAttachmentSelectorActivity.class);
            intentImage.putExtra(contact != null ? MobiComAttachmentSelectorActivity.USER_ID : MobiComAttachmentSelectorActivity.GROUP_ID, contact != null ? contact.getUserId() : channel.getKey());
            intentImage.putExtra(contact != null ? MobiComAttachmentSelectorActivity.DISPLAY_NAME : MobiComAttachmentSelectorActivity.GROUP_NAME, contact != null ? contact.getDisplayName() : channel.getName());
            if (fileUri != null) {
                intentImage.putExtra(MobiComAttachmentSelectorActivity.URI_LIST, fileUri);
            }
            startActivity(intentImage);
        }
    }


    public void finishActivity(Intent intent) {
        String forwardMessage = getIntent().getStringExtra(FORWARD_MESSAGE);
        if (!TextUtils.isEmpty(forwardMessage)) {
            intent.putExtra(FORWARD_MESSAGE, forwardMessage);
        }

        String sharedText = getIntent().getStringExtra(SHARED_TEXT);
        if (!TextUtils.isEmpty(sharedText)) {
            intent.putExtra(SHARED_TEXT, sharedText);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSelectionCleared() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
            // For platforms earlier than Android 3.0, triggers the search activity
        } else if (i == R.id.menu_search) {// if (!Utils.hasHoneycomb()) {
            onSearchRequested();
            //}

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return !isSearchResultView && super.onSearchRequested();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (alCustomizationSettings.isCreateAnyContact()) {
            this.searchTerm = query;
            startNewConversation(query);
            isSearching = false;
        }

        if (alCustomizationSettings.isContactSearchFromServer()) {
            processSearchCall(query);
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        this.searchTerm = query;
        if (getSearchListFragment() != null) {
            getSearchListFragment().onQueryTextChange(query);
            isSearching = true;

            if (query.isEmpty()) {
                isSearching = false;
            }
        }
        return true;
    }

    public void processSearchCall(String query) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getResources().getString(R.string.applozic_contacts_loading_info));
        dialog.show();

        AlTask.execute(new AlUserSearchTask(this, query, new AlUserSearchTask.AlUserSearchHandler() {
            @Override
            public void onSuccess(List<Contact> contacts, Context context) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (!contacts.isEmpty() && appContactFragment != null) {
                    appContactFragment.restartLoader();
                }
            }

            @Override
            public void onFailure(Exception e, Context context) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(context, R.string.applozic_server_error, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public SearchListFragment getSearchListFragment() {
        return searchListFragment;
    }

    public void setSearchListFragment(SearchListFragment searchListFragment) {
        this.searchListFragment = searchListFragment;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(appContactFragment, getString(R.string.Contact));
        adapter.addFrag(channelFragment, getString(R.string.Group));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition(), true);
        switch (tab.getPosition()) {
            case 0:
                setSearchListFragment((AppContactFragment) adapter.getItem(0));
                if (getSearchListFragment() != null) {
                    getSearchListFragment().onQueryTextChange(null);
                }
                break;
            case 1:
                setSearchListFragment((ChannelFragment) adapter.getItem(1));
                if (getSearchListFragment() != null) {
                    getSearchListFragment().onQueryTextChange(null);
                }
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onContactsInteractionListener != null) {
            onContactsInteractionListener = null;
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

    }

    private static class ShareAsyncTask extends AlAsyncTask<Void, File> {
        WeakReference<Context> contextWeakReference;
        Uri uri;
        FileClientService fileClientService;
        Contact contact;
        Channel channel;
        String mimeType;

        public ShareAsyncTask(Context context, Uri uri, Contact contact, Channel channel, String mimType) {
            this.contextWeakReference = new WeakReference<>(context);
            this.uri = uri;
            this.contact = contact;
            this.channel = channel;
            this.mimeType = mimType;
            this.fileClientService = new FileClientService(context);
        }

        @Override
        protected File doInBackground() {
            if (contextWeakReference != null) {
                Context context = contextWeakReference.get();
                return new FileClientService(context).saveFileToApplozicLocalStorage(uri, mimeType);
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (contextWeakReference != null) {
                Context context = contextWeakReference.get();
                if (file != null && file.exists() && context != null) {
                    Uri fileUri = Uri.parse(file.getAbsolutePath());
                    Intent sendAttachmentIntent = new Intent(context, MobiComAttachmentSelectorActivity.class);
                    if (channel != null) {
                        sendAttachmentIntent.putExtra(MobiComAttachmentSelectorActivity.GROUP_ID, channel.getKey());
                        sendAttachmentIntent.putExtra(MobiComAttachmentSelectorActivity.GROUP_NAME, channel.getName());
                    } else if (contact != null) {
                        sendAttachmentIntent.putExtra(MobiComAttachmentSelectorActivity.USER_ID, contact.getUserId());
                        sendAttachmentIntent.putExtra(MobiComAttachmentSelectorActivity.DISPLAY_NAME, contact.getDisplayName());
                    }
                    if (fileUri != null) {
                        sendAttachmentIntent.putExtra(MobiComAttachmentSelectorActivity.URI_LIST, fileUri);
                    }
                    context.startActivity(sendAttachmentIntent);
                }
            }
        }
    }
}



