package gg.my.gamemanager.provider;

import android.content.Context;

import gg.my.gamemanager.R;
import gg.my.gamemanager.model.Game;

public class RatingInfo {
    public String name;
    public int count;
    public int color;

    public RatingInfo(String name, int count, int color){
        this.name = name;
        this.count = count;
        this.color = color;
    }

    public static RatingInfo[] CreateFromGame(Game game, Context context){
        return new RatingInfo[]{
            new RatingInfo(context.getString(R.string.rating_good), game.getRateGood(), context.getColor(R.color.colorGood)),
            new RatingInfo(context.getString(R.string.rating_soso), game.getRateSoso(), context.getColor(R.color.colorSoso)),
            new RatingInfo(context.getString(R.string.rating_bad), game.getRateBad(), context.getColor(R.color.colorBad))
        };
    }
}
