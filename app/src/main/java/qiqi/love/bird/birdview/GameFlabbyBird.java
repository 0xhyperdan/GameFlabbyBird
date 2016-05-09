package qiqi.love.bird.birdview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import qiqi.love.bird.R;

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

        initBitmaps();
    }

    /**
     * 初始化图片
     */
    private void initBitmaps() {
        mBg = loadImageByResId(R.mipmap.bg1);
        mBirdBitmap = loadImageByResId(R.mipmap.b1);
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
            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
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
