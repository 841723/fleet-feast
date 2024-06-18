package es.unizar.eina.fleetfeast.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


/**
 * Data Access Object (DAO) para los detalles de los pedidos.
 *
 * Esta interfaz define los métodos de acceso a la base de datos para la entidad OrderDetails.
 * Incluye operaciones como inserción, actualización, eliminación y consulta.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
@Dao
public interface OrderDetailsDao {

    /**
     * Inserta un nuevo detalle de pedido en la base de datos.
     *
     * @param orderDet El detalle de pedido a insertar.
     * @return El ID del detalle de pedido insertado.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(OrderDetails orderDet);

    /**
     * Actualiza un detalle de pedido existente en la base de datos.
     *
     * @param orderDet El detalle de pedido a actualizar.
     * @return El número de detalles de pedido actualizados (debería ser 1, ya que se espera un ID único).
     */
    @Update
    int update(OrderDetails orderDet);

    /**
     * Elimina un detalle de pedido de la base de datos.
     *
     * @param orderDet El detalle de pedido a eliminar.
     * @return El número de detalles de pedido eliminados (debería ser 1, ya que se espera un ID único).
     */
    @Delete
    int delete(OrderDetails orderDet);

    /**
     * Elimina todos los detalles de pedido de la base de datos.
     */
    @Query("DELETE FROM OrderDetails")
    void deleteAll();


    /**
     * Obtiene todos los detalles de pedido de la base de datos.
     *
     * @return LiveData<List<OrderDetails>> que contiene todos los detalles de pedido.
     */
    @Query("SELECT * FROM OrderDetails")
    LiveData<List<OrderDetails>> getAllOrderDetails();

    /**
     * Obtiene todos los detalles de pedido de un pedido.
     *
     * @param orderId El ID del pedido del que se quieren obtener los detalles.
     * @return LiveData<List<OrderDetails>> que contiene todos los detalles de pedido del pedido.
     */
    @Query("SELECT * FROM OrderDetails WHERE orderId = :orderId")
    LiveData<List<OrderDetails>> getOrdersDetailsByOrderId(long orderId);
}
