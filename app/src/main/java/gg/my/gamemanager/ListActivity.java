package gg.my.gamemanager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import gg.my.gamemanager.helpers.GameDataProvider;
import gg.my.gamemanager.models.DlcInfo;
import gg.my.gamemanager.models.Game;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;


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
 * {@link ListActivity#MSG_GAME_INDEX}: to set or get the index of passed list_item, in its original list
 * <p>
 * The value of {@link ListActivity#REQUEST_TYPE} should be one of following:
 * {@link ListActivity#TYPE_VIEW_GAME} call {@link GameDetailActivity} to view a game when clicking on a game list_item in the list
 * {@link ListActivity#TYPE_ADD_GAME} call {@link GameDetailActivity} to add a game when clicking the floating "add" button in game list mode
 * {@link ListActivity#TYPE_EDIT_DLC} call {@link DlcEditActivity} to edit a DLC when clicking on a DLC list_item
 * {@link ListActivity#TYPE_LIST_DLC} call {@link ListActivity} to list the DLC(s) of a game, when clicking on the dlc button in {@link GameDetailActivity}
 * {@link ListActivity#TYPE_ADD_DLC} call {@link DlcEditActivity} to add a DLC when clicking the floating "add" button in DLC list mode
 */
public class ListActivity extends AppCompatActivity {
    public static final String REQUEST_TYPE = "gg.my.gamemanager.type";
    public static final String MSG_ITEM = "gg.my.gamemanager.list_item";
    public static final String MSG_GAME_INDEX = "gg.my.gamemanager.gameindex";
    public static final String MSG_DLC_INDEX = "gg.my.gamemanager.dlcindex";

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
    private TextView listCount;
    private FloatingActionButton fabCancel;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabNew;

    // used in game list mode

    // used in DLC list mode

    /***
     * this is a deep copy of the DLCs of given game.
     */
    private List<DlcInfo> backupDlcs;
    /***
     * this is the real DLC list.
     */
    private List<DlcInfo> currentDlcs;
    /**
     * This is the real thing, DO NOT modify it.
     */
    private Game currentGame;
    private int currentGameIndex;
    /**
     * Whether we are showing game, or DLCs
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
        this.update();
    }


    private void initFieldsAndViews() {
        Intent intent = getIntent();
        String type = intent.getStringExtra(REQUEST_TYPE);

        if (type != null && type.equals(TYPE_LIST_DLC)) {
            currentGameIndex = intent.getIntExtra(MSG_GAME_INDEX, -1);
            currentGame = GameDataProvider.games.get(currentGameIndex);
            dlcMode = true;
            this.currentDlcs = currentGame.getDlcs();
            this.backupDlcs = currentGame.cloneDlcs();
        }
        // type is null means Game mode
        else {
            GameDataProvider.init(this);
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
        recyclerView = this.findViewById(R.id.listView1);
        LinearLayoutManager portraitLayoutManager = new LinearLayoutManager(this);
        portraitLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        GridLayoutManager landscapeLayoutManager = new GridLayoutManager(this, 2);
        switch (getResources().getConfiguration().orientation) {
            case ORIENTATION_LANDSCAPE:
                portraitLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(portraitLayoutManager);
                break;
            default:

                recyclerView.setLayoutManager(portraitLayoutManager);
        }
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

        // fabCancel is only available for dlc mode
        fabCancel = this.findViewById(R.id.fab_cancel);
        fabCancel.setVisibility(dlcMode ? View.VISIBLE : View.GONE);
        fabCancel.setOnClickListener(this::clickCancelDlcs);

        listCount = this.findViewById(R.id.list_countText);
        listCount.setText(String.format(getString(R.string.info_template_listCount), dlcMode ? this.currentDlcs.size() : GameDataProvider.games.size()));
    }

    /**
     * Updates views and saves data.
     */
    private void update() {
        RecyclerView.Adapter adapter;
        if (this.dlcMode) {
            adapter = ListAdapter.ForDlcs(this.currentDlcs, this::clickViewDlc);
        } else {
            adapter = new CardAdapter(this, GameDataProvider.games, this::clickViewGame);
        }
        recyclerView.setAdapter(adapter);
        fabSave.setImageResource(dlcDirty ? android.R.drawable.ic_menu_save : android.R.drawable.ic_menu_revert);
        listCount.setText(String.format(getString(R.string.info_template_listCount), dlcMode ? this.currentDlcs.size() : GameDataProvider.games.size()));
    }

    /**
     * When this activity calls another activity, this method will be invoked after the other activity returns.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.update();
        super.onActivityResult(requestCode, resultCode, data);
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
        GameDataProvider.games.add(new Game());
        // the index of the Game in gameList
        intent.putExtra(MSG_GAME_INDEX, GameDataProvider.games.size() - 1);
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
        intent.putExtra(MSG_GAME_INDEX, index);
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
        this.currentDlcs.add(new DlcInfo());
        intent.putExtra(MSG_DLC_INDEX, currentDlcs.size() - 1);
        intent.putExtra(MSG_GAME_INDEX, currentGameIndex);
        startActivityForResult(intent, CODE_ADD_DLC);
    }

    //when I click on some existing Dlc under DLc mode
    //calls DlcEditActivity with request code CODE_VIEW_DLC
    private void clickViewDlc(int index) {
        if (!dlcMode) {
            throw new AssertionError("Should be DLC mode!");
        }
        Intent intent = new Intent(ListActivity.this, DlcEditActivity.class);
        intent.putExtra(REQUEST_TYPE, TYPE_VIEW_DLC);
        intent.putExtra(MSG_DLC_INDEX, index);
        intent.putExtra(MSG_GAME_INDEX, currentGameIndex);
        startActivityForResult(intent, CODE_VIEW_DLC);
    }

    // when I click the "back/save" button under DLC mode
    // DLC mode means there is a parent activity
    private void clickSaveDlcs(View v) {
        Intent intent = new Intent();
        // save the shadow to the real thing
        if (dlcDirty) {
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    private void clickCancelDlcs(View v) {
        Intent intent = new Intent();
        currentGame.setDlcs(backupDlcs);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    /* for recycler view */

    //when I click Back button instead of clicking custom button.
    public void onBackPressed() {
        if (dlcMode && dlcDirty) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(getString(R.string.hint_title));
            ab.setMessage(getString(R.string.hint_cancelConfirm));
            ab.setPositiveButton(getString(R.string.button_yes), (di, num) -> {
                di.dismiss();
                this.clickSaveDlcs(null);
            });

            ab.setNegativeButton(getString(R.string.button_no), (di, num) -> {
                di.dismiss();
            });

            ab.show();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

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
