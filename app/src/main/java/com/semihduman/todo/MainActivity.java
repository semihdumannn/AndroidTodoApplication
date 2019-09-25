package com.semihduman.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
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
import helpers.adapters.BroadcastReceiver.NetworkChangeReceiver;
import helpers.adapters.Helper;
import helpers.adapters.SwipeController;
import helpers.adapters.SwipeControllerActions;
import helpers.adapters.TodoListAdapter;
import objects.Todo;





public class MainActivity extends AppCompatActivity {

    FirabaseDb reference ;
    FirebaseAuth mAuth ;
    FirebaseUser firebaseUser;
    FirebaseAuth.AuthStateListener mAuthListener ;

    RecyclerView listRecyler;
    public static ArrayList<Todo> todoList;
    TodoListAdapter adapter;
    Button btnNew;
    String userId;
    //SwipeController
    SwipeController swipeController = null;
    //Network Kontrol
    private NetworkChangeReceiver receiver;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_menu:
                startAdd();
            break;

            case R.id.logout_menu:
                logoutApp();
                break;

            case R.id.profile_menu:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize layout components
        btnNew = findViewById(R.id.btnMainNewTodo);
        listRecyler = findViewById(R.id.todoListRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swpLayout);





        listRecyler.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();
        reference =  new FirabaseDb("Todo");
        //firebase user setup and listener
        setupFirebaseUser();
        userId = mAuth.getUid();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        if (receiver.isNetworkAvailable(getApplicationContext()))
            loadItems();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkChangeReceiver.isConnected)
                    loadItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//receiver durduruluyor
    }

    public void  setupFirebaseUser(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fuser = firebaseAuth.getCurrentUser();
                checkCurrentUser(fuser);

                if (fuser != null) {
                    //user is signed
                    userId = fuser.getUid();
                    //System.out.println("check user  : " + userId  );
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);

                }
            }
        };

    }

    private void checkCurrentUser(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
        startAdd();
    }

    private void startAdd(){
        Intent i = new Intent(this,AddNewActivity.class);
        i.putExtra("action","new");
        i.putExtra("userId",  userId);
        startActivity(i);
    }

    public void logout(View view){
       logoutApp();
    }
    private void logoutApp(){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
    }
}
