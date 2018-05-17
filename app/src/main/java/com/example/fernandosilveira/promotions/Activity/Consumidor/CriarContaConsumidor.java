package com.example.fernandosilveira.promotions.Activity.Consumidor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Geral.Loading;
import com.example.fernandosilveira.promotions.Activity.Geral.TermosUso;
import com.example.fernandosilveira.promotions.Activity.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.Consumidor;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.lang.ref.WeakReference;

public class CriarContaConsumidor extends Activity
{
    private EditText edtNomeCriar, edtTelefoneCriar, edtEmailCriar, edtSenhaCriar;
    private Button criarnovaconta;
    private Consumidor consumidor;
    private ProgressBar progressBarCC;
    private FirebaseAuth autenticacao;
    private TextView TermosUso;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta_consumidor);

        progressBarCC = (ProgressBar) findViewById(R.id.progressBarCC);
        edtNomeCriar = (EditText) findViewById(R.id.edtNomeCC);
        edtSenhaCriar = (EditText) findViewById(R.id.edtSenhaCC);
        edtEmailCriar = (EditText) findViewById(R.id.edtEmailCC);
        edtTelefoneCriar = (EditText) findViewById(R.id.edtTelefoneCC);
        criarnovaconta = (Button) findViewById(R.id.btnCriarContaConsumidor);
        TermosUso = (TextView) findViewById(R.id.txtTermosdeUso);

        TelefoneValidar validarTelefone = new TelefoneValidar(new WeakReference<EditText>(edtTelefoneCriar));
        edtTelefoneCriar.addTextChangedListener(validarTelefone);

        TermosUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CriarContaConsumidor.this, TermosUso.class);
                startActivity( intent );
            }
        });
        criarnovaconta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String nome = edtNomeCriar.getText().toString();
                String email = edtEmailCriar.getText().toString();
                final String senha = edtSenhaCriar.getText().toString();
                String telefone = edtTelefoneCriar.getText().toString();
                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(telefone))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Telefone!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(senha))
                {
                    Toast.makeText(getApplicationContext(), "Digite sua senha!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(nome))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Nome!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    progressBarCC.setVisibility(View.VISIBLE);
                    consumidor = new Consumidor();
                    consumidor.setNome(edtNomeCriar.getText().toString());
                    consumidor.setEmail(edtEmailCriar.getText().toString());
                    consumidor.setSenha(edtSenhaCriar.getText().toString());
                    consumidor.setTelefone(edtTelefoneCriar.getText().toString());
                    cadastrarUsuario();
                }
            }
        });
    }
    private void cadastrarUsuario()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                consumidor.getEmail(),
                consumidor.getSenha()
        ).addOnCompleteListener(CriarContaConsumidor.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    consumidor.setIduser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    consumidor.salvar();
                    progressBarCC.setVisibility(View.INVISIBLE);
                    Toast.makeText(CriarContaConsumidor.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CriarContaConsumidor.this, Loading.class);
                    startActivity( intent );
                    finish();

                } else
                    {
                        String erro = "";
                        try
                        {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e)
                        {
                            erro = "Escolha uma senha que contenha, letras e números.";
                        } catch (FirebaseAuthInvalidCredentialsException e)
                        {
                            erro = "Email indicado não é válido.";
                        } catch (FirebaseAuthUserCollisionException e)
                        {
                            erro = "Já existe uma conta com esse e-mail.";
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Toast.makeText(CriarContaConsumidor.this, "Erro ao cadastrar usuário: " + erro, Toast.LENGTH_LONG).show();
                        progressBarCC.setVisibility(View.INVISIBLE);
                    }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
