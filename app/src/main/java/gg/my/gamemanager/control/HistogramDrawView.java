package gg.my.gamemanager.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import gg.my.gamemanager.R;

public class HistogramDrawView extends android.support.v7.widget.AppCompatImageView {
    private final Paint paint;
    private final Paint textPaint;
    private final Context context;
    private int[] data;
    @ColorInt
    private int[] colors;
    private int total;
    private float density;
    private int marginRightDp;

    public HistogramDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = getContext().getResources().getDisplayMetrics().density;
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Style.STROKE);
        textPaint.setColor(Color.BLACK);
        //marginRightDp = dp2px(12);
    }

    public void setData(int[] data, @ColorInt int[] colors) {
        this.data = data;
        this.colors = colors;
        this.total = 0;
        for (int i = 0; i < data.length; i++) {
            this.total += data[i];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //textPaint.setStrokeWidth(this.dp2px(20));
        final int height = dp2px(24);
        final int space = dp2px(8);
        textPaint.setTextSize(height);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        int bottom = dp2px(8);
        int top = bottom + height;

        for (int i = 0; i < this.data.length; i++) {
            paint.setColor(colors[i]);
            float right = getBarLength(this.data[i]);
            canvas.drawRect(0, top, right, bottom, this.paint);
            canvas.drawText(Integer.toString(this.data[i]), right - dp2px(4),  top-dp2px(4), this.textPaint);
            bottom = top + space;
            top = bottom + height;
        }
        super.onDraw(canvas);
    }

    private float getBarLength(int value) {
        return (float) value / total * (getWidth() - dp2px(marginRightDp));
    }

    private int dp2px(int dp) {
        return (int) (dp * this.density + 0.5);
    }
}
