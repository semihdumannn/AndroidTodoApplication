package com.semihduman.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import database.FirabaseDb;
import helpers.adapters.Helper;
import helpers.adapters.SwipeController;
import helpers.adapters.SwipeControllerActions;
import helpers.adapters.TodoListAdapter;
import objects.Todo;



public class MainActivity extends AppCompatActivity {

    FirabaseDb reference ;
    FirebaseAuth mAuth ;
    FirebaseUser firebaseUser;
    RecyclerView listRecyler;
    public static ArrayList<Todo> todoList;
    TodoListAdapter adapter;
    Button btnNew;
    String userId;
    //SwipeController
    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize layout components
        btnNew = findViewById(R.id.btnMainNewTodo);
        listRecyler = findViewById(R.id.todoListRecyclerView);

        setupFirebaseUser();

        userId = mAuth.getUid();

        listRecyler.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();
        reference =  new FirabaseDb("Todo");
        //Load Items
        loadItems();
    }

    public void  setupFirebaseUser(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser(); // authenticated user
        if (firebaseUser == null){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private void loadItems() {
        todoList.clear();
        Query queryRef = FirebaseDatabase.getInstance().getReference("Todo").orderByChild("userId").equalTo(userId);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("data : " + dataSnapshot);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Todo p = dataSnapshot1.getValue(Todo.class);
                        todoList.add(p);
                    }
                    adapter = new TodoListAdapter(MainActivity.this, todoList);
                    listRecyler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.showToast(databaseError.getMessage(), MainActivity.this);
            }
        });

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(final int p) {
                try{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Uyarı !!")
                            .setMessage("Silmek istediğinize emin misiniz ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    FirebaseDatabase.getInstance().getReference().child("Todo").child(adapter.todos.get(p).getKey()).removeValue();
                                    Helper.showToast("Silme İşlemi Başarılı Şekilde Gerçekleşti :)",MainActivity.this);
                                    adapter.todos.remove(p);
                                    adapter.notifyItemRemoved(p);
                                    adapter.notifyItemRangeChanged(p, adapter.getItemCount());

                                }})
                            .setNegativeButton("Hayır", null).show();
                }catch (Exception e){
                    Helper.showToast(e.getMessage(),MainActivity.this);
                }

            }

            @Override
            public void onLeftClicked(int position) {
                Intent intent = new Intent(MainActivity.this,AddNewActivity.class);
                intent.putExtra("key",adapter.todos.get(position).getKey());
                intent.putExtra("action","old");
                intent.putExtra("finished",adapter.todos.get(position).getFinished());
                startActivity(intent);

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(listRecyler);

        listRecyler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }


    public void addNew(View view){
        //String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent i = new Intent(this,AddNewActivity.class);
        i.putExtra("action","new");
        i.putExtra("userId",  userId);
        startActivity(i);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
    }
}
