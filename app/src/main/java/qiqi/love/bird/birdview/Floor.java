package qiqi.love.bird.birdview;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Created by iscod on 2016/5/10.
 */
public class Floor {
    /**
     * 地板位置，游戏面板高度的4/5到底部
     */
    private static final float FLOOR_Y_POS_RADIO = 4 / 5F;
    /**
     * 地板x坐标
     */
    private int x;
    /**
     * 地板y坐标
     */
    private int y;
    /**
     * 填充物
     */
    private BitmapShader mFloorShader;

    private int mGameWidth;
    private int mGameHeight;

    public Floor(int gameWidth, int gameHeight, Bitmap floorBg) {
        mGameWidth = gameWidth;
        mGameHeight = gameHeight;
        y = (int) (gameHeight * FLOOR_Y_POS_RADIO);
        mFloorShader = new BitmapShader(floorBg, Shader.TileMode.REPEAT,
                Shader.TileMode.CLAMP);

    }

    /**
     * 绘制自己
     *
     * @param canvas
     * @param mPaint
     */
    public void draw(Canvas canvas, Paint mPaint) {
        if (-x > mGameWidth) {
            x = x % mGameWidth;
        }
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(x, y);
        mPaint.setShader(mFloorShader);
        canvas.drawRect(x, 0, -x + mGameWidth, mGameHeight - y, mPaint);
        canvas.restore();
        mPaint.setShader(null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
}
