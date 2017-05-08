package com.example.hashh.souschef;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hashh on 16-04-2017.
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> arrayList = new ArrayList<String>();
    private Context context;

    public CustomAdapter(ArrayList<String> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.inglist, parent, false);
            //Displays ingredients from the list
            TextView ingItem = (TextView)view.findViewById(R.id.ingItem);
            ingItem.setText(arrayList.get(position));
            //Add event on remove button click
            Button removeButton = (Button)view.findViewById(R.id.removeButton);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.remove(position);
                    notifyDataSetChanged();
                }
            });

        }
        return view;
    }
}
