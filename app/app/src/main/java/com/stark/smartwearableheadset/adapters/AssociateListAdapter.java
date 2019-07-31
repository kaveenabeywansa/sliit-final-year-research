package com.stark.smartwearableheadset.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.models.User;

import java.util.List;

public class AssociateListAdapter extends ArrayAdapter {
    private Context context;
    private List<User> associateList;

    public AssociateListAdapter(Context context, List<User> users) {
        super(context, R.layout.known_associates_list_item, users);
        this.context = context;
        this.associateList = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // create new inflator
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.known_associates_list_item, parent, false);

        // init comps
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        TextView userUName = (TextView) convertView.findViewById(R.id.username);
//        ImageView userImg = (ImageView) convertView.findViewById(R.id.user_img); // uncomment this after adding user img func

        // set values
        userName.setText(associateList.get(position).getName());
        userUName.setText(associateList.get(position).getUsername());
//        Picasso.with(context).load(blindList.get(position).getImg()).into(userImg); // uncomment this after adding user img func

        return convertView;
    }
}
