package database;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirabaseDb {

    public DatabaseReference databaseReference;
    public String result;
    public FirabaseDb(String parentNode) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(parentNode);
    }

    public DatabaseReference getChildReference(String child){
        return this.databaseReference.child(child);
    }

    public String insert(Object object,String key){

        this.databaseReference.child(key).setValue(object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                result = "Ekleme İşlemi Başarılı :)";
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result = e.getLocalizedMessage();
            }
        });
        return result;
    }

    public String update(Object object,String uuid){
        this.databaseReference.child(uuid).setValue(object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                result = "Güncelleme İşlemi Başarılı";
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result = e.getLocalizedMessage();
            }
        });
        return result;
    }

    public String delete(String uuid){

        this.databaseReference.child(uuid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                result = "1";
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result = e.getLocalizedMessage();
            }
        });
        return  result;
    }
}
