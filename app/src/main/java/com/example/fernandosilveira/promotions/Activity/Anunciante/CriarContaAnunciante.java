package com.example.fernandosilveira.promotions.Activity.Anunciante;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Geral.TermosUso;
import com.example.fernandosilveira.promotions.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Validacao.ValidarCampos;
import com.example.fernandosilveira.promotions.Validacao.ValidarCnpj;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CriarContaAnunciante extends Activity
{
    private EditText edtNomeAnunc,edtEmailAnunc,edtSenhaAnunc,edtCnpj,edtTelefone;
    private Button btnCriarContaAnunc;
    private TextView TermosdeUso;
    private FirebaseAuth autenticacao;
  //  private ProgressBar progressBar;
    private Anunciante_Mod anuncianteMod;
    private CategoriaProd categoriaProd;
    private ValidarCnpj validarCnpj;
    private TextWatcher cnpjMask;
    private AVLoadingIndicatorView progressBar;
    private CardView cvAdd;
    private FloatingActionButton fab;
    private TextView semcadastro;

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
      //  progressBar = (ProgressBar) findViewById(R.id.progressBarCA);
        TermosdeUso = (TextView) findViewById(R.id.txtTermosdeUso);
        semcadastro = (TextView) findViewById(R.id.txtSemCadastro_CA);
        btnCriarContaAnunc = (Button) findViewById(R.id.btnCCAnunciante);
        cvAdd =(CardView) findViewById(R.id.cv_add);
        fab = (FloatingActionButton) findViewById(R.id.fabCA);
        progressBar = (AVLoadingIndicatorView) findViewById(R.id.avi);

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });

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
                startAnim();

                //valida os campos e cria um array list com os erros para eventuais tratamentos
                ArrayList resultado = validacao(telefone,cnpj,email,senha,nome);
                for (int i = 0; i < resultado.size(); i++)
                {

                  if (resultado.get(i).equals("Valida Login"))
                  {
                      if (TextUtils.isEmpty(nome))
                      {
                          stopAnim();
                          edtNomeAnunc.setError("Digite seu Nome!");
                      }
                      else
                      {
                          anuncianteMod = new Anunciante_Mod();
                          anuncianteMod.setNome(edtNomeAnunc.getText().toString());
                          anuncianteMod.setEmail(edtEmailAnunc.getText().toString());
                          anuncianteMod.setSenha(edtSenhaAnunc.getText().toString());
                          anuncianteMod.setCnpj(edtCnpj.getText().toString());
                          anuncianteMod.setTelefone(edtTelefone.getText().toString());
                          cadastrarAnunciante();
                      }
                  }
                  else if (resultado.get(i).equals("Email Erro"))
                  {
                      stopAnim();
                     semcadastro.setText("O email precisa estar no formato user@user.com");
                  }
                  else if (resultado.get(i).equals("Cnpj Erro"))
                  {
                      stopAnim();
                    semcadastro.setText("\n Verifique seu CNPJ e tente novamente!");
                  }
                  else if (resultado.get(i).equals("Senha Erro"))
                  {
                      stopAnim();
                    semcadastro.setText("Sua senha deve: \n Ter entre 8 e 40 caracteres\n" +
                            "Contenha pelo menos um dígito e um caracter especial de [@ # $%! . ].\n" +
                            "Contenha pelo menos um caractere minúsculo e um caractere maiúsculo.\n");
                  }
                  else if (resultado.get(i).equals("Telefone Erro"))
                  {
                      stopAnim();
                      semcadastro.setText("\n Seu Telefone esta em formato incorreto!");
                  }
                }
                if (TextUtils.isEmpty(cnpj) && TextUtils.isEmpty(email) && TextUtils.isEmpty(telefone) && TextUtils.isEmpty(senha))
                {
                    semcadastro.setText("Preencha todos os campos ante de clicar em Criar Conta!");
                }else
                if (TextUtils.isEmpty(email))
                {
                    stopAnim();
                   edtEmailAnunc.setError("Digite seu email!");
                }
                else if (TextUtils.isEmpty(telefone))
                {
                    stopAnim();
                   edtTelefone.setError("Digite seu telefone");
                }
                else if (TextUtils.isEmpty(senha))
                {
                    stopAnim();
                    edtSenhaAnunc.setError("Digite sua senha!");
                }
                else if (TextUtils.isEmpty(cnpj))
                {
                    stopAnim();
                    edtCnpj.setError("Digite seu CNPJ!");
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                CriarContaAnunciante.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
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
    public ArrayList validacao(String telefone,String cnpj,String email , String senha, String nome)
    {
        ArrayList resultado = new ArrayList();
        ValidarCampos validarCampos = new ValidarCampos();
        String resemail = validarCampos.validarEmail(email);
        String ressenha = validarCampos.validarSenha(senha);
        String restelefone = validarCampos.validarTelefone(telefone);
        String rescnpj = validarCampos.validarCNPJ(cnpj);
        if (resemail == "OK" && ressenha == "OK" && rescnpj == "OK" && restelefone == "OK")
        {
            resultado.add("Valida Login");
        }
        else if (resemail == "ERRO")
        {
            resultado.add("Email Erro");
        } else resultado.add("Email OK");

        if (rescnpj == "ERRO")
        {
            resultado.add("Cnpj Erro");
        }else resultado.add("Cnpj OK");

        if (ressenha == "ERRO")
        {
            resultado.add("Senha Erro");
        }else resultado.add("Senha OK");

        if (restelefone == "ERRO")
        {
            resultado.add("Telefone Erro");
        }else resultado.add("Telefone OK");
        return resultado;

    }

}

