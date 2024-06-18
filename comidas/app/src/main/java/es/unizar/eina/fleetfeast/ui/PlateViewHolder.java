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
 * Clase PlateViewHolder para el RecyclerView de platos.
 * Gestiona la vista para un solo elemento de plato.
 * Implementa el menú contextual para acciones de eliminar y editar.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
class PlateViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private final TextView mPlateItemView;

    /**
     * Constructor para PlateViewHolder.
     * Inicializa los componentes de la vista y establece el listener
     * del menú contextual.
     * @param itemView La vista para un solo elemento de plato.
     */
    private PlateViewHolder(View itemView) {
        super(itemView);
        mPlateItemView = itemView.findViewById(R.id.textView);

        itemView.setOnCreateContextMenuListener(this);
    }

    /**
     * Vincula los datos a la vista.
     * @param text El texto que se mostrará en la vista.
     */
    public void bind(String text, String category) {
        mPlateItemView.setText(category+" - "+text);
    }

    /**
     * Crea un nuevo PlateViewHolder.
     * @param parent El ViewGroup padre.
     * @return El nuevo PlateViewHolder.
     */
    static PlateViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_plate, parent, false);
        return new PlateViewHolder(view);
    }

    /**
     * Maneja la creación del menú contextual.
     * Agrega opciones para las acciones de eliminar y editar.
     * @param menu     El menú contextual.
     * @param v        La vista.
     * @param menuInfo La información del menú contextual.
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, ListaPlatos.DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, ListaPlatos.EDIT_ID, Menu.NONE, R.string.menu_edit);
    }
}
