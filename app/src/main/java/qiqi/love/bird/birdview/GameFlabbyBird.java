package qiqi.love.bird.birdview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import qiqi.love.bird.R;
import qiqi.love.bird.Util;

/**
 * Created by iscod on 2016/5/9.
 */
public class GameFlabbyBird extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private Thread t;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;
    /**
     * 当前view的尺寸
     */
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();
    /**
     * 背景
     */
    private Bitmap mBg;
    /**
     * *********鸟相关**********
     */
    private Bird mBird;
    private Bitmap mBirdBitmap;
    /**
     * **********地板相关*******
     */
    private Floor mFloor;
    private Bitmap mFloorBg;
    private Paint mPaint;
    /**
     * 地板移动速度
     */
    private int mSpeed;
    /**
     * **********管道相关********
     */
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRect;
    private int mPipeWidth;
    /**
     * 管道宽度60dp
     */
    private static final int PIPE_WIDTH = 60;
    private List<Pipe> mPipes = new ArrayList<Pipe>();

    private Grades mGrades;
    /**
     * 记录游戏状态
     */
    private GameStatus mStatus = GameStatus.WAITING;
    /**
     * 触摸上升的距离，因为是上升，所以是负值
     */
    private static final int TOUCH_UP_SIZE = -16;
    /**
     * 将上升的距离转化为px,这里多储存一个变量。变量在run中计算。
     */
    private final int mBirdUpDis = Util.dp2px(getContext(), TOUCH_UP_SIZE);
    private int mTmpBirdDis;
    private final int mAutoDownSpeed = Util.dp2px(getContext(), 2);
    /**
     * 两个管道之间的距离
     */
    private final int PIPE_DIS_BETWEEN_TWO = Util.dp2px(getContext(), 300);
    private int mTmpMoveDistance;
    private List<Pipe> mNeedRemovePipe = new ArrayList<Pipe>();

    public GameFlabbyBird(Context context) {
        super(context);
        init();
    }

    public GameFlabbyBird(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameFlabbyBird(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);//设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        //设置可获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmaps();
        //初始化速度
        mSpeed = Util.dp2px(getContext(), 2);

        mPipeWidth = Util.dp2px(getContext(), PIPE_WIDTH);
    }

    /**
     * 初始化图片
     */
    private void initBitmaps() {
        mBg = loadImageByResId(R.mipmap.bg1);
        mBirdBitmap = loadImageByResId(R.mipmap.b1);
        mFloorBg = loadImageByResId(R.mipmap.floor_bg2);
        mPipeTop = loadImageByResId(R.mipmap.g2);
        mPipeBottom = loadImageByResId(R.mipmap.g1);
    }

    /**
     * 根据resId加载图片
     *
     * @param resId
     * @return
     */
    private Bitmap loadImageByResId(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();

            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        try {
            //获得Canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //TODO drawSomething······
                drawBg();
                drawBird();
                drawFloor();
                drawPipes();
                drawGrades();
            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 处理一些逻辑上的计算
     */
    private void logic() {
        switch (mStatus) {
            case RUNNING:
                mGrades.mGrade = 0;
                logicPipe();
                //管道
                mTmpMoveDistance += mSpeed;
                if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
                    Pipe pipe = new Pipe(getContext(), mWidth, mHeight,
                            mPipeTop, mPipeBottom);
                    mPipes.add(pipe);
                    mTmpMoveDistance = 0;
                }
                //更新我们地板绘制的x坐标，地板移动
                mFloor.setX(mFloor.getX() - mSpeed);
                //默认下落，点击时瞬间上升
                mTmpBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mTmpBirdDis);

                //计算分数
                mGrades.mGrade += mRemovedPipe;
                for (Pipe pipe : mPipes) {
                    if (pipe.getX() + mPipeWidth < mBird.getX()) {
                        mGrades.mGrade++;
                    }
                }
                checkGameOver();
                break;

            case OVER:
                //如果鸟还在空中，先让它掉下来
                if (mBird.getY() < mFloor.getY() - mBird.getmWidth()) {
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                } else {
                    mStatus = GameStatus.WAITING;
                    initPos();
                }
                break;
            default:
                break;
        }
    }

    private int mRemovedPipe = 0;

    private void logicPipe() {
        //管道移动
        for (Pipe pipe : mPipes) {
            if (pipe.getX() < -mPipeWidth) {
                //mPipes.remove(pipe);//会报错,异常 java.util.ConcurrentModificationException
                mNeedRemovePipe.add(pipe);
                /**
                 * 有人会说，为啥要多创建个mNeedRemovePipe呢？你for循环移除不就行了~嗯，这样是不行的，会报错；
                 * 又有人说，我知道那样会报错，但是你可以用CopyOnWriteArrayList这类安全的List，就能for循环时，
                 * 移除了~~嗯，这样是可以，但是这类List的方法中为了安全，
                 * 各种clone，势必造成运行速度慢~~我们这里是游戏，千万要避免不必要的速度丢失~~~
                 */
                mRemovedPipe++;
                continue;
            }
            pipe.setX(pipe.getX() - mSpeed);
        }
        //移除管道
        mPipes.removeAll(mNeedRemovePipe);
        Log.e("CID", "现存管道数量：" + mPipes.size());
    }

    /**
     * 重置鸟的位置等数据
     */
    private void initPos() {
        mRemovedPipe = 0;
        mPipes.clear();
        mNeedRemovePipe.clear();
        //重置鸟的位置
        mBird.setY(mHeight * 2 / 3);
        //重置下落速度
        mTmpBirdDis = 0;
    }

    private void checkGameOver() {
        //如果碰触地板，GG
        if (mBird.getY() > mFloor.getY() - mBird.getmHeight()) {
            mStatus = GameStatus.OVER;
        }
        //如果撞到管道
        for (Pipe wall : mPipes) {
            //已经穿过
            if (wall.getX() + mPipeWidth < mBird.getX()) {
                continue;
            }
            if (wall.touchBird(mBird)) {
                mStatus = GameStatus.OVER;
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            switch (mStatus) {
                case WAITING:
                    mStatus = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mTmpBirdDis = mBirdUpDis;
                    break;
            }
        }
        return true;
    }

    private void drawGrades() {
        mGrades.draw(mCanvas, mWidth, mHeight);
    }

    /**
     * 绘制管道
     */
    private void drawPipes() {
        for (Pipe pipe : mPipes) {
            pipe.draw(mCanvas, mPipeRect);
        }
    }

    private void drawFloor() {
        mFloor.draw(mCanvas, mPaint);
    }

    private void drawBird() {
        mBird.draw(mCanvas);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawBitmap(mBg, null, mGamePanelRect, null);
    }

    /**
     * 初始化相关尺寸
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mGamePanelRect.set(0, 0, w, h);
        //初始化mBird
        mBird = new Bird(getContext(), mWidth, mHeight, mBirdBitmap);
        //初始化地板
        mFloor = new Floor(mWidth, mHeight, mFloorBg);
        //初始化管道范围i
        mPipeRect = new RectF(0, 0, mPipeWidth, mHeight);
        //初始化分数
        mGrades = new Grades(getResources(), h, w);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //通知关闭线程
        isRunning = false;
    }
}
