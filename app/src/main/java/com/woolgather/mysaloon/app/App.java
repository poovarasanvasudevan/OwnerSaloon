package com.woolgather.mysaloon.app;

import android.app.Application;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.cache.ResponseCacheMiddleware;
import com.koushikdutta.ion.Ion;

import net.wequick.small.Small;

import java.io.IOException;

/**
 * Created by poovarasanv on 21/8/17.
 *
 * @author poovarasanv
 * @project MySaloon
 * @on 21/8/17 at 8:54 AM
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);

        Ion.getDefault(this).configure().setLogging("NetworkLOG", Log.DEBUG);

        try {
            ResponseCacheMiddleware.addCache(AsyncHttpClient.getDefaultInstance(),
                    getFileStreamPath("NetworkCache"),
                    1024 * 1024 * 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Small.preSetUp(this);
        Small.setLoadFromAssets(true);
    }
}
