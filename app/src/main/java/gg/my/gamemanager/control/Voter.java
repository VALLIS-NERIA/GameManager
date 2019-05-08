package gg.my.gamemanager.control;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;

import gg.my.gamemanager.R;
import gg.my.gamemanager.helpers.RatingInfo;

public class Voter extends android.support.constraint.ConstraintLayout {
    private RadioButton setHist;
    private RadioButton setDonut;
    private DonutDrawView donut;
    private HistogramDrawView hist;
    private RatingInfo[] data;

    public Voter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.control_voter, this, true);

        this.setHist = findViewById(R.id.voter_set_hist);
        this.setDonut = findViewById(R.id.voter_set_donut);
        this.donut = findViewById(R.id.voter_donut);
        this.hist = findViewById(R.id.voter_hist);

        this.setHist.toggle();
        this.setDonut.toggle();
    }

    public void setData(RatingInfo[] data) {
        this.data = data;
        this.hist.setData(data);
        this.donut.setData(data);
    }


}
