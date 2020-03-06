package xyz.kymirai.translator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class GSSwipeRefreshLayout extends SwipeRefreshLayout {

    private float mInitialDownYValue = 0;
    private int miniTouchSlop;

    public GSSwipeRefreshLayout(Context context) {
        super(context);
    }

    public GSSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        miniTouchSlop = 200;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownYValue = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float yDiff = ev.getY() - mInitialDownYValue;
                if (yDiff < miniTouchSlop) {
                    return false;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}