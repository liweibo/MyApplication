package com.example.lwb.myapplication.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.example.lwb.myapplication.MyApplication;
import com.example.lwb.myapplication.R;
import com.example.lwb.myapplication.bean.Wares;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by <a href="http://www.cniao5.com">菜鸟窝</a>
 * 一个专业的Android开发在线教育平台
 */
public class WaresAdapter extends SimpleAdapter<Wares> {



    public WaresAdapter(Context context, List<Wares> datas) {
        super(context, R.layout.template_grid_wares, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, Wares item) {

        viewHoder.getTextView(R.id.text_title).setText(item.getName());
        viewHoder.getTextView(R.id.text_price).setText("￥"+item.getPrice());
        ImageView  draweeView = (ImageView) viewHoder.getView(R.id.drawee_view);
        Picasso.with(MyApplication.getContext()).load(item.getImgUrl()).into(draweeView);
    }



}
