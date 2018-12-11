package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Rankinglist extends BaseAdapter {
    private Context context;
    private List<String> ranking;
    private List<String> image_list;
    private List<String> name_list;
    private List<String> point_list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public Rankinglist(List<String> ranking, List<String> image_list, List<String> name_list, List<String> point_list, Context context){
        this.ranking = ranking;
        this.image_list = image_list;
        this.name_list = name_list;
        this.point_list = point_list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return point_list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.rankinglist,null);

            viewHolder = new ViewHolder();
            viewHolder.textView_ranking = (TextView) convertView.findViewById(R.id.textView_ranking);
            viewHolder.imageView_profile = (ImageView) convertView.findViewById(R.id.imageView_profile);
            viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            viewHolder.textView_point = (TextView) convertView.findViewById(R.id.textView_point);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.

        if(position > 0) {
            if (point_list.get(position).equals(point_list.get(position - 1))) {
                ranking.set(position,  ranking.get(position - 1));
            }
        }

        viewHolder.textView_ranking.setText(ranking.get(position));

        if(image_list.get(position) == null)
            viewHolder.imageView_profile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.user));
        else {
            Picasso.with(context)
                    .load(image_list.get(position))
                    .fit()
                    .centerInside()
                    .into(viewHolder.imageView_profile, new Callback.EmptyCallback() {
                        public void onSuccess() {
                        }
                    });
        }

        viewHolder.textView_name.setText(name_list.get(position));
        viewHolder.textView_point.setText(point_list.get(position));

        return convertView;
    }

    class ViewHolder{
        public TextView textView_ranking;
        public ImageView imageView_profile;
        public TextView textView_name;
        public TextView textView_point;
    }

}
