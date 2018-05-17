package com.example.fernandosilveira.promotions.AlarmReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fernandosilveira.promotions.Activity.Validacao.ValidarData;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmDataAnunciante extends BroadcastReceiver
{
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private FirebaseAuth minhaauth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("ServicoDATAAnunciante", "Iniciando Alarm");
        final String meuuid = minhaauth.getCurrentUser().getUid();
        ref.child("minhascategorias").child(meuuid).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    final String cat = ds.getKey();
                    ref.child("produtos").child(cat).child(meuuid).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot dados: dataSnapshot.getChildren())
                            {
                                try
                                {
                                    ValidarData validarData = new ValidarData();
                                    Produto produto = dados.getValue(Produto.class);
                                    String dataatual = validarData.dataAtual();
                                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                    Date dataFIN = formato.parse(produto.dataFinal);
                                    Date dataATU = formato.parse(dataatual);
                                    String diasrest = validarData.diasRestantes(dataatual,produto.dataFinal);
                                    ref.child("produtos").child(cat).child(meuuid).child(produto.nome).child("diasRestantes").setValue(diasrest);
                                    Log.d("ALARM","diasrest ="+diasrest);
                                    if (diasrest == "0")
                                    {
                                        Log.d("ALARM","dias rest = vence hoje");
                                        ref.child("produtos").child(cat).child(meuuid).child(produto.nome).child("diasRestantes").setValue("vence hoje");
                                    }
                                    if (dataFIN.before(dataATU))
                                    {
                                        Log.d("ALARM","VENCEU");
                                        ref.child("produtos").child(cat).child(meuuid).child(produto.nome).child("diasRestantes").setValue("vencido");
                                    }
                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            throw databaseError.toException();
                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

}