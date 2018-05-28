package com.example.fernandosilveira.promotions.Activity.Anunciante;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;

public class MinhaContaAnunciante extends AppCompatActivity
{
    private EditText edtnomeanun,edtemailanun,edttelefoneanun,edtCnpj;
    private TextView txtEditarNome,txtEditarTelefone;
    // recuperar estancia do usuario
    private FirebaseAuth minhaauth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    // recupera referencia  do banco
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(minhaauth.getCurrentUser().getUid());
    private Button btnSalvarAlteracoes;
    private boolean tellbol = false;
    private boolean nomebol = false;
    private ProgressBar prgsalvar;
    @Override
    protected void onStart()
    {
        super.onStart();
        firebase.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                edtemailanun.setText(dataSnapshot.child("email").getValue().toString());
                edtnomeanun.setText(dataSnapshot.child("nome").getValue().toString());
                edttelefoneanun.setText(dataSnapshot.child("telefone").getValue().toString());
                edtCnpj.setText(dataSnapshot.child("cnpj").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta_anunciante);
        edtnomeanun = (EditText) findViewById(R.id.edtNomeMCA);
        edtemailanun = (EditText) findViewById(R.id.edtEmailMCA);
        edttelefoneanun = (EditText) findViewById(R.id.edtTelefoneMCA);
        edtCnpj = (EditText) findViewById(R.id.edtCnpjMCA);
        edtemailanun.setEnabled(false);
        edtnomeanun.setEnabled(false);
        edttelefoneanun.setEnabled(false);
        edtCnpj.setEnabled(false);
        btnSalvarAlteracoes =(Button) findViewById(R.id.btnAlterarDadosMCA);
        prgsalvar = (ProgressBar)findViewById(R.id.prgbSalvarAlt_MCA);
        txtEditarNome =(TextView) findViewById(R.id.txtvEditarNome_MCA);
        txtEditarTelefone =(TextView) findViewById(R.id.txtvEditarTelefone_MCA);
        TelefoneValidar validarTelefone = new TelefoneValidar(new WeakReference<EditText>(edttelefoneanun));
        edttelefoneanun.addTextChangedListener(validarTelefone);
        txtEditarTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edttelefoneanun.setEnabled(true);
                tellbol=true;
                Log.v("OI", String.valueOf(edttelefoneanun.length()));
            }
        });
        txtEditarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtnomeanun.setEnabled(true);
                nomebol=true;
            }
        });

        btnSalvarAlteracoes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                verificartiposalvar();
            }
        });
    }
    private void verificartiposalvar()
    {
        if (nomebol == true && tellbol == false)
        {
            prgsalvar.setVisibility(View.VISIBLE);
            salvarNome();
            prgsalvar.setVisibility(View.INVISIBLE);
            finish();
        }
        else if (nomebol ==false && tellbol == true)
        {
            prgsalvar.setVisibility(View.VISIBLE);
            salvarTel();
            prgsalvar.setVisibility(View.INVISIBLE);
            finish();
        }
        else if (nomebol == true && tellbol == true)
        {
            prgsalvar.setVisibility(View.VISIBLE);
            salvarNome();
            salvarTel();
            prgsalvar.setVisibility(View.INVISIBLE);
            finish();
        }

    }
    private void salvarNome()
    {
        if (!edtnomeanun.getText().toString().isEmpty())
        {
            firebase.child("nome").setValue(edtnomeanun.getText().toString()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Erro nas alterações de nome!",Toast.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Toast.makeText(getApplicationContext(),"Alteração comcluida com sucesso!",Toast.LENGTH_LONG).show();
                }
            });
        }
        else Toast.makeText(getApplicationContext(),"O campo Nome não pode ficar vazio!",Toast.LENGTH_LONG).show();
    }
    private void salvarTel()
    {
        if (tellbol == true)
        {
            if (!edttelefoneanun.getText().toString().isEmpty())
            {
                if (edttelefoneanun.getText().length() >= 15)
                {
                    firebase.child("telefone").setValue(edttelefoneanun.getText().toString()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Erro nas alterações de Telefone!",Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(getApplicationContext(),"Alteração comcluida com sucesso!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else Toast.makeText(getApplicationContext(),"Numero de Telefone Invalido!",Toast.LENGTH_LONG).show();
             }
            else Toast.makeText(getApplicationContext(),"Campo Telefone não pode ficar vazio",Toast.LENGTH_LONG).show();
        }
    }
}
