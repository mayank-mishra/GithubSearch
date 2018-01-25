package com.git.search.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.git.search.Objects.ContributerResponseObject;
import com.git.search.Objects.RepoObject;
import com.git.search.R;
import com.git.search.util.AppUtils;

import java.util.List;

/**
 * Created by mayank on 19/01/2018.
 */


public class ContributerGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<ContributerResponseObject> mconList;
    private OnGridItemClickListener mListener;
    // Keep all Images in array
    // Constructor
    public ContributerGridAdapter(Context c, List<ContributerResponseObject> conList,OnGridItemClickListener listener){
        mContext = c;
        mconList= conList;
        mListener=listener;
    }

    @Override
    public int getCount() {
        return mconList.size();
    }

    @Override
    public ContributerResponseObject getItem(int position) {
        return mconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        ImageView image_view=(ImageView)itemView.findViewById(R.id.image_view);
        TextView text_name=(TextView)itemView.findViewById(R.id.txt_view_login);
        /*imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(70, 70));*/
        text_name.setText(AppUtils.NullChecker(getItem(position).login));
        Glide.with(mContext)
                .load(AppUtils.NullChecker(getItem(position).avatar_url))
                .apply(RequestOptions.circleCropTransform())
                .into(image_view);
        itemView.setTag(position);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onGridItemClick(getItem((int)view.getTag()));
            }
        });


        return itemView;
    }

    public interface OnGridItemClickListener{
        public abstract void onGridItemClick(ContributerResponseObject contributerResponseObject);
    }

}
