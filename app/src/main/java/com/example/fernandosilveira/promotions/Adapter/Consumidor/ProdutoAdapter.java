package com.example.fernandosilveira.promotions.Adapter.Consumidor;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.fernandosilveira.promotions.Interface.CustomItemClickListener;
import com.example.fernandosilveira.promotions.Model.Produto;
import com.example.fernandosilveira.promotions.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Fernando Silveira on 25/01/2018.
 */

public class ProdutoAdapter extends RecyclerView.Adapter<SwipeProduto>
{

    private List<Produto> prodList;
    private List<Produto> itemsPendingRemoval;
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler handler = new Handler();
    private Context mContext;
    HashMap<Produto, Runnable> pendingRunnables = new HashMap<>();
    CustomItemClickListener listener;


    public ProdutoAdapter(Context mContext, List<Produto> prodList, CustomItemClickListener listener)
    {
        this.mContext = mContext;
        this.listener = listener;
        this.prodList = prodList;
        itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public SwipeProduto onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe, parent, false);
        final SwipeProduto mViewHolder = new SwipeProduto(itemView);
        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(SwipeProduto holder, int position)
    {

        Produto produto = prodList.get(position);
//        holder.descricao.setText(produto.descricao);
        holder.preco.setText("R"+produto.preco);
        holder.nome.setText(produto.nome);
        Glide.with(mContext).load(produto.codimg).into(holder.imagem);
        BigDecimal valorExato = new BigDecimal(produto.classificacao).setScale(1, BigDecimal.ROUND_DOWN);
        String inteiro = String.valueOf(valorExato).split("\\.")[0];
        String flutuante = String.valueOf(valorExato).split("\\.")[1];
        classStars(inteiro,flutuante,holder);
        if (produto.diasRestantes.equals("vencido"))
        {
            holder.validade.setText("Anuncio venceu!");
        } else
        {
            holder.validade.setText("Valido por: " + produto.diasRestantes + " dias");
        }
        if (itemsPendingRemoval.contains(prodList))
        {
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return prodList.size();
    }

    private int classStars(String num1, String num2,final SwipeProduto holder)
    {
        int inteiro = Integer.parseInt(num1);
        int res = 0;
        switch (inteiro)
        {
            case 1:
                holder.star1.setImageResource(R.drawable.class_cheio);
                holder.star2.setImageResource(classiTipo(Integer.parseInt(num2),holder));
                break;
            case 2:
                holder.star1.setImageResource(R.drawable.class_cheio);
                holder.star2.setImageResource(R.drawable.class_cheio);
                holder.star3.setImageResource(classiTipo(Integer.parseInt(num2),holder));
                break;
            case 3:
                holder.star1.setImageResource(R.drawable.class_cheio);
                holder.star2.setImageResource(R.drawable.class_cheio);
                holder.star3.setImageResource(R.drawable.class_cheio);
                holder.star4.setImageResource(classiTipo(Integer.parseInt(num2),holder));
                break;
            case 4:
                holder.star1.setImageResource(R.drawable.class_cheio);
                holder.star2.setImageResource(R.drawable.class_cheio);
                holder.star3.setImageResource(R.drawable.class_cheio);
                holder.star4.setImageResource(R.drawable.class_cheio);
                holder.star5.setImageResource(classiTipo(Integer.parseInt(num2),holder));
                break;
            case 5:
                holder.star1.setImageResource(R.drawable.class_cheio);
                holder.star2.setImageResource(R.drawable.class_cheio);
                holder.star3.setImageResource(R.drawable.class_cheio);
                holder.star4.setImageResource(R.drawable.class_cheio);
                holder.star5.setImageResource(R.drawable.class_cheio);
        }
        return res;
    }
    private int classiTipo(int num,final SwipeProduto holder)
    {
        switch (num)
        {
            case 0:
                holder.res = R.drawable.class_vazio;
                break;
            case 1:
                holder.res = R.drawable.class_vazio;
                break;
            case 2:
                holder.res = R.drawable.class_vazio;
                break;
            case 3:
                holder.res = R.drawable.class_metade;
                break;
            case 4:
                holder.res = R.drawable.class_metade;
                break;
            case 5:
                holder.res = R.drawable.class_metade;
                break;
            case 6:
                holder.res = R.drawable.class_metade;
                break;
            case 7:
                holder.res = R.drawable.class_metade;
                break;
            case 8:
                holder.res = R.drawable.class_cheio;
                break;
            case 9:
                holder.res = R.drawable.class_cheio;
        }
        return holder.res;
    }

    public void pendingRemoval(int position)
    {
        final Produto data = prodList.get(position);
        if (!itemsPendingRemoval.contains(data))
        {
            itemsPendingRemoval.add(data);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(prodList.indexOf(data));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }

    public void remove(int position)
    {
        Produto data = prodList.get(position);
        if (itemsPendingRemoval.contains(data))
        {
            itemsPendingRemoval.remove(data);
        }
        if (prodList.contains(data))
        {
            prodList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position)
    {
        Produto data = prodList.get(position);
        return itemsPendingRemoval.contains(data);
    }
}


