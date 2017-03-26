package com.example.memory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shai
 * List adapter class for list views.
 */

public class ListAdapter extends ArrayAdapter<UserScore> {


    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<UserScore> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row2, null);
        }

        UserScore us = getItem(position);

        if (us != null) {
            TextView user = (TextView) v.findViewById(R.id.user);
            TextView score = (TextView) v.findViewById(R.id.score);


            if (user != null) {
             user.setText(us.getName());
            }

            if (score != null) {
              score.setText(Integer.toString(us.getScore()));
            }

        }

        return v;
    }

}