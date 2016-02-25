package mu.lab.tufeedback.common;

import android.content.Context;
import android.content.Intent;

import com.umeng.fb.push.FBMessage;
import com.umeng.fb.push.FeedbackPush;

import mu.lab.tufeedback.ui.UmengFeedbackActivity;

/**
 * 1. You should init TUFeedback in Application;
 * 2. You should start feedback push service in main activity;
 * 3. You should use openFeedbackActivity to start a custom feedback activity.
 * Created by coderhuhy on 15/10/10.
 */
public class TUFeedback {

    private TUFeedback() {}

    /**
     * You should init feedback push an feedback in application with init(context)
     * @param context Application context
     */
    public static void init(Context context) {
        FeedbackPush.getInstance(context).init(UmengFeedbackActivity.class, true);
        FeedbackFactory.init(context);
    }

    /**
     * Start feedback push service in main activity
     */
    public static void start() {
        DefaultFeedbackAgent agent = FeedbackFactory.getAgent();
        agent.openFeedbackPush();
        agent.sync(UmengFeedbackActivity.class);
    }

    /**
     * @param context Start custom feedback activity
     */
    public static void openFeedbackActivity(Context context) {
        Intent intent = new Intent(context, UmengFeedbackActivity.class);
        context.startActivity(intent);
    }

    /**
     * @param context Context
     * @param custom Custom message
     * @return Return this custom message is feedback or not
     */
    public static boolean isFeedbackMessage(Context context, String custom) {
        FBMessage message = new FBMessage(custom);
        return FeedbackPush.getInstance(context).dealFBMessage(message);
    }

}
