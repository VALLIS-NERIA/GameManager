package gg.my.gamemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;

import static gg.my.gamemanager.ListActivity.CODE_LIST_DLC;
import static gg.my.gamemanager.ListActivity.MSG_INDEX;
import static gg.my.gamemanager.ListActivity.MSG_ITEM;
import static gg.my.gamemanager.ListActivity.MSG_RETURN_DATA;
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
    private Button dateButton;
    private Button dlcButton;
    private EditText descEdit;

    private FloatingActionButton buttonEdit;
    private FloatingActionButton buttonSave;
    private FloatingActionButton buttonCancel;
    private FloatingActionButton buttonDel;

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
        currentGame = (Game) intent.getSerializableExtra(MSG_ITEM);
        gameIndex = intent.getIntExtra(MSG_INDEX, -1);

        nameEdit = findViewById(R.id.detail_name_edit);
        priceEdit = findViewById(R.id.detail_price_edit);
        dateButton = findViewById(R.id.detail_date_button);
        descEdit = findViewById(R.id.detail_desc_edit);
        dlcButton = findViewById(R.id.detail_dlc_button);
        buttonEdit = findViewById(R.id.detail_fabEdit);
        buttonSave = findViewById(R.id.detail_fabSave);
        buttonCancel = findViewById(R.id.detail_fabCancel);
        buttonDel = findViewById(R.id.detail_fabDel);
        dateButton.setOnClickListener(this::clickDate);
        dlcButton.setOnClickListener(this::clickDlc);
        isNewGame = type.equals(TYPE_ADD_GAME);
        editMode = isNewGame;
        dlcDirty = false;
    }

    private void initViews() {
        loc = Locale.getDefault();
        getSupportActionBar().setTitle(String.format(getString(R.string.title_template_gameDetail), currentGame.getName()));
        nameEdit.setText(currentGame.getName());
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
        priceEdit.setText(String.format(loc, "%.2f", currentGame.getPrice()));
        dateButton.setText(formatDate(currentGame.getDate()));
        descEdit.setText(currentGame.getDescription());
        dlcButton.setText(String.format(loc, "%d", currentGame.getDlcs().size()));
        if (editMode) {
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
            if (!isNewGame) buttonDel.setVisibility(View.VISIBLE);
            buttonSave.setOnClickListener(this::clickSave);
            buttonCancel.setOnClickListener(this::clickCancel);
            buttonDel.setOnClickListener(this::clickDelete);
        } else {
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
                    Game returnedGame = (Game) data.getSerializableExtra(MSG_RETURN_DATA);
                    this.currentGame.setDlcs(returnedGame.getDlcs());
                    // update views
                    dlcButton.setText(String.format(loc, "%d", currentGame.getDlcs().size()));
                    dlcDirty = true;
                    buttonSave.setVisibility(View.VISIBLE);
                    buttonSave.setOnClickListener(this::clickSave);
                    buttonCancel.setVisibility(View.GONE);
                }
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String formatDate(Calendar date) {
        return String.format("%d-%d-%d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }

    // when I click on the date button, it shows a date picker dialog
    private void clickDate(View view) {
        if (view.getId() != R.id.detail_date_button) {
            return;
        }
        Calendar current = currentGame.getDate();
        DatePickerDialog datePicker = new DatePickerDialog(
                GameDetailActivity.this,
                this::pickedDate, // called after picking a date
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH) + 1,
                current.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    // after I pick a date in the date picker dialog
    private void pickedDate(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
        Calendar picked = Calendar.getInstance();
        picked.set(Calendar.YEAR, year);
        picked.set(Calendar.MONTH, monthOfYear);
        picked.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // update views
        selectedDate = picked;
        dateButton.setText(formatDate(selectedDate));
    }

    // when I click on the DLC button, it goes to ListActivity
    private void clickDlc(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra(REQUEST_TYPE, TYPE_LIST_DLC);
        intent.putExtra(MSG_ITEM, this.currentGame);
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
        if (view.getId() != R.id.detail_fabSave || (!editMode && !dlcDirty))
            throw new AssertionError();

        // write to currentGame
        currentGame.setName(nameEdit.getText().toString());
        currentGame.setDescription(descEdit.getText().toString());
        currentGame.setPrice(Float.parseFloat(priceEdit.getText().toString()));
        if (selectedDate != null) {
            currentGame.setDate(selectedDate);
        }

        // return to ListActivity
        Intent intent = new Intent();
        intent.putExtra(MSG_RETURN_DATA, currentGame);
        intent.putExtra(MSG_INDEX, gameIndex);
        setResult(RESULT_OK, intent);
        finish();
    }

    // when I click on the cancel button
    private void clickCancel(View view) {
        if (view.getId() != R.id.detail_fabCancel ) throw new AssertionError();

        // if I'm editing a new game, just return
        if (isNewGame || !editMode) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        // otherwise back to view mode
        else {
            editMode = false;
            this.initViews();
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
            intent.putExtra(MSG_INDEX, gameIndex);
            setResult(RESULT_DELETED, intent);
            finish();
        });

        ab.setNegativeButton(getString(R.string.button_no), (di, num) -> {
            di.dismiss();
        });

        ab.show();
    }
}
