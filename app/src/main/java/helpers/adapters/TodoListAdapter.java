package helpers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.semihduman.todo.AddNewActivity;
import com.semihduman.todo.MainActivity;
import com.semihduman.todo.R;

import java.util.ArrayList;

import objects.Todo;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.MyViewHolder> {

    public Context context;
    public ArrayList<Todo> todos;
    public TodoListAdapter(Context c,ArrayList<Todo> p){
        context = c;
        todos = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.title.setText(todos.get(position).getTitle());
        holder.desc.setText(todos.get(position).getDesc());
        String[] dateSplit = todos.get(position).getDate().split("-");
       // System.out.println("split : "+todos.get(position).getDate()+" "+ dateSplit[0]+" "+dateSplit[1]);
        holder.date.setText(dateSplit[0]);
        holder.time.setText(dateSplit[1]);

       /* if (todos.get(position).getFinished()){
            holder.finished.setChecked(true);
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_item_finished));
        }*/
        holder.finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = false;
                if (holder.finished.isChecked()) {
                    result = true;
                }
                try{
                    FirebaseDatabase.getInstance().getReference().child("Todo").child(todos.get(position).getKey()).child("finished").setValue(result);
                    if (todos.get(position).getFinished()){
                        holder.finished.setChecked(true);
                        holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_item_finished));
                    }
                    MainActivity.todoList.clear();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        //MainActivity.todoList.clear();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AddNewActivity.class);
                intent.putExtra("key",todos.get(position).getKey());
                intent.putExtra("action","old");
                intent.putExtra("finished",todos.get(position).getFinished());
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return todos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title,desc,date,time;
        CheckBox finished;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleItemTextView);
            desc = itemView.findViewById(R.id.descItemTextView);
            date = itemView.findViewById(R.id.dateItemTextView);
            finished = itemView.findViewById(R.id.todoItemCheckBox);
            time =itemView.findViewById(R.id.timeItemTextView);
        }
    }
}
