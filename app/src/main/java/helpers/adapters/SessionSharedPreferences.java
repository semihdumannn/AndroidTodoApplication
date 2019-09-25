package helpers.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionSharedPreferences {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionSharedPreferences(Context cntx) {
        // TODO Auto-generated constructor stub
        this.prefs =  cntx.getSharedPreferences("uuid",Context.MODE_PRIVATE) ;
        this.editor = prefs.edit();
        //PreferenceManager.getDefaultSharedPreferences();
    }

    public void setUuid(String uuid) {
        this.editor.putString("uuid", uuid).commit();
    }

    public String getUuid() {
        return this.prefs.getString("uuid","");
    }

    public void removeUuid(){
        this.editor.remove("uuid");
    }


}
