package com.casky.remote.widget;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * draw dashedline widget
 * @author
 */
public class DashedLine extends View {
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    //private Rect mRect;
  
    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);          
        
    }

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startX = this.getWidth()/2;
        startY = this.getTop();
        endX = startX;
        endY = startY + this.getHeight();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2);
        Path path = new Path();
        Log.d("DashedLine", "Height:"+this.getHeight());
        Log.d("DashedLine", "Left:"+this.getLeft());
        Log.d("DashedLine", "startX:"+startX+"startY:"+startY+"endX:"+endX+"endY:"+endY);
        path.moveTo(startX, startY);
        path.lineTo(endX,endY);      
        PathEffect effects = new DashPathEffect(new float[]{10,10,10,10},0);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
