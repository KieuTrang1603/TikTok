package com.example.tiktok.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.WatchVideoActivity;
import com.example.tiktok.adapters.CommentFragmentAdapter;
import com.example.tiktok.adapters.VideoFragmentAdapter;
import com.example.tiktok.adapters.VideoGridAdapter;
import com.example.tiktok.models.Comment;
import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.KeyboardUtil;
import com.example.tiktok.utils.MyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentFragment extends Fragment {
    public static final String TAG = "CommentFragment";
    public static Comment newComment = new Comment();
    private final Video video;
    Context context;
    RecyclerView recycler_view_comments, recycler_view_videos;
    List<Comment> comments = new ArrayList<>();
    static String commentId;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
    private static final List<CommentFragment> instances = new ArrayList<>();

    private CommentFragment(Video video, Context context) {
        this.video = video;
        this.context = context;
    }

    // New instance
    public static CommentFragment getInstance(Video video, Context context) {
        for (CommentFragment instance : instances) {
            if (instance.video.getVideo_id().equals(video.getVideo_id())) {
                return instance;
            }
        }
        CommentFragment instance = new CommentFragment(video, context);
        instances.add(instance);
        return instance;
    }

    public static CommentFragment getInstance(Video video, Context context, String cID) {
        commentId = cID;
        return getInstance(video, context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        view.setVisibility(View.VISIBLE);

        // Reset new comment
        newComment = new Comment();

        // Disable scrollable when layout comment is visible
        recycler_view_comments = view.findViewById(R.id.recycler_view_comments);
        recycler_view_videos = requireActivity().findViewById(R.id.recycler_view_videos);
        recycler_view_videos.addOnItemTouchListener(VideoFragmentAdapter.disableTouchListener);
        recycler_view_comments.setLayoutManager(new LinearLayoutManager(context));// Decorate the list
        recycler_view_comments.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // Get comments from video id
        getComments();

        // Avatar
        ImageView avatar = view.findViewById(R.id.img_avatar);

        try {
            String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + MyUtil.user_current.getAvatar();
            if (MyUtil.user_current.getAvatar() != null && !MyUtil.user_current.getAvatar().isEmpty()) {
                Glide.with(context)
                        .load(imgURL)
                        .error(R.drawable.default_avatar)
                        .into(avatar);
            }else
                // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
                avatar.setImageResource(R.drawable.default_avatar);
        } catch (Exception e) {
            Log.w(TAG, "Glide error: " + e.getMessage());
        }

        EditText txt_comment_input = view.findViewById(R.id.txt_comment_input);
        ImageView ic_send_comment = view.findViewById(R.id.ic_send_comment);
        txt_comment_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String content = charSequence.toString().trim();
                if (content.length() > 0)
                    ic_send_comment.setVisibility(View.VISIBLE);
                else ic_send_comment.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set onClickListener for ic_send_comment
        ic_send_comment.setOnClickListener(v -> handleSendComment(txt_comment_input));

        View layout_comment_header = view.findViewById(R.id.layout_comment_header);
        layout_comment_header.setOnClickListener(v -> {
            // Hide this fragment
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).hideCommentFragment();
            } else if (requireActivity() instanceof WatchVideoActivity) {
                ((WatchVideoActivity) requireActivity()).hideCommentFragment();
            }
        });

        // Disable comment input if user is not logged in
        if (!MainActivity.isLoggedIn()) {
            txt_comment_input.setEnabled(false);
            txt_comment_input.setHint("Chưa đăng nhập");
        }

        // Refresh image
        ImageView ic_refresh = view.findViewById(R.id.ic_refresh);
        ic_refresh.setOnClickListener(v -> {
            // Rotate refresh icon
            RotateAnimation r =
                    new RotateAnimation(0f, 360f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
            r.setDuration((long) 2 * 500);
            r.setRepeatCount(0);
            ic_refresh.startAnimation(r);

            // Refresh comment
            getComments();
        });

        ImageView ic_cancel_reply = view.findViewById(R.id.ic_cancel_reply);
        ic_cancel_reply.setOnClickListener(v -> {
            // Set reply to comment id
            CommentFragment.newComment.setParent_comment_id(null);

            // Hide reply comment title in input
            TextView title = ((MainActivity) context).findViewById(R.id.txt_reply_comment_title);
            title.setText("");
            ConstraintLayout layout_reply_comment_title = ((MainActivity) context).findViewById(R.id.layout_reply_comment_title);
            layout_reply_comment_title.setVisibility(View.GONE);
        });

        return view;
    }

    private void handleSendComment(EditText txt_comment_input) {
        // Get comment content
        String content = txt_comment_input.getText().toString().trim();
        newComment.setContent(content);
//        newComment.setTime(MyUtil.dateTimeToString(new Date()));
        newComment.setUsername(MainActivity.getCurrentUser().getUsername());
        newComment.setVideo_id(video.getVideo_id());

        // Add comment to db
        addCommentToVideo(newComment, video);

        // Toast message
        Toast.makeText(context, "Bình luận thành công!", Toast.LENGTH_SHORT).show();

        // Refresh comments
//        getComments();

        // Scroll to top
        recycler_view_comments.scrollToPosition(0);

        // Clear comment input
        txt_comment_input.setText("");

        // Reset new comment
        newComment = new Comment();

        // Hide keyboard
        KeyboardUtil.hideKeyboard(requireActivity());
    }

    private void getComments() {
//        comments.clear();
//        if (video.getComments() != null) {
//            video.getComments().forEach((comment_id) -> {
                apitiktok.getAllComment(null, video.getVideo_id(), null, null).enqueue(new Callback<Root<Data<Comment>>>() {
                    @Override
                    public void onResponse(Call<Root<Data<Comment>>> call, Response<Root<Data<Comment>>> response) {
                        if (response.isSuccessful()) {
                            Log.d("Lay comment thanh cong", response.message());
                            comments = response.body().data.content;
//                            if (comments.size() == video.getComments().size()) {
//                                comments.removeIf(c -> !c.isValid());
//                                // Sort comments by time
//                                Comment.sortByTimeNewsest(comments);

                                CommentFragmentAdapter adapter = (CommentFragmentAdapter) recycler_view_comments.getAdapter();
                                if (adapter != null)
                                    adapter.setComments(comments);
                                else {
                                    // Set adapter for recycler view
                                    adapter = new CommentFragmentAdapter(comments, context);
                                    recycler_view_comments.setAdapter(adapter);
                                }
                                if (CommentFragment.commentId != null) {
                                    // Scroll to comment
                                    scrollToComment(commentId);
                                }
                            }
                        }
//                    }

                    @Override
                    public void onFailure(Call<Root<Data<Comment>>> call, Throwable t) {
                        Log.d("That bai", t.getMessage());
                    }
                });

//                            if (comments.size() == video.getComments().size()) {
//                                comments.removeIf(c -> !c.isValid());
//                                // Sort comments by time
//                                Comment.sortByTimeNewsest(comments);
//
//                                CommentFragmentAdapter adapter = (CommentFragmentAdapter) recycler_view_comments.getAdapter();
//                                if (adapter != null)
//                                    adapter.setComments(comments);
//                                else {
//                                    // Set adapter for recycler view
//                                    adapter = new CommentFragmentAdapter(comments, context);
//                                    recycler_view_comments.setAdapter(adapter);
//                                }
//                                if (CommentFragment.commentId != null) {
//                                    // Scroll to comment
//                                    scrollToComment(commentId);
//                                }
//                            }
//                        }, error -> {
//                            Toast.makeText(context, "Lỗi khi lấy bình luận!", Toast.LENGTH_SHORT).show();
//                        }
//                );
//            });
//        }
    }

    public void scrollToComment(String commentId) {
        boolean found = false;
        // Scroll to comment
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getComment_id().equals(commentId)) {
                recycler_view_comments.scrollToPosition(i);
                found = true;
                break;
            }
        }
        if (!found) {
            Toast.makeText(context, "Không tìm thấy bình luận!", Toast.LENGTH_SHORT).show();
        }
    }
    private void addCommentToVideo(Comment newComment, Video video){
        apitiktok.addcomment(MyUtil.user_current.getUser_id(), newComment.getContent(), video.getVideo_id()).enqueue(new Callback<Root<Comment>>() {
            @Override
            public void onResponse(Call<Root<Comment>> call, Response<Root<Comment>> response) {
                if(response.isSuccessful()){
                    Log.d("Thanh cong", response.message());
                    Toast.makeText(context, "Bình luận thành công!", Toast.LENGTH_SHORT).show();
                    //api thong bao
                    Comment comment = new Comment();
                    comment = response.body().data;
//                    video.setNum_comments(video.getNum_like() + 1);
//                    comments.add(response.body().data);
//                    video.setComments(comment.getCommentIds(comments));
                    CommentFragmentAdapter commentFragmentAdapter = (CommentFragmentAdapter) recycler_view_comments.getAdapter();
                    if (commentFragmentAdapter != null) {
                        // Thêm comment mới vào danh sách comment trong adapter
                        commentFragmentAdapter.getComments().add(comment);

                        // Thông báo cho adapter rằng có một phần tử mới
                        commentFragmentAdapter.notifyItemInserted(commentFragmentAdapter.getItemCount());
                    }
                }
            }

            @Override
            public void onFailure(Call<Root<Comment>> call, Throwable t) {
                Log.d("That bai", t.getMessage());
            }
        });
    }
}