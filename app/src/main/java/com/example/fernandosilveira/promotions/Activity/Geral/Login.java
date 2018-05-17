package com.example.fernandosilveira.promotions.Activity.Geral;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Helper.PermissionsUtils;
import com.example.fernandosilveira.promotions.Model.Consumidor;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends Activity
{
    private Consumidor consumidor;
    private FirebaseAuth autenticacao;
    private ProgressBar progressBar;
    private TextView esqueceusenha,Termosuso;
    private Button btnLogin,btnCriarConta;
    private EditText edtEmailLogin, edtSenhaLogin;
    String[] permissoes = new String[]
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        verificarUsuarioLogado();
        setContentView(R.layout.activity_login);
        edtEmailLogin = (EditText) findViewById(R.id.edtEmailLogin);
        edtSenhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);
        esqueceusenha = (TextView) findViewById(R.id.txtEsqueceuSenha);
        btnCriarConta = (Button) findViewById(R.id.btnCriarConta);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        Termosuso = (TextView) findViewById(R.id.txtTermosdeUso);


        PermissionsUtils.ActivePermissions(this,permissoes,1);

        esqueceusenha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(Login.this, ResetPasswordActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = edtEmailLogin.getText().toString();
                final String senha = edtSenhaLogin.getText().toString();
                if (TextUtils.isEmpty(email))
                {
                    //Toast.makeText(getApplicationContext(), "Digite seu Email!", Toast.LENGTH_SHORT).show();
                    edtEmailLogin.setError("Digite seu Email!");
                    return;
                }
                if (TextUtils.isEmpty(senha))
                {
                    //Toast.makeText(getApplicationContext(), "Digite sua Senha!", Toast.LENGTH_SHORT).show();
                    edtSenhaLogin.setError("Digite sua Senha!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                consumidor = new Consumidor();
                consumidor.setEmail( edtEmailLogin.getText().toString() );
                consumidor.setSenha( edtSenhaLogin.getText().toString() );
                validarLogin();
            }
        });
        btnCriarConta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, EscolhaCadastro.class);
                startActivity( intent );
                overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
            }
        });
       Termosuso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, TermosUso.class);
                startActivity( intent );
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults)
        {
            if (result == PackageManager.PERMISSION_DENIED)
            {
                ativeasPermissoes();
                return;
            }
        }
    }
    private void alertAndFinish()
    {
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name).setMessage("Para utilizar este aplicativo, você precisa aceitar as permissões.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void validarLogin()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                consumidor.getEmail(),
                consumidor.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() )
                {
                    abrirTelaPrincipal();
                    // salvar email banco local
//                    LoginDAO dao = new LoginDAO(getBaseContext());
  //                  dao.salvarEmail(0,consumidor.getEmail());
                    Toast.makeText(Login.this, "Login Efetuado com Sucesso!", Toast.LENGTH_LONG ).show();
                }else
                    {
                       // Toast.makeText(Login.this, "Erro ao fazer login,verifique seu email e senha e tente novamente", Toast.LENGTH_LONG ).show();
                        edtEmailLogin.setError("Erro ao fazer login,verifique seu email!");
                        edtSenhaLogin.setError("Erro ao fazer login,verifique sua senha e tente novamente!");
                        progressBar.setVisibility(View.GONE);
                    }
            }
        });
    }
    private void verificarUsuarioLogado()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if( autenticacao.getCurrentUser() != null )
        {
            abrirTelaPrincipal();
        }
    }
    private void abrirTelaPrincipal()
    {
        Intent intent = new Intent(Login.this, Loading.class);
        startActivity( intent );
        finish();
    }
    private void ativeasPermissoes()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar este app, é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

