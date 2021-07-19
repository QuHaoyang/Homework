package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.SpDemoActivity;

import java.io.File;
import java.util.Date;


public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private AppCompatRadioButton lowRadio;
    private AppCompatRadioButton mediumRadio;
    private AppCompatRadioButton highRadio;


    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    private boolean if_saved = false;
    private final String SP = "tmp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        lowRadio = findViewById(R.id.btn_low);
        mediumRadio = findViewById(R.id.btn_medium);
        highRadio = findViewById(R.id.btn_high);
        lowRadio.setChecked(true);

        addBtn = findViewById(R.id.btn_add);

        SharedPreferences sp = NoteActivity.this.getSharedPreferences(SP, MODE_PRIVATE);
        String value = sp.getString("content", "");
        editText.setText(value);
        int priority = sp.getInt("priority",0);
        switch (priority){
            case 0:
                lowRadio.setChecked(true);
                break;
            case 1:
                mediumRadio.setChecked(true);
                break;
            case 2:
                highRadio.setChecked(true);
                break;
            default:
                break;
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),
                        getSelectedPriority());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(if_saved){
            SharedPreferences sp = NoteActivity.this.getSharedPreferences(SP, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("content", "");
            editor.putInt("priority", 0);
            editor.apply();
        }
        else{
            SharedPreferences sp = NoteActivity.this.getSharedPreferences(SP, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("content", editText.getText().toString());
            editor.putInt("priority", getSelectedPriority().intValue);
            editor.apply();
        }
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;

    }

    private boolean saveNote2Database(String content, Priority priority) {
        // TODO: 2021/7/19 8. 这里插入数据库
        ContentValues values = new ContentValues();
        Date date = new Date();
        values.put("date",date.toString());
        values.put("content",content);
        values.put("state","0");
        values.put("priority",priority.intValue);
        database.insert("NOTE",null,values);
        if_saved = true;
        return true;
    }

    private Priority getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_high:
                return Priority.High;
            case R.id.btn_medium:
                return Priority.Medium;
            default:
                return Priority.Low;
        }
    }
}
