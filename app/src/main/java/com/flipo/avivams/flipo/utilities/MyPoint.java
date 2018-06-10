package com.flipo.avivams.flipo.utilities;

    public class MyPoint {
        private float m_X;
        private float m_Y;

        public MyPoint(){
            m_X = 0;
            m_Y = 0;
        }

        public MyPoint(float i_X, float i_Y){
            m_X = i_X;
            m_Y = i_Y;
        }

        public float getX() {
            return m_X;
        }

        public float getY() {
            return m_Y;
        }

        public void setX(float i_X) {
            this.m_X = i_X;
        }

        public void setY(float i_Y) {
            this.m_Y = i_Y;
        }
    }
