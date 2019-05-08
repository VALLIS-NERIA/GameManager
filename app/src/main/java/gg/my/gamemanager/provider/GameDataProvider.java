package gg.my.gamemanager.provider;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gg.my.gamemanager.R;
import gg.my.gamemanager.model.DlcInfo;
import gg.my.gamemanager.model.Game;

import static android.content.Context.MODE_PRIVATE;

public class GameDataProvider {
    public static GameDataProvider getInstance(){
        return instance;
    }

    public static GameDataProvider tryCreateInstance(Context context){
        if(instance != null) return instance;
        instance = new GameDataProvider();
        instance.init(context);
        return instance;
    }

    private static GameDataProvider instance;

    private GameDataProvider(){
        instance = this;
    }

    public List<Game> games;

    public void save(Context context){
        writeGamesToJson(context);
    }

    private void init(Context context) {
        List<Game> list = getGamesFromJson(context);
        // list is null usually means the file does not exist.
        if (list == null) {
            // create a sample game list
            games = new ArrayList<>();
            games.add(getSampleGame(context));
            games.add(getSampleGame2(context));
            // create the json file
            File dir = context.getFilesDir();
            File file = new File(dir, "games.json");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // write game list to file
            writeGamesToJson(context);
        } else {
            games = list;
        }
    }

    /**
     * Try to get a list of {@link Game} from file "games.json".
     *
     * @return the game list. Returns null if file does not exist.
     */
    private List<Game> getGamesFromJson(Context context) {
        try {
            FileInputStream is = context.openFileInput("games.json");
            InputStreamReader sr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(sr);
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
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
    private void writeGamesToJson(Context context) {
        JSONArray arr = new JSONArray();
        for (Game g : games) {
            JSONObject obj = g.toJson();
            arr.put(obj);
        }
        try {
            OutputStream os = context.openFileOutput("games.json", MODE_PRIVATE);
            OutputStreamWriter sw = new OutputStreamWriter(os);
            sw.write(arr.toString());
            sw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // returns a sample game named "Layers of fear".
    private Game getSampleGame(Context context) {
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

    private Game getSampleGame2(Context context) {
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
