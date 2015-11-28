package mu.lab.tufeedback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by coderhuhy on 15/9/25.
 */
public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {

    float mWorkingHeight = -1;
    int mPassingPointerId = -1;

    /**
     * Prevent SwipeRefreshLayout from blocking child views receive events
     * which starts below {@code height} (Y value) on the screen.
     *
     * @param height The pixel value which SwipeRefreshLayout will responses to from the top.
     */
    public void setWorkingHeight(float height) {
        mWorkingHeight = height;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mWorkingHeight != -1 && ev.getY() >= mWorkingHeight) {
                    mPassingPointerId = ev.getPointerId(0);
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPassingPointerId != -1 && ev.findPointerIndex(mPassingPointerId) != -1) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mPassingPointerId != -1 && ev.findPointerIndex(mPassingPointerId) != -1) {
                    mPassingPointerId = -1;
                    return false;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
