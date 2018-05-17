package com.example.fernandosilveira.promotions.DialogFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Adapter.Consumidor.TabAdapterConsumidor;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlterarSenha extends DialogFragment
{
    private EditText mEditText;
    private Button btnAlterar;
    public AlterarSenha()
    {
    }
    public static AlterarSenha novaestancia(String titulo)
    {
        AlterarSenha frag = new AlterarSenha();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_alterarsenha, container);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mEditText = (EditText) view.findViewById(R.id.edtEmailAlterar);
        btnAlterar = (Button)view.findViewById(R.id.btnAlterar);
        getDialog().setTitle("Alteração de email");
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String email = mEditText.getText().toString();
                if (!email.isEmpty())
                {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(),"Um email de redefinição de senha foi enviado em seu email cadastrado!",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getContext(), TabAdapterConsumidor.class));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"email incorreto \n Digite um email válido e tente novamente!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                    {
                        Toast.makeText(getContext(),"Voce Precisa digitar um email antes de continuar!",Toast.LENGTH_LONG).show();
                    }
            }
        });
    }
}