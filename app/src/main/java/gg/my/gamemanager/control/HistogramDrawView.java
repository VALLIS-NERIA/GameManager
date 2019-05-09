package gg.my.gamemanager.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import gg.my.gamemanager.helpers.RatingInfo;

public class HistogramDrawView extends android.support.v7.widget.AppCompatImageView {
    private final Paint paint;
    private final Paint textPaint;
    private RatingInfo[] data;
    private int total;
    private float density;

    public HistogramDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = getContext().getResources().getDisplayMetrics().density;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Style.STROKE);
        textPaint.setColor(Color.BLACK);
        //marginRightDp = dp2px(12);
    }

    public void setData(RatingInfo[] data) {
        this.data = data;
        this.total = 0;
        for (RatingInfo info : data) {
            this.total += info.count;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //textPaint.setStrokeWidth(this.dp2px(20));
        final int height = dp2px(22);
        final int space = dp2px(10);
        textPaint.setTextSize(height);
        int bottom = dp2px(8);
        int top = bottom + height;

        for (RatingInfo info : this.data) {
            paint.setColor(info.color);
            float right = getBarLength(info.count);
            canvas.drawRect(0, top, right, bottom, this.paint);
            if((float)info.count/total>0.65) {
                textPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(Integer.toString(info.count) + "×" + info.name, right - dp2px(4), top - dp2px(4), this.textPaint);
            }
            else{
                textPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(Integer.toString(info.count) + "×" + info.name, right + dp2px(4), top - dp2px(4), this.textPaint);
            }
            bottom = top + space;
            top = bottom + height;
        }
        super.onDraw(canvas);
    }

    private float getBarLength(int value) {
        return (float) value / total * (getWidth());
    }

    private int dp2px(int dp) {
        return (int) (dp * this.density + 0.5);
    }
}
