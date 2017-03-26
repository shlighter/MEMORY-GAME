package com.example.memory;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Shai on 2/10/2017.
 * This is a custom adapter glass for the game's grid view. Every cell is a Square object.
 */
public class CustomAdapter extends ArrayAdapter<Square> {

    final AnimatorSet setRightOut;
    final AnimatorSet setLeftIn;
    int lastPosition;
    View oldView;
    ArrayList<Square> list;
    View[] view;
    boolean equal, notInOrder;
    GameActivity context2;

    public CustomAdapter(GameActivity context, ArrayList<Square> list) {
        super(context, 0, list);

        setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.flip_right_out);
        setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.flight_left_in);
        equal=false;
        notInOrder=false;
        oldView=null;
        lastPosition=-1;
        this.list= list;
        view= new View[list.size()];
        this.context2=context;
    }


    public View getView (int position, View convertView, ViewGroup parent) {

        //gridRefresh verifies that every cell is being checked only once, it's important for the animation.
        if (this.context2.gridRefresh[position] == false) {
            this.context2.gridRefresh[position] = true;
            ImageView img1, img2;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
            img1 = (ImageView) convertView.findViewById(R.id.imageView);
            img2 = (ImageView) convertView.findViewById(R.id.imageView2);
            img2.setImageResource(getItem(position).id);

            //Square.selected==1 when the user clicks on square's card
            if (getItem(position).selected == 1) {
                setRightOut.setTarget(img1);
                setLeftIn.setTarget(img2);
                setRightOut.start();
                setLeftIn.start();
                getItem(position).selected = 2;
            }

            //Card should stay flipped (visible)
            else if (getItem(position).selected == 2) {
                img2.setAlpha(1.0f);
                img1.setAlpha(0.0f);
            }

            //Card should be invisible
            else if (getItem(position).selected == 3) {
                img2.setAlpha(0.0f);
                img1.setAlpha(1.0f);
                getItem(position).selected = 0;
            }

            view[position] = convertView;
            return convertView;
        }
        return view[position];
    }


}
