package com.example.fernandosilveira.promotions.Activity.Geral;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Anunciante.CriarContaAnunciante;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Helper.PermissionsUtils;
import com.example.fernandosilveira.promotions.Model.Consumidor;
import com.example.fernandosilveira.promotions.R;
import com.example.fernandosilveira.promotions.Validacao.ValidarCampos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class Login extends Activity
{
    private Consumidor consumidor;
    private FirebaseAuth autenticacao;
    private AVLoadingIndicatorView progressBar;
    private TextView esqueceusenha,Termosuso,semcadastro;
    private Button btnLogin,btnCriarConta;
    private EditText edtEmailLogin, edtSenhaLogin;
    private FloatingActionButton fab;
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
        esqueceusenha = (TextView) findViewById(R.id.txtEsqueceuSenha);
        semcadastro = (TextView) findViewById(R.id.txtSemCadastro_Login);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Termosuso = (TextView) findViewById(R.id.txtTermosdeUso);
        progressBar = (AVLoadingIndicatorView) findViewById(R.id.avi);

        PermissionsUtils.ActivePermissions(this,permissoes,1);


        fab.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view)
            {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, fab, fab.getTransitionName());
                startActivity(new Intent(Login.this, EscolhaCadastro.class), options.toBundle());
            }
        });
        esqueceusenha.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v)
            {
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                Intent i2 = new Intent(Login.this,ResetPasswordActivity.class);
                startActivity(i2, oc2.toBundle());
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = edtEmailLogin.getText().toString();
                final String senha = edtSenhaLogin.getText().toString();
                startAnim();
                ArrayList resval = validacao(email,senha);
                for (int i = 0; i < resval.size(); i++)
                {
                    if (resval.get(i).equals("Email Vazio"))
                    {
                        stopAnim();
                        edtEmailLogin.setError("Digite seu Email!");
                    }
                    else if (resval.get(i).equals("Senha Vazia"))
                    {
                        stopAnim();
                        edtSenhaLogin.setError("Digite sua Senha!");
                    }
                    else if (resval.get(i).equals("Valida Login"))
                    {
                        consumidor = new Consumidor();
                        consumidor.setEmail( email );
                        consumidor.setSenha( senha );
                        validarLogin();
                    }
                    else if (resval.get(i).equals("Email Erro"))
                    {
                        stopAnim();
                        semcadastro.setText("Email com formato incorreto!");
                    }
                }
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
                    Toast.makeText(Login.this, "Login Efetuado com Sucesso!", Toast.LENGTH_LONG ).show();
                    abrirTelaPrincipal();
                }else
                    {
                       // Toast.makeText(Login.this, "Erro ao fazer login,verifique seu email e senha e tente novamente", Toast.LENGTH_LONG ).show();
                        semcadastro.setText("Não Possui Cadastro? \n Clique em + e crie uma conta Finder!");
  //                      progressBar.setVisibility(View.GONE);
                        stopAnim();
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
    void startAnim()
    {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.show();
        // or avi.smoothToShow();
    }

    void stopAnim()
    {
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.hide();
        // or avi.smoothToHide();
    }
    public ArrayList validacao(String email , String senha)
    {
        ArrayList resultado = new ArrayList();
        if (TextUtils.isEmpty(email))
        {
            resultado.add("Email Vazio");

        }
        if (TextUtils.isEmpty(senha))
        {
            resultado.add("Senha Vazia");
        }
        ValidarCampos validarCampos = new ValidarCampos();
        String resemail = validarCampos.validarEmail(email);
       // String ressenha = validarCampos.validarSenha(senha);
        if (resemail == "OK" )
        {
            resultado.add("Valida Login");
        }
        else if (resemail == "ERRO")
        {
            resultado.add("Email Erro");

        }
        return resultado;

    }

}

