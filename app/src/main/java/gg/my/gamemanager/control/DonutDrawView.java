package gg.my.gamemanager.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import gg.my.gamemanager.helpers.RatingInfo;

public class DonutDrawView extends android.support.v7.widget.AppCompatImageView {
    private final Paint paint;
    private final Paint textPaint;
    private final Context context;
    private RatingInfo[] data;
    private int total;
    private float density;

    public DonutDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = getContext().getResources().getDisplayMetrics().density;
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.STROKE);
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

        int strokeWidth = dp2px(24);
        paint.setStrokeWidth(strokeWidth);
        textPaint.setTextSize(dp2px(16));
        textPaint.setTextAlign(Paint.Align.LEFT);

        int w = getWidth();
        int h = getHeight();
        int shorter = Math.min(w, h);
        final int r = (shorter - strokeWidth) / 2;
        final int x = r + strokeWidth / 2;
        final int y = h / 2;

        float left = x - r;
        float right = x + r;
        float top = y - r;
        float bottom = y + r;

        float legendLeft = right + strokeWidth / 2 + dp2px(16);
        float legendHeight = dp2px(16);
        float legendRight = legendLeft + legendHeight * 1.5f;
        float legendSpace = dp2px(8);
        float legendTop = (h - this.data.length * legendHeight - (this.data.length - 1) * legendSpace) / 2;


        float startAngle = 270;
        for (RatingInfo aData : this.data) {
            paint.setColor(aData.color);
            float percent = (float) aData.count / this.total;
            float angle = percent * 360;
            this.paint.setStyle(Style.STROKE);
            canvas.drawArc(left, top, right, bottom, startAngle, angle, false, this.paint);
            startAngle += angle;
            this.paint.setStyle(Style.FILL);
            canvas.drawRect(legendLeft, legendTop, legendRight, legendTop + legendHeight, this.paint);
            canvas.drawText(Integer.toString(aData.count) + "×" + aData.name, legendRight + dp2px(4), legendTop + legendHeight - dp2px(2), this.textPaint);
            legendTop += legendHeight + legendSpace;
        }
        super.onDraw(canvas);
    }

    private int dp2px(int dp) {
        return (int) (dp * this.density + 0.5);
    }
}
