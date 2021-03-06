package github.toninhobueno.beberagua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button btnNotify;
    private EditText editMinutes;
    private TimePicker timePicker;
    private int hour;
    private int minute;
    private int interval;

    private boolean activate = false;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    btnNotify = findViewById(R.id.btn_notify);
    editMinutes = findViewById(R.id.edit_txt_number_interval);
    timePicker = findViewById(R.id.time_picker);

    timePicker.setIs24HourView(true);

    preferences = getSharedPreferences("db", Context.MODE_PRIVATE);

    activate =  preferences.getBoolean("activated",false);

    if (activate){
        btnNotify.setText(R.string.pause);
        int color = ContextCompat.getColor(this, android.R.color.black);
        btnNotify.setBackgroundTintList(ColorStateList.valueOf(color));

       int interval = preferences.getInt("interval",0);
       int hour = preferences.getInt("hour",timePicker.getCurrentHour());
       int minute = preferences.getInt("minute",timePicker.getCurrentMinute());

    editMinutes.setText(String.valueOf(interval));
    timePicker.setCurrentHour(hour);
    timePicker.setCurrentMinute(minute);
    }


/*
    bntNotify.setOnClickListener(notifyClick (pode passar a função direta no código chamado de:  evento de click com objeto anonimo ));
*/
    }
/*
---------------------------------------------------------------------------Outro método para acionar o click do botão (evento de click com variavel anonima)------------------------------------------------------------------
    public View.OnClickListener notifyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sInterval = editMinutes.getText().toString();
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
            interval = Integer.parseInt(sInterval);

            Log.d("Teste", "hora: " + hour + " minutos: " + minute +  " intervalo: " + interval);
        }
    };
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 */


// Evento de click via XML
    public void notifyClick (View view){
       String sInterval = editMinutes.getText().toString();

       if (sInterval.isEmpty()){
           Toast.makeText(this, R.string.msg_error, Toast.LENGTH_LONG).show();
           return;
       }
       hour = timePicker.getCurrentHour();
       minute = timePicker.getCurrentMinute();
       interval = Integer.parseInt(sInterval);

        if (!activate) {
            btnNotify.setText(R.string.pause);
            int color = ContextCompat.getColor(this, android.R.color.black);
            btnNotify.setBackgroundTintList(ColorStateList.valueOf(color));
            activate = true;

           SharedPreferences.Editor editor = preferences.edit();
           editor.putBoolean("activated", true);
           editor.putInt("interval", interval);
           editor.putInt("hour", hour);
           editor.putInt("minute", minute);
           editor.apply();

          Calendar calendar = Calendar.getInstance();
          calendar.set(Calendar.HOUR_OF_DAY,hour);
          calendar.set(Calendar.MINUTE,minute);


           Intent notificationIntent = new Intent(MainActivity.this,NotificationPublisher.class);
           notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION_ID,1);
           notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION,"Hora de beber água");

            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),interval  * 1000 * 60, broadcast);
        }
        else{
            btnNotify.setText(R.string.notify);
            int color = ContextCompat.getColor(this, R.color.colorAccent);
            btnNotify.setBackgroundTintList(ColorStateList.valueOf(color));
            activate = false;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("activated", false);
            editor.remove("interval");
            editor.remove("hour");
            editor.remove("minute");
            editor.apply();


            Intent notificationIntent = new Intent(MainActivity.this,NotificationPublisher.class);

            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent,0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(broadcast);

        }
       Log.d("Teste", "hora: " + hour + " minutos: " + minute +  " intervalo: " + interval);
    }
}