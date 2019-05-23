package com.example.haoch.wocao.file_api;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.haoch.wocao.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileviewAdapter extends BaseAdapter {

    Context context;
    List<File> list;

    public FileviewAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            //布局实例化
            convertView = View.inflate(context, R.layout.adapter_file, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        File file = (File) getItem(position);
        if (file.isDirectory()) {
            viewHolder.img.setImageResource(R.drawable.folder);
        } else {
            if (file.getName().endsWith(".csv")) {
                viewHolder.img.setImageResource(R.drawable.excel);
            }
        }

        viewHolder.name.setText(file.getName());
        viewHolder.time.setText(new SimpleDateFormat("yy-M-d HH:mm:ss")
                .format(new Date(file.lastModified())));


        String value=null;
        long Filesize=getFolderSize(file)/1024;//call function and convert bytes into Kb
        if(Filesize>=1024)
            value=Filesize/1024+" Mb";
        else
            value=Filesize+" Kb";

        viewHolder.size.setText(value);



        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView name;
        TextView time;
        TextView size;

        public ViewHolder(View convertView) {
            img = (ImageView) convertView.findViewById(R.id.img);
            name = (TextView) convertView.findViewById(R.id.name);
            time = (TextView) convertView.findViewById(R.id.time);
            size = (TextView) convertView.findViewById(R.id.size);
        }

    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

}