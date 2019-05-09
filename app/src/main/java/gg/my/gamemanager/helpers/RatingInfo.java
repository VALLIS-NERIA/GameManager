package gg.my.gamemanager.helpers;

import android.content.Context;

import gg.my.gamemanager.R;
import gg.my.gamemanager.models.Game;

public class RatingInfo {
    public String name;
    public int count;
    public int color;
    public Runnable voteCallback;

    public RatingInfo(String name, int count, int color, Runnable voteCallback){
        this.name = name;
        this.count = count;
        this.color = color;
        this.voteCallback = voteCallback;
    }

    public static RatingInfo[] CreateFromGame(Game game, Context context){
        return new RatingInfo[]{
            new RatingInfo(context.getString(R.string.rating_good), game.getRateGood(), context.getColor(R.color.colorGood), game::voteGood),
            new RatingInfo(context.getString(R.string.rating_soso), game.getRateSoso(), context.getColor(R.color.colorSoso), game::voteSoso),
            new RatingInfo(context.getString(R.string.rating_bad), game.getRateBad(), context.getColor(R.color.colorBad), game::voteBad)
        };
    }
}
