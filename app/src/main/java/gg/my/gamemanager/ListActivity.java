package gg.my.gamemanager;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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


// 这是一个复用的Activity，用来显示游戏列表或者DLC列表。app启动时默认进入ListActivity，并且显示游戏列表。

/**
 * This activity is used for both game list and DLC list.
 *
 * <p>At the app starts, the game list is the default view, showing a sample {@link Game}.
 * Click the floating button to add a game, or click an existing game to view.
 * <p>
 * The {@link GameDetailActivity} can invoke this activity to display the DLCs of a game.
 * Click the floating button to add a DLC, or click on an existing DLC to edit it.</p>
 * <p>
 * For cross-activity interactions there are 4 fields.
 * {@link ListActivity#REQUEST_TYPE}: (REQUIRED) determines the action type. {@see {@link ListActivity#TYPE_VIEW_GAME}}
 * {@link ListActivity#MSG_ITEM}: to set or get the passed {@link Game} or {@link DlcInfo}
 * {@link ListActivity#MSG_INDEX}: to set or get the index of passed item, in its original list({@link ListActivity#games} or {@link Game#dlcs})
 * {@link ListActivity#MSG_RETURN_DATA}: to set or get the {@link Game} or {@link DlcInfo} return by child activity
 * <p>
 * The value of {@link ListActivity#REQUEST_TYPE} should be one of following:
 * {@link ListActivity#TYPE_VIEW_GAME} call {@link GameDetailActivity} to view a game when clicking on a game item in the list
 * {@link ListActivity#TYPE_ADD_GAME} call {@link GameDetailActivity} to add a game when clicking the floating "add" button in game list mode
 * {@link ListActivity#TYPE_EDIT_DLC} call {@link DlcEditActivity} to edit a DLC when clicking on a DLC item
 * {@link ListActivity#TYPE_LIST_DLC} call {@link ListActivity} to list the DLC(s) of a game, when clicking on the dlc button in {@link GameDetailActivity}
 * {@link ListActivity#TYPE_ADD_DLC} call {@link DlcEditActivity} to add a DLC when clicking the floating "add" button in DLC list mode
 */
public class ListActivity extends AppCompatActivity {
    public static final String REQUEST_TYPE = "gg.my.gamemanager.type";
    public static final String MSG_ITEM = "gg.my.gamemanager.item";
    public static final String MSG_INDEX = "gg.my.gamemanager.index";
    public static final String MSG_RETURN_DATA = "gg.my.gamemanager.return";

    public static final String TYPE_VIEW_GAME = "gg.my.gamemanager.viewG";
    public static final String TYPE_ADD_GAME = "gg.my.gamemanager.addG";
    public static final String TYPE_VIEW_DLC = "gg.my.gamemanager.viewD";
    public static final String TYPE_EDIT_DLC = "gg.my.gamemanager.editD";
    public static final String TYPE_LIST_DLC = "gg.my.gamemanager.listD";
    public static final String TYPE_ADD_DLC = "gg.my.gamemanager.addD";


    public static final int CODE_VIEW_GAME = 0x2857;
    public static final int CODE_ADD_GAME = 0x6657;
    public static final int CODE_LIST_DLC = 0x1024;
    public static final int CODE_VIEW_DLC = 0x1234;
    public static final int CODE_EDIT_DLC = 0x6234;
    public static final int CODE_ADD_DLC = 0x4399;

    /**
     * an extension of result code beyond {@link android.app.Activity#RESULT_OK} and {@link android.app.Activity#RESULT_CANCELED}
     */
    public static final int RESULT_DELETED = 0xDEAD;

    // some views
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView listCount;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabNew;

    // used in game list mode
    private List<Game> games;

