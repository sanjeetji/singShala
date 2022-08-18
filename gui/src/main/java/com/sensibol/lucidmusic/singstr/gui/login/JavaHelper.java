package com.sensibol.lucidmusic.singstr.gui.login;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

public class JavaHelper {

    public static void printHashKey(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.sensibol.lucidmusic.singstr.gui", PackageManager.GET_SIGNATURES);
            Timber.e("========= Package name is :: "+context.getPackageName());
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d( "printHashKey() Hash Key: " , hashKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("====== Exception is :", e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("====== Exception One is:", e.getMessage());
        }

    }

}
