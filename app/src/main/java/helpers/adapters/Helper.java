package helpers.adapters;

import android.content.Context;
import android.widget.Toast;

public class Helper {

    public static void  showToast(String message, Context context){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

}
