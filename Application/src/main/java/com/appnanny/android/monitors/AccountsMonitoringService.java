<!--
 Copyright 2016 The Android Open Source Project

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
//Changed by Schmeelk and Aho
///*Copyright 2017 Suzanna Schmeelk and Alfred Aho

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/


package com.appnanny.android.monitors;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Vibrator;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import com.google.android.gms.auth.*;
import com.google.android.gms.auth.api.signin.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.model.people.Person;

import com.appnanny.android.MonitorPickerFragment;
import com.appnanny.android.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import static android.R.attr.data;
import static java.security.AccessController.getContext;

public class AccountsMonitoringService extends Service {

    private static final String TAG = "AccountsMonService";

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        AccountsMonitoringService getService() {
            return AccountsMonitoringService.this;
        }
    }

    Intent accountsStatus;
    IntentFilter ifilter;
    IntentFilter filter;

    @Override
    public void onCreate() {
        Log.i(TAG, String.format("[AccountsMonServ]- Create"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[AccountsMonServ]- Start"));
        //final AccountManager accountManager = AccountManager.get(getContext());
        //final String accountType = AuthenticatorService.ACCOUNT_TYPE;

        //final Account[] availableAccounts = accountManager.getAccountsByType(accountType);
        //for (final Account availableAccount : availableAccounts) {
        //    final AccountManagerFuture<Boolean> booleanAccountManagerFuture = accountManager.removeAccount(availableAccount, null, null);
            //assertTrue("Impossible to delete existing account for this application", booleanAccountManagerFuture.getResult(1, TimeUnit.SECONDS));
        //}

        AccountManager am = AccountManager.get(this); // "this" references the current Context
        //Account[] accounts = am.getAccountsByType("com.google");
        PackageManager pm = this.getPackageManager();
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        int ga = pm.checkPermission("android.permission.GET_ACCOUNTS", this.getPackageName());
        //checkPermission("android.permission.GET_ACCOUNTS", successCallback, errorCallback);
        Log.i(TAG, String.format("[AccountsMonServ]- length: "+ accounts.length));
        for (int i = 0; i < accounts.length; i++){
            Log.i(TAG, String.format("[AccountsMonServ]- "+ accounts[i].name + " " + accounts[i].type));
        }
        //Log.i(TAG, String.format("[AccountsMonServ]- "));

        //GoogleSignInResult result = GoogleSignInApi.getSignInResultFromIntent(data);
        //GoogleSignInAccount acct = result.getSignInAccount();
        //String personName = acct.getDisplayName();
        //String personGivenName = acct.getGivenName();
        //String personFamilyName = acct.getFamilyName();
        //String personEmail = acct.getEmail();
        //String personId = acct.getId();
        //Uri personPhoto = acct.getPhotoUrl();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInOptions.Builder gssob = new GoogleSignInOptions.Builder(gso);

/*        GoogleSignInResult result = com.google.android.gms.auth.api.signin.GoogleSignInAccount.getSignInResultFromIntent(data);
        GoogleSignInAccount acct = result.getSignInAccount();
        String personName = acct.getDisplayName();
        String personGivenName = acct.getGivenName();
        String personFamilyName = acct.getFamilyName();
        String personEmail = acct.getEmail();
        String personId = acct.getId();
        Uri personPhoto = acct.getPhotoUrl();


*/

        AccountManager actmg = AccountManager.get(this);
        Log.i(TAG, String.format("[AccountsMonServ]- "+ actmg.toString()));
        Log.i(TAG, String.format("[AccountsMonServ]- "+ actmg.getAccounts()));
        //Log.i(TAG, String.format("[AccountsMonServ]- "+ actmg.addAccount()));
        /*
        String accountName = "name";
        String accountPassword = "password";
        final Account account = new Account(accountName, "account_type");

        AccountManager mAccountManager = AccountManager.get(getBaseContext());

        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String refreshToken = intent.getStringExtra(AccountManager.KEY_USERDATA);
        //String authTokenType = AccountGeneral.ACCOUNT_TYPE;
        String authTokenType = AccountManager.KEY_ACCOUNTS;
        mAccountManager.addAccountExplicitly(account, accountPassword, null);
        mAccountManager.setAuthToken(account, authTokenType, authToken);
        mAccountManager.setUserData(account, "refreshToken", refreshToken);
        Log.i(TAG, String.format("[AccountsMonServ]- "+ actmg.getAccounts()));
        */
        Log.i(TAG, String.format("[AccountsMonServ]- "+ gso.toString()));
        Log.i(TAG, String.format("[AccountsMonServ]- "+ gso.zzmI()));
        Log.i(TAG, String.format("[AccountsMonServ]- "+ gso.zzmR()));
        Log.i(TAG, String.format("[AccountsMonServ]- "+ gssob.toString()));
        //Log.i(TAG, String.format("[AccountsMonServ]- "+ gso.zzmS()));
        //Log.i(TAG, String.format("[AccountsMonServ]- "+ gso.getAccount().toString()));
        //String name = gso.getAccount().toString();
        Log.i(TAG, String.format("[AccountsMonServ]- End "));
        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }


}
