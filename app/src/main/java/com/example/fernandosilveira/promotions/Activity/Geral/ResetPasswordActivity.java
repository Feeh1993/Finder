package com.example.fernandosilveira.promotions.Activity.Geral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

public class ResetPasswordActivity extends Activity
{
    private EditText edtEmailRecuperar;
    private Button btnRecuperarSenha;
    private FirebaseAuth autenticar = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private AVLoadingIndicatorView progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        edtEmailRecuperar = (EditText) findViewById(R.id.edtEmailRecuperar);
        btnRecuperarSenha = (Button) findViewById(R.id.btnRecuperarSenha);
        progressBar = (AVLoadingIndicatorView) findViewById(R.id.progressBarReset);
        btnRecuperarSenha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = edtEmailRecuperar.getText().toString().trim();
                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplication(), "Digite seu e-mail cadastrado\n", Toast.LENGTH_SHORT).show();
                    return;
                }
                startAnim();
                autenticar.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(ResetPasswordActivity.this, "Foi Enviado instruções para redefinir sua senha!\n Verifique sua caixa de e-mail !", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), Login.class);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

                                } else
                                {
                                    Toast.makeText(ResetPasswordActivity.this, "Falha ao enviar email de recuperação !\n", Toast.LENGTH_SHORT).show();
                                }
                                stopAnim();
                            }
                        });
            }
        });
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
}