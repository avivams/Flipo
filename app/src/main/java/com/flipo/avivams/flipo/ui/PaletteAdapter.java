package com.flipo.avivams.flipo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.flipo.avivams.flipo.R;

import java.util.ArrayList;

/**
 * Created by aviv_ams on 22/05/2018.
 */

public class PaletteAdapter extends BaseAdapter {

    private Context m_Context;
    private ArrayList<Integer> m_Colors;

    public PaletteAdapter(Context context){
        m_Context = context;
        m_Colors = new ArrayList<>();

        // get colors from 'array.xml'
        TypedArray colors = m_Context.getResources().obtainTypedArray(R.array.palette_colors);
        for (int i=0; i < colors.length(); i++) {
           m_Colors.add(colors.getColor(i, 0));
        }
        colors.recycle(); // finished using the colors TypedArray
    }

    @Override
    public int getCount() {
        return m_Colors.size();
    }

    @Override
    public Object getItem(int position) {
        if( position >= 0 && position < m_Colors.size())
            return m_Colors.get(position);
        else return null;
    }

    /**
     * no implemented, no need for this use here
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View color;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            color = new View(m_Context);
            color.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            //color.setPadding(8, 8, 8, 8);
        } else {
            color = convertView;
        }

        color.setBackgroundColor(m_Colors.get(position));
        return color;

    }
}
