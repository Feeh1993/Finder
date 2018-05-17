package com.example.fernandosilveira.promotions.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Adapter.Consumidor.TabAdapterConsumidor;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class ErroAnuncio extends DialogFragment
{
    private TextView txtErro;
    private Button btnOK;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private String meuuid,key,tipo;
    public static ErroAnuncio novaestancia(String tipo,String key,String meuuid)
    {
        ErroAnuncio frag = new ErroAnuncio();
        Bundle args = new Bundle();
        args.putString("tipo",tipo);
        args.putString("key", key);
        args.putString("meuuid", meuuid);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.activity_erro_anuncio, container);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        meuuid = getArguments().getString("meuuid");
        tipo = getArguments().getString("tipo");
        key = getArguments().getString("key");
        txtErro = (TextView) view.findViewById(R.id.txtverro_anuncio);
        btnOK = (Button)view.findViewById(R.id.btnOK_erro_anuncio);
        getDialog().setTitle("Ausencia de Anuncio");
        txtErro.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deletar();
            }
        });
    }
    public void deletar()
    {
                if (tipo.contains("favoritos"))
                {
                    ref.child("usuarios").child(meuuid).child("favoritos").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Anuncio removido com sucesso da sua lista",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getContext(), TabAdapterConsumidor.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Erro na conexão!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    ref.child("usuarios").child(meuuid).child("historico").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Anuncio removido com sucesso do seu historico",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getContext(), TabAdapterConsumidor.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Erro na conexão!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
    }
}