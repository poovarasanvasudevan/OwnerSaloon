package com.woolgather.lib.kernel;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatTextView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by poovarasanv on 21/8/17.
 *
 * @author poovarasanv
 * @project MySaloon
 * @on 21/8/17 at 6:01 PM
 */

public class Base {

    public static GoogleApiClient getGoogleApiClient(Activity context, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .build();


        return googleApiClient;
    }

    public static Bitmap getCircleBitmap(Bitmap bm) {

        int sice = Math.min((bm.getWidth()), (bm.getHeight()));

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int   color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect  rect  = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void showToast(Context context, CharSequence charSequence) {
        LinearLayout              linearLayout   = new LinearLayout(context);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(linLayoutParam);

        AppCompatTextView         appCompatTextView = new AppCompatTextView(context);
        LinearLayout.LayoutParams linLayoutParam2   = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linLayoutParam2.setMargins(16, 16, 16, 16);
        appCompatTextView.setLayoutParams(linLayoutParam2);

        appCompatTextView.setTextColor(Color.WHITE);
        linearLayout.setBackgroundColor(Color.parseColor("#999999"));
        linearLayout.addView(appCompatTextView);


        appCompatTextView.setText(charSequence);
        ToastUtils.showCustomLongSafe(linearLayout);
    }


    public static void initFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("segoeuii.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
