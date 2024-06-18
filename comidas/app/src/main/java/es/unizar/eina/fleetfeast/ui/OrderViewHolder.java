package es.unizar.eina.fleetfeast.ui;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private final TextView mOrderItemView;

    /**
     * Constructor para OrderViewHolder.
     * Inicializa los componentes de la vista y establece el listener del menú contextual.
     * @param itemView La vista de un solo elemento de pedido.
     */
    private OrderViewHolder(View itemView) {
        super(itemView);
        mOrderItemView = itemView.findViewById(R.id.textView);

        itemView.setOnCreateContextMenuListener(this);
    }

    /**
     * Vincula los datos a la vista.
     * @param text El texto que se mostrará en la vista.
     */
    public void bind(String text) {
        mOrderItemView.setText(text);
    }

    /**
     * Crea un nuevo OrderViewHolder.
     * @param parent El ViewGroup padre.
     * @return El nuevo OrderViewHolder.
     */
    static OrderViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_plate, parent, false);
        return new OrderViewHolder(view);
    }

    /**
     * Maneja la creación del menú contextual.
     * Agrega opciones para las acciones de eliminar y editar.
     * @param menu     El menú contextual.
     * @param v        La vista.
     * @param menuInfo La información del menú contextual.
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, ListaPedidos.DELETE_ID, Menu.NONE, R.string.menu_delete_order);
        menu.add(Menu.NONE, ListaPedidos.EDIT_ID, Menu.NONE, R.string.menu_edit_order);
        menu.add(Menu.NONE, ListaPedidos.SEND_WAS_ID, Menu.NONE, "Send WhatsApp");
        menu.add(Menu.NONE, ListaPedidos.SEND_SMS_ID, Menu.NONE, "Send SMS");
    }
}
