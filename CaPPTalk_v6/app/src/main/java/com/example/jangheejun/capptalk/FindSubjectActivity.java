package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindSubjectActivity extends Activity {

    private List<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private Rowlist adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraylist;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findsubject);

        Intent intent = getIntent();
        uid = intent.getStringExtra("myUid");

        editSearch = (EditText) findViewById(R.id.editSearch);
        listView = (ListView) findViewById(R.id.listView);

        // 리스트를 생성한다.
        list = new ArrayList<String>();
        arraylist = new ArrayList<String>();

        // 검색에 사용할 데이터을 미리 저장한다.
        settingList();

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        // arraylist.addAll(list);

        // 리스트에 연동될 아답터를 생성한다.
        //adapter = new Rowlist(list, AddSubjectActivity.this);

        // 리스트뷰에 아답터를 연결한다.
        //listView.setAdapter(adapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        Button nextButton = (Button) findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindSubjectActivity.this, sample_activity_1.class);
                intent.putExtra("myUid", uid);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindSubjectActivity.this, AddSubjectActivity.class);
                intent.putExtra("myUid", uid);
                intent.putExtra("info", list.get(position).toString());
                startActivity(intent);

            }
        });

    }

    // 검색을 수행하는 메소드
    public void search(String charText) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    // 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingList(){
        Log.d("MainActivity", "settingList");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.child("rooms").getChildren()) {
                    list.add(snapshot.child("info").child("name").getValue().toString() + " / " + snapshot.child("info").child("professor").getValue().toString() + " / " + snapshot.getKey().toString());
                    arraylist.add(snapshot.child("info").child("name").getValue().toString() + " / " + snapshot.child("info").child("professor").getValue().toString() + " / " + snapshot.getKey().toString());
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.child("info").child("name").getValue().toString() + " / " + snapshot.child("info").child("professor").getKey().toString()  + " / " + snapshot.getValue().toString());
                    adapter = new Rowlist(list, FindSubjectActivity.this);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
}
