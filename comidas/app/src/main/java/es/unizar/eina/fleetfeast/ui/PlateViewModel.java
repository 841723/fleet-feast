package es.unizar.eina.fleetfeast.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.unizar.eina.fleetfeast.database.Plate;
import es.unizar.eina.fleetfeast.database.PlateRepository;

/**
 * Clase PlateViewModel para gestionar los datos relacionados con la interfaz de usuario de la entidad Plate.
 * Extiende AndroidViewModel para ser consciente del ciclo de vida.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class PlateViewModel extends AndroidViewModel {

    private PlateRepository mRepository;
    private final LiveData<List<Plate>> mAllPlates;

    /**
     * Constructor para PlateViewModel.
     * Inicializa el repositorio y recupera todos los platos de él.
     * @param application El contexto de la aplicación.
     */
    public PlateViewModel(Application application) {
        super(application);
        mRepository = new PlateRepository(application);
        mAllPlates = mRepository.getAllPlates();
    }

    /**
     * Recupera el repositorio de platos.
     * @return PlateRepository que representa el repositorio de platos.
     */
    public PlateRepository getRepository() {
        return mRepository;
    }

    /**
     * Recupera LiveData que contiene la lista de todos los platos.
     * @return LiveData<List<Plate>> que representa todos los platos.
     */
    LiveData<List<Plate>> getAllPlates() {
        return mAllPlates;
    }

    LiveData<List<Plate>> getAllPlatesSortBy(String sort) {
        return mRepository.getAllPlatesSortBy(sort);
    }

    /**
     * Inserta un nuevo plato en el repositorio.
     * @param plate El plato que se va a insertar.
     */
    public void insert(Plate plate) {
        mRepository.insert(plate);
    }

    /**
     * Actualiza un plato existente en el repositorio.
     * @param plate El plato que se va a actualizar.
     */
    public void update(Plate plate) {
        mRepository.update(plate);
    }

    /**
     * Elimina un plato del repositorio.
     * @param plate El plato que se va a eliminar.
     */
    public void delete(Plate plate) {
        mRepository.delete(plate);
    }

    /**
     * Devuelve el plato con un identificador dado.
     * @param plateId El identificador del plato.
     * @return El plato con el identificador dado.
     */
    public Plate getPlateById(int plateId) {
        return mRepository.getPlateById(plateId);}

    /**
     * Devuelve la lista de platos que no están en un pedido dado.
     * @param orderid El identificador del pedido.
     * @return La lista de platos que no están en el pedido dado.
     */
    public LiveData<List<Plate>> getAllPlatesNotInOrder(long orderid) {
        return mRepository.getAllPlatesNotInOrder(orderid);
    }

    /**
     * Devuelve el identificador de un plato insertado, esperando a que se complete la inserción.
     * @param plate El plato insertado.
     * @return El identificador del plato insertado.
     */
    public long insertAndWait(Plate plate) {
        return mRepository.insertAndWait(plate);
    }

}
