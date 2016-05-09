package qiqi.love.bird.birdview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import qiqi.love.bird.Util;

/**
 * Created by iscod on 2016/5/9.
 */
public class Bird {
    /**
     * 鸟在屏幕高度2/3位置
     */
    private static final float RADIO_POS_HEIGHT = 2 / 3f;
    /**
     * 鸟的宽度 30dp
     */
    private static final int BIRD_SIZE = 30;
    /**
     * 鸟的横坐标
     */
    private int x;
    /**
     * 鸟的纵坐标
     */
    private int y;
    /**
     * 鸟的宽度
     */
    private int mWidth;
    /**
     * 鸟的高度
     */
    private int mHeight;
    /**
     * 鸟的Bitmap
     */
    private Bitmap bitmap;
    /**
     * 鸟的绘制范围
     */
    private RectF rect = new RectF();

    public Bird(Context context, int gameWith, int gameHeight, Bitmap bitmap) {
        this.bitmap = bitmap;
        //鸟的位置
        x = gameWith / 2 - bitmap.getWidth() / 2;
        y = (int) (gameHeight * RADIO_POS_HEIGHT);

        //计算鸟的高度和宽度
        mWidth = Util.dp2px(context, BIRD_SIZE);
        mHeight = (int) (mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());
    }

    public void draw(Canvas canvas) {
        rect.set(x, y, x + mWidth, y + mHeight);
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getmWidth() {
        return mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }
}
