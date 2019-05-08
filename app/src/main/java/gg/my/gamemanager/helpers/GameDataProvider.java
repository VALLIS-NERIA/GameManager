package gg.my.gamemanager.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gg.my.gamemanager.R;
import gg.my.gamemanager.models.DlcInfo;
import gg.my.gamemanager.models.Game;

import static android.content.Context.MODE_PRIVATE;

public class GameDataProvider {
    private static File dir;
    private static File jsonFile;
    public static List<Game> games;

    private GameDataProvider() {
    }

    public static void save() {
        writeGamesToJson();
    }

    public static void init(Context context) {
        dir = context.getFilesDir();
        jsonFile = new File(dir, "games.json");
        if (jsonFile.exists()) {
            games = getGamesFromJson();
        } else {
            // create a sample game list
            games = new ArrayList<>();
            games.add(getSampleGame(context));
            games.add(getSampleGame2(context));
            try {
                jsonFile.createNewFile();
                writeGamesToJson();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Try to get a list of {@link Game} from file "games.json".
     *
     * @return the game list. Returns null if file does not exist.
     */
    private static List<Game> getGamesFromJson() {
        try {
            FileInputStream is = new FileInputStream(jsonFile);
            InputStreamReader sr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(sr);
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                is.close();
                JSONArray arr = new JSONArray(sb.toString());
                int len = arr.length();
                List<Game> list = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    list.add(Game.fromJsonObject(arr.getJSONObject(i)));
                }
                return list;
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    reader.close();
                    sr.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Writes the game list to json file.
     */
    private static void writeGamesToJson() {
        JSONArray arr = new JSONArray();
        for (Game g : games) {
            JSONObject obj = g.toJson();
            arr.put(obj);
        }
        try {
            OutputStream os = new FileOutputStream(jsonFile);
            OutputStreamWriter sw = new OutputStreamWriter(os);
            sw.write(arr.toString());
            sw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // returns a sample game named "Layers of fear".
    private static Game getSampleGame(Context context) {
        Game sampleGame = new Game();
        sampleGame.setName(context.getString(R.string.default_gameName));
        sampleGame.setDescription(context.getString(R.string.default_gameDesc));
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2019);
        date.set(Calendar.MONTH, 1);
        date.set(Calendar.DAY_OF_MONTH, 19);
        sampleGame.setDate(date);

        DlcInfo sampleDlc = new DlcInfo();
        sampleDlc.setName(context.getString(R.string.default_dlcName));
        sampleDlc.setDescription(context.getString(R.string.default_dlcDesc));
        sampleGame.voteGood();
        sampleGame.voteGood();
        sampleGame.voteSoso();
        sampleGame.voteBad();
        sampleGame.addDlc(sampleDlc);
        return sampleGame;
    }

    private static Game getSampleGame2(Context context) {
        Game sampleGame = new Game();
        sampleGame.setName(context.getString(R.string.default2_gameName));
        sampleGame.setDescription(context.getString(R.string.default2_gameDesc));
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2014);
        date.set(Calendar.MONTH, 1);
        date.set(Calendar.DAY_OF_MONTH, 19);
        sampleGame.setDate(date);
        sampleGame.voteGood();
        sampleGame.voteSoso();
        sampleGame.voteSoso();
        sampleGame.voteBad();
        return sampleGame;
    }
}
