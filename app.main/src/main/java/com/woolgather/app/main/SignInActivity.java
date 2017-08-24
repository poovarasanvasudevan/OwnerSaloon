package com.woolgather.app.main;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.wangjie.shadowviewhelper.ShadowProperty;
import com.wangjie.shadowviewhelper.ShadowViewDrawable;
import com.woolgather.lib.kernel.Base;
import com.woolgather.lib.kernel.activity.BaseActivity;
import com.woolgather.lib.kernel.core.Prefs;

import net.wequick.small.Small;

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 5521;
    private Button            gplusSignInButton;
    private GoogleApiClient   mGoogleApiClient;
    private LinearLayout      signInHolder;
    private Button            fbSignInButton;
    private Button            twitterSignInButton;
    private ImageView         profilePic;
    private ProgressView      imageProgress;
    private AppCompatTextView nameText;


    @Override
    protected void onStart() {
        super.onStart();

        if(mGoogleApiClient !=null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient !=null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Base.initFont();

        setContentView(R.layout.activity_sign_in);
        initView();

        mGoogleApiClient = Base.getGoogleApiClient(this,this);

        gplusSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initView() {
        gplusSignInButton = (Button) findViewById(R.id.gplusSignInButton);
        signInHolder = (LinearLayout) findViewById(R.id.signInHolder);


        ShadowProperty sp = new ShadowProperty()
                .setShadowColor(0x77000000)
                .setShadowDy(ConvertUtils.dp2px(0.5f))
                .setShadowRadius(ConvertUtils.dp2px(3))
                .setShadowSide(ShadowProperty.TOP);

        ShadowViewDrawable sd = new ShadowViewDrawable(sp, Color.WHITE, 0, 0);
        ViewCompat.setBackground(signInHolder, sd);
        ViewCompat.setLayerType(signInHolder, ViewCompat.LAYER_TYPE_SOFTWARE, null);

        fbSignInButton = (Button) findViewById(R.id.fbSignInButton);
        twitterSignInButton = (Button) findViewById(R.id.twitterSignInButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            fbSignInButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    new IconicsDrawable(this, CommunityMaterial.Icon.cmd_facebook),
                    null, null, null
            );

            twitterSignInButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    new IconicsDrawable(this, CommunityMaterial.Icon.cmd_twitter),
                    null, null, null
            );

            gplusSignInButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    new IconicsDrawable(this, CommunityMaterial.Icon.cmd_google_plus),
                    null, null, null
            );
        }

        profilePic = (ImageView) findViewById(R.id.profilePic);
        imageProgress = (ProgressView) findViewById(R.id.imageProgress);
        nameText = (AppCompatTextView) findViewById(R.id.nameText);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorMessage() != null) {
            ToastUtils.showLong(connectionResult.getErrorMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();

            Prefs.with(getApplicationContext()).write("display_name",acct.getDisplayName());
            Prefs.with(getApplicationContext()).write("email",acct.getEmail());
            Prefs.with(getApplicationContext()).write("profilePic",acct.getPhotoUrl().toString());


            Ion.with(getApplicationContext())
                    .load(acct.getPhotoUrl().toString())
                    .noCache()
                    .progressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            imageProgress.setProgress((downloaded / total) * 100);
                        }
                    })
                    .withBitmap()
                    .animateIn(R.anim.image_anim)
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (e == null) {
                                profilePic.setImageBitmap(Base.getCircleBitmap(result));
                            }
                        }
                    });
            nameText.setText(String.format("Hi %s", acct.getDisplayName()));

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Small.openUri("main",SignInActivity.this);
                }
            },4000);
        } else {
            // Signed out, show unauthenticated UI.
            ToastUtils.showLong("Failed");
        }
    }


}
