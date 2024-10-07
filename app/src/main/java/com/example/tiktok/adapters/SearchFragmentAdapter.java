package com.example.tiktok.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;
import com.example.tiktok.utils.RecyclerViewDisabler;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.SearchViewHolder> {
    private static final String TAG = "SearchFragementAdapter";
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);

    public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
    Context context;
    private List<User> users;

    public SearchFragmentAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SearchViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_account, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User user = users.get(position);
        System.out.println("position: " + position);
        holder.txt_username.setText(user.getUsername());
        holder.txt_fullname.setText(user.getFullName());
        if (user.getNum_followers() == 0) {
            holder.txt_number_follow.setText("0");
        } else {
            holder.txt_number_follow.setText(String.valueOf(user.getNum_followers()));
            //get user current
            User currentUser = MainActivity.getCurrentUser();
            if ((Integer.parseInt(String.valueOf(user.getNum_followers())) > 0 && currentUser.isFollowing(user.getUsername())) || user.getUsername().equals(currentUser.getUsername())) {
				holder.btn_follow.setText("Following");
				//set backgroundTint to button
				holder.btn_follow.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.teal_200)));
				holder.btn_follow.setEnabled(false);
                //hide button
                holder.btn_follow.setVisibility(View.GONE);
            }
        }
        // Load avatar
        String imgURL = RetrofitClient.getBaseUrl() +"/api/file/video/view?fileName=" + user.getAvatar();
        try {
            Glide.with(context)
                    .load(imgURL)
                    .error(R.drawable.default_avatar)
                    .into(holder.img_avatar);
        } catch (Exception e) {
            Log.w(TAG, "Glide error: " + e.getMessage());
        }

        //handle follow button
        holder.btn_follow.setOnClickListener(v -> {
            //api follow
            apitiktok.follow(user.getUser_id(),MyUtil.user_current.getUser_id()).enqueue(new Callback<Root<User>>() {
                @Override
                public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                    Log.d("UnFollow thanh cong", response.message());
                    holder.btn_follow.setText("Following");
                    //set backgroundTint to button
                    holder.btn_follow.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.teal_200)));
                    holder.btn_follow.setEnabled(false);
                    holder.btn_follow.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Root<User>> call, Throwable t) {
                    Log.d("Follow that bai", t.getMessage());
                }
            });
//				//hide button
            Toast.makeText(context, "Đã theo dõi " + user.getUsername(), Toast.LENGTH_SHORT).show();
        });
        //handle click item
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onClick on Search: " + user.getUser_id());
            MyUtil.goToUser((Activity) context, user.getUser_id());
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_fullname, txt_number_follow;
        Button btn_follow;
        CircularImageView img_avatar;
        SearchView searchView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_usernameSearch);
            txt_fullname = itemView.findViewById(R.id.txt_fullnameSearch);
            txt_number_follow = itemView.findViewById(R.id.number_followerSearch);
            img_avatar = itemView.findViewById(R.id.img_avatarUser);
            searchView = itemView.findViewById(R.id.searchView);
            btn_follow = itemView.findViewById(R.id.btn_followSearch);
        }
    }
}