    // used in DLC list mode
    private List<DlcInfo> dlcs;
    private Game currentGame;
    /**
     * Whether we are showing games, or DLCs
     */
    private boolean dlcMode;
    /**
     * The {@link ListActivity#dlcDirty} field is true if any DLC is modified but has not yet saved.
     * When {@link ListActivity#dlcDirty} is true, the "back" floating button becomes "save".
     */
    private boolean dlcDirty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_list);

        this.initFieldsAndViews();
        this.updateAndSave();
    }

    /**
     * Try to get a list of {@link Game} from file "games.json".
     *
     * @return the game list. Returns null if file does not exist.
     */
    private List<Game> getGamesFromJson() {
        try {
            FileInputStream is = this.openFileInput("games.json");
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
    private void writeGamesToJson() {
        JSONArray arr = new JSONArray();
        for (Game g : this.games) {
            JSONObject obj = g.toJson();
            arr.put(obj);
        }
        try {
            OutputStream os = this.openFileOutput("games.json", MODE_PRIVATE);
            OutputStreamWriter sw = new OutputStreamWriter(os);
            sw.write(arr.toString());
            sw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void initFieldsAndViews() {
        Intent intent = getIntent();
        String type = intent.getStringExtra(REQUEST_TYPE);

        if (type != null && type.equals(TYPE_LIST_DLC)) {
            currentGame = (Game) intent.getSerializableExtra(MSG_ITEM);
            dlcMode = true;
            this.dlcs = currentGame.getDlcs();
        }
        // type is null means Game mode
        else {
            List<Game> list = getGamesFromJson();
            // list is null usually means the file does not exist.
            if (list == null) {
                // create a sample game list
                this.games = new ArrayList<>();
                this.games.add(getSampleGame());
                // create the json file
                File dir = this.getFilesDir();
                File file = new File(dir, "games.json");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // write game list to file
                this.writeGamesToJson();
            } else {
                this.games = list;
            }
            dlcMode = false;
        }

        dlcDirty = false;

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        String title;
        if (dlcMode)
            title = String.format(getString(R.string.title_template_listDlc), currentGame.getName());
        else title = getString(R.string.title_listGame);
        getSupportActionBar().setTitle(title);

        // init recycler view
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView = this.findViewById(R.id.listView1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDivider(this, 2, getColor(R.color.colorPrimary)));

        // the button has different click behavior for game mode & DLC mode
        fabNew = this.findViewById(R.id.fab_new);
        fabNew.setOnClickListener(dlcMode ? this::clickAddDlc : this::clickAddGame);

        // fabSave is only available for dlc mode
        fabSave = this.findViewById(R.id.fab_save);
        fabSave.setVisibility(dlcMode ? View.VISIBLE : View.GONE);
        fabSave.setImageResource(dlcDirty ? android.R.drawable.ic_menu_save : android.R.drawable.ic_menu_revert);
        fabSave.setOnClickListener(this::clickSaveDlcs);

        listCount = this.findViewById(R.id.list_countText);
        listCount.setText(String.format(getString(R.string.info_template_listCount), dlcMode ? this.dlcs.size() : this.games.size()));
    }

    /**
     * Updates views and saves data.
     */
    private void updateAndSave() {
        MyAdapter adapter;
        if (this.dlcMode) {
            adapter = MyAdapter.ForDlcs(this.dlcs, this::clickViewDlc);
        } else {
            adapter = MyAdapter.ForGames(this.games, this::clickViewGame);
        }
        recyclerView.setAdapter(adapter);
        if (!dlcMode) writeGamesToJson();
        fabSave.setImageResource(dlcDirty ? android.R.drawable.ic_menu_save : android.R.drawable.ic_menu_revert);
    }

    /**
     * When this activity calls another activity, this method will be invoked after the other activity returns.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // [GAMEmode] after I tap the "add" button to create a new Game
            case CODE_ADD_GAME:
                if (resultCode == RESULT_OK) {
                    // get the returned Game
                    Game returnedGame = (Game) data.getSerializableExtra(MSG_RETURN_DATA);
                    this.games.add(returnedGame);
                    this.updateAndSave();
                }
                break;
            // [GAMEmode] after I tap on an existing Game to view/modify it.
            case CODE_VIEW_GAME:
                // the game is modified.
                if (resultCode == RESULT_OK) {
                    Game returnedGame = (Game) data.getSerializableExtra(MSG_RETURN_DATA);
                    int index = data.getIntExtra(MSG_INDEX, -1);
                    if (returnedGame != null && index > -1) {
                        this.games.set(index, returnedGame);
                        this.updateAndSave();
                    }
                }
                // the game is deleted
                else if (resultCode == RESULT_DELETED) {
                    int index = data.getIntExtra(MSG_INDEX, -1);
                    if (index > -1) {
                        this.games.remove(index);
                        this.updateAndSave();
                    }
                }
                break;
            // [DLCmode] after I tap on an existing DLC to edit it
            case CODE_VIEW_DLC:
                // the DLC is modified
                if (resultCode == RESULT_OK) {
                    DlcInfo returnedDlc = (DlcInfo) data.getSerializableExtra(MSG_RETURN_DATA);
                    int index = data.getIntExtra(MSG_INDEX, -1);
                    if (returnedDlc != null && index > -1) {
                        this.dlcs.set(index, returnedDlc);
                        dlcDirty = true;
                        this.updateAndSave();
                    }
                }
                // the DLC is deleted
                else if (resultCode == RESULT_DELETED) {
                    int index = data.getIntExtra(MSG_INDEX, -1);
                    if (index > -1) {
                        this.dlcs.remove(index);
                        dlcDirty = true;
                        this.updateAndSave();
                    }
                }
                break;
            // [DLCmode] after I tap the "add" button to create a new DLC
            case CODE_ADD_DLC:
                if (resultCode == RESULT_OK) {
                    DlcInfo returnedDlc = (DlcInfo) data.getSerializableExtra(MSG_RETURN_DATA);
                    if (returnedDlc != null) {
                        this.dlcs.add(returnedDlc);
                        dlcDirty = true;
                        this.updateAndSave();
                    }
                }
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // returns a sample game named "Layers of fear".
    private Game getSampleGame() {
        Game sampleGame = new Game();
        sampleGame.setName(getString(R.string.default_gameName));
        sampleGame.setDescription(getString(R.string.default_gameDesc));
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2019);
        date.set(Calendar.MONTH, 1);
        date.set(Calendar.DAY_OF_MONTH, 19);
        sampleGame.setDate(date);

        DlcInfo sampleDlc = new DlcInfo();
        sampleDlc.setName(getString(R.string.default_dlcName));
        sampleDlc.setDescription(getString(R.string.default_dlcDesc));
        sampleGame.addDlc(sampleDlc);
        return sampleGame;
    }

    // when I click the "add" button under game mode,
    // calls GameDetailActivity with request code CODE_ADD_GAME
    private void clickAddGame(View view) {
        if (dlcMode) {
            throw new AssertionError("should NOT be DLC mode");
        }
        Intent intent = new Intent(this, GameDetailActivity.class);
        // I don't know how to read request code in child activity, so had to put a string indicating the request type
        intent.putExtra(REQUEST_TYPE, TYPE_ADD_GAME);
        // pass the Game instance. That's why Game and DlcInfo class implements Serializable interface.
        intent.putExtra(MSG_ITEM, new Game());
        // the index of the Game in gameList
        intent.putExtra(MSG_INDEX, -1);
        // use request code so that onActivityResult can determine the child activity
        startActivityForResult(intent, CODE_ADD_GAME);
    }

    // when I click on some existing game under game mode
    // calls GameDetailActivity with request code CODE_VIEW_GAME
    private void clickViewGame(int index) {
        if (dlcMode) {
            throw new AssertionError("should NOT be DLC mode");
        }
        Intent intent = new Intent(ListActivity.this, GameDetailActivity.class);
        intent.putExtra(REQUEST_TYPE, TYPE_VIEW_GAME);
        intent.putExtra(MSG_ITEM, this.games.get(index));
        intent.putExtra(MSG_INDEX, index);
        startActivityForResult(intent, CODE_VIEW_GAME);
    }

    // when I click the "add" button under DLC mode
    // calls DlcEditActivity with request code CODE_ADD_DLC
    private void clickAddDlc(View view) {
        if (!dlcMode) {
            throw new AssertionError("should be DLC mode");
        }
        Intent intent = new Intent(this, DlcEditActivity.class);
        intent.putExtra(REQUEST_TYPE, TYPE_ADD_DLC);
        intent.putExtra(MSG_ITEM, new DlcInfo());
        intent.putExtra(MSG_INDEX, -1);
        startActivityForResult(intent, CODE_ADD_DLC);
    }


    //when I click on some existing Dlc under DLc mode
    //calls DlcEditActivity with request code CODE_VIEW_DLC
    private void clickViewDlc(int index){
        if(!dlcMode){
            throw new AssertionError("Should be DLC mode!");
        }
        Intent intent = new Intent(ListActivity.this,DlcEditActivity.class);
        intent.putExtra(REQUEST_TYPE,TYPE_VIEW_DLC);
        intent.putExtra(MSG_ITEM,this.dlcs.get(index));
        intent.putExtra(MSG_INDEX,index);
        startActivityForResult(intent,CODE_VIEW_DLC);
    }

    // when I click on some existing DLC under DLC mode
    // calls DlcEditActivity with request code CODE_EDIT_DLC
    private void clickEditDlc(int index) {
        if (!dlcMode) {
            throw new AssertionError("should be DLC mode");
        }
        Intent intent = new Intent(this, DlcEditActivity.class);
        intent.putExtra(REQUEST_TYPE, TYPE_EDIT_DLC);
        intent.putExtra(MSG_ITEM, this.dlcs.get(index));
        intent.putExtra(MSG_INDEX, index);
        startActivityForResult(intent, CODE_EDIT_DLC);
    }

    // when I click the "back/save" button under DLC mode
    // DLC mode means there is a parent activity
    private void clickSaveDlcs(View v) {
        Intent intent = new Intent();
        // if the DLC list is dirty, we return the current game (along with its DLCs)
        if (dlcDirty) {
            intent.putExtra(MSG_RETURN_DATA, currentGame);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }
    /* for recycler view */



    // a simple divider in recycler view.
    private class MyDivider extends RecyclerView.ItemDecoration {
        private Paint paint;
        private Drawable divider;
        private int height;

        public MyDivider(Context context, int height, int dividerColor) {
            TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            this.divider = a.getDrawable(0);
            a.recycle();
            this.height = height;
            this.paint = new Paint(1);
            this.paint.setColor(dividerColor);
            this.paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, this.height);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            c.save();

            int left = 0;
            int right = parent.getWidth();

            int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; ++i) {
                View child = parent.getChildAt(i);
                int top = child.getBottom();
                int bottom = top + this.height;
                this.divider.setBounds(left, top, right, bottom);
                this.divider.draw(c);

                c.drawRect((float) left, (float) top, (float) right, (float) bottom, this.paint);

            }
            c.restore();
        }
    }
}
