package com.ducnd.customgallery.libs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ducnd.customgallery.R;
import com.ducnd.customgallery.libs.utils.ItemGallaryImage;
import com.ducnd.customgallery.libs.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by ducnd on 7/5/17.
 */

public class GalleryImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private IGalleryImageAdapter mInterf;

    public GalleryImageAdapter(IGalleryImageAdapter interf) {
        mInterf = interf;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_square, parent, false);
            return new ViewHolderImage(view, this);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buttom_load_more, parent, false);
        return new ViewHolderLoadMore(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_IMAGE) {
            ViewHolderImage image = (ViewHolderImage) holder;
            ItemGallaryImage gallaryImage = mInterf.getData(position);
            if (gallaryImage.getPairInt() == null) {
                if (StringUtils.isEmpty(gallaryImage.getThumbnail())) {
                    Picasso.with(image.itemView.getContext())
                            .load(new File(gallaryImage.getPathFile()))
                            .placeholder(android.R.color.darker_gray)
                            .error(android.R.color.darker_gray)
                            .resize(300, 300)
                            .into(image.ivImage);
                } else {
                    Picasso.with(image.itemView.getContext())
                            .load(new File(gallaryImage.getThumbnail()))
                            .placeholder(android.R.color.darker_gray)
                            .error(android.R.color.darker_gray)
                            .into(image.ivImage);
                }
            } else {
                Picasso.with(image.itemView.getContext())
                        .load(new File(gallaryImage.getPathFile()))
                        .placeholder(android.R.color.darker_gray)
                        .error(android.R.color.darker_gray)
                        .resize(gallaryImage.getPairInt().getFirst(), gallaryImage.getPairInt().getSecond())
                        .into(image.ivImage);
            }
        } else {
            mInterf.loadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mInterf.getCount();
    }

    @Override
    public void onClick(View v) {
        IGetPosition getPosition = (IGetPosition) v.getTag();
        mInterf.onClickItemImage(getPosition.getPosition());
    }

    private static class ViewHolderImage extends RecyclerView.ViewHolder {
        private ImageView ivImage;

        public ViewHolderImage(View itemView, View.OnClickListener onClick) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            IGetPosition getPosition = new IGetPosition() {
                @Override
                public int getPosition() {
                    return getAdapterPosition();
                }
            };
            itemView.setTag(getPosition);
            itemView.setOnClickListener(onClick);
        }
    }

    private static class ViewHolderLoadMore extends RecyclerView.ViewHolder {
        public ViewHolderLoadMore(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mInterf.getCount() - 1 && mInterf.hasLoadMore()) {
            return TYPE_LOAD_MORE;
        }
        return TYPE_IMAGE;
    }

    public interface IGetPosition {
        int getPosition();
    }

    public interface IGalleryImageAdapter {
        int getCount();

        ItemGallaryImage getData(int position);

        boolean hasLoadMore();

        void loadMore();

        void onClickItemImage(int position);
    }
}
