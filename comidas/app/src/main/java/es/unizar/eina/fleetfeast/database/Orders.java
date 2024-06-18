package es.unizar.eina.fleetfeast.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase anotada como entidad que representa un pedido y que consta de nombre
 * de cliente, teléfono, fecha y hora y selección de platos
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
@Entity(tableName = "orders")
public class  Orders{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "phone")
    private long phone;

    @NonNull
    @ColumnInfo(name = "date")
    // uuuu-MM-dd'T'HH:mm
    private String date;

    @NonNull
    @ColumnInfo(name = "state")
    private String state;


    /**
     * Constructor de la clase Orders.
     *
     * @param name  Nombre del cliente del pedido.
     * @param phone Teléfono del cliente del pedido.
     * @param date  Fecha y hora de recogida del pedido.
     *              Formato: uuuu-MM-dd  HH:mm
     * @param state Estado del pedido.
     */
    public Orders(@NonNull String name, @NonNull long phone, @NonNull String date, @NonNull String state) {
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.state = state;
    }


    /**
     * Obtiene el identificador del pedido.
     *
     * @return El identificador del pedido.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Permite actualizar el identificador del pedido.
     *
     * @param id El nuevo identificador del pedido.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Permite actualizar el estado del pedido.
     *
     * @param state El nuevo estado del pedido.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Obtiene el nombre del cliente del pedido.
     *
     * @return El nombre del cliente.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Obtiene el teléfono del cliente del pedido.
     *
     * @return El teléfono del cliente.
     */
    public long getPhone() {
        return this.phone;
    }

    /**
     * Obtiene la fecha y hora de recogida del pedido.
     *
     * @return La fecha y hora de recogida.
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Obtiene el estado del pedido.
     *
     * @return El estado del pedido.
     */
    public String getState() {
        return this.state;
    }
}
