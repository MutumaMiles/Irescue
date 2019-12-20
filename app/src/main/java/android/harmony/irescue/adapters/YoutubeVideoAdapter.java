package android.harmony.irescue.adapters;

import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.R;
import android.harmony.irescue.YoutubePlayerActivity;
import android.harmony.irescue.model.YoutubeVideoModel;
import android.harmony.irescue.utility.Constants;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;


public class YoutubeVideoAdapter extends RecyclerView.Adapter<YoutubeVideoAdapter.YoutubeViewHolder> {
    private static final String TAG = YoutubeVideoAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList;


    public YoutubeVideoAdapter(Context context, ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList) {
        this.context = context;
        this.youtubeVideoModelArrayList = youtubeVideoModelArrayList;
    }

    @Override
    public YoutubeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.youtube_row, parent, false);
        return new YoutubeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(YoutubeViewHolder holder, final int position) {

        final YoutubeVideoModel youtubeVideoModel = youtubeVideoModelArrayList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, YoutubePlayerActivity.class);
                intent.putExtra("video_id",youtubeVideoModel.getVideoId());
                context.startActivity(intent);
            }
        });

        holder.videoTitle.setText(youtubeVideoModel.getTitle());
        holder.videoDuration.setText(youtubeVideoModel.getDuration());


        /*  initialize the thumbnail image view , we need to pass Developer Key */
        holder.videoThumbnailImageView.initialize(Constants.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                //when initialization is sucess, set the video id to thumbnail to load
                youTubeThumbnailLoader.setVideo(youtubeVideoModel.getVideoId());

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        //print or show error when thumbnail load failed
                        Log.e(TAG, "YoutubeActivity Thumbnail Error");
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //print or show error when initialization failed
                Log.e(TAG, "YoutubeActivity Initialization Failure");

            }
        });

    }

    @Override
    public int getItemCount() {
        return youtubeVideoModelArrayList != null ? youtubeVideoModelArrayList.size() : 0;
    }

    public class YoutubeViewHolder extends RecyclerView.ViewHolder {
        public YouTubeThumbnailView videoThumbnailImageView;
        public TextView videoTitle, videoDuration;

        public YoutubeViewHolder(View itemView) {
            super(itemView);
            videoThumbnailImageView = itemView.findViewById(R.id.video_thumbnail_image_view);
            videoTitle = itemView.findViewById(R.id.video_title_label);
            videoDuration = itemView.findViewById(R.id.video_duration_label);
        }
    }
}
