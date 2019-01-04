package com.xabber.android.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xabber.android.BuildConfig;
import com.xabber.android.R;
import com.xabber.android.data.xaccount.XMPPAccountSettings;
import com.xabber.android.data.xaccount.XabberAccount;
import com.xabber.android.data.xaccount.XabberAccountManager;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by valery.miller on 27.07.17.
 */

public class XabberAccountInfoFragment extends Fragment {

    private static final String LOG_TAG = XabberAccountInfoFragment.class.getSimpleName();

    private TextView tvAccountName;
    private TextView tvAccountUsername;
    private TextView tvLanguage;
    private TextView tvPhone;

    private TextView tvLinks;
    private View viewShowLinks;
    private View viewLinks;
    private ImageView ivChevron;

    private ProgressBar progressBar;
    private View progressView;
    private Fragment fragmentSync;
    private FragmentTransaction fTrans;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public static XabberAccountInfoFragment newInstance() {
        XabberAccountInfoFragment fragment = new XabberAccountInfoFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xaccount_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.toolbarProgress);
        tvPhone = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvAccountName = (TextView) view.findViewById(R.id.tvAccountName);
        tvAccountUsername = (TextView) view.findViewById(R.id.tvAccountUsername);
        tvLanguage = (TextView) view.findViewById(R.id.tvLanguage);

        progressView = view.findViewById(R.id.progressView);
        ivChevron = view.findViewById(R.id.ivChevron);
        tvLinks = view.findViewById(R.id.tvLinks);
        viewLinks = view.findViewById(R.id.viewLinks);
        viewShowLinks = view.findViewById(R.id.viewShowLinks);
        viewShowLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLinks.setVisibility(viewLinks.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE );
                ivChevron.setImageResource(viewLinks.getVisibility() == View.VISIBLE
                        ? R.drawable.ic_chevron_up : R.drawable.ic_chevron_down);
            }
        });

        Fragment linksFragment = XAccountLinksFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.accountLinksFrame, linksFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeForXabberAccount();
        getSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeSubscription.clear();
    }

    public void updateData(XabberAccount account) {
        if (account == null) return;

        String accountName = account.getFullUsername();

        tvAccountName.setText(accountName);
        tvAccountUsername.setText("Free account");

        if (account.getLanguage() != null && !account.getLanguage().equals("")) {
            tvLanguage.setText(account.getLanguage());
            tvLanguage.setVisibility(View.VISIBLE);
        } else tvLanguage.setVisibility(View.GONE);

        if (BuildConfig.FLAVOR.equals("ru")) {
            tvPhone.setVisibility(View.VISIBLE);
            String phone = account.getPhone();
            tvPhone.setText(phone != null ? phone : getString(R.string.no_phone));
        } else tvPhone.setVisibility(View.GONE);

        // links
        if (account.getSocialBindings().size() > 0 || account.getEmails().size() > 0)
            tvLinks.setText(R.string.title_linked_accounts);
        else tvLinks.setText(R.string.title_no_linked_accounts);
    }

    public void updateLastSyncTime() {
        if (fragmentSync != null && fragmentSync.isVisible())
            ((AccountSyncFragment) fragmentSync).updateLastSyncTime();
    }

    private void subscribeForXabberAccount() {
        compositeSubscription.add(XabberAccountManager.getInstance().subscribeForAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<XabberAccount>() {
                    @Override
                    public void call(XabberAccount account) {
                        updateData(account);
                    }
                }).subscribe());
    }

    public void showProgressInAccount(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    /** GET SETTINGS */

    public void getSettings() {

    }


}
