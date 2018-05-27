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
    private Rect m_ShapeRect;
    private Point m_ShapePosition;

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

    public void setRect(Rect i_Rect){
        m_ShapeRect = i_Rect;
    }

    public Rect getRect(){
        return m_ShapeRect;
    }

    public void setPosition(Point i_Position){
        m_ShapePosition = i_Position;
    }

    public Point getPosition(){
        return m_ShapePosition;
    }
}
