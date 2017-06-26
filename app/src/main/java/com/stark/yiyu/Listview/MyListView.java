package com.stark.yiyu.Listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.stark.yiyu.R;
import com.stark.yiyu.Util.Elastic;
import com.stark.yiyu.toast.ListAnimImageView;

public class MyListView extends ListView implements OnScrollListener {

    //等待释放
    private final static int RELEASE_TO_REFRESH = 0;
    //下拉充能
    private final static int PULL_TO_REFRESH = 1;
    //正在刷新
    private final static int REFRESHING = 2;
    //刷新完成
    private final static int DONE = 3;

    private final static int PULL_TO_UP = 5;

    private final static int LOADING = 4;

    private final static int RADIO = 2;

    private LayoutInflater mInflater;
    private LinearLayout mHeadView;
    private ImageView mArrowImageView;
    private TextView headHint;
    private ListAnimImageView mProgressBar;

    private RotateAnimation mAnimation;
    private RotateAnimation mReverseAnimation;
    private boolean mIsRecored;
    private int mHeadContentHeight;
    private int mStartX;
    private int mStartY;
    private int mendX;
    private int mendY;
    private int mFirstItemIndex;
    private int mState;
    private int Offset;
    private boolean mIsBack;
    private boolean mISRefreshable;
    private Context context;
    private OnRefreshListener mRefreshListener;
    private OnBackListener mBackListener;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }
    public void ReplaceHead(int i){
        mHeadView = (LinearLayout) mInflater.inflate(i, null);
    }
    private void init() {
        mInflater = LayoutInflater.from(context);
        mHeadView = (LinearLayout) mInflater.inflate(R.layout.list_head, null);
        mArrowImageView = (ImageView) mHeadView.findViewById(R.id.head_arrowImageView);
        mProgressBar = (ListAnimImageView) mHeadView.findViewById(R.id.head_progressBar);
        headHint=(TextView)mHeadView.findViewById(R.id.refresh_head_text);

        measureView(mHeadView);
        mHeadContentHeight = mHeadView.getMeasuredHeight();
        mHeadView.setPadding(0, -mHeadContentHeight, 0, 0);
        mHeadView.invalidate();
        addHeaderView(mHeadView, null, false);
        setOnScrollListener(this);

        mAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(250);
        mAnimation.setFillAfter(true);

        mReverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseAnimation.setInterpolator(new LinearInterpolator());
        mReverseAnimation.setDuration(250);
        mReverseAnimation.setFillAfter(true);

        mState = DONE;
        mISRefreshable = false;
    }

    private void measureView(View child) {
        android.view.ViewGroup.LayoutParams params = child.getLayoutParams();
        if(params == null) {
            params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if(lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstItemIndex = firstVisibleItem;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    private void onRefresh() {
        if(mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    public interface OnBackListener{
        void onBack();
    }
    private void onBack(){
        if(mBackListener!=null){
            mBackListener.onBack();
        }
    }

    public void onRefreshComplete() {
        mState = DONE;
        changeHeaderViewByState();
    }
    public void Reset(){
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(VISIBLE);
        mHeadView.setPadding(0, -mHeadContentHeight, 0, 0);
        this.setPadding(0, 0, 0, 0);
        mProgressBar.setVisibility(GONE);
        mArrowImageView.setImageResource(R.drawable.tianqing);
    }
    public void setonBackListener(OnBackListener onBackListener) {
        this.mBackListener = onBackListener;
    }
    public void setonRefreshListener(OnRefreshListener onRefreshListener) {
        this.mRefreshListener = onRefreshListener;
        mISRefreshable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mISRefreshable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(mFirstItemIndex == 0 && !mIsRecored) {
                        mIsRecored = true;
                    }
                    mStartX = (int) ev.getX();
                    mStartY = (int) ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    Offset=(mendY-mStartY)/RADIO;
                    if(mState != REFRESHING && mState != LOADING) {
                        switch (mState){
                            case DONE:
                                break;
                            case PULL_TO_UP:
                            case PULL_TO_REFRESH:
                                mState = DONE;
                                changeHeaderViewByState();
                                break;
                            case RELEASE_TO_REFRESH:
                                mState = REFRESHING;
                                changeHeaderViewByState();
                                onRefresh();
                                break;
                        }
                    }
                    mIsBack = false;
                    mIsRecored = false;
                    if(mendY-mStartY<mendX-mStartX){
                        onBack();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    mendX = (int) ev.getX();
                    mendY = (int) ev.getY();
                    if(!mIsRecored && mFirstItemIndex == 0) {
                        mIsRecored = true;
                        mStartY = mendY;
                    }
                    if(mState != REFRESHING && mIsRecored && mState != LOADING) {
                        if(mState == RELEASE_TO_REFRESH) {
                            setSelection(0);
                            if((mendY - mStartY)/RADIO < mHeadContentHeight && (mendY - mStartY) > 0) {
                                mState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            } else if(mendY - mStartY <= 0) {
                                mState = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        if(mState == PULL_TO_REFRESH) {
                            setSelection(0);
                            if((mendY - mStartY)/RADIO >= mHeadContentHeight) {
                                mState = RELEASE_TO_REFRESH;
                                mIsBack = true;
                                changeHeaderViewByState();
                            }else if(mendY - mStartY <= 0) {
                                mState = PULL_TO_UP;
                                changeHeaderViewByState();
                            }
                        }
                        if(mendY - mStartY <= 0) {
                            mState = PULL_TO_UP;
                            changeHeaderViewByState();
                        }
                        if(mState == DONE||mState==PULL_TO_UP) {
                            if(mendY - mStartY > 0) {
                                mState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                        }
                        if(mState == PULL_TO_UP){
                            this.setPadding(0, (mendY - mStartY) / RADIO, 0, 0);
                        }
                        if(mState == PULL_TO_REFRESH) {
                            mHeadView.setPadding(0, (mendY - mStartY)/RADIO - mHeadContentHeight, 0, 0);
                        }

                        if(mState == RELEASE_TO_REFRESH) {
                            mHeadView.setPadding(0, (mendY - mStartY)/RADIO - mHeadContentHeight, 0, 0);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void changeHeaderViewByState() {
        switch (mState) {
            case PULL_TO_REFRESH:
                this.setPadding(0,0,0,0);
                mHeadView.setPadding(0,-mHeadContentHeight,0,0);
                mProgressBar.setVisibility(GONE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(VISIBLE);
                headHint.setText("下拉刷新");
                if(mIsBack) {
                    mIsBack = false;
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mReverseAnimation);
                } else {
                }
                break;
            case PULL_TO_UP:
                this.setPadding(0,0,0,0);
                mHeadView.setPadding(0,-mHeadContentHeight,0,0);
                mArrowImageView.clearAnimation();
                mProgressBar.setVisibility(GONE);
                mArrowImageView.setImageResource(R.drawable.tianqing);
                break;
            case DONE:
                if(Offset>0) {
                    Elastic.setRefreshUp(Offset, mHeadView, mHeadContentHeight);
                }else{
                    Elastic.set(Offset, this);
                }
                Offset=0;
                mArrowImageView.clearAnimation();
                mProgressBar.setVisibility(GONE);
                mArrowImageView.setImageResource(R.drawable.tianqing);
                break;

            case REFRESHING:
                Elastic.setRefreshUp(Offset - mHeadContentHeight, mHeadView);
                mProgressBar.setVisibility(VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(GONE);
                Offset=mHeadContentHeight;
                headHint.setText("正在刷新...");
                break;

            case RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
                mArrowImageView.clearAnimation();
                mArrowImageView.startAnimation(mAnimation);
                headHint.setText("释放刷新");
                break;
            default:
                break;
        }
    }
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if(deltaY<0&&!isTouchEvent) {
            this.Reset();
            if(-deltaY>mHeadContentHeight){
                mState = REFRESHING;
                changeHeaderViewByState();
                onRefresh();
            }else {
                Elastic.set(-deltaY, mHeadView, mHeadContentHeight);
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }
}