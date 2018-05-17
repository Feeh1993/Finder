package com.example.fernandosilveira.promotions.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.fernandosilveira.promotions.AlarmReceiver.AlarmDataAnunciante;

import java.util.Calendar;

public class ServicoDataAnunciante extends Service
{
    public ServicoDataAnunciante()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("ServicoDATAAnunciante", "Iniciando serviço");

        //Definir início para as 10 horas
        Calendar calendar = Calendar.getInstance();
        Log.i("ServicoDATAAnunciante", calendar.getTime().toString());

//Definir intervalo de 6 horas
        long intervalo = 6*60*60*1000; //6 horas em milissegundos

        Intent tarefaIntent = new Intent(getApplicationContext(), AlarmDataAnunciante.class);
        PendingIntent tarefaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1234, tarefaIntent,0);

        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

//Definir o alarme para acontecer de 6 em 6 horas a partir das 10 horas
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                intervalo, tarefaPendingIntent);
        return START_STICKY;
    }
}
