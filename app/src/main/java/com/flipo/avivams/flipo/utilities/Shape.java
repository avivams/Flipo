package com.flipo.avivams.flipo.utilities;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import java.util.LinkedList;

public class Shape{
    private LinkedList<Stroke> m_Shape;
    private Rect m_Rect;
    private Point m_Position;

    public Shape(LinkedList<Stroke> shape){
        m_Shape = shape;
    }

    public void SetShape(LinkedList<Stroke> i_Stroke)
    {
        m_Shape = i_Stroke;
    }

    public LinkedList<Stroke> getShape() {
        return m_Shape;
    }

    public void CalculateShapeRect(){

        for(Stroke stroke : m_Shape){
            //Rect strokeRect = stroke.get
        }
    }

    public void setRect(Rect i_Rect){
        m_Rect = i_Rect;
    }

    public Rect getRect(){
        return m_Rect;
    }

    public void setPosition(Point i_Position){
        m_Position = i_Position;
    }

    public Point getPosition(){
        return m_Position;
    }
}
