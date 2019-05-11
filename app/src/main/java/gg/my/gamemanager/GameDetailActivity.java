package gg.my.gamemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import gg.my.gamemanager.control.DatePickButton;
import gg.my.gamemanager.control.Voter;
import gg.my.gamemanager.helpers.GameDataProvider;
import gg.my.gamemanager.helpers.RatingInfo;
import gg.my.gamemanager.models.Game;

import static gg.my.gamemanager.ListActivity.CODE_LIST_DLC;
import static gg.my.gamemanager.ListActivity.MSG_GAME_INDEX;
import static gg.my.gamemanager.ListActivity.REQUEST_TYPE;
import static gg.my.gamemanager.ListActivity.RESULT_DELETED;
import static gg.my.gamemanager.ListActivity.TYPE_ADD_GAME;
import static gg.my.gamemanager.ListActivity.TYPE_LIST_DLC;

/**
 * This activity is invoked by {@link ListActivity} to view and edit some game.
 * This activity also invokes {@link ListActivity} to view the DLCs of current game.
 */
public class GameDetailActivity extends AppCompatActivity {

    private EditText nameEdit;
    private EditText priceEdit;
    private DatePickButton dateButton;
    private Button dlcButton;
    private EditText descEdit;
    private TextView screenHour;
    private EditText buttonHour;
    private Voter voter;

    private FloatingActionButton buttonEdit;
    private FloatingActionButton buttonSave;
    private FloatingActionButton buttonCancel;
    private FloatingActionButton buttonDel;

    /**
     * this is a deep copy of the game passed in.
     */
    private Game backupGame;
    private Game currentGame;
    private Boolean isNewGame;
    private Boolean editMode;
    private Boolean dlcDirty;
    private Calendar selectedDate;

