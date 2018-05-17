package com.example.fernandosilveira.promotions.Service.Geofence;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.fernandosilveira.promotions.Config.ConexaoTeste;
import com.example.fernandosilveira.promotions.Model.LocalProd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServicoLocalConsumidor extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = ServicoLocalConsumidor.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private PendingIntent geofencePendingIntent;
    private float GEOFENCE_RADIUS = 200f;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private String meuUid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<Geofence> listageofence = new ArrayList<Geofence>();
    private ArrayList<String> listdestaques =new ArrayList<>();
    private List<String> listids = new ArrayList<>() ;
    private ConexaoTeste conexaoTeste = new ConexaoTeste();

    @Override
    public void onCreate()
    {
        super.onCreate();
        criarGoogleApi();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "iniciar comando");
        if (!googleApiClient.isConnected())
            googleApiClient.connect();
        return START_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(TAG, "conectado " + bundle);
        Location l = null;
        try
        {
            l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (SecurityException e)
        {
            e.printStackTrace();
        }
        if (l != null)
        {
            Log.i(TAG, "lat " + l.getLatitude());
            Log.i(TAG, "lng " + l.getLongitude());
        }
        iniciarLocalizacao();
        recuperar();
        if (listageofence.size()  == 0)
        {
            addGeofence();
            addGeofences(listageofence);
        }else {addGeofences(listageofence);}

    }
    //adiciona as geofences
    private void addGeofences(ArrayList<Geofence> geofence)
    {
        try
        {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(geofence),
                    getGeofencePendingIntent()
            );
        } catch (SecurityException e)
        {e.printStackTrace();}
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    // método nativo que checa a localização no intervalo de tempo especificado
    @Override
    public void onLocationChanged(Location location)
    {
        Log.i("furunco", "lat carregada " + location.getLatitude());
        Log.i("furunco", "lng carregada " + location.getLongitude());
        if (listageofence.size() == 0)
        {
            addGeofence();
        }
        recperarids();
        recuperar();
        addGeofences(listageofence);
        if (listids.size() !=0)
        {
            removeGeofence();
        }
        Log.d("furunco", "geofence olha aqui --> " + listageofence.size());
        Log.d("furunco", "ids olha aqui --> " + listids.size());
        Log.d("furunco", "Destques olha aqui --> " + listdestaques.size() +listdestaques);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "google api desconectado");
        googleApiClient.unregisterConnectionCallbacks(this);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent());
        googleApiClient.disconnect();
    }
    private void criarGoogleApi()
    {
        Log.d(TAG, "criarGoogleApi()");
        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    //inicia a localização e adiciona o tempo de atualização do GPS
    private void iniciarLocalizacao()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        // atualiza lista de geofence a cada 40 segundos
        mLocationRequest.setInterval(8000);
        mLocationRequest.setFastestInterval(8000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }
    //inicia a geofences
    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofence)
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofence);
        Log.d("furunco", String.valueOf(geofence));
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent()
    {
        // Reutilize o PendingIntent se já o tivermos.
        if (geofencePendingIntent != null)
        {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, ServicoTransicIntent.class);
        // Usamos FLAG_UPDATE_CURRENT para que recebamos a mesma intenção pendente quando
        // chamando addGeofences () e removerGeofences().
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
    public void removeGeofence()
    {
        if (listids.size() > 0)
        {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, listids);
            Log.d("furunco", "Remove Geofence olha aqui --> " + listids.size() +listids);
        }
    }
    //recupera os anuncios do banco e adiciona na geofence
    private void recuperarlocais(final String cat , final List<String> ids )
    {
        String conexao = conexaoTeste.checkNetworkType(getApplicationContext());
        if(conexao.contains("OK"))
        {
            ref.child("locais").child(cat).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if (ds.getValue() != null)
                        {
                            LocalProd localProd = ds.getValue(LocalProd.class);
                            String key = ds.getKey();
                            String idgeo = key+":"+cat;
                            String id = localProd.empres + ":" + localProd.nomeProd +
                                    ":" + localProd.preco +
                                    ":" + localProd.diasRestantes+" dias"+":" + cat + ":" + localProd.uid;
                            if (verificarids(idgeo) == false)
                            {
                                if (verificargeo(idgeo, listageofence) == false)
                                {
                                    listageofence.add(new Geofence.Builder()
                                            .setRequestId(idgeo)
                                            .setCircularRegion(
                                                    localProd.lat,
                                                    localProd.lng,
                                                    GEOFENCE_RADIUS
                                            )
                                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                            .build());
                                    Log.d("furunco", "geofence olha aqui --> " + listageofence.size());
                                }
                            } else {
                                removergeofence(idgeo);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    //recupera uma lista dos ids das geofences que ja foram adicionadas
    private void recperarids()
    {
        ref.child("usuarios").child(meuUid).child("idsnotificacao").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listids.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        listids.add(ds.getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    // adiciona geofence vazia para evitar erro
    private void addGeofence()
    {
        listageofence.add(new Geofence.Builder()
                .setRequestId("00")
                .setCircularRegion(
                        0.0,
                        0.0,
                        GEOFENCE_RADIUS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
    }
    //recupera uma lista de destaques do banco
    private void recuperar()
    {
        listdestaques.clear();
        ref.child("destaques").child(meuUid).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String cat = ds.getKey();
                    listdestaques.add(cat);
                    recuperarlocais(cat,listids);
                    Log.d("furunco", "Destques olha aqui --> " + listdestaques.size() +listdestaques);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //verifica se ja foi adicionado essa geofence no banco de dados
    private boolean verificargeo(String id,ArrayList<Geofence> listgeo)
    {
        boolean res = false;
        for (int i = listgeo.size() - 1; i >= 0; i--)
        {
            if (listgeo.get(i).getRequestId().contains(id))
            {
                res = true;
            }
        }
        Log.d("furunco", "VerificarGeo res olha aqui --> " + res);
        return res;
    }
    //verifica se ja recebeu notificação deste anuncio
    private boolean verificarids(String id)
    {
        boolean res = false;
        for (int i = 0; i < listids.size(); i++)
        {
            if (listids.get(i).contains(id))
            {
                res = true;
            }

        }
        Log.d("furunco", "Verificar ID res olha aqui --> " + res);
        return res;
    }
    //remove geofence caso houver alterações no banco
    private void removergeofence(String id)
    {
        for (int i = 0; i < listageofence.size(); i++)
        {
            if (listageofence.get(i).getRequestId().contains(id))
            {
                listageofence.remove(i);
            }
        }
    }

}