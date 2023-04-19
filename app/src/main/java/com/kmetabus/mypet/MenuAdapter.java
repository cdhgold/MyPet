package com.kmetabus.mypet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItem> menuItems;
    private OnMenuItemClickListener onMenuItemClickListener;
    public MenuAdapter(List<MenuItem> menuItems,OnMenuItemClickListener onMenuItemClickListener) {
        this.menuItems = menuItems;
        this.onMenuItemClickListener = onMenuItemClickListener;

    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(view,onMenuItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.menuItemTitle.setText(menuItem.getTitle());
        holder.menuItemDescription.setText(menuItem.getDescription());
        holder.menuItemImg.setImageResource(menuItem.getImageResourceId() );
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView menuItemTitle;
        TextView menuItemDescription;
        ImageView menuItemImg;
        OnMenuItemClickListener onMenuItemClickListener;
        public MenuViewHolder(@NonNull View itemView, OnMenuItemClickListener onMenuItemClickListener) {
            super(itemView);
            menuItemTitle = itemView.findViewById(R.id.menu_item_title);
            menuItemDescription = itemView.findViewById(R.id.menu_item_description);
            menuItemImg = itemView.findViewById(R.id.menu_item_image);
            this.onMenuItemClickListener = onMenuItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMenuItemClickListener.onMenuItemClick(menuItems.get(getBindingAdapterPosition()));
        }


    }
}
