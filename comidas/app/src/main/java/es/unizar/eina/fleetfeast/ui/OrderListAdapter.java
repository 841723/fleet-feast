package es.unizar.eina.fleetfeast.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.unizar.eina.fleetfeast.database.Orders;
import es.unizar.eina.fleetfeast.database.Plate;

/**
 * Adaptador para la lista de pedidos en la interfaz de usuario.
 *
 * Este adaptador se utiliza para vincular datos de la lista de pedidos a la
 * interfaz de usuario mediante un patrón de diseño de vista de lista. Utiliza
 * un patrón de difusión para optimizar las actualizaciones en la lista.
 *
 * La clase contiene métodos para gestionar la creación y actualización de las
 * vistas de elementos de la lista, y proporciona un método para obtener el
 * pedido actualmente seleccionado en la lista.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class OrderListAdapter extends ListAdapter<Orders, OrderViewHolder> {
    private int position;

    /**
     * Constructor de la clase `OrderListAdapter`.
     *
     * @param diffCallback El objeto que implementa la lógica de comparación
     *                     de diferencias entre elementos.
     */
    public OrderListAdapter(@NonNull DiffUtil.ItemCallback<Orders> diffCallback) {
        super(diffCallback);
    }

    /**
     * Obtiene la posición actual en la lista.
     *
     * @return La posición actual en la lista.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Establece la posición actual en la lista.
     *
     * @param position La nueva posición en la lista.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Crea una nueva instancia de `OrderViewHolder` para cada
     * elemento de la lista.
     *
     * @param parent   El grupo al que pertenece la vista de elementos.
     * @param viewType El tipo de vista.
     * @return Una nueva instancia de `OrderViewHolder`.
     */
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return OrderViewHolder.create(parent);
    }

    /**
     * Obtiene el plato actual.
     * @return El plato actual.
     */
    public Orders getCurrent() {
        return getItem(getPosition());
    }

    /**
     * Vincula los datos del pedido actual a la vista de elementos
     * correspondiente.
     *
     * @param holder   El `OrderViewHolder` que contiene la vista de elementos.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Orders current = getItem(position);
        holder.bind(current.getName());

        // Establece un clic largo en la vista de elementos para realizar una acción cuando se mantiene presionado.
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }

    /**
     * Clase interna que implementa la interfaz `DiffUtil.ItemCallback`
     * para comparar elementos.
     */
    static class OrderDiff extends DiffUtil.ItemCallback<Orders> {
        /**
         * Compara si los elementos son los mismos.
         *
         * @param oldItem El antiguo elemento.
         * @param newItem El nuevo elemento.
         * @return `true` si los elementos son los mismos,
         * `false` de lo contrario.
         */
        @Override
        public boolean areItemsTheSame(@NonNull Orders oldItem,
                                       @NonNull Orders newItem) {
            return oldItem.getId() == newItem.getId();
        }

        /**
         * Compara si los contenidos de los elementos son los mismos.
         *
         * @param oldItem El antiguo elemento.
         * @param newItem El nuevo elemento.
         * @return `true` si los contenidos son los mismos,
         * `false` de lo contrario.
         */
        @Override
        public boolean areContentsTheSame(@NonNull Orders oldItem,
                                          @NonNull Orders newItem) {
            // Solo nos preocupamos por las diferencias en la representación visual, es decir, cambios en el nombre.
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
