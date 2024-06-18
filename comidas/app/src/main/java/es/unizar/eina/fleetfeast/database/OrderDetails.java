package es.unizar.eina.fleetfeast.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** Clase anotada como entidad que representa los detalles de un
 *  pedido y que consta de id del pedido, id del plato y cantidad de raciones
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
@Entity(tableName = "orderDetails", primaryKeys = {"orderId", "plateId"})
public class OrderDetails {
    @ColumnInfo(name = "orderId")
    private long orderId;

    @ColumnInfo(name = "plateId")
    private int plateId;

    @NonNull
    @ColumnInfo(name = "quantity")
    private int quantity;

    @NonNull
    @ColumnInfo(name = "prize")
    private float prize;

    /**
     * Constructor de la clase OrderDetails.
     *
     * @param orderId   ID del pedido al que pertenecen los detalles.
     * @param plateId   ID del plato asociado a los detalles.
     * @param quantity  Cantidad de raciones solicitadas.
     * @param prize  Precio de las raciones solicitadas.
     */
    public OrderDetails(@NonNull long orderId, @NonNull int plateId,
                 @NonNull int quantity, @NonNull float prize) {
        this.orderId = orderId;
        this.plateId = plateId;
        this.quantity = quantity;
        this.prize = prize;
    }

    /**
     * Obtiene el ID del pedido asociado a los detalles.
     *
     * @return El ID del pedido.
     */
    public long getOrderId() { return this.orderId;}

    /**
     * Obtiene el ID del plato asociado a los detalles.
     *
     * @return El ID del plato.
     */
    public int getPlateId() { return this.plateId;}

    /**
     * Establece el ID del pedido asociado a los detalles.
     *
     * @param id El nuevo ID del pedido.
     */
    public void setOrderId(int id) { this.orderId = id;}

    /**
     * Establece el ID del plato asociado a los detalles.
     *
     * @param id El nuevo ID del plato.
     */
    public void setPlateId(int id) { this.plateId = id;}

    /**
     * Obtiene la cantidad de raciones solicitadas.
     *
     * @return La cantidad de raciones.
     */
    public int getQuantity() { return this.quantity;}

    /**
     * Obtiene el precio de las raciones solicitadas.
     *
     * @return El precio de las raciones.
     */
    public float getPrize() { return this.prize;}

    /**
     * Modifica la cantidad de raciones solicitadas.
     * @param quantity La nueva cantidad de raciones.
     */
    public void setQuantity(int quantity) { this.quantity = quantity;}

    /**
     * Modifica el precio de las raciones solicitadas.
     * @param prize El nuevo precio de las raciones.
     */
    public void setPrize(float prize) {
        this.prize = prize;
    }
}
