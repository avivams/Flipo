package com.flipo.avivams.flipo.utilities;

import android.graphics.Color;

public class AnimationPath {
    private Stroke m_Path;
    private Color m_PathColor;

    public void SetPath(Stroke i_Path){
        m_Path = i_Path;
    }

    public Stroke GetPath(){
        return m_Path;
    }

    public void SetColor(Color i_Color){
        m_PathColor = i_Color;
    }

    public Color GetColor(){
        return m_PathColor;
    }
}
