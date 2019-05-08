package gg.my.gamemanager.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import gg.my.gamemanager.R;

/**
 *  Draws a simple circle/square.
 */
public class DrawImageView extends android.support.v7.widget.AppCompatImageView {

    private final Paint paint;
    private final Context context;
    private boolean hasDlc = false;


    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.STROKE);
    }

    public void setDlc(boolean value) {
        this.hasDlc = value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        int innerCircle = dip2px(context, 12);
        this.paint.setStrokeWidth(8);

        if(this.hasDlc){
            paint.setColor(context.getColor(R.color.colorPrimary));
            canvas.drawCircle(center, center, innerCircle, this.paint);
        }
        else{
            paint.setColor(context.getColor(R.color.colorAccent));
            canvas.drawRect(12, getWidth() - 12, getWidth() - 12, 12, paint);
        }
        super.onDraw(canvas);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}