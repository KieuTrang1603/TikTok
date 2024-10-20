package com.example.tiktok.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.fragment.CommentFragment;
import com.example.tiktok.models.Comment;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.ItemClickListener;
import com.example.tiktok.utils.MyUtil;

import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentFragmentAdapter extends RecyclerView.Adapter<CommentFragmentAdapter.ViewHolder> {
        // tag'
        private static final String TAG = "CommentFragmentAdapter";
        Context context;
        private final List<Comment> comments;
        PopupMenu popupMenu;
        final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
	    public CommentFragmentAdapter(List<Comment> comments, Context context) {
            this.comments = comments;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.comment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (!payloads.isEmpty()) {
                if (position >= comments.size())
                    notifyItemChanged(position);
                else if (payloads.get(0) instanceof Comment) {
                    // Log
                    Log.i(TAG, "Comment is updated at position " + position);

                    Comment comment = (Comment) payloads.get(0);
                    holder.txt_content.setText(comment.getContent());
                }
            } else
                super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.txt_content.setText(comment.getContent());
            holder.txt_username.setText(comment.getUsername());
//            holder.txt_time_comment.setText(MyUtil.getTimeAgo(comment.getTime()));
            holder.txt_time_comment.setText(comment.getTime());
            holder.txt_num_likes_comment.setText(String.valueOf(comment.getNum_like()));
            String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + comment.getAvatar();
            try {
                if (comment.getAvatar() != null && !comment.getAvatar().isEmpty()) {
                    Glide.with(context)
                            .load(imgURL)
                            .error(R.drawable.default_avatar)
                            .into(holder.img_avatar);
                }else
                    // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
                    holder.img_avatar.setImageResource(R.drawable.default_avatar);
            } catch (Exception e) {
                Log.w(TAG, "Glide error: " + e.getMessage());
            }
//            apitiktok.getByIdUser(comment.getUser_id()).enqueue(new Callback<Root<User>>() {
//                @Override
//                public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
//                    User user = response.body().data;
//                    String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + user.getAvatar();
//                    try {
//                        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
//                            Glide.with(context)
//                                    .load(imgURL)
//                                    .error(R.drawable.default_avatar)
//                                    .into(holder.img_avatar);
//                        }else
//                            // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
//                            holder.img_avatar.setImageResource(R.drawable.default_avatar);
//                    } catch (Exception e) {
//                        Log.w(TAG, "Glide error: " + e.getMessage());
//                    }
//                }

//                @Override
//                public void onFailure(Call<Root<User>> call, Throwable t) {
//                    Log.d("Tai nguoi dung dang video that bai" , t.getMessage());
//                }
//        });

            RecyclerView recycler_reply_comment = holder.recycler_reply_comment;
            recycler_reply_comment.setLayoutManager(new LinearLayoutManager(context));

            // TODO: get replies, do it if have enough time
            // getReplies(comment, recycler_reply_comment);

            holder.setItemClickListener((view, position1, isLongClick) -> {
                if (!isLongClick) {
                    popupMenu = new PopupMenu(holder.txt_content.getContext(), view);
                    popupMenu.inflate(R.menu.popup_menu_comment);
                    popupMenu.setGravity(Gravity.CENTER);

                    // Check owner
                    if (MainActivity.isLoggedIn()
                            && (MainActivity.getCurrentUser().getUser_id().equals(comment.getUser_id())
                            || MainActivity.getCurrentUser().isAdmin()))
                        popupMenu.getMenu().findItem(R.id.menu_comment_delete).setVisible(true);

                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_comment_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Bạn có chắc chắn muốn xóa bình luận này?");
                                builder
                                        .setPositiveButton("Xóa", (dialog, which) -> {
                                            //api xoa cmt
//                                            VideoFirebase.getVideoByVideoIdOneTime(comment.getVideoId(),
//                                                    video -> {
//                                                        CommentFirebase.deleteCommentFromVideo(comment, video);
//                                                        comments.remove(comment);
//                                                        notifyItemRemoved(position);
//                                                        notifyItemRangeChanged(0, comments.size());
//                                                        Toast.makeText(context, "Xóa bình luận thành công!", Toast.LENGTH_SHORT).show();
//                                                    }, error -> {
//                                                        Toast.makeText(context, "Xóa bình luận thất bại!", Toast.LENGTH_SHORT).show();
//                                                        Log.e(TAG, "onBindViewHolder: " + error.getMessage());
//                                                    });
                                        })
                                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                                builder.show();
                                break;

                            case R.id.menu_comment_reply:

                            case R.id.menu_comment_report:
                                Toast.makeText(context, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.menu_comment_copy:
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("content", comment.getContent());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "Đã copy!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    });

                    popupMenu.show();
                }
            });

            holder.txt_reply_comment.setOnClickListener(view ->
                            Toast.makeText(context, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show()
//				handleReplyComment(comment)
            );

            holder.ic_like_comment.setOnClickListener(view -> handleLikeComment(comment));

            if (comment.isLiked()) {
                holder.ic_like_comment.setImageResource(R.drawable.ic_liked);
            } else {
                holder.ic_like_comment.setImageResource(R.drawable.ic_like_outline);
            }

            holder.img_avatar.setOnClickListener(view -> {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.hideCommentFragment();
                MyUtil.goToUser(context, comment.getUser_id());
            });
        }

        private void handleLikeComment(Comment comment) {
            if (MainActivity.isLoggedIn()) {
                if (comment.isLiked()) {
//                    CommentFirebase.unlikeComment(comment);
                } else {
//                    CommentFirebase.likeComment(comment);
                }
                notifyItemChanged(comments.indexOf(comment));
            } else
                Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
        }

        private void handleReplyComment(Comment comment) {
            // Set reply to comment id
            CommentFragment.newComment.setParent_comment_id(comment.getComment_id());

            // Show reply comment title in input
            TextView title = ((MainActivity) context).findViewById(R.id.txt_reply_comment_title);
            String htmlText = "Trả lời bình luận của <b>@" + comment.getUsername() + "</b>";
            title.setText(HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            ConstraintLayout layout_reply_comment_title = ((MainActivity) context).findViewById(R.id.layout_reply_comment_title);
            layout_reply_comment_title.setVisibility(View.VISIBLE);

        }


        @Override
        public int getItemCount() {
            return comments.size();
        }

        public List<Comment> getComments() {
            return comments;
        }

        public int getPosition(Comment comment) {
            return comments.indexOf(comment);
        }

        public void setComments(List<Comment> newComments) {
            for (Comment comment : comments) {
                if (!comments.contains(comment)) {
                    comments.add(comment);
                    notifyItemInserted(comments.size() - 1);
                } else {
                    int index = comments.indexOf(comment);
                    notifyItemChanged(index, comment);
                }
            }

            comments.removeIf(comment -> {
                if (!newComments.contains(comment)) {
                    notifyItemRemoved(comments.indexOf(comment));
                    return true;
                }
                return false;
            });
//            Iterator<Comment> iterator = comments.iterator();
//            while (iterator.hasNext()) {
//                Comment comment = iterator.next();
//                if (!newComments.contains(comment)) {
//                    int index = comments.indexOf(comment);
//                    iterator.remove(); // Sử dụng comment để xóa video an toàn
//                    notifyItemRemoved(index);
//                }
//            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView txt_username, txt_content, txt_time_comment, txt_reply_comment, txt_num_likes_comment;
            ImageView img_avatar, ic_like_comment;
            RecyclerView recycler_reply_comment;
            private ItemClickListener itemClickListener;

            public ViewHolder(View itemView) {
                super(itemView);
                txt_username = itemView.findViewById(R.id.txt_email);
                txt_content = itemView.findViewById(R.id.txt_content);
                img_avatar = itemView.findViewById(R.id.img_avatar);
                txt_time_comment = itemView.findViewById(R.id.txt_time_comment);
                recycler_reply_comment = itemView.findViewById(R.id.recycler_reply_comment);
                txt_reply_comment = itemView.findViewById(R.id.txt_reply_comment);
                ic_like_comment = itemView.findViewById(R.id.ic_like_comment);
                txt_num_likes_comment = itemView.findViewById(R.id.txt_num_likes_comment);

                itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
                itemView.setOnLongClickListener(this); // Mấu chốt ở đây , set sự kiên onLongClick cho View
            }

            //Tạo setter cho biến itemClickListenenr
            public void setItemClickListener(ItemClickListener itemClickListener) {
                this.itemClickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }

            @Override
            public boolean onLongClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition(), true);
                return true;
            }
        }
}
