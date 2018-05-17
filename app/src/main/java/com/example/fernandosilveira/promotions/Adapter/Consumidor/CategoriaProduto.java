package com.example.fernandosilveira.promotions.Adapter.Consumidor;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fernandosilveira.promotions.R;

/**
 * Created by Fernando Silveira on 29/01/2018.
 */

public class CategoriaProduto extends RecyclerView.ViewHolder
{

    public LinearLayout regularLayout;
    public SparseBooleanArray array=new SparseBooleanArray();

    public CheckBox check_categoria;
    public ImageView icone;

    public CategoriaProduto(View view)
    {
        super(view);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                array.put(getAdapterPosition(),true);
            }
        });
        regularLayout = (LinearLayout) view.findViewById(R.id.linear_cat);
        check_categoria = (CheckBox) view.findViewById(R.id.checkbox_categoria);
        icone = (ImageView) view.findViewById(R.id.imgInclude_adapter);

    }
}
