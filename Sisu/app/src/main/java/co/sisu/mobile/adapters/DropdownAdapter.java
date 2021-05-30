package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.sisu.mobile.controllers.ColorSchemeManager;

/**
 * Created by Brady Groharing on 9/2/2018.
 */

public class DropdownAdapter extends ArrayAdapter<String>{

    private ColorSchemeManager colorSchemeManager;

    public DropdownAdapter(Context context, int textViewResourceId, List<String> objects, ColorSchemeManager colorSchemeManager){
        super(context, textViewResourceId, objects);
        this.colorSchemeManager = colorSchemeManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        ((TextView) v).setTextSize(16);
        ((TextView) v).setTextColor(colorSchemeManager.getDarkerText());

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);

        ((TextView) v).setTextColor(colorSchemeManager.getDarkerText());

//        ((TextView) v).setTypeface(fontStyle);
        ((TextView) v).setGravity(Gravity.CENTER);

        return v;
    }
}
