package gg.my.gamemanager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * The DLC model class.
 */

class DlcInfo implements Serializable {
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESC = "description";
    private String name;
    private String description;

    public DlcInfo(){
        this.name = "";
        this.description = "";
    }

    /**
     * Creates a {@link DlcInfo} instance from JsonObject.
     */
    public static DlcInfo fromJson(JSONObject obj) throws JSONException {
        DlcInfo d = new DlcInfo();
        d.name = obj.getString(FIELD_NAME);
        d.description = obj.getString(FIELD_DESC);
        return d;
    }

    /**
     * Converts to a JsonObject.
     */
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put(FIELD_NAME,this.name);
        obj.put(FIELD_DESC,this.description);
        return obj;
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
}
