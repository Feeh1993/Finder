package com.example.fernandosilveira.promotions.Activity.Geral;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fernandosilveira.promotions.Adapter.Anunciante.TabAdapterAnunc;
import com.example.fernandosilveira.promotions.Adapter.Consumidor.TabAdapterConsumidor;
import com.example.fernandosilveira.promotions.Config.ConexaoTeste;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Helper.Ajuda_Inicio;
import com.example.fernandosilveira.promotions.Maps.Local;
import com.example.fernandosilveira.promotions.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Loading extends Activity
{
    private FirebaseAuth minhaauth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference referenceBanco;
    private DatabaseReference referenceTipo;
    private TextView txt;
    private String texto = "" ;
    private TextView txterro;
    private Button btnErro;
    private ConfiguracaoFirebase configuracaoFirebase;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        txt = (TextView) findViewById(R.id.txt);
        btnErro = (Button) findViewById(R.id.btnSemConexao);
        iv = (ImageView) findViewById(R.id.logo_loading);
        StartAnimations();
    }
    private void StartAnimations()
    {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.main_layout);
        if (l != null)
        {
            l.clearAnimation();
            l.startAnimation(anim);
        }
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();

        if (iv != null)
        {
            txt.clearAnimation();
            iv.clearAnimation();
            txt.startAnimation(anim);
            iv.startAnimation(anim);
        }
        int SPLASH_DISPLAY_LENGTH = 3500;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                    if (conexaoteste() == true)
                    {
                        texto = recuperarlogin();
                        tipologin(texto);
                    } else semconexao();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public void Click(View view)
    {
        startActivity(new Intent(Loading.this, Loading.class));
    }
    private boolean conexaoteste()
    {
        boolean conectado = true;
        ConexaoTeste conexaoTeste = new ConexaoTeste();
        String s =conexaoTeste.checkNetworkType(this);
        if (s =="Sem conexão")
        {
            conectado = false;
        }
        return conectado;
    }
    private String recuperarlogin()
    {
        String res = "";
        if (minhaauth.getCurrentUser() != null)
        {
            res = "Preparando Interface";
        }else
        {
            res = "Seja bem vindo ao Finder";
            Intent intent = new Intent(this,Ajuda_Inicio.class);
            startActivity(intent);
            finish();
        }
        return res;
    }
    private void semconexao()
    {
        View  view = (View) findViewById(R.id.loading_layout);
        view.setBackgroundResource(R.drawable.semconexaonet);
        btnErro.setVisibility(View.VISIBLE);
        iv.setVisibility(View.INVISIBLE);
        txt.setVisibility(View.INVISIBLE);
    }
    private void tipologin(String tipo)
    {
        if (tipo == null)
        {
            semconexao();
        }
        else
        {
            if (tipo.equals("Preparando Interface"))
            {
                referenceBanco = configuracaoFirebase.getFirebase().child("usuarios").child(minhaauth.getCurrentUser().getUid()).child("tipo");
                referenceBanco.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String ds = dataSnapshot.getValue().toString();
                        if (!ds.contains("usuario"))
                        {
                            verificarlocal();
                        } else
                            {
                                Intent intent= new Intent(getApplicationContext(),TabAdapterConsumidor.class);
                                startActivity(intent);
                                finish();
                            }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        semconexao();
                    }
                });
            } else
                {
                    texto = tipo;
                }
        }
    }
    private void verificarlocal()
    {
        if (minhaauth.getCurrentUser()!= null)
        {
            referenceTipo = configuracaoFirebase.getFirebase().child("localizacao").child(minhaauth.getCurrentUser().getUid());
            referenceTipo.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.getValue() == null)
                    {
                        startActivity(new Intent(getApplicationContext(), Local.class));
                        finish();
                    }else
                        {
                            Intent intent= new Intent(getApplicationContext(),TabAdapterAnunc.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        semconexao();
                    }
                });
        }
    }
}