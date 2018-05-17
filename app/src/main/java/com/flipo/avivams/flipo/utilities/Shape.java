package com.flipo.avivams.flipo.utilities;

import java.util.LinkedList;

public class Shape {
    private LinkedList<Stroke> m_Shape;

    public Shape(LinkedList<Stroke> shape){
        m_Shape = shape;
    }

    public void SetShape(LinkedList<Stroke> i_Stroke)
    {
        m_Shape = i_Stroke;
    }

    public LinkedList<Stroke> getM_Shape() {
        return m_Shape;
    }
}
