package xyz.kymirai.translator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by yuanjl
 */
public class LeftAndRightDeleteListView extends ListView {
    private int slidePosition;
    /**
     * The finger presses the coordinates of X.
     */
    private int downY;
    /**
     * The finger presses the coordinates of Y.
     */
    private int downX;
    /**
     * Screen width.
     */
    private int screenWidth;
    /**
     * ListView item.
     */
    private View itemView;
    /**
     * Sliding class.
     */
    private Scroller scroller;
    private static final int SNAP_VELOCITY = 600;
    /**
     * Speed tracking object.
     */
    private VelocityTracker velocityTracker;
    /**
     * Whether to respond to sliding, the default is not responding.
     */
    private boolean isSlide = false;
    /**
     * Think of the minimum distance the user is sliding.
     */
    private int mTouchSlop;
    /**
     * Remove the callback interface after the item.
     */
    private RemoveListener mRemoveListener;
    /**
     * Used to indicate the direction in which item is slid out of the screen.
     * left or right, marked with an enumeration value.
     */
    private RemoveDirection removeDirection;

    // Slide the enumeration value in the delete direction
    public enum RemoveDirection {
        RIGHT, LEFT;
    }


    public LeftAndRightDeleteListView(Context context) {
        this(context, null);
    }

    public LeftAndRightDeleteListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftAndRightDeleteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * Set the callback interface for sliding deletion.
     *
     * @param removeListener
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    /**
     * Distribute the event, the main thing is to determine the click is the item.
     * and through the postDelayed to set the response to the left and right sliding events.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);

                // If scroller scrolling is not over, we return directly
                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();
                slidePosition = pointToPosition(downX, downY);
                // Invalid position, do not do anything
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(event);
                }

                // Get the item view we clicked on
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                        || (Math.abs(event.getX() - downX) > mTouchSlop && Math
                        .abs(event.getY() - downY) < mTouchSlop)) {
                    isSlide = true;
                    //return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * Swipe to the right, getScrollX () returns the distance from the left edge.
     * that is. the distance from the left edge of the View to the beginning of the slide.
     * so slide to the right to a negative value.
     */
    private void scrollRight() {
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (screenWidth + itemView.getScrollX());
        // Call the startScroll method to set some scrolling parameters, we call scrollTo in the computeScroll () method to scroll the item
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate(); // Refresh itemView
    }

    /**
     * Swipe left, according to the above we know that the left slide to a positive value.
     */
    private void scrollLeft() {
        removeDirection = RemoveDirection.LEFT;
        final int delta = (screenWidth - itemView.getScrollX());
        // Call the startScroll method to set some scrolling parameters, we call scrollTo in the computeScroll () method to scroll the item
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                Math.abs(delta));
        postInvalidate(); // Refresh itemView
    }

    /**
     * According to the distance of the finger to scroll the itemView
     * to determine whether to scroll to the starting position or to the left or right.
     */
    private void scrollByDistanceX() {
        //If the distance to the left is greater than one-half of the screen, let it be deleted
        if (itemView.getScrollX() >= screenWidth / 2) {
            scrollLeft();
        } else if (itemView.getScrollX() <= -screenWidth / 2) {
            scrollRight();
        } else {
            // Roll back to the original position, in order to steal lazy here is to call scrollto directly
            itemView.scrollTo(0, 0);
        }

    }

//    @Override
//    public boolean performClick() {
//        return super.performClick();
//    }

    /**
     * Handle the logic of dragging the ListView item.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
            requestDisallowInterceptTouchEvent(true);
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    onTouchEvent(cancelEvent);
                    int deltaX = downX - x;
                    downX = x;

                    if (y - downY > 120) {
                        itemView.scrollTo(0, 0);
                        break;
                    }

                    // Finger drag itemView scrolling, deltaX is greater than 0 to scroll left, less than 0 to roll right
                    itemView.scrollBy(deltaX, 0);

                    return true;  //The ListView does not scroll when dragging
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }
                    recycleVelocityTracker();
                    // When the fingers leave, they do not respond to the left and right scrolling
                    isSlide = false;
                    break;
            }
        }

        //Otherwise go directly to the ListView to handle the onTouchEvent event
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        // Call startScroll scroller.computeScrollOffset () returns true
        if (scroller.computeScrollOffset()) {
            // Let the ListView item scroll according to the current scroll offset
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
            // The callback interface is called when the scroll animation ends
            if (scroller.isFinished()) {
                if (mRemoveListener == null) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }
                itemView.scrollTo(0, 0);
                mRemoveListener.removeItem(removeDirection, slidePosition);
            }
        }
    }

    /**
     * Add the user's speed tracker
     *
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * Remove the user speed tracker
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * Get the X direction of the sliding speed.
     * more than 0 to the right slide, and vice versa to the left.
     *
     * @return
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return velocity;
    }

    /**
     * When the ListView item slides out of the screen, callback the interface
     * We need to remove the Item in the callback method removeItem () and then refresh the ListView.
     */
    public interface RemoveListener {
        public void removeItem(RemoveDirection direction, int position);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Set to Integer.MAX_VALUE >> 2 is listview all expanded
        int measureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        //Set to 1500 is set the height of the listview can only have 400 not all the expansion can achieve the effect of sliding
        int measureSpec1 = MeasureSpec.makeMeasureSpec(1500, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, measureSpec1);
    }
}