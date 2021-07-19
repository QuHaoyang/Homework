package com.byted.camp.todolist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.debug.FileDemoActivity;
import com.byted.camp.todolist.debug.SpDemoActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    private String[] protection = {
      "id","date","state","content","priority"
    };
    String sortOrder = "priority DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                // TODO: 2021/7/19 7. 此处删除数据库数据
                String selection = "id LIKE ?";
                String[] selectionArgs = {note.id + ""};
                int deletedRows = database.delete("NOTE",selection,selectionArgs);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                String selection = "id LIKE ?";
                String[] selectionArgs = {note.id + ""};
                ContentValues values = new ContentValues();
                values.put("DATE",note.getDate().toString());
                values.put("state",note.getState().intValue + "");
                values.put("content",note.getContent());
                values.put("priority",note.getPriority().intValue + "");
                database.update("NOTE",values,selection,selectionArgs);
                notesAdapter.refresh(loadNotesFromDatabase());
                // TODO: 2021/7/19 7. 此处更新数据库数据
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_file:
                startActivity(new Intent(this, FileDemoActivity.class));
                return true;
            case R.id.action_sp:
                startActivity(new Intent(this, SpDemoActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        // TODO: 2021/7/19 7. 此处query数据库数据
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM ddHH:mm:ss 'GMT' yyyy", Locale.US);
        Cursor cursor = database.query(
                "NOTE",
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (cursor.moveToNext()){
            Note note = new Note(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            String tmp = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//            tmp = tmp.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
            SimpleDateFormat sf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            try {
                note.setDate(sf1.parse(tmp));
//                note.setDate(simpleDateFormat.parse(tmp));
            }
            catch (Exception e){
                e.printStackTrace();
            }
            note.setState(State.from(cursor.getInt(cursor.getColumnIndexOrThrow("state"))));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
            note.setPriority(Priority.from(cursor.getInt(cursor.getColumnIndexOrThrow("priority"))));
            result.add(note);
        }
        return result;
    }
}
