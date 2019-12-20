package android.harmony.irescue.utility;

import android.harmony.irescue.model.AlertModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculateTime {
    public static Object calculateTime(Long time){
        Long diffTime=System.currentTimeMillis()-time;
        int secs=diffTime.intValue()/1000;
        int mins=secs/60;
        int hours=mins/60;
        int days=hours/24;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(time));
        if(secs<60){
            return secs+" seconds";
        }else if(mins<60){
            return mins+" minutes";
        }else if(hours<=24){
            return hours+" hours";
        }else if(hours<48){
            return "Yesterday";
        }else if(days<=7){
            return days+" days";
        }else{
            return dateString;
        }
    }
}
