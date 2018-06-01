package com.flipo.avivams.flipo.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;
import android.view.animation.Animation;

public class MyView extends View{

    private Path m_Object;
    private MyPoint m_TopLeft;
    private MyPoint m_BottomRight;
    private int m_Width;
    private int m_Height;


    public MyView(Context i_Context) {
        super(i_Context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2.0f);
        canvas.drawPath(m_Object, paint);
    }

    private Bitmap createBitmap(){
        Bitmap bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        return bmp;
    }

    public void setObject(Path i_Object) {
        this.m_Object = i_Object;
    }

    public Path getObject() {
        return m_Object;
    }

    public MyPoint getBottomRight() {
        return m_BottomRight;
    }

    public MyPoint getTopLeft() {
        return m_TopLeft;
    }

    public void setBottomRight(MyPoint i_BottomRight) {
        this.m_BottomRight = i_BottomRight;
    }

    public void setTopLeft(MyPoint i_TopLeft) {
        this.m_TopLeft = i_TopLeft;
    }

    public int getMyHeight() {
        return m_Height;
    }

    public void setHeight(int i_Height) {
        this.m_Height = i_Height;
    }

    public void setWidth(int i_Width) {
        this.m_Width = i_Width;
    }

    public int getMyWidth() {
        return m_Width;
    }
}
