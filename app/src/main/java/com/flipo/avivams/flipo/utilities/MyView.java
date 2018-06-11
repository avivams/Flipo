package com.flipo.avivams.flipo.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;

public class MyView extends View{

    private LinkedList<Path> m_Object;
    private MyPoint m_TopLeft;
    private MyPoint m_BottomRight;
    private int m_Width;
    private int m_Height;
    private ArrayList<Integer> m_Colors;


    public MyView(Context i_Context) {

        super(i_Context);
        m_Object = new LinkedList<>();
        m_Colors = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);

        int i=0;
        for(Path path : m_Object) {
            paint.setColor(m_Colors.get(i++));
            canvas.drawPath(path, paint);
        }
    }

    private Bitmap createBitmap(){
        Bitmap bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        return bmp;
    }

    public void setObject(LinkedList<Path> i_Object) {
        this.m_Object = i_Object;
    }

    public LinkedList<Path> getObject() {
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

    public void setColor(int color){m_Colors.add(color);}

    public ArrayList<Integer> getColor(){return m_Colors;}
}
