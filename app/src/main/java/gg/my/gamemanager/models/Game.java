package gg.my.gamemanager.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The game model class.
 */

public class Game {
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESC = "description";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_DLC = "dlcs";
    private static final String FIELD_HOUR = "hours";
    private static final String FIELD_GOOD = "rate_good";
    private static final String FIELD_SOSO = "rate_soso";
    private static final String FIELD_BAD = "rate_bad";
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String name;
    private String description;
    private Float price;
    private Calendar date;
    private int hours;
    private List<DlcInfo> dlcs;
    private int rateGood;
    private int rateSoso;
    private int rateBad;

    public Game() {
        this.date = Calendar.getInstance();
        this.name = "";
        this.description = "";
        this.price = 0f;
        this.hours = 0;
        this.dlcs = new ArrayList<>();
    }

    /**
     * Creates a {@link Game} instance from JsonObject.
     */
    public static Game fromJsonObject(JSONObject jobj) throws JSONException {
        Game g = new Game();
        g.name = jobj.getString(FIELD_NAME);
        g.description = jobj.getString(FIELD_DESC);
        g.price = (float) jobj.getDouble(FIELD_PRICE);
        g.hours = jobj.getInt(FIELD_HOUR);
        g.rateGood = jobj.getInt(FIELD_GOOD);
        g.rateSoso = jobj.getInt(FIELD_SOSO);
        g.rateBad = jobj.getInt(FIELD_BAD);

        try {
            g.date.setTime(df.parse(jobj.getString(FIELD_DATE)));
        } catch (ParseException e) {
            throw new JSONException("Invalid format for date");
        }
        JSONArray arr = jobj.getJSONArray(FIELD_DLC);
        int len = arr.length();
        for (int i = 0; i < len; i++) {
            DlcInfo d = DlcInfo.fromJson(arr.getJSONObject(i));
            g.dlcs.add(d);
        }

        return g;
    }

    /**
     * Converts to a JsonObject.
     */
    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put(FIELD_NAME, this.name);
            obj.put(FIELD_DESC, this.description);
            obj.put(FIELD_PRICE, (double) this.price);
            obj.put(FIELD_DATE, df.format(this.date.getTime()));
            obj.put(FIELD_HOUR, hours);
            obj.put(FIELD_GOOD, rateGood);
            obj.put(FIELD_SOSO, rateSoso);
            obj.put(FIELD_BAD, rateBad);

            JSONArray arr = new JSONArray();
            for (DlcInfo d : this.dlcs) {
                arr.put(d.toJson());
            }
            obj.put(FIELD_DLC, arr);
            return obj;
        } catch (JSONException e) {
            return null;
        }
    }

    public void loadFrom(Game other){
        this.date = (Calendar) other.date.clone();
        this.name = other.name;
        this.description = other.description;
        this.price = other.price;
        this.hours = other.hours;
        this.dlcs = other.cloneDlcs();
        this.rateGood = other.rateGood;
        this.rateSoso = other.rateSoso;
        this.rateBad = other.rateBad;
    }
    
    public Game getClone(){
        Game g = new Game();
        g.date = (Calendar) this.date.clone();
        g.name = this.name;
        g.description = this.description;
        g.price = this.price;
        g.hours = this.hours;
        g.dlcs = this.cloneDlcs();
        g.rateGood = this.rateGood;
        g.rateSoso = this.rateSoso;
        g.rateBad = this.rateBad;

        return g;
    }

    public List<DlcInfo> cloneDlcs(){
        List<DlcInfo> l = new ArrayList<>();
        for (DlcInfo info : this.dlcs) {
            l.add(info.getClone());
        }

        return l;
    }

    @Override
    public Object clone(){
        return this.getClone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public List<DlcInfo> getDlcs() {
        return dlcs;
    }

    public int getDlcCount() {
        if (this.dlcs == null) {
            this.dlcs = new ArrayList<>();
            return 0;
        }

        return dlcs.size();
    }

    public void setDlcs(List<DlcInfo> dlcs) {
        this.dlcs = dlcs;
    }

    public void setHours(int hour) {
        this.hours += Math.abs(hour);
    }

    public int getHours() {
        return hours;
    }

    public void removeDlc(DlcInfo dlc) {
        if (this.dlcs.contains(dlc)) {
            this.dlcs.remove(dlc);
        }
    }

    public void addDlc(DlcInfo dlc) {
        this.dlcs.add(dlc);
    }


    public int getRateGood() {
        return rateGood;
    }

    public void voteGood() {
        rateGood += 1;
    }

    public void voteSoso() {
        rateSoso += 1;
    }

    public void voteBad() {
        rateBad += 1;
    }

    public int getRateCount() {
        return rateGood + rateSoso + rateBad;
    }

    public int getRateSoso() {
        return rateSoso;
    }

    public int getRateBad() {
        return rateBad;
    }
}
