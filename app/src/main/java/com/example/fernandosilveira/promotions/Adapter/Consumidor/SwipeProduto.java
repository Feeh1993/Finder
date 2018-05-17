package com.example.fernandosilveira.promotions.Adapter.Consumidor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fernandosilveira.promotions.R;

/**
 * Created by Fernando Silveira on 25/01/2018.
 */

public class SwipeProduto extends RecyclerView.ViewHolder
{

    public LinearLayout regularLayout;
    public LinearLayout swipeLayout;

    public TextView nome,validade,preco;
    public ImageView imagem,star1,star2,star3,star4,star5;
    public int res = 0;


    public SwipeProduto(View view)
    {
        super(view);
        regularLayout = (LinearLayout) view.findViewById(R.id.linear_swipe);
        swipeLayout = (LinearLayout) view.findViewById(R.id.swipeLayout);
        preco = (TextView) view.findViewById(R.id.txtprecoInclude_swipe);
        validade = (TextView) view.findViewById(R.id.txtvalidadeInclude_swipe);
        nome = (TextView) view.findViewById(R.id.txtNomeProd_swipe);

        imagem = (ImageView) view.findViewById(R.id.imgInclude_swipe);
        star1 = (ImageView) view.findViewById(R.id.img1_include_swipe);
        star2 = (ImageView) view.findViewById(R.id.img2_include_swipe);
        star3 = (ImageView) view.findViewById(R.id.img3_include_swipe);
        star4 = (ImageView) view.findViewById(R.id.img4_include_swipe);
        star5 = (ImageView) view.findViewById(R.id.img5_include_swipe);

    }
}