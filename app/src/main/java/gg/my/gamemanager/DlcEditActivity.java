package gg.my.gamemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import static gg.my.gamemanager.ListActivity.MSG_INDEX;
import static gg.my.gamemanager.ListActivity.MSG_ITEM;
import static gg.my.gamemanager.ListActivity.MSG_RETURN_DATA;
import static gg.my.gamemanager.ListActivity.REQUEST_TYPE;
import static gg.my.gamemanager.ListActivity.RESULT_DELETED;
import static gg.my.gamemanager.ListActivity.TYPE_ADD_DLC;
import static gg.my.gamemanager.ListActivity.TYPE_EDIT_DLC;
import static gg.my.gamemanager.ListActivity.TYPE_VIEW_DLC;


/**
 * This activity is invoked by {@link ListActivity}(DLC mode) to edit a DLC.
 */
public class DlcEditActivity extends AppCompatActivity {
    private EditText nameEdit;
    private EditText descEdit;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabCancel;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabEdit;

    private boolean dlcEditMode;

    private DlcInfo info;
    private int infoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlc_edit);
        initFields();
        initview();
    }

    private void initFields() {
        Intent intent = getIntent();
        String type = intent.getStringExtra(REQUEST_TYPE);
        if (type == null || (!type.equals(TYPE_VIEW_DLC) && !type.equals(TYPE_ADD_DLC))) {
            throw new IllegalArgumentException("REQUEST_TYPE should be TYPE_EDIT_DLC or TYPE_ADD_DLC");
        }

        info = (DlcInfo) intent.getSerializableExtra(MSG_ITEM);
        infoIndex = intent.getIntExtra(MSG_INDEX, -1);
        dlcEditMode = false;
        nameEdit = findViewById(R.id.dlc_name_edit);
        descEdit = findViewById(R.id.dlc_desc_edit);
        fabSave = findViewById(R.id.dlc_fabSave);
        fabCancel = findViewById(R.id.dlc_fabCancel);
        fabDelete = findViewById(R.id.dlc_fabDel);
        fabEdit = findViewById(R.id.detail_fabEdit2);
        fabEdit.setOnClickListener(this::clickEdit);
        //fabEdit.setImageResource(android.R.drawable.ic_menu_edit);
        getSupportActionBar().setTitle(String.format(getString(R.string.title_template_dlcDetail), info.getName()));

    }

    private void initview(){
        // add a listener so that the title changes as the input text changes.
        nameEdit.setText(info.getName());
        descEdit.setText(info.getDescription());
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getSupportActionBar().setTitle(String.format(getString(R.string.title_template_dlcDetail), charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(dlcEditMode) {
            fabEdit.setEnabled(false);
            fabEdit.setVisibility(View.INVISIBLE);
            fabEdit.setVisibility(View.VISIBLE);
            fabCancel.setVisibility(View.VISIBLE);
            fabSave.setVisibility(View.VISIBLE);
            fabDelete.setVisibility(View.VISIBLE);
            nameEdit.setText(info.getName());
            descEdit.setText(info.getDescription());
            nameEdit.setEnabled(true);
            descEdit.setEnabled(true);
            fabCancel.setEnabled(true);
            fabDelete.setEnabled(true);
            fabSave.setEnabled(true);
            fabSave.setOnClickListener(this::clickSave);
            fabCancel.setOnClickListener(this::clickCancel);
            fabDelete.setOnClickListener(this::clickDelete);
        } else {
            nameEdit.setEnabled(false);
            descEdit.setEnabled(false);
            fabCancel.setEnabled(false);
            fabDelete.setEnabled(false);
            fabSave.setEnabled(false);
            fabCancel.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
            fabSave.setVisibility(View.GONE);
            fabSave.setOnClickListener(this::clickSave);
            fabCancel.setOnClickListener(this::clickCancel);
            fabDelete.setOnClickListener(this::clickDelete);

        }
    }


    private void clickSave(View view) {
        info.setName(nameEdit.getText().toString());
        info.setDescription(descEdit.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(MSG_RETURN_DATA, info);
        intent.putExtra(MSG_INDEX, infoIndex);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void clickCancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void clickEdit(View view){
        if (view.getId() != R.id.detail_fabEdit2 || dlcEditMode) throw new AssertionError();
        this.dlcEditMode = true;
        this.initview();
    }

    private void clickDelete(View view) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(getString(R.string.hint_title));
        ab.setMessage(getString(R.string.hint_deleteConfirm));
        ab.setPositiveButton(getString(R.string.button_yes), (di,num)->{
            di.dismiss();
            Intent intent = new Intent();
            intent.putExtra(MSG_INDEX, infoIndex);
            setResult(RESULT_DELETED, intent);
            finish();
        });

        ab.setNegativeButton(getString(R.string.button_no), (di,num)->{
            di.dismiss();
        });

        ab.show();
    }

}
