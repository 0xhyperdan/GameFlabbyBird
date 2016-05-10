package qiqi.love.bird.birdview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import qiqi.love.bird.R;


/**
 * Created by iscod on 2016/5/10.
 */
public class Grades {
    /**
     * ********分数相关********
     */
    private final int[] mNums = new int[]{
            R.mipmap.n0, R.mipmap.n1,
            R.mipmap.n2, R.mipmap.n3,
            R.mipmap.n4, R.mipmap.n5,
            R.mipmap.n6, R.mipmap.n7,
            R.mipmap.n8, R.mipmap.n9,
    };
    private Bitmap[] mNumBitmap;
    public int mGrade = 100;
    /**
     * 单个数字的高度1/15
     */
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15F;
    /**
     * 单个字符的宽度
     */
    private int mSingleGradeWidth;
    /**
     * 单个字符的高度
     */
    private int mSingleGradeHeight;
    /**
     * 单个数字范围
     */
    private RectF mSingleNumRectF;

    public Grades(Resources resources, int height, int width) {
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNumBitmap.length; i++) {
            mNumBitmap[i] = BitmapFactory.decodeResource(resources, mNums[i]);
        }
        //初始化分数
        mSingleGradeHeight = (int) (height * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f
                / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
    }

    /**
     * 绘制分数
     */
    public void draw(Canvas canvas, int width, int height) {
        String gradle = mGrade + "";
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(width / 2 - gradle.length() * mSingleGradeWidth / 2,
                1f / 8 * height);
        for (int i = 0; i < gradle.length(); i++) {
            String numStr = gradle.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            canvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectF, null);
            canvas.translate(mSingleGradeWidth, 0);
        }
        canvas.restore();
    }
}
