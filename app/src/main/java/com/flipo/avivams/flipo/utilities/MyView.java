package com.flipo.avivams.flipo.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;

public class MyView extends View{

    private Path m_Object;

    public MyView(Context i_Context) {
        super(i_Context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setBitmap(createBitmap());
        this.layout(0, 0, this.getLayoutParams().width, this.getLayoutParams().height);
        this.draw(canvas);
    }

    private Bitmap createBitmap(){
        Bitmap bmp = Bitmap.createBitmap( this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);

        return bmp;
    }

    public void setObject(Path i_Object) {
        this.m_Object = i_Object;
    }

    public Path getObject() {
        return m_Object;
    }
}
