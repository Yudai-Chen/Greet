package xyz.crabfish.nfccard.db;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.crabfish.nfccard.R;
import xyz.crabfish.nfccard.model.Note;

/**
 * Created by CrabFish on 2017/12/19.
 */

public class MyAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<Note> array;

    public MyAdapter(LayoutInflater inf,ArrayList<Note> arry){
        this.inflater=inf;
        this.array=arry;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            convertView=inflater.inflate(R.layout.note, null);
            vh.tv1=(TextView) convertView.findViewById(R.id.textView1);
            vh.tv2=(TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(vh);
        }
        vh=(ViewHolder) convertView.getTag();
        vh.tv1.setText(array.get(position).getTitle());
        vh.tv2.setText(array.get(position).getTime());
        return convertView;
    }
    class ViewHolder{
        TextView tv1,tv2;
    }
}
