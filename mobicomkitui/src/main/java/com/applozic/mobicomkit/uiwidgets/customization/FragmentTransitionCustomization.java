package com.applozic.mobicomkit.uiwidgets.customization;

import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for storing and retrieving custom transitions for the various fragments
 *
 * <p>This class is intended to be used for fragment transitions/animations
 * with reference to {@link androidx.fragment.app.FragmentTransaction#setCustomAnimations(int, int, int, int)}</p>
 */
public class FragmentTransitionCustomization {
    private static volatile FragmentTransitionCustomization INSTANCE;

    private FragmentTransitionCustomization(FragmentActivity fragmentActivity, AlCustomizationSettings alCustomizationSettings) {
        HashMap<String, Map<String, String>> fragmentTransitionFilesMap = getFragmentTransitionsMapFrom(alCustomizationSettings);
        if (fragmentTransitionsMap == null) {
            fragmentTransitionsMap = new HashMap<>();
            for (FragmentTransitionCustomization.TransitionFragmentKeys fragmentKey : FragmentTransitionCustomization.TransitionFragmentKeys.values()) {
                if (fragmentTransitionFilesMap.containsKey(fragmentKey.keyForFragment)) {
                    fragmentTransitionsMap.put(fragmentKey.keyForFragment, getSingleFragmentTransitionResourceIdsObjectFromMap(fragmentActivity, fragmentTransitionFilesMap.get(fragmentKey.keyForFragment)));
                }
            }
        }
    }

    public static FragmentTransitionCustomization getInstance(FragmentActivity fragmentActivity, AlCustomizationSettings alCustomizationSettings) {
        if (INSTANCE == null) {
            synchronized (FragmentTransitionCustomization.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FragmentTransitionCustomization(fragmentActivity, alCustomizationSettings);
                }
            }
        }
        return INSTANCE;
    }

    private static final String enterTransitionHashMapKey = "enterTransition";
    private static final String exitTransitionHashMapKey = "exitTransition";
    private static final String popEnterTransitionHashMapKey = "popEnterTransition";
    private static final String popExitTransitionHashMapKey = "popExitTransition";

    //map with the resourceIds for the transition files for the various transitions
    private Map<String, SingleFragmentTransitionResourceIds> fragmentTransitionsMap;

    /**
     * get the respective transition from {@link FragmentTransitionCustomization#fragmentTransitionsMap}
     *
     * @param fragmentTag fragment tag, see static fragment tag strings in {@link com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService}
     * @return the fragment transition for the corresponding fragment
     */
    public SingleFragmentTransitionResourceIds getTransitionResourceIdsForFragment(String fragmentTag) {
        if (fragmentTransitionsMap != null && !fragmentTransitionsMap.isEmpty()) {
            return fragmentTransitionsMap.get(fragmentTag);
        }
        return null;
    }

    private HashMap<String, Map<String, String>> getFragmentTransitionsMapFrom(AlCustomizationSettings alCustomizationSettings) {
        HashMap<String, Map<String, String>> applozicFragmentTransitionFilesHashMap = new HashMap<>();
        if (fragmentTransitionFilesMapHasTransitions(alCustomizationSettings.getConversationFragmentTransitions())) {
            applozicFragmentTransitionFilesHashMap.put(TransitionFragmentKeys.CONVERSATION_FRAGMENT.keyForFragment, alCustomizationSettings.getConversationFragmentTransitions());
        }
        if (fragmentTransitionFilesMapHasTransitions(alCustomizationSettings.getConversationListFragmentTransitions())) {
            applozicFragmentTransitionFilesHashMap.put(TransitionFragmentKeys.QUICK_CONVERSATION_FRAGMENT.keyForFragment, alCustomizationSettings.getConversationListFragmentTransitions());
        }
        if (fragmentTransitionFilesMapHasTransitions(alCustomizationSettings.getProfileFragmentTransitions())) {
            applozicFragmentTransitionFilesHashMap.put(TransitionFragmentKeys.PROFILE_FRAGMENT.keyForFragment, alCustomizationSettings.getProfileFragmentTransitions());
        }
        if (fragmentTransitionFilesMapHasTransitions(alCustomizationSettings.getMessageInfoFragmentTransitions())) {
            applozicFragmentTransitionFilesHashMap.put(TransitionFragmentKeys.MESSAGE_INFO_FRAGMENT.keyForFragment, alCustomizationSettings.getMessageInfoFragmentTransitions());
        }
        return applozicFragmentTransitionFilesHashMap;
    }

    private boolean fragmentTransitionFilesMapHasTransitions(Map<String, String> transitionHashMap) {
        return transitionHashMap != null && !transitionHashMap.isEmpty() &&
                (!TextUtils.isEmpty(transitionHashMap.get(enterTransitionHashMapKey)) ||
                        !TextUtils.isEmpty(transitionHashMap.get(exitTransitionHashMapKey)) ||
                        !TextUtils.isEmpty(transitionHashMap.get(popEnterTransitionHashMapKey)) ||
                        !TextUtils.isEmpty(transitionHashMap.get(popExitTransitionHashMapKey)));
    }

    private SingleFragmentTransitionResourceIds getSingleFragmentTransitionResourceIdsObjectFromMap(FragmentActivity fragmentActivity, Map<String, String> singleFragmentTransitionFileNamesMap) {
        SingleFragmentTransitionResourceIds singleFragmentTransitionResourceIds = new SingleFragmentTransitionResourceIds();
        if (fragmentTransitionFilesMapHasTransitions(singleFragmentTransitionFileNamesMap)) {
            singleFragmentTransitionResourceIds.enterTransitionResourceId = getFragmentTransitionResourceIdFromName(fragmentActivity, singleFragmentTransitionFileNamesMap.get(enterTransitionHashMapKey));
            singleFragmentTransitionResourceIds.exitTransitionFileResourceId = getFragmentTransitionResourceIdFromName(fragmentActivity, singleFragmentTransitionFileNamesMap.get(exitTransitionHashMapKey));
            singleFragmentTransitionResourceIds.popEnterTransitionFileResourceId = getFragmentTransitionResourceIdFromName(fragmentActivity, singleFragmentTransitionFileNamesMap.get(popEnterTransitionHashMapKey));
            singleFragmentTransitionResourceIds.popExitTransitionFileResourceId = getFragmentTransitionResourceIdFromName(fragmentActivity, singleFragmentTransitionFileNamesMap.get(popExitTransitionHashMapKey));
        }
        return singleFragmentTransitionResourceIds;
    }

    private int getFragmentTransitionResourceIdFromName(FragmentActivity fragmentActivity, String animationFileName) {
        if (TextUtils.isEmpty(animationFileName) || fragmentActivity == null) {
            return 0;
        }
        return fragmentActivity.getResources().getIdentifier(animationFileName, "anim", fragmentActivity.getPackageName());
    }

    public static class SingleFragmentTransitionResourceIds {
        public int enterTransitionResourceId;
        public int exitTransitionFileResourceId;
        public int popEnterTransitionFileResourceId;
        public int popExitTransitionFileResourceId;
    }

    /**
     * For keeping the keys to the various fragments.
     *
     * <p>Used to index the HashMap containing custom transitions for the various Fragments.
     * See {@link FragmentTransitionCustomization#fragmentTransitionsMap}</p>
     */
    public enum TransitionFragmentKeys {
        CONVERSATION_FRAGMENT(ConversationUIService.CONVERSATION_FRAGMENT),
        QUICK_CONVERSATION_FRAGMENT(ConversationUIService.QUICK_CONVERSATION_FRAGMENT),
        PROFILE_FRAGMENT(ConversationUIService.USER_PROFILE_FRAMENT),
        MESSAGE_INFO_FRAGMENT(ConversationUIService.MESSGAE_INFO_FRAGMENT);

        private final String keyForFragment;

        TransitionFragmentKeys(String keyForFragment) {
            this.keyForFragment = keyForFragment;
        }
    }
}
