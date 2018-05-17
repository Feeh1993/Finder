package com.example.fernandosilveira.promotions.Adapter.Consumidor;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Interface.CustomCheckClickListener;
import com.example.fernandosilveira.promotions.Model.Categoria_Mod;
import com.example.fernandosilveira.promotions.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Fernando Silveira on 25/01/2018.
 */
public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaProduto>
{
    private List<Categoria_Mod> catList;
    private Handler handler = new Handler();
    private Context mContext;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private String meuUid = ConfiguracaoFirebase.getUID();
    private ArrayList<String> addDestaques = new ArrayList<>();
    CustomCheckClickListener checkbox;

    public CategoriaAdapter(Context mContext, List<Categoria_Mod> catList, CustomCheckClickListener checkbox)
    {
        this.mContext = mContext;
        this.catList = catList;
        this.checkbox =checkbox;
    }
    @Override
    public CategoriaProduto onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoria_adapter, parent, false);
        final CategoriaProduto mViewHolder = new CategoriaProduto(itemView);
        mViewHolder.check_categoria.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkbox.oncheckClick(mViewHolder.check_categoria,mViewHolder.getAdapterPosition(),mViewHolder.check_categoria.getText().toString());
            }
        });
        return mViewHolder;
    }
    @Override
    public void onBindViewHolder(final CategoriaProduto holder, final int position)
    {
        Categoria_Mod categoriaMod = catList.get(position);
        if(holder.array.get(position))
        {
            holder.check_categoria.setChecked(true);
        }else
            {
                holder.check_categoria.setChecked(false);
            }
            holder.check_categoria.setText(categoriaMod.cat);
            holder.icone.setImageResource(categoriaMod.img);

        ref.child("destaques").child(meuUid).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot dados : dataSnapshot.getChildren())
                {
                    if (holder.check_categoria.getText().toString().contains(dados.getKey()))
                    {
                        Log.i("ADAPTER CAT/",dados.getKey());
                        Log.i("ADAPTER CAT/check",holder.check_categoria.getText().toString());
                        if (dados.getValue().toString().contains("true"))
                        {
                            holder.check_categoria.setChecked(true);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    @Override
    public int getItemCount() {
        return catList.size();
    }
   }


