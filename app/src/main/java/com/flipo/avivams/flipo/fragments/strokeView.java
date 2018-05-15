package com.flipo.avivams.flipo.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by aviva on 15/05/2018.
 */

public class strokeView extends View {

    private Path p;
    private Paint paint;

    public strokeView(Context context, AttributeSet att) {
        super(context, att);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1.0f);
    }

    public void setPath(Path path){
        p = path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(p!= null && paint != null)
            canvas.drawPath(p, paint);
    }


}
