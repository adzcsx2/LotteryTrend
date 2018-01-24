package org.devloper.melody.lotterytrend.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.devloper.melody.lotterytrend.R;
import org.devloper.melody.lotterytrend.model.TrendModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuqiang on 2014/12/29.
 * 绘制的走势图View
 */
public class TrendView extends View {
    //小球文字画笔
    private Paint mPaintText = null;

    //小球画笔
    private Paint mBallPaint = null;

    //网格线画笔
    private Paint mPaintLine = null;

    //小球之间的连线画笔
    private Paint mLinkLine = null;

    //网格的水平间距
    private float mDeltaX;

    //网格垂直间距
    private float mDeltaY;

    //当前View的宽度
    private int mWidth;

    //当前View的高度
    private int mHeight;

    // 球的总号数
    private int mCount = 0;
    // 球的总数
    private int mNum = 0;
    //总期数
    private int mCycle = 0;
    //    球的数值
    private String[][] mBallNumber;

    private int mBallWH = 0;

    public String[][] getmBallNumber() {
        return mBallNumber;
    }

    public void setmBallNumber(String[][] mBallNumber) {
        this.mBallNumber = mBallNumber;
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getmNum() {
        return mNum;
    }

    public void setmNum(int mNum) {
        this.mNum = mNum;
    }

    public int getmCycle() {
        return mCycle;
    }

    public void setmCycle(int mCycle) {
        this.mCycle = mCycle;
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mBallWH = getResources().getDimensionPixelSize(R.dimen.trend_ball_wh);
        initSource();
    }
    /***
     * 初始化资源
     */
    private void initSource() {
        int dpValue = getScreenDenisty();
        //网格线画笔
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.GRAY);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(dpValue * 0.6f / 160);

        //小球上面的文字画笔
        mPaintText = new Paint();
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize((dpValue * 12 / 160));
        mPaintText.setAntiAlias(true);
        mPaintText.setStrokeWidth(2f);

        //小球之间连线画笔
        mLinkLine = new Paint();
        mLinkLine.setColor(Color.BLUE);
        mLinkLine.setAntiAlias(true);
        mLinkLine.setStrokeWidth(dpValue * 0.6f / 160);

        //小球画笔
        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        //设置单个网格的水平和垂直间距
        this.mDeltaY = mBallWH * 2f;
        Log.i("delta", "deltay:" + mDeltaY);//高度;50
        this.mDeltaX = this.mDeltaY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置测量View的大小:宽度和高度
        setMeasuredDimension((int) ((mCount * mNum) * mDeltaX), (int) (mCycle * mDeltaY));
        //取得测量之后当前View的宽度
        this.mWidth = getMeasuredWidth();
        //取得测量之后当前View的高度
        this.mHeight = getMeasuredHeight();
        //重新绘制,不重绘,不会生效;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawXYLine(canvas);
        for (int num = 0; num < mNum; num++) {
            drawBlueBall(canvas, num);
            drawLinkLine(canvas, num);
        }
    }


    /***
     * 绘制蓝球
     * @param canvas
     */
    private void drawBlueBall(Canvas canvas, int num) {
        //红球
        mBallPaint.setARGB(255, 255, 68, 68);
        //蓝球
//        mBallPaint.setARGB(255,51,181,229);
        Rect src = new Rect();
        Rect dst = new Rect();
        //最外面控制期号
        for (int i = 0; i < mCycle; i++) {
            int value = Integer.parseInt(mBallNumber[i][num]);
            float[] xy = this.translateBallXY(i, value - 1);
            //重新定位蓝球在x轴的位置;在红球后面;
            xy[0] = xy[0] + mCount * num * mDeltaX;
            src.left = 0;
            src.top = 0;
            src.bottom = mBallWH;
            src.right = mBallWH;

            dst.left = (int) (xy[0] + this.mDeltaX * 0.05f);
            dst.top = (int) (xy[1] + this.mDeltaY * 0.1f);
            dst.bottom = (int) (dst.top + mDeltaY * 0.8f);
            dst.right = (int) (dst.left + mDeltaX * 0.85f);

            RectF rf = new RectF(dst.left, dst.top, dst.right, dst.bottom);
            //画圆
            canvas.drawOval(rf, mBallPaint);

            //如果有合适的小球图片,就用下面的方法...上面的去掉
            //在对应的位置画球球
            // canvas.drawBitmap(mBall, src, dst, this.mBallPaint);
            if (value < 10) {
                //居中显示:占一个字符
                canvas.drawText(" " + String.valueOf(value), dst.left + src.right / 2, dst.top + this.mDeltaY * 0.5f, mPaintText);
            } else {
                canvas.drawText(String.valueOf(value), dst.left + src.right / 2, dst.top + this.mDeltaY * 0.5f, mPaintText);
            }
        }
    }

    /**
     * 蓝球之间的连线
     *
     * @param canvas
     */
    private void drawLinkLine(Canvas canvas, int num) {
        //先获取第一个球的x轴和y轴位置
        float[] lastXy = this.translateBallXY(0, Integer.parseInt(mBallNumber[0][num])-1);
        //重新定位:当前X轴的位置还要加上前面球的位置
        lastXy[0] = lastXy[0] + mCount * num * mDeltaX;
        for (int i = 0; i < mCycle; i++) {
            int value = Integer.parseInt(mBallNumber[i][num]);
            float[] xy = this.translateBallXY(i, value - 1);
            //重新定位...同上;
            xy[0] = xy[0] + mCount * num * mDeltaX;
            //画两个球的中心点的连线...
            canvas.drawLine(lastXy[0] + mDeltaX * 0.5f, lastXy[1] + mDeltaY * 0.5f, xy[0] + mDeltaX * 0.5f, xy[1] + mDeltaY * 0.5f, mLinkLine);
            //赋值....
            lastXy[0] = xy[0];
            lastXy[1] = xy[1];
        }
    }

    /***
     * 绘制X轴和Y轴的网格线
     * @param canvas 画布
     */
    private void drawXYLine(Canvas canvas) {
        //期号数:X轴;含顶部和底部的边角线;
        for (int i = 0; i <= mCycle; i++) {
            canvas.drawLine(0, this.mDeltaY * i, this.mWidth, this.mDeltaY * i, mPaintLine);
        }
        //y轴
        for (int i = 0; i <= (mCount * mNum); i++) {
            canvas.drawLine(this.mDeltaX * i, 0, this.mDeltaX * i, this.mHeight, mPaintLine);
        }
    }

    /**
     * 返回小球坐标
     *
     * @param rowIndex   行索引X轴;
     * @param valueIndex 当前中奖号码数字:相当于在哪个位置处:Y轴的索引.
     * @return
     */
    private float[] translateBallXY(int rowIndex, int valueIndex) {
        float[] xy = new float[2];
        xy[0] = this.mDeltaX * valueIndex;
        xy[1] = this.mDeltaY * rowIndex;
        return xy;
    }

    /**
     * 获取当前屏幕的密度
     *
     * @return
     */
    public int getScreenDenisty() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.densityDpi;
    }
}