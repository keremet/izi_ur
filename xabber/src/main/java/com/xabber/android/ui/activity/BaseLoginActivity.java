package com.xabber.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.xabber.android.R;
import com.xabber.android.data.xaccount.XabberAccount;
import com.xabber.android.data.xaccount.XabberAccountManager;
import com.xabber.android.ui.dialog.AddEmailDialogFragment;
import com.xabber.android.ui.dialog.ConfirmEmailDialogFragment;
import com.xabber.android.ui.fragment.XAccountLinksFragment;
import com.xabber.android.ui.helper.OnSocialBindListener;

import java.util.Collections;



import rx.subscriptions.CompositeSubscription;

/**
 * Created by valery.miller on 31.07.17.
 */

public abstract class BaseLoginActivity extends ManagedActivity implements
        GoogleApiClient.OnConnectionFailedListener, XAccountLinksFragment.Listener,
        AddEmailDialogFragment.Listener, ConfirmEmailDialogFragment.Listener, OnSocialBindListener {

    private final static String LOG_TAG = BaseLoginActivity.class.getSimpleName();

    // facebook auth
    private CallbackManager callbackManager;

    // twitter auth
    private TwitterAuthClient twitterAuthClient;
    private Callback<TwitterSession> twitterSessionCallback;

    // google auth
    private static final int RC_SIGN_IN = 9001;

    private Gson gson = new Gson();
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // social auth
        initFacebookAuth();
        initTwitterAuth();
        initGoogleAuth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // facebook auth
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        // twitter auth
        if (twitterAuthClient != null)
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        // google auth
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void loginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile"));
    }

    private void initGoogleAuth() {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private void handleSignInResult(GoogleSignInResult result) {

    }

    private void initFacebookAuth() {

    }

    private void initTwitterAuth() {

    }

    protected abstract void onSocialAuthSuccess(final String provider, final String credentials);

    protected abstract void showProgress(String title);

    protected abstract void hideProgress();

    protected void handleError(Throwable throwable, String errorContext, String logTag) {

    }

    /** SYNCHRONIZATION */

    protected void synchronize(boolean needGoToMainActivity) {
        XabberAccount account = XabberAccountManager.getInstance().getAccount();
        if (account != null && account.getToken() != null) {
            showProgress(getResources().getString(R.string.progress_title_sync));
            getAccountWithUpdate(account.getToken(), needGoToMainActivity);
        } else {
            Toast.makeText(BaseLoginActivity.this, R.string.sync_fail, Toast.LENGTH_SHORT).show();
        }
    }

    protected void getAccountWithUpdate(String token, final boolean needGoToMainActivity) {

    }

    protected void goToMainActivity() {
        Intent intent = ContactListActivity.createIntent(BaseLoginActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    protected abstract void onSynchronized();

    /** ADD EMAIL */

    protected void resendConfirmEmail(String email) {

    }


    /** CONFIRM EMAIL */

    protected void confirmEmail(String code) {

    }

    /** DELETE EMAIL */

    protected void deleteEmail(int emailId) {

    }


    /** SOCIAL BIND / UNBIND */

    protected void bindSocial(String provider, String credentials) {

    }

    protected void unbindSocial(String provider) {

    }

    /** XABBER ACCOUNT LINKS LISTENER */

    @Override
    public void onBindClick(String provider) {

    }

    @Override
    public void onSocialUnbindClick(String provider) {
        unbindSocial(provider);
    }

    @Override
    public void onAddEmailClick(String email) {
        resendConfirmEmail(email);
    }

    @Override
    public void onDeleteEmailClick(int emailId) {
        deleteEmail(emailId);
    }

    @Override
    public void onResendCodeClick(String email) {
        resendConfirmEmail(email);
    }

    @Override
    public void onConfirmClick(String email, String code) {
        confirmEmail(code);
    }
}
