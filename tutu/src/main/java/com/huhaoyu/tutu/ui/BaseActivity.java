package com.huhaoyu.tutu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * Base activity
 * Created by coderhuhy on 15/11/21.
 */
public class BaseActivity extends AppCompatActivity {

    private long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: umeng components
        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeStamp = System.currentTimeMillis();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long interval = System.currentTimeMillis() - timeStamp;
        MobclickAgent.setSessionContinueMillis(interval);
        MobclickAgent.onPause(this);
    }

    private void startOtherActivity(Intent intent, boolean isFinish) {
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * start other activity
     * @param activity start which activity
     * @param isFinish do finish current activity
     */
    protected void startOtherActivity(Class activity, boolean isFinish) {
        Intent intent = new Intent(this, activity);
        startOtherActivity(intent, isFinish);
    }

    /**
     * start other activity and won't finish current activity
     * @param activity start which activity
     */
    protected void startOtherActivity(Class activity) {
        startOtherActivity(activity, false);
    }

    /**
     * @param activity start activity for result
     * @param requestCode request code
     */
    protected void startOtherActivityForResult(Class activity, int requestCode) {
        startOtherActivityForResult(activity, null, requestCode);
    }

    /**
     * @param activity start which activity
     * @param data transmit bundle data
     * @param requestCode request code for result
     */
    protected void startOtherActivityForResult(Class activity, Bundle data, int requestCode) {
        Intent intent = new Intent(this, activity);
        if (data != null) {
            intent.putExtras(data);
        }
        startActivityForResult(intent, requestCode);
    }

}
