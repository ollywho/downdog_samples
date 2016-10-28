package com.downdogapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.downdogapp.singleton.App;

public class CountdownView extends View {

  private double proportionRemaining;
  private Paint paint;
  private RectF rect;


  public CountdownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(2 * getHalfStrokeWidth());
    proportionRemaining = 1;
  }

  public void update(double proportionRemaining) {
    if (this.proportionRemaining != proportionRemaining) {
      this.proportionRemaining = proportionRemaining;
      invalidate();
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    float halfStrokeWidth = getHalfStrokeWidth();
    rect = new RectF(halfStrokeWidth, halfStrokeWidth, w - halfStrokeWidth, h - halfStrokeWidth);
  }

  private float getHalfStrokeWidth() {
    return (float) (1.5 * App.INSTANCE.getDisplayDensity());
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawArc(rect, -90, (float) (-360 * this.proportionRemaining), false, paint);
  }
}
