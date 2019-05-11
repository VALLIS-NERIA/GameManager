package gg.my.gamemanager.control;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import gg.my.gamemanager.R;
import gg.my.gamemanager.helpers.RatingInfo;

public class Voter extends android.support.constraint.ConstraintLayout {

    private RadioGroup radioGroup;
    private RadioButton setHist;
    private RadioButton setDonut;
    private DonutDrawView donut;
    private HistogramDrawView hist;
    private FloatingActionButton fabSubmit;
    private RatingInfo[] data;
    private String[] names;
    public Runnable afterSubmitCallback;

    public Voter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.control_voter, this, true);

        this.radioGroup = findViewById(R.id.voter_group);
        this.setHist = findViewById(R.id.voter_set_hist);
        this.setDonut = findViewById(R.id.voter_set_donut);
        this.donut = findViewById(R.id.voter_donut);
        this.hist = findViewById(R.id.voter_hist);
        this.fabSubmit = findViewById(R.id.voter_submit);

        this.setDonut.toggle();

        this.donut.setVisibility(VISIBLE);
        this.hist.setVisibility(GONE);
        this.fabSubmit.setOnClickListener(this::onSubmitClick);

        radioGroup.setOnCheckedChangeListener((RadioGroup rg, int _id) -> {
            int id = rg.getCheckedRadioButtonId();
            switch (id) {
                case R.id.voter_set_hist:
                    hist.setVisibility(VISIBLE);
                    donut.setVisibility(GONE);
                    break;
                default:
                case R.id.voter_set_donut:
                    hist.setVisibility(GONE);
                    donut.setVisibility(VISIBLE);
                    break;
            }
        });
    }

    public void setData(RatingInfo[] data) {
        this.data = data;
        this.names = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            names[i] = data[i].name;
        }
        this.hist.setData(data);
        this.donut.setData(data);
        this.invalidate();
    }

    public void updateData(int[] ratings) {
        for (int i = 0; i < data.length; i++) {
            data[i].count = ratings[i];
        }
        this.hist.setData(data);
        this.donut.setData(data);
        this.hist.invalidate();
        this.donut.invalidate();
    }

    public void setSubmitAvailable(boolean enabled) {
        fabSubmit.setVisibility(enabled ? VISIBLE : GONE);
        radioGroup.setVisibility(enabled ? GONE : VISIBLE);
    }

    private void onSubmitClick(View v) {
        AlertDialog alertDialog3 = new AlertDialog.Builder(getContext())
                .setTitle(R.string.hint_rateGame)
                .setIcon(R.mipmap.ic_launcher)
                .setItems(
                        names,
                        (di, i) -> {
                            this.data[i].voteCallback.run();
                            if (this.afterSubmitCallback != null) {
                                this.afterSubmitCallback.run();
                            }
                        }
                )
                .create();
        alertDialog3.show();
    }
}
