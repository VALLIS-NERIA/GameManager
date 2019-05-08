package gg.my.gamemanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gg.my.gamemanager.control.DrawImageView;
import gg.my.gamemanager.models.DlcInfo;
import gg.my.gamemanager.models.Game;

/**
 * Adapter for RecyclerView.
 * Use factory methods to create instances.
 */
class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemView> {
    // a function delegate
    @FunctionalInterface
    public interface ItemClickListener {
        void Invoke(int index);
    }

    private boolean dlcMode;
    private List<DlcInfo> dlcs;

    private List<Game> games;
    private ItemClickListener callback;

    public static MyAdapter ForGames(List<Game> games, ItemClickListener callback) {
        MyAdapter a = new MyAdapter();
        a.games = games;
        a.dlcs = null;
        a.callback = callback;
        a.dlcMode = false;
        return a;
    }

    public static MyAdapter ForDlcs(List<DlcInfo> dlcs, ItemClickListener callback) {
        MyAdapter a = new MyAdapter();
        a.dlcs = dlcs;
        a.games = null;
        a.callback = callback;
        a.dlcMode = true;
        return a;
    }

    private MyAdapter() {
    }

    @Override
    public void onBindViewHolder(@NonNull ItemView holder, int position) {
        String name;
        String desc;
        if (this.dlcMode) {
            holder.imageView.setVisibility(View.GONE);
            DlcInfo dlcInfo = dlcs.get(position);
            name = dlcInfo.getName();
            desc = dlcInfo.getDescription();
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            Game game = games.get(position);
            holder.imageView.setDlc(game.getDlcCount() > 0);
            name = game.getName();
            desc = game.getDescription();
        }

        holder.name.setText(name);
        if (desc.isEmpty()) {
            holder.desc.setText(R.string.no_desc);
        } else {
            holder.desc.setText(desc);
        }
        holder.itemView.setOnClickListener(v -> this.callback.Invoke(position));
    }

    @Override
    public int getItemCount() {
        if (dlcMode) {
            return dlcs.size();
        } else {
            return games.size();
        }
    }

    // binds item.xml layout to ItemView
    @Override
    public ItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemView(v);
    }

    public class ItemView extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView desc;
        public final DrawImageView imageView;


        public ItemView(View v) {
            super(v);
            name = v.findViewById(R.id.itemView_name);
            desc = v.findViewById(R.id.itemView_Desc);
            imageView = v.findViewById(R.id.itemView_image);
        }

    }
}
