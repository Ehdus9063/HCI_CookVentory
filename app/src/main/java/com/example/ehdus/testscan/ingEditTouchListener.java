package com.example.ehdus.testscan;


import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class ingEditTouchListener implements RecyclerView.OnItemTouchListener {

    private ClickListener clicklistener;
    private GestureDetector gestureDetector;

    public ingEditTouchListener(final Context context, final RecyclerView rv, final FilterAdapter a) {

        this.clicklistener = new ingEditTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Toast.makeText(context, "Click on position: " + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View edit = inflater.inflate(R.layout.ingredient_edit, null, false);
                TextView t = edit.findViewById(R.id.ing_title);
                t.setText("Pressed item with name " + a.get(position).getName());
                ConstraintLayout rootView = ((Activity) context).findViewById(R.id.constraintLayout);
                rootView.addView(edit, new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT));

                ConstraintSet cs = new ConstraintSet();
                cs.clone(rootView);
                cs.connect(edit.getId(), ConstraintSet.BOTTOM, rootView.getId(), ConstraintSet.BOTTOM);
                cs.connect(edit.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);
                cs.connect(edit.getId(), ConstraintSet.RIGHT, rootView.getId(), ConstraintSet.RIGHT);
                cs.connect(edit.getId(), ConstraintSet.LEFT, rootView.getId(), ConstraintSet.LEFT);

                cs.applyTo(rootView);
            }
        };

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(child, rv.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
            clicklistener.onClick(child, rv.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}