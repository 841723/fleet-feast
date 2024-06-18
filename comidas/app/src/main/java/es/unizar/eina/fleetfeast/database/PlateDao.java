package es.unizar.eina.fleetfeast.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/** Definición de un Data Access Object para los platos.
 *
 * Esta interfaz proporciona métodos de acceso a la base de
 * datos para la entidad Plate. Incluye operaciones como inserción,
 * actualización, eliminación y consultas específicas para obtener información
 * sobre los platos almacenados en la base de datos.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
@Dao
public interface PlateDao {

    /**
     * Inserta un plato en la base de datos.
     *
     * @param plate El plato a insertar.
     * @return El identificador del plato insertado.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Plate plate);

    /**
     * Actualiza un plato en la base de datos.
     *
     * @param plate El plato a actualizar.
     * @return El número de filas actualizadas (debería ser 1).
     */
    @Update
    int update(Plate plate);

    /**
     * Elimina un plato de la base de datos.
     *
     * @param plate El plato a eliminar.
     * @return El número de filas eliminadas (debería ser 1).
     */
    @Delete
    int delete(Plate plate);

    /**
     * Obtiene todos los platos ordenados por categoría de forma ascendente.
     * @return Una lista observable de platos.
     */
    @Query("SELECT * FROM Plate ORDER BY CASE category "+
            "WHEN 'PRIMERO' THEN 1 "+
            "WHEN 'SEGUNDO' THEN 2 "+
            "WHEN 'POSTRE' THEN 3 "+
            "ELSE 4 END")
    LiveData<List<Plate>> getAllPlatesSortByCategory();

    /**
     * Elimina todos los platos de la base de datos.
     */
    @Query("DELETE FROM Plate")
    void deleteAll();

    /**
     * Obtiene todos los platos ordenados por nombre de forma ascendente.
     * @return Una lista observable de platos.
     */
    @Query("SELECT * FROM Plate ORDER BY name ASC")
    LiveData<List<Plate>> getAllPlatesSortByName();

    /**
     * Obtiene todos los platos ordenados por categoría y nombre de forma ascendente.
     * @return Una lista observable de platos.
     */
    @Query("SELECT * FROM Plate ORDER BY CASE category "+
            "WHEN 'PRIMERO' THEN 1 "+
            "WHEN 'SEGUNDO' THEN 2 "+
            "WHEN 'POSTRE' THEN 3 "+
            "ELSE 4 END ASC, name ASC")
    LiveData<List<Plate>> getAllPlatesSortByCatAndName();

    /**
     * Obteine un plato de la base de datos a partir de su identificador.
     */
    @Query("SELECT * FROM Plate WHERE id = :id")
    Plate getPlateById(int id);


    /**
     * Obtiene los platos que no estan en un pedido dado
     * @param orderid El identificador del pedido.
     */
    @Query("SELECT * FROM Plate WHERE id NOT IN (SELECT plateid FROM OrderDetails WHERE orderid = :orderid)")
    LiveData<List<Plate>> getAllPlatesNotInOrder(long orderid);
}
