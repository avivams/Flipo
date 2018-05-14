package com.flipo.avivams.flipo.utilities;

import java.util.ArrayList;

public class Shape {
    private ArrayList<Stroke> m_Shape;

    public void SetShape(ArrayList<Stroke> i_Stroke)
    {
        m_Shape = i_Stroke;
    }

    public ArrayList<Stroke> getM_Shape() {
        return m_Shape;
    }
}
