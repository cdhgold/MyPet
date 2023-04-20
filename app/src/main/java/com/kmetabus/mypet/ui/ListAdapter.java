package com.kmetabus.mypet.ui;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kmetabus.mypet.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<ListItem> listItems;   // list 항목들
    private OnListItemClickListener onListItemClickListener;    // click interface
    public ListAdapter(List<ListItem> listItems, OnListItemClickListener onListItemClickListener) {
        this.listItems = listItems;
        this.onListItemClickListener = onListItemClickListener;

    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view,onListItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListItem listitem = listItems.get(position);
        holder.seq.setText(listitem.getSeq());
        holder.col1.setText(listitem.getCol1());
        holder.col2.setText(listitem.getCol2());
        holder.col3.setText(listitem.getCol3());
        holder.col4.setText(listitem.getCol4());

        holder.ListItemImg.setImageResource(listitem.getImageResourceId() );
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView seq;
        TextView col1;
        TextView col2;
        TextView col3;
        TextView col4;
        ImageView ListItemImg;
        OnListItemClickListener onListItemClickListener;
        public ListViewHolder(@NonNull View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView);
            seq = itemView.findViewById(R.id.seq);
            col1 = itemView.findViewById(R.id.col1);
            col2 = itemView.findViewById(R.id.col2);
            col3 = itemView.findViewById(R.id.col3);
            col4 = itemView.findViewById(R.id.col4);

            ListItemImg = itemView.findViewById(R.id.menu_item_image);
            this.onListItemClickListener = onListItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListItemClickListener.onListItemClick(listItems.get(getBindingAdapterPosition()));
        }


    }
}
