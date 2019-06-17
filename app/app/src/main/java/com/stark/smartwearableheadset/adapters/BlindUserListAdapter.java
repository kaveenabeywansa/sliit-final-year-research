package com.stark.smartwearableheadset.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.models.BlindUser;

import java.util.List;

public class BlindUserListAdapter extends ArrayAdapter<BlindUser> {
    private Context context;
    private List<BlindUser> blindList;

    public BlindUserListAdapter(Context context, List<BlindUser> users) {
        super(context, R.layout.blind_user_list_item, users);
        this.context = context;
        this.blindList = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // create new inflator
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.blind_user_list_item, parent, false);

        // init comps
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        TextView userPhone = (TextView) convertView.findViewById(R.id.user_phone);
//        ImageView userImg = (ImageView) convertView.findViewById(R.id.user_img); // uncomment this after adding user img func

        // set values
        userName.setText(blindList.get(position).getName());
        userPhone.setText(blindList.get(position).getPhone());
//        Picasso.with(context).load(blindList.get(position).getImg()).into(userImg); // uncomment this after adding user img func

        return convertView;
    }
}
