package gg.my.gamemanager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import gg.my.gamemanager.control.DrawImageView;
import gg.my.gamemanager.helpers.Utils;
import gg.my.gamemanager.models.DlcInfo;
import gg.my.gamemanager.models.Game;

/**
 * Adapter for RecyclerView.
 * Use factory methods to create instances.
 */
class CardAdapter extends RecyclerView.Adapter<CardAdapter.ItemView> {
    // a function delegate
    @FunctionalInterface
    public interface ItemClickListener {
        void Invoke(int index);
    }

    private Context context;
    private List<Game> games;
    private ItemClickListener callback;
    private static Locale loc = Locale.getDefault();

    public CardAdapter(Context context, List<Game> games, ItemClickListener callback) {
        this.context = context;
        this.games = games;
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemView holder, int position) {
        String desc;
        Game game = games.get(position);
        desc = game.getDescription();

        holder.name.setText(game.getName());
        if (desc.isEmpty()) {
            holder.desc.setText(R.string.no_desc);
        } else {
            holder.desc.setText(desc);
        }
        ViewTreeObserver observer = holder.desc.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int maxLines = (int) holder.desc.getHeight()
                        / holder.desc.getLineHeight();
                holder.desc.setMaxLines(maxLines);
            }
        });
        holder.price.setText(String.format(loc, "%.2f", game.getPrice()));
        holder.date.setText(Utils.formatDate(game.getDate()));
        holder.dlc.setText(String.format(loc, context.getString(R.string.template_DLC), game.getDlcs().size()));
        float score;
        if(game.getRateCount() == 0){
            score = -1;
        }
        else {
            score = (float) (game.getRateGood() * 5 + game.getRateSoso() * 3 + game.getRateBad()) / game.getRateCount();
        }
        if(score>3.7){
            holder.rating.setText(R.string.rating_good);
            holder.rating.setBackgroundColor(context.getColor(R.color.colorGood));
        }
        else if(score > 2.5){
            holder.rating.setText(R.string.rating_soso);
            holder.rating.setBackgroundColor(context.getColor(R.color.colorSoso));
        }
        else if (score > 1){
            holder.rating.setText(R.string.rating_bad);
            holder.rating.setBackgroundColor(context.getColor(R.color.colorBad));
        }
        else{
            holder.rating.setText(R.string.rating_none);
            holder.rating.setBackgroundColor(Color.TRANSPARENT);
        }


        holder.itemView.setOnClickListener(v -> this.callback.Invoke(position));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    // binds list_item.xmlm.xml layout to ItemView
    @NonNull
    @Override
    public ItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ItemView(v);
    }

    public class ItemView extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView price;
        public final TextView dlc;
        public final TextView date;
        public final TextView rating;
        public final TextView desc;


        public ItemView(View v) {
            super(v);
            name = v.findViewById(R.id.itemView_name);
            price = v.findViewById(R.id.itemView_price);
            date = v.findViewById(R.id.itemView_date);
            dlc = v.findViewById(R.id.itemView_dlcCount);
            rating = v.findViewById(R.id.itemView_userRating);
            desc = v.findViewById(R.id.itemView_Desc);
        }

    }
}
