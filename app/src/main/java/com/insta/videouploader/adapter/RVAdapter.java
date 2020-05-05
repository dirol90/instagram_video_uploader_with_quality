package com.insta.videouploader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.insta.videouploader.InfoPage;
import com.insta.videouploader.R;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Integer> mData;
    private LayoutInflater mInflater;
    private RecyclerView rv;

    public RVAdapter(Context context, List<Integer> data, RecyclerView rv) {
        this.mInflater = LayoutInflater.from(context);
        this.rv = rv;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(mInflater.getContext())
                .load(mInflater.getContext().getResources().getDrawable(mData.get(position)))
                .centerCrop()
                .into(holder.myIv);

        if (position == 0 ){
            holder.tv.setText("App works on official instagram web page with modifications,\n use it as usual instagram app");
        }if (position == 1 ){
            holder.tv.setText("You can download any photo or video from instagram");
        }if (position == 2 ){
            holder.tv.setText("Upload your photos and video using photo or plus icons, video will be converted automatically before sending to instagram");
        }if (position == 3 ){
            holder.tv.setText("All downloaded and uploaded files are stored in InstaVideoUploader folder");
        }

        holder.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < 3) {
                    rv.smoothScrollToPosition(position+1);
                } else {

                    Intent i = new Intent(mInflater.getContext(), InfoPage.class);
                    mInflater.getContext().startActivity(i);
                    ((Activity)  mInflater.getContext()).finish();
            }
            }
        });

        holder.t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  url = "https://sites.google.com/view/instavideouploader/%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse(url));
                mInflater.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView myIv;
        Button b;
        TextView t;
        TextView tv;
        ViewHolder(View itemView) {
            super(itemView);
            myIv = itemView.findViewById(R.id.card_iv);
            b = itemView.findViewById(R.id.next_btn);
            t = itemView.findViewById(R.id.politics_tv);
            tv = itemView.findViewById(R.id.tv_1);
        }

    }
}