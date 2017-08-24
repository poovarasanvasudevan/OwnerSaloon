package com.woolgather.app.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.LogUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by poovarasanv on 23/8/17.
 *
 * @author poovarasanv
 * @project MySaloon
 * @on 23/8/17 at 2:50 PM
 */

public class WebMapActivity extends AppCompatActivity {


    private WebView web;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_web);


        initView();

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);

        web.clearFormData();
        web.clearHistory();
        web.clearMatches();
        web.clearCache(true);
        web.clearSslPreferences();
clearCookies(
        this
);
        // firstname=test&country=United%20States&amount=100&lastname=testln&phone=5854412&email=sg%40gmail.com&address=adasd&
        // city=New%20york&state=NY&pincode=133434&memberid=asdase3&citizenship=United%20States&passport=123&pan=abcd123
        Ion.with(this)
                .load("https://donations.heartfulness.org/kanha-event")
                //.basicAuthentication("civiwp","civiwp")
               // .addHeader("Authorization","Basic Y2l2aXdwOmNpdml3cA==")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .setBodyParameter("firstname","test")
                .setBodyParameter("country","India")
                .setBodyParameter("amount","100")
                .setBodyParameter("lastname","testln")
                .setBodyParameter("phone","5854412")
                .setBodyParameter("email","poosan9@gmail.com")
                .setBodyParameter("address","sdfdsfdsfdsf")
                .setBodyParameter("city","New York")
                .setBodyParameter("state","NY")
                .setBodyParameter("pincode","5465465")
                .setBodyParameter("memberid","asdase3")
                .setBodyParameter("passport","65465465486")
                .setBodyParameter("pan","abcd123")
                .setBodyParameter("initiative","donation-kanha-building-mobile")
                .setBodyParameter("mode","mobile")
                .setBodyParameter("citizenship","India")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        LogUtils.d(result);
                        LogUtils.d(web.getOriginalUrl());
                        web.loadData(result,"text/html", "UTF-8");
                    }
                });
    }

    public void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr =CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void initView() {
        web = (WebView) findViewById(R.id.web);
    }
}
