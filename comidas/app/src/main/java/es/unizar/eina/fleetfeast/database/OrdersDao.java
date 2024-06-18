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
 * Definición de un Data Access Object para los pedidos en la base de datos.
 *
 * Este DAO proporciona métodos para realizar operaciones CRUD (Crear, Leer,
 * Actualizar, Eliminar) en la entidad Orders en la base de datos, como la
 * inserción, actualización, eliminación y consulta de pedidos.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
@Dao
public interface OrdersDao {

    /**
     * Inserta un nuevo pedido en la base de datos.
     *
     * @param order El objeto Orders que se va a insertar.
     * @return El identificador del nuevo pedido insertado.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Orders order);

    /**
     * Actualiza un pedido existente en la base de datos.
     *
     * @param order El objeto Orders que se va a actualizar.
     * @return El número de filas afectadas por la actualización.
     */
    @Update
    int update(Orders order);

    /**
     * Elimina un pedido existente de la base de datos.
     *
     * @param order El objeto Orders que se va a eliminar.
     * @return El número de filas afectadas por la eliminación.
     */
    @Delete
    int delete(Orders order);

    /**
     * Elimina todos los pedidos de la base de datos.
     */
    @Query("DELETE FROM Orders")
    void deleteAll();

    /**
     * Obtiene todos los pedidos ordenados por nombre de forma ascendente.
     *
     * @return Una lista observable de pedidos ordenados por nombre.
     */
    @Query("SELECT * FROM Orders ORDER BY name ASC")
    LiveData<List<Orders>> getOrderedOrders();
}
