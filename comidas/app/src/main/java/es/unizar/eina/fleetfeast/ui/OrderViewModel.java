package es.unizar.eina.fleetfeast.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.unizar.eina.fleetfeast.database.Orders;
import es.unizar.eina.fleetfeast.database.OrdersRepository;

/**
 * Clase ViewModel para gestionar los datos relacionados con la interfaz de usuario de la entidad Order.
 * Extiende AndroidViewModel para ser consciente del ciclo de vida.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class OrderViewModel extends AndroidViewModel {

    private OrdersRepository mRepository;
    private final LiveData<List<Orders>> mAllOrders;

    /**
     * Constructor para OrderViewModel.
     * Inicializa el repositorio y recupera todos los pedidos de él.
     * @param application El contexto de la aplicación.
     */
    public OrderViewModel(Application application) {
        super(application);
        mRepository = new OrdersRepository(application);
        mAllOrders = mRepository.getAllOrders();
    }

    /**
     * Recupera el repositorio de pedidos.
     * @return OrdersRepository que representa el repositorio de pedidos.
     */
    public OrdersRepository getRepository() {
        return mRepository;
    }


    /**
     * Recupera LiveData que contiene la lista de todos los pedidos.
     * @return LiveData<List<Orders>> que representa todos los pedidos.
     */
    LiveData<List<Orders>> getAllOrders() {
        return mAllOrders;
    }

    /**
     * Inserta un nuevo pedido en el repositorio.
     * @param order El pedido que se va a insertar.
     */
    public void insert(Orders order) {
        mRepository.insert(order);
    }

    /**
     * Inserta un nuevo pedido en el repositorio y espera a que se complete.
     * @param order El pedido que se va a insertar.
     * @return El id del pedido insertado.
     */
    public long insertAndWait(Orders order) {
        return mRepository.insertAndWait(order);
    }

    /**
     * Actualiza un pedido existente en el repositorio.
     * @param order El pedido que se va a actualizar.
     */
    public void update(Orders order) {
        mRepository.update(order);
    }

    /**
     * Elimina un pedido del repositorio.
     * @param order El pedido que se va a eliminar.
     */
    public void delete(Orders order) {
        mRepository.delete(order);
    }
}
