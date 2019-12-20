package android.harmony.irescue.adapters;

import android.content.Context;
import android.harmony.irescue.CommentActivity;
import android.harmony.irescue.R;
import android.harmony.irescue.model.Comment;
import android.harmony.irescue.utility.CalculateTime;
import android.harmony.irescue.utility.Constants;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public CommentsAdapter(List<Comment> comments,Context context) {
        mContext = context;
        mComments = comments;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_layout,null);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment=mComments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder{
        ImageView userImageView;
        TextView comment,time;
        CommentsViewHolder(View itemView) {
            super(itemView);
            userImageView=itemView.findViewById(R.id.user_image_view);
            comment=itemView.findViewById(R.id.comment);
            time=itemView.findViewById(R.id.time);
        }
        void bind(Comment comment){
            this.comment.setText(comment.getComment());
            Glide.with(mContext)
                    .load(comment.getUser().getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userImageView);
            time.setText(String.valueOf(CalculateTime.calculateTime(Long.valueOf(comment.getTime()))).equals("Yesterday") ?"Yesterday":"About "+CalculateTime.calculateTime(Long.valueOf(comment.getTime()))+" ago");

        }
    }
}
