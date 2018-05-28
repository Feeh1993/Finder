package com.example.fernandosilveira.promotions.Activity.Consumidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Geral.Login;
import com.example.fernandosilveira.promotions.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.R;
import com.example.fernandosilveira.promotions.Service.Geofence.ServicoLocalConsumidor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
public class MinhaContaConsumidor extends AppCompatActivity
{
    private EditText edtnomecli;
    private EditText edtemailcli;
    private EditText edttelefonecli;
    private FirebaseAuth minhaauth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
    private Button btnSalvarAlteracoes,btnEditarNome,btnEditarTelefone;
    private boolean tellbol = false;
    private boolean nomebol = false;
    private ProgressBar prgsalvar;
    private AlertDialog alerta;
    @Override
    protected void onStart()
    {
        super.onStart();
        firebase.child("usuarios").child(ConfiguracaoFirebase.getUID()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                edtemailcli.setText(dataSnapshot.child("email").getValue().toString());
                edtnomecli.setText(dataSnapshot.child("nome").getValue().toString());
                edttelefonecli.setText(dataSnapshot.child("telefone").getValue().toString());
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
        setContentView(R.layout.activity_minha_conta_consumidor);
        edtnomecli = (EditText) findViewById(R.id.edtNomeMCC);
        edtemailcli = (EditText) findViewById(R.id.edtEmailMCC);
        edttelefonecli = (EditText) findViewById(R.id.edtTelefoneMCC);
        edtemailcli.setEnabled(false);
        edtnomecli.setEnabled(false);
        edttelefonecli.setEnabled(false);
        btnSalvarAlteracoes =(Button) findViewById(R.id.btnCriarContaConsumidor);
        prgsalvar = (ProgressBar)findViewById(R.id.prgbSalvarAlt_MCU);
        btnEditarNome =(Button) findViewById(R.id.btnEditarNome_MCU);
        btnEditarTelefone =(Button) findViewById(R.id.btnEditarTelefone_MCU);
        TelefoneValidar validarTelefone = new TelefoneValidar(new WeakReference<EditText>(edttelefonecli));
        edttelefonecli.addTextChangedListener(validarTelefone);
        btnEditarTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edttelefonecli.setEnabled(true);
                tellbol=true;
                Log.v("OI", String.valueOf(edttelefonecli.length()));
            }
        });
        btnEditarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtnomecli.setEnabled(true);
                nomebol=true;
            }
        });

        btnSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        }
        else if (nomebol ==false && tellbol == true)
        {
            prgsalvar.setVisibility(View.VISIBLE);
            salvarTel();
            prgsalvar.setVisibility(View.INVISIBLE);
        }
    }
    private void salvarNome()
    {
         if (!edtnomecli.getText().toString().isEmpty())
         {
             firebase.child("nome").setValue(edtnomecli.getText().toString()).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Toast.makeText(getApplicationContext(),"Erro nas alterações de nome!",Toast.LENGTH_LONG).show();
                 }
             }).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     finish();
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
            if (!edttelefonecli.getText().toString().isEmpty())
            {
                if (edttelefonecli.getText().length() >= 15)
                {
                    firebase.child("usuarios").child(minhaauth.getCurrentUser().getUid()).child("telefone").setValue(edttelefonecli.getText().toString()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Erro nas alterações de Telefone!",Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            Toast.makeText(getApplicationContext(),"Alteração comcluida com sucesso!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else Toast.makeText(getApplicationContext(),"Numero de Telefone Invalido!",Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(getApplicationContext(),"Campo Telefone não pode ficar vazio",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_deletar_conta, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menudeletarconta:
                deletarconta();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void deletarconta()
    {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Excluir conta Finder");
            builder.setMessage("Tem certeza que voce quer excluir sua conta?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface arg0, int arg1)
                {
                    deletardados();
                }
            });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
            alerta = builder.create();
            alerta.show();
    }

    private void deletardados()
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Excluir Permanentemente?");
        builder.setMessage("Lembre-se ,todos os seus dados serão excluidos e voce perdera todas as suas promoções... ");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                      final FirebaseUser  user =  minhaauth.getCurrentUser();
                        final String meuuid = user.getUid();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                              firebase.child("usuarios").child(meuuid).setValue(null);
                              firebase.child("destaques").child(meuuid).setValue(null);
                              firebase.child("classificacaouser").child(meuuid).setValue(null);
                              DeslogarUser();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        alerta = builder.create();
        alerta.show();
    }
    public void DeslogarUser()
    {
        //busca a instancia do usuario e desloga o usuario
        FirebaseAuth.getInstance().signOut();
        stopService(new Intent(getApplicationContext(), ServicoLocalConsumidor.class));
        Toast.makeText(getApplicationContext(),"Conta excluida com sucesso!",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
        finish();
    }
}