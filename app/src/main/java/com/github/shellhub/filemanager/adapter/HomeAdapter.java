package com.github.shellhub.filemanager.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileType;
import com.github.shellhub.filemanager.event.FileEntityEvent;
import com.github.shellhub.filemanager.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Setter
    List<FileEntity> fileEntities = new ArrayList<>();
    private Context mContext;

    private final int TYPE_FOLDER = 0;
    private final int TYPE_AUDIO = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        switch (viewType) {
            case TYPE_FOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_home_folder_item, parent, false);
                ButterKnife.bind(this, view);
                return new HomeFolderViewHolder(view);
            case TYPE_AUDIO:
                view = LayoutInflater.from(mContext).inflate(R.layout.nav_home_audio_item, parent, false);
                ButterKnife.bind(this, view);
                return new HomeAudioViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeFolderViewHolder) {
            ((HomeFolderViewHolder) holder).bind(position);
        } else if (holder instanceof HomeAudioViewHolder) {
            ((HomeAudioViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (fileEntities.get(position).getFileType() == FileType.TYPE_FOLDER) {
            return TYPE_FOLDER;
        } else {
            return TYPE_AUDIO;
        }
    }

    @Override
    public int getItemCount() {
        return fileEntities.size();
    }


    public class HomeFolderViewHolder extends RecyclerView.ViewHolder {
        private String TAG = this.getClass().getSimpleName();
        @BindView(R.id.tvHomeAudioName)
        TextView tvHomeFolderName;

        @BindView(R.id.tv_home_folder_sub_count)
        TextView tvHomeFolderSubCount;

        @BindView(R.id.tv_folder_last_modify_time)
        TextView tvFolderLastModifyTime;


        @BindView(R.id.iv_home_audio_more_menu)
        ImageView ivHomeFolderMoreMenu;

        @BindView(R.id.iv_home_folder)
        ImageView ivHomeFolder;

        public HomeFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);
            tvHomeFolderName.setText(fileEntity.getName());

            //init last modify time
            DateFormat dateFormat = new SimpleDateFormat("MM/d/YY,hh:mm a", Locale.ENGLISH);
            String result = dateFormat.format(new Date(fileEntity.getLastMidify()));
            tvFolderLastModifyTime.setText(result);

            String subCountTitle;
            if (fileEntity.getSubCount() > 1) {
                subCountTitle = "(" + fileEntities.get(position).getSubCount() + " " + mContext.getResources().getString(R.string.items) + ")";//e.g(2 items)
            } else {
                subCountTitle = "(" + fileEntities.get(position).getSubCount() + " " + mContext.getResources().getString(R.string.item) + ")";//e.g(1 item)
            }
            tvHomeFolderSubCount.setText(subCountTitle);

            ivHomeFolderMoreMenu.setOnClickListener((view) -> {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.pop, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.rename:
                            break;
                        case R.id.delete:
                            break;
                        case R.id.copy:
                            break;
                        case R.id.cut:
                            break;
                        default:
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
            itemView.setOnClickListener((view) -> {
                EventBus.getDefault().post(new FileEntityEvent(fileEntities.get(position)));
            });
        }
    }

    public class HomeAudioViewHolder extends RecyclerView.ViewHolder {

        private String TAG = this.getClass().getSimpleName();
        @BindView(R.id.tvHomeAudioName)
        TextView tvHomeAudioName;

        @BindView(R.id.iv_home_audio_more_menu)
        ImageView ivHomeAudioMoreMenu;

        @BindView(R.id.iv_audio_album_cover)
        ImageView ivAudioAlbumCover;

        @BindView(R.id.tv_album_name)
        TextView tvAlbumName;

        @BindView(R.id.tv_home_audio_duration)
        TextView tvHomeAudioDuration;

        public HomeAudioViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);

            //init audio(title)
            tvHomeAudioName.setText(fileEntity.getName());

            //extra meta tools
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(fileEntity.getPath());

            //init album name
            tvAlbumName.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

            //init album cover
            Glide.with(mContext).load(mmr.getEmbeddedPicture()).into(ivAudioAlbumCover);

            //init duration
            tvHomeAudioDuration.setText(TimeUtils.formatDuration(Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));
        }
    }
}
