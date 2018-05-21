package com.flipo.avivams.flipo.utilities;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Build;
import android.view.View;

public class Animation {
    private AnimationPath m_AnimationPath;
    private int m_Speed;
    private static int count = 0;
    private String m_Name;
    private Shape m_AnimationObject;

    public Animation(){
        m_Name = "animation n" + (count++);
    }

    public Animation SetAnimationObject(Shape i_AnimationObject) {
        this.m_AnimationObject = i_AnimationObject;
        return this;
    }

    public Animation SetAnimationPath(AnimationPath i_AnimationPath) {
        this.m_AnimationPath = i_AnimationPath;
        return this;
    }

    public Animation SetName(String i_Name) {
        this.m_Name = i_Name;
        return this;
    }

    public Animation SetSpeed(int i_Speed) {
        this.m_Speed = i_Speed;
        return this;
    }

    public AnimationPath GetAnimationPath() {
        return m_AnimationPath;
    }

    public int GetSpeed() {
        return m_Speed;
    }

    public Shape GetAnimationObject() {
        return m_AnimationObject;
    }

    public String GetName() {
        return m_Name;
    }
}
