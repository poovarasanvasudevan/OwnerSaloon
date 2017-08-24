package com.woolgather.mysaloon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.wequick.small.Small;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Small.setUp(this, new net.wequick.small.Small.OnCompleteListener() {

            @Override
            public void onComplete() {
                Small.openUri("main/signin", MainActivity.this);
                finish();
            }
        });
    }
}
