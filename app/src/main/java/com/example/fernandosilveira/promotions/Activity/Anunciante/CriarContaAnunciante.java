package com.example.fernandosilveira.promotions.Activity.Anunciante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Geral.TermosUso;
import com.example.fernandosilveira.promotions.Activity.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Activity.Validacao.ValidarCnpj;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Maps.Local;
import com.example.fernandosilveira.promotions.Model.Anunciante_Mod;
import com.example.fernandosilveira.promotions.Model.CategoriaProd;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.lang.ref.WeakReference;

public class CriarContaAnunciante extends Activity
{
    private EditText edtNomeAnunc,edtEmailAnunc,edtSenhaAnunc,edtCnpj,edtTelefone;
    private Button btnCriarContaAnunc;
    private TextView TermosdeUso;
    private FirebaseAuth autenticacao;
    private ProgressBar progressBar;
    private Anunciante_Mod anuncianteMod;
    private CategoriaProd categoriaProd;
    private ValidarCnpj validarCnpj;
    private TextWatcher cnpjMask;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta_anunciante);
        edtEmailAnunc = (EditText) findViewById(R.id.edtEmailCA);
        edtTelefone = (EditText) findViewById(R.id.edtTelefoneCA);
        edtCnpj = (EditText) findViewById(R.id.edtCnpjCA);
        edtNomeAnunc = (EditText) findViewById(R.id.edtNomeCA);
        edtSenhaAnunc = (EditText) findViewById(R.id.edtSenhaCA);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCA);
        TermosdeUso = (TextView) findViewById(R.id.txtTermosdeUso);
        btnCriarContaAnunc = (Button) findViewById(R.id.btnCCAnunciante);
        TermosdeUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermosUso.class);
                startActivity(intent);
            }
        });
//mascara cnpj
        cnpjMask = validarCnpj.inserir(edtCnpj);
        edtCnpj.addTextChangedListener(cnpjMask);
        TelefoneValidar validarTelefone = new TelefoneValidar(new WeakReference<EditText>(edtTelefone));
//mascara telefone
        edtTelefone.addTextChangedListener(validarTelefone);

        btnCriarContaAnunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nome = edtNomeAnunc.getText().toString();
                String email = edtEmailAnunc.getText().toString();
                final String senha = edtSenhaAnunc.getText().toString();
                final String cnpj = edtCnpj.getText().toString();
                final String telefone = edtTelefone.getText().toString();
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
                else if (TextUtils.isEmpty(cnpj))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Cnpj!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(nome))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Nome!", Toast.LENGTH_SHORT).show();
                    return;
                }else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    anuncianteMod = new Anunciante_Mod();
                    anuncianteMod.setNome(edtNomeAnunc.getText().toString());
                    anuncianteMod.setEmail(edtEmailAnunc.getText().toString());
                    anuncianteMod.setSenha(edtSenhaAnunc.getText().toString());
                    anuncianteMod.setCnpj(edtCnpj.getText().toString());
                    anuncianteMod.setTelefone(edtTelefone.getText().toString());
                    cadastrarAnunciante();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    //cadastra anuncio após realizar validação
    private void cadastrarAnunciante()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                anuncianteMod.getEmail(),
                anuncianteMod.getSenha()
        ).addOnCompleteListener(CriarContaAnunciante.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(CriarContaAnunciante.this, "Sucesso ao cadastrar anuncianteMod", Toast.LENGTH_LONG).show();
                    anuncianteMod.setId(ConfiguracaoFirebase.getUID());
                    anuncianteMod.salvar();
                    Intent intent = new Intent(CriarContaAnunciante.this, Local.class);
                    startActivity( intent );
                    finish();
                }
                else
                {
                    String erro = "";
                    try {
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
                    Toast.makeText(CriarContaAnunciante.this, "Erro ao cadastrar Anunciante_Mod: " + erro, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