    private int gameIndex = -1;
    private Locale loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initFields();
        initViews();
    }

    private void initFields() {
        Intent intent = getIntent();
        String type = intent.getStringExtra(REQUEST_TYPE);
        if (type == null || (!type.equals(TYPE_ADD_GAME) && !type.equals(ListActivity.TYPE_VIEW_GAME))) {
            throw new IllegalArgumentException("REQUEST_TYPE should be TYPE_ADD_GAME or TYPE_VIEW_GAME");
        }
        gameIndex = intent.getIntExtra(MSG_GAME_INDEX, -1);
        currentGame = GameDataProvider.games.get(gameIndex);
        backupGame = currentGame.getClone();

        nameEdit = findViewById(R.id.detail_name_edit);
        priceEdit = findViewById(R.id.detail_price_edit);
        dateButton = findViewById(R.id.detail_date_button);
        descEdit = findViewById(R.id.detail_desc_edit);
        dlcButton = findViewById(R.id.detail_dlc_button);
        buttonEdit = findViewById(R.id.detail_fabEdit);
        buttonSave = findViewById(R.id.detail_fabSave);
        buttonCancel = findViewById(R.id.detail_fabCancel);
        buttonDel = findViewById(R.id.detail_fabDel);
        buttonHour = findViewById(R.id.detail_gamehour_edit);
        screenHour = findViewById(R.id.detial_screenGamehours);
        voter = findViewById(R.id.detail_voter);

        voter.setData(RatingInfo.CreateFromGame(this.currentGame, this));
        voter.afterSubmitCallback = ()->{
            this.voter.updateData(new int[]{currentGame.getRateGood(),currentGame.getRateSoso(), currentGame.getRateBad()});
        };
        dlcButton.setOnClickListener(this::clickDlc);
        isNewGame = type.equals(TYPE_ADD_GAME);

        editMode = isNewGame;
        dlcDirty = false;
    }

    private void initViews() {
        voter.setSubmitAvailable(editMode);
        this.voter.updateData(new int[]{currentGame.getRateGood(),currentGame.getRateSoso(), currentGame.getRateBad()});

        loc = Locale.getDefault();
        getSupportActionBar().setTitle(String.format(getString(R.string.title_template_gameDetail), currentGame.getName()));
        nameEdit.setText(currentGame.getName());
        //当名字改变时，显示效果跟随其改变
        //Name will be updated when it has been changed.
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getSupportActionBar().setTitle(String.format(getString(R.string.title_template_gameDetail), charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        screenHour.setText(getString(R.string.hours) + " " + String.format(loc, "%d", currentGame.getHours()) + " " + getString(R.string.Hours));
        priceEdit.setText(String.format(loc, "%.2f", currentGame.getPrice()));
        dateButton.setDate(currentGame.getDate());
        dateButton.setAfterPickCallback(d -> this.selectedDate = d);
        descEdit.setText(currentGame.getDescription());
        dlcButton.setText(String.format(loc, "%d", currentGame.getDlcs().size()));
        if (editMode) {
            buttonHour.setEnabled(true);
            nameEdit.setEnabled(true);
            priceEdit.setEnabled(true);
            dateButton.setEnabled(true);
            descEdit.setEnabled(true);
            dlcButton.setEnabled(false); // this is FALSE
            buttonEdit.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.VISIBLE);
            buttonCancel.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            buttonCancel.setOnClickListener(this::clickCancel);
            buttonSave.setVisibility(View.VISIBLE);
            buttonSave.setImageResource(android.R.drawable.ic_menu_save);
            if (!isNewGame) buttonDel.setVisibility(View.VISIBLE);
            buttonSave.setOnClickListener(this::clickSave);
            buttonCancel.setOnClickListener(this::clickCancel);
            buttonDel.setOnClickListener(this::clickDelete);
        } else {
            buttonHour.setEnabled(false);
            nameEdit.setEnabled(false);
            priceEdit.setEnabled(false);
            dateButton.setEnabled(false);
            descEdit.setEnabled(false);
            dlcButton.setEnabled(true); // this is TRUE
            buttonEdit.setVisibility(View.VISIBLE);
            buttonEdit.setImageResource(android.R.drawable.ic_menu_edit);
            buttonSave.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.VISIBLE);
            buttonCancel.setImageResource(android.R.drawable.ic_menu_revert);
            buttonCancel.setOnClickListener(this::clickCancel);
            buttonDel.setVisibility(View.GONE);
            buttonEdit.setOnClickListener(this::clickEdit);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // after I click the dlc button to invoke ListActivity to show DLCs
            case CODE_LIST_DLC:
                // DLC list is modified
                if (resultCode == RESULT_OK) {
                    // update views
                    dlcButton.setText(String.format(loc, "%d", currentGame.getDlcs().size()));
                    dlcDirty = true;
                    buttonSave.setVisibility(View.VISIBLE);
                    buttonSave.setOnClickListener(this::clickSave);
                    buttonSave.setImageResource(android.R.drawable.ic_menu_save);
                    buttonCancel.setVisibility(View.GONE);
                }
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // when I click on the DLC button, it goes to ListActivity
    private void clickDlc(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra(REQUEST_TYPE, TYPE_LIST_DLC);
        intent.putExtra(MSG_GAME_INDEX, this.gameIndex);
        startActivityForResult(intent, CODE_LIST_DLC);
    }

    // when I click on the edit button
    private void clickEdit(View view) {
        if (view.getId() != R.id.detail_fabEdit || editMode) throw new AssertionError();

        this.editMode = true;
        this.initViews();
    }

    // when I click on the save button
    private void clickSave(View view) {
        // write to currentGame
        currentGame.setName(nameEdit.getText().toString());
        currentGame.setDescription(descEdit.getText().toString());
        currentGame.setPrice(Float.parseFloat(priceEdit.getText().toString()));
        try {
            currentGame.setHours(Integer.parseInt(buttonHour.getText().toString()));
        } catch (NumberFormatException ignored) {
        }
        if (selectedDate != null) {
            currentGame.setDate(selectedDate);
        }

        GameDataProvider.save();
        // return to ListActivity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //when I click Back button instead of clicking custom button.
    public void onBackPressed() {
        if (dlcDirty || editMode) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(getString(R.string.hint_title));
            ab.setMessage(getString(R.string.hint_cancelConfirm));
            ab.setPositiveButton(getString(R.string.button_yes), (di, num) -> {
                di.dismiss();
                this.clickSave(null);
            });

            ab.setNegativeButton(getString(R.string.button_no), (di, num) -> {
                di.dismiss();
            });

            ab.show();
        } else {
            this.clickCancel(null);
        }
    }

    // when I click on the cancel button
    private void clickCancel(View view) {
        // if I'm editing a new game, cancel means delete
        if (isNewGame) {
            Intent intent = new Intent();
            GameDataProvider.games.remove(gameIndex);
            GameDataProvider.save();
            setResult(RESULT_DELETED, intent);
            finish();
        } else if (editMode) {
            editMode = false;
            // revert
            currentGame.loadFrom(backupGame);
            this.initViews();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }

    }

    // when I click on the click button
    private void clickDelete(View view) {
        if (view.getId() != R.id.detail_fabDel || !editMode) throw new AssertionError();

        // create delete confirmation dialog.
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(getString(R.string.hint_title));
        ab.setMessage(getString(R.string.hint_deleteConfirm));
        ab.setPositiveButton(getString(R.string.button_yes), (di, num) -> {
            di.dismiss();
            Intent intent = new Intent();
            GameDataProvider.games.remove(gameIndex);
            setResult(RESULT_DELETED, intent);
            finish();
        });

        ab.setNegativeButton(getString(R.string.button_no), (di, num) -> {
            di.dismiss();
        });

        ab.show();
    }
}
