package com.flipo.avivams.flipo.utilities;

import java.util.LinkedList;

public class AnimationPath {
    private LinkedList<Stroke> m_Path;

    public AnimationPath(LinkedList<Stroke> path){
        m_Path = path;
    }

    public void SetPath(LinkedList<Stroke> i_Path){
        m_Path = i_Path;
    }

    public LinkedList<Stroke> GetPath(){
        return m_Path;
    }
}
