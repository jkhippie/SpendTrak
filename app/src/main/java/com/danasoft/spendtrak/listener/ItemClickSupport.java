package com.danasoft.spendtrak.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.danasoft.spendtrak.R;

import org.jetbrains.annotations.NotNull;

import static android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

public class ItemClickSupport {
    private final RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                ViewHolder holder = recyclerView.getChildViewHolder(view);
                onItemClickListener.onItemClicked(recyclerView, holder.getAdapterPosition(), view);
            }
        }
    };
    private final View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                ViewHolder holder = recyclerView.getChildViewHolder(view);
                return onItemLongClickListener.onItemLongClicked(recyclerView, holder.getAdapterPosition(), view);
            }
            return false;
        }
    };
    private final OnChildAttachStateChangeListener attachListener = new OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(@NonNull View view) {
            if (onItemClickListener != null) {
                view.setOnClickListener(onClickListener);
            }
            if (onItemLongClickListener != null) {
                view.setOnLongClickListener(onLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(@NonNull View view) {
        }
    };

    private ItemClickSupport(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.setTag(R.id.item_click_support, this);
        this.recyclerView.addOnChildAttachStateChangeListener(attachListener);
    }

    public static ItemClickSupport addTo(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    public static void removeFrom(@NotNull RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
    }

    @SuppressWarnings("all")
    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
        return this;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(attachListener);
        view.setTag(R.id.item_click_support, null);
    }

    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View view);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View view);
    }
}


