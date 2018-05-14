package com.flipo.avivams.flipo.utilities;

public class Animation {
    private AnimationPath m_AnimationPath;
    private int m_Speed;
    private String m_Name;
    private Shape m_AnimationObject;

    public void SetAnimationObject(Shape i_AnimationObject) {
        this.m_AnimationObject = i_AnimationObject;
    }

    public void SetAnimationPath(AnimationPath i_AnimationPath) {
        this.m_AnimationPath = i_AnimationPath;
    }

    public void SetName(String i_Name) {
        this.m_Name = i_Name;
    }

    public void SetSpeed(int i_Speed) {
        this.m_Speed = i_Speed;
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
