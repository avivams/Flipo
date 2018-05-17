package com.flipo.avivams.flipo.utilities;

import android.graphics.Color;
import android.graphics.RectF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.wacom.ink.manipulation.Intersectable;
import com.wacom.ink.path.PathBuilder;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.utils.Utils;

public class Stroke implements Intersectable{

    private FloatBuffer m_Points;
    private int m_Color;
    private int m_Stride;
    private int m_Size;
    private float m_Width;
    private float m_StartT;
    private float m_EndT;
    private BlendMode m_BlendMode;
    private int m_PaintIndex;
    private int m_Seed;
    private boolean m_HasRandomSeed;

    private int m_formerColor;

    private FloatBuffer m_SegmentsBounds;
    private RectF bounds;

    public Stroke(){
        bounds = new RectF();
        m_formerColor = -1;
    }

    public Stroke(int size) {
        this();
        SetPoints(Utils.createNativeFloatBufferBySize(size), size);
        //default values
        m_StartT = 0.0f;
        m_EndT = 1.0f;
    }

    public Stroke(Stroke source){
        m_Color = source.m_Color;
        m_Stride = source.m_Stride;
        m_Size = source.m_Size;
        m_Width = source.m_Width;
        m_StartT = source.m_StartT;
        m_EndT = source.m_EndT;
        m_BlendMode = source.m_BlendMode;
        m_PaintIndex = source.m_PaintIndex;
        m_Seed = source.m_Seed;
        m_HasRandomSeed = source.m_HasRandomSeed;
        bounds = new RectF(source.bounds);
        m_SegmentsBounds = source.m_SegmentsBounds;
        m_formerColor = source.m_formerColor;
        CopyPoints(source.m_Points, 0, source.m_Size);
    }

    public int getStride() {
        return m_Stride;
    }

    public void setStride(int stride) {
        this.m_Stride = stride;
    }

    public FloatBuffer getPoints() {
        return m_Points;
    }

    public int getSize() {
        return m_Size;
    }

    public int GetColor() {
        return m_Color;
    }

    public void SetColor(int color) {
        this.m_Color = color;
    }

    public float getWidth() {
        return m_Width;
    }

    public void SetWidth(float width) {
        this.m_Width = width;
    }

    public float getStartValue() {
        return m_StartT;
    }

    public float getEndValue() {
        return m_EndT;
    }

    public int getFormerColor(){return m_formerColor;}

    public void setFormerColor(int color){m_formerColor = color;}

    @Override
    public FloatBuffer getSegmentsBounds() {
        return m_SegmentsBounds;
    }

    @Override
    public RectF getBounds() {
        return bounds;
    }

    public void SetInterval(float startT, float endT) {
        this.m_StartT = startT;
        this.m_EndT = endT;
    }

    public void SetPoints(FloatBuffer points, int pointsSize) {
        m_Size = pointsSize;
        this.m_Points = points;
    }

    public void CopyPoints(FloatBuffer source, int sourcePosition, int size) {
        this.m_Size = size;
        m_Points = ByteBuffer.allocateDirect(size * Float.SIZE/Byte.SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        Utils.copyFloatBuffer(source, m_Points, sourcePosition, 0, size);
    }

    public void SetBlendMode(BlendMode blendMode){
        this.m_BlendMode = blendMode;
    }

    public BlendMode GetBlendMode() {
        return m_BlendMode;
    }

    public void setPaintIndex(int paintIndex) {
        this.m_PaintIndex = paintIndex;
    }

    public int getPaintIndex() {
        return m_PaintIndex;
    }

    public int getSeed() {
        return m_Seed;
    }

    public void setSeed(int seed){
        this.m_Seed = seed;
    }

    public void setHasRandomSeed(boolean hasRandomSeed) {
        this.m_HasRandomSeed = hasRandomSeed;
    }

    public boolean hasRandomSeed() {
        return m_HasRandomSeed;
    }


    public void calculateBounds(){
        //a segment is a rectangle of float values
        RectF segmentBounds = new RectF();
        Utils.invalidateRectF(bounds);
        //Allocate a float buffer to hold the segments' bounds.
        //each segment has 4 fields
        FloatBuffer segmentsBounds = Utils.createNativeFloatBuffer(PathBuilder.calculateSegmentsCount(m_Size, m_Stride) * 4);
        //set the buffer at position 0
        segmentsBounds.position(0);

        for (int index = 0; index<PathBuilder.calculateSegmentsCount(m_Size, m_Stride); index++){
            PathBuilder.calculateSegmentBounds(getPoints(), getStride(), getWidth(), index, 0.0f, segmentBounds);
            segmentsBounds.put(segmentBounds.left); //left x
            segmentsBounds.put(segmentBounds.top); //left y
            segmentsBounds.put(segmentBounds.width());
            segmentsBounds.put(segmentBounds.height());
            Utils.uniteWith(bounds, segmentBounds);
        }
        this.m_SegmentsBounds = segmentsBounds;
    }

}
