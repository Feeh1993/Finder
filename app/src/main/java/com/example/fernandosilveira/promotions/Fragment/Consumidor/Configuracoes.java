package com.example.fernandosilveira.promotions.Fragment.Consumidor;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fernandosilveira.promotions.Activity.Geral.FaleConosco;
import com.example.fernandosilveira.promotions.Activity.Geral.TermosUso;
import com.example.fernandosilveira.promotions.Activity.Consumidor.MinhaContaConsumidor;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.DialogFragment.AlterarSenha;
import com.example.fernandosilveira.promotions.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
public class Configuracoes extends Fragment
{
    private Button minhaconta,alterarsenha,termos,faleconosco;
    private TextView txtEmail;
    private FirebaseAuth minhaauth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(minhaauth.getCurrentUser().getUid());
    public Configuracoes()
    {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_configuracoes_consumidor, container, false);
        minhaconta =(Button)view.findViewById(R.id.btnMinhaConta_Usuario);
        alterarsenha =(Button)view.findViewById(R.id.btnAlterarSenha_Usuario);
        txtEmail =(TextView) view.findViewById(R.id.txtEmail_config_usuario);
        termos =(Button)view.findViewById(R.id.btnTermos_Conf_Usuario);
        faleconosco =(Button)view.findViewById(R.id.btnFaleConosco_Usuario);
        minhaconta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getActivity(), MinhaContaConsumidor.class));}
        });
        alterarsenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showAlertDialog();}
        });
        termos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getActivity(), TermosUso.class));}
        });
        faleconosco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getActivity(), FaleConosco.class));}
        });
        firebase.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                txtEmail.setText(dataSnapshot.child("email").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
        return view;
    }
    private void showAlertDialog()
    {
        FragmentManager fm = getFragmentManager();
        AlterarSenha alertDialog = AlterarSenha.novaestancia("Alteração de Senha");
        alertDialog.show(fm, "fragment_alert");
    }
}