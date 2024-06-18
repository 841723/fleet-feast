package es.unizar.eina.fleetfeast.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.unizar.eina.fleetfeast.database.OrderDetails;
import es.unizar.eina.fleetfeast.database.Plate;

/**
 * Adaptador de lista para la entidad Plate cuando se añade a un pedido.
 * Gestiona la lista de platos en RecyclerView y calcula las
 * diferencias entre platos antiguos y nuevos para actualizaciones eficientes.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class PlateListAdapterParaAnadir extends ListAdapter<Plate, PlateViewHolderParaAnadir> {
    private int position;
    private long orderid;
    OrderDetailsViewModel mOrderDetailsViewMolder;

    /**
     * Obtiene la posición actual.
     * @return La posición actual.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Establece la posición actual.
     * @param position La nueva posición.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Constructor para PlateListAdapter.
     * @param diffCallback El callback para calcular las diferencias.
     */
    public PlateListAdapterParaAnadir(@NonNull DiffUtil.ItemCallback<Plate> diffCallback,
                                      OrderDetailsViewModel mOrderDetailsViewMolder,
                                      long orderid) {
        super(diffCallback);
        this.mOrderDetailsViewMolder = mOrderDetailsViewMolder;
        this.orderid = orderid;
    }

    /**
     * Crea un nuevo PlateViewHolder.
     * @param parent El ViewGroup padre.
     * @param viewType El tipo de vista.
     * @return El nuevo PlateViewHolder.
     */
    @Override
    public PlateViewHolderParaAnadir onCreateViewHolder(ViewGroup parent, int viewType) {
        return PlateViewHolderParaAnadir.create(parent);
    }

    /**
     * Obtiene el plato actual.
     * @return El plato actual.
     */
    public Plate getCurrent() {
        return getItem(getPosition());
    }

    /**
     * Vincula los datos del plato al portador de la vista.
     * @param holder El PlateViewHolder.
     * @param position La posición del plato.
     */
    @Override
    public void onBindViewHolder(PlateViewHolderParaAnadir holder, int position) {
        Plate p = getItem(position);
        holder.bind(p.getName(), p.getCategory());

        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int plateid = p.getId();
                float price = p.getPrize();
                mOrderDetailsViewMolder.insert(new OrderDetails(orderid, plateid, 1,price));
            }
        });
    }

    /**
     * Callback para calcular las diferencias entre platos antiguos y nuevos.
     */
    static class PlateDiff extends DiffUtil.ItemCallback<Plate> {

        /**
         * Comprueba si los elementos son iguales.
         * @param oldItem El plato antiguo.
         * @param newItem El nuevo plato.
         * @return True si los elementos son iguales, false en caso contrario.
         */
        @Override
        public boolean areItemsTheSame(@NonNull Plate oldItem,
                                       @NonNull Plate newItem) {
            return oldItem.getId() == newItem.getId();
        }

        /**
         * Comprueba si el contenido de los elementos es el mismo.
         * @param oldItem El plato antiguo.
         * @param newItem El nuevo plato.
         * @return True si el contenido es el mismo, false en caso contrario.
         */
        @Override
        public boolean areContentsTheSame(@NonNull Plate oldItem, @NonNull Plate newItem) {
            // Solo nos preocupamos por las diferencias en la representación visual, es decir, cambios en el nombre
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
