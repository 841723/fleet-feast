package es.unizar.eina.fleetfeast.ui;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.unizar.eina.fleetfeast.R;

/**
 * Clase ViewHolder para la RecyclerView de pedidos.
 * Gestiona la vista de un solo elemento de pedido.
 * Implementa el menú contextual para acciones de eliminar y editar.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
    private final TextView mPlateNameItemView;
    public final TextView mQuantityItemView;
    private final TextView mPriceItemView;

    public final Button mAddButton;
    public final Button mRestButton;

    /**
     * Constructor para OrderViewHolder.
     * Inicializa los componentes de la vista y establece el listener del menú contextual.
     *
     * @param itemView La vista de un solo elemento de pedido.
     */
    private OrderDetailsViewHolder(View itemView) {
        super(itemView);

        mPlateNameItemView = itemView.findViewById(R.id.platename);
        mQuantityItemView = itemView.findViewById(R.id.quantity);
        mAddButton = itemView.findViewById(R.id.button_add);
        mRestButton = itemView.findViewById(R.id.button_rest);
        mPriceItemView = itemView.findViewById(R.id.price);

//        itemView.setOnCreateContextMenuListener(this);
    }

    /**
     * Vincula los datos a la vista.
     *
     * @param text El texto que se mostrará en la vista.
     */
    public void bind_name(String text) {
        mPlateNameItemView.setText(text);
    }

    public void bind_quantity(String text) {
        mQuantityItemView.setText(text);
    }

    public void bind_price(String text) {
        mPriceItemView.setText(text);
    }

    /**
     * Crea un nuevo OrderViewHolder.
     *
     * @param parent El ViewGroup padre.
     * @return El nuevo OrderViewHolder.
     */
    static OrderDetailsViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_plate_in_order, parent, false);
        return new OrderDetailsViewHolder(view);
    }
}