package com.example.fernandosilveira.promotions.Service.Geofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.fernandosilveira.promotions.Activity.Consumidor.Anuncio_Notificacao;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.LocalProd;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServicoTransicIntent extends IntentService
{
    private static final String TAG = ServicoTransicIntent.class.getSimpleName();
    public int GEOFENCE_NOTIFICATION_ID;

    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private ArrayList<String> lisids = new ArrayList<>();

    public ServicoTransicIntent() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Recuperar a intenção de Geofencing
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Erros de manipulação
        if (geofencingEvent.hasError())
        {
            String errorMsg = getErroString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMsg);
            return;
        }

        final List<String> geofenceIds = new ArrayList<>();
        for (Geofence geofence : geofencingEvent.getTriggeringGeofences())
        {
            geofenceIds.add(geofence.getRequestId());
        }
        for (int i = 0; i < geofenceIds.size(); i++)
        {
            final String[] recuperarStrings = geofenceIds.get(i).split(":");
            final String ids = geofenceIds.get(i);
            final String meuUid = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getUid();
            Log.i("Enviando PUSH", "Enviando PUSH " + ids);
            lisids.clear();
            recuperarIDS(meuUid,ids,recuperarStrings);
        }
    }

    // Enviar uma notificação
    private void enviarNotificacao(LocalProd prod, String[] ids)
    {
        Log.i(TAG, "enviarNotificação: " + prod);
        int random = (int) System.currentTimeMillis();
        // Intenção de iniciar a atividade principal
        Intent Anuncio = new Intent(getApplicationContext(), Anuncio_Notificacao.class);
        Bundle dados = new Bundle();
        dados.putString("empres", prod.empres);
        dados.putString("nome", prod.nomeProd);
        dados.putString("cat", ids[1]);
        dados.putString("activity", "Servico");
        Anuncio.putExtras(dados);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                random, Anuncio, PendingIntent.FLAG_UPDATE_CURRENT);

        // Criando e enviando notificação
        NotificationManager notificacao =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificacao.notify(gerarId(), setBigTextStyleNotification(notificationPendingIntent, prod));
        // criarNotificacao(info, notificationPendingIntent));
    }

    private Notification setBigTextStyleNotification(PendingIntent notificationPendingIntent, LocalProd prod)
    {
        NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
        notiStyle.setBigContentTitle(prod.nomeProd);
        notiStyle.setSummaryText("Finder");
        // Add the big text to the style.
        CharSequence bigText = "O " + prod.nomeProd +
            " esta apenas " + prod.preco + "a(o) " +prod.tipo+"\n" +
                "Valido por " + prod.diasRestantes + " dias \n" +
                " Clique aqui e confira!";
        notiStyle.bigText(bigText);
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_logo_finder)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentTitle("Finder")
                .setContentText("O " + prod.nomeProd +
                        " esta apenas " + prod.preco + "a(o) " +prod.tipo+"\n" +
                        "Valido por " + prod.diasRestantes + " dias \n" +
                        " Clique aqui e confira!")
                .setStyle(notiStyle).build();
    }

    // Manipular erros
    private static String getErroString(int codigoErro)
    {
        switch (codigoErro) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence indisponivel";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "muitas Geofence";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Demasiadas intenções pendentes";
            default:
                return "Erro desconhecido";
        }
    }

    private int gerarId()
    {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hhmmssMs");
        String datetime = ft.format(dNow);
        Log.i(TAG, datetime);
        int random = (int) System.currentTimeMillis();
        GEOFENCE_NOTIFICATION_ID = Integer.parseInt(datetime);
        return random;
    }

    private void salvarNotificacao(final String meuuid, final LocalProd prod, final String geofence, final String[] ids)
    {
                final String notificacaomaps = ids[1]+":"+prod.nomeProd;
                final String historico = prod.empres + ":" + prod.nomeProd + ":" + ids[1];
                ref.child("usuarios").child(meuuid).child("idsnotificacao").push().setValue(geofence).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        ref.child("usuarios").child(meuuid).child("historico").push().setValue(historico);
                        ref.child("usuarios").child(meuuid).child("idmaps").push().setValue(notificacaomaps);
                        enviarNotificacao(prod,ids);
                    }
                });

    }

    public void recuperarIDS(final String meuid, final String geofence , final String[]ids)
    {
        ref.child("usuarios").child(meuid).child("idsnotificacao").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    lisids.add(ds.getValue().toString());
                }
                compararIDS(lisids,meuid,geofence,ids);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void compararIDS(ArrayList<String> resul, final String meuid, final String texto, final String[]ids)
    {
        boolean resultado = false;
        for (int i = 0; i < resul.size(); i++)
        {
            if (resul.get(i).contains(texto))
            {
                resultado = true;
            }

        }
        if (resultado == false)
        {
            ref.child("locais").child(ids[1]).child(ids[0]).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    LocalProd prod = dataSnapshot.getValue(LocalProd.class);
                    Log.v("ServicoTrans","Produto ="+prod+" meuuid ="+meuid+" geofence ="+texto);
                    salvarNotificacao(meuid,prod,texto,ids);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void recuperarProduto(String[] ids, final String meuid, final String texto)
    {

    }
}