package gg.my.gamemanager;

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

class Game implements Serializable {
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESC = "description";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_DLC = "dlcs";
    private static final String FIELD_HOUR = "hours";
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String name;
    private String description;
    private Float price;
    private Calendar date;
    private int hours;
    private List<DlcInfo> dlcs;

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
        try {
            g.date.setTime(df.parse(jobj.getString(FIELD_DATE)));
        } catch (ParseException e) {
            throw new JSONException("Invalid format for date");
        }
        JSONArray arr = jobj.getJSONArray(FIELD_DLC);
        int len = arr.length();
        for(int i=0;i<len;i++){
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
            obj.put(FIELD_HOUR,hours);

            JSONArray arr = new JSONArray();
            for (DlcInfo d : this.dlcs) {
                arr.put(d.toJson());
            }
            obj.put(FIELD_DLC, arr);
            return obj;
        }
        catch(JSONException e){
            return null;
        }
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

    public int getDlcCount(){
        if(this.dlcs==null) {
            this.dlcs = new ArrayList<>();
            return 0;
        }

        return dlcs.size();
    }

    public void setDlcs(List<DlcInfo> dlcs) {
        this.dlcs = dlcs;
    }

    public void setHours(int hour){
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
}
