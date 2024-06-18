package es.unizar.eina.fleetfeast.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.unizar.eina.fleetfeast.database.OrderDetails;
import es.unizar.eina.fleetfeast.database.Plate;
import es.unizar.eina.fleetfeast.database.PlateDao;
import es.unizar.eina.fleetfeast.database.PlateDao_Impl;
import es.unizar.eina.fleetfeast.database.PlateRepository;

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
 * Utiliza una clase interna `NoteDiff` que implementa la interfaz
 * `DiffUtil.ItemCallback` para comparar elementos y determinar si son los
 * mismos o contienen los mismos contenidos.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class OrderDetailsListAdapter extends ListAdapter<OrderDetails, OrderDetailsViewHolder> {
    private int position;
    PlateViewModel mPlateViewModel;
    OrderDetailsViewModel mOrderDetailsViewMolder;
    /**
     * Constructor de la clase `OrderListAdapter`.
     *
     * @param diffCallback El objeto que implementa la lógica de comparación
     *                     de diferencias entre elementos.
     */
    public OrderDetailsListAdapter(@NonNull DiffUtil.ItemCallback<OrderDetails> diffCallback,
                                   PlateViewModel mPlateViewModel,
                                   OrderDetailsViewModel mOrderDetailsViewMolder) {
        super(diffCallback);
        this.mPlateViewModel = mPlateViewModel;
        this.mOrderDetailsViewMolder = mOrderDetailsViewMolder;
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
    public OrderDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return OrderDetailsViewHolder.create(parent);
    }


    /**
     * Obtiene el plato actual.
     * @return El plato actual.
     */
    public OrderDetails getCurrent() {
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
    public void onBindViewHolder(OrderDetailsViewHolder holder, int position) {
        OrderDetails current = getItem(position);

        Plate plate = mPlateViewModel.getPlateById(current.getPlateId());
        System.out.println("PLATO: "+plate);
        holder.bind_name(plate.getName());
        holder.bind_quantity(""+current.getQuantity());
        holder.bind_price(current.getPrize()+"€");

        // Establece un clic largo en la vista de elementos para realizar una acción cuando se mantiene presionado.
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        // Establece un clic en el botón de añadir para incrementar la cantidad de raciones.
        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = current.getQuantity();
                if (quantity < 99) {
                    current.setQuantity(quantity + 1);
                    holder.bind_quantity("" + current.getQuantity());
                    mOrderDetailsViewMolder.update(current);
                }
            }
        });

        // Establece un clic en el botón de restar para decrementar la cantidad de raciones.
        holder.mRestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = current.getQuantity();
                if (quantity > 0) {
                    current.setQuantity(quantity - 1);
                    holder.bind_quantity("" + current.getQuantity());
                    mOrderDetailsViewMolder.update(current);
                }
            }
        });
    }

    /**
     * Clase interna que implementa la interfaz `DiffUtil.ItemCallback`
     * para comparar elementos.
     */
    static class OrderDetailsDiff extends DiffUtil.ItemCallback<OrderDetails> {

        /**
         * Compara si los elementos son los mismos.
         *
         * @param oldItem El antiguo elemento.
         * @param newItem El nuevo elemento.
         * @return `true` si los elementos son los mismos,
         * `false` de lo contrario.
         */
        @Override
        public boolean areItemsTheSame(@NonNull OrderDetails oldItem,
                                       @NonNull OrderDetails newItem) {
            return oldItem.getOrderId() == newItem.getOrderId() && oldItem.getPlateId() == newItem.getPlateId();
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
        public boolean areContentsTheSame(@NonNull OrderDetails oldItem,
                                          @NonNull OrderDetails newItem) {
            // Solo nos preocupamos por las diferencias en la representación visual, es decir, cambios en el nombre.
            return oldItem.getQuantity() == newItem.getQuantity();
        }
    }
}
