package es.unizar.eina.fleetfeast.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.unizar.eina.fleetfeast.database.OrderDetails;
import es.unizar.eina.fleetfeast.database.OrderDetailsRepository;
import es.unizar.eina.fleetfeast.database.Orders;
import es.unizar.eina.fleetfeast.database.OrdersRepository;
import es.unizar.eina.fleetfeast.database.Plate;

/**
 * Clase ViewModel para gestionar los datos relacionados con la interfaz de usuario de la entidad Order.
 * Extiende AndroidViewModel para ser consciente del ciclo de vida.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class OrderDetailsViewModel extends AndroidViewModel {

    private OrderDetailsRepository mRepository;
    private final LiveData<List<OrderDetails>> mAllOrdersDetails;

    /**
     * Constructor para OrderDetailsViewModel.
     * Inicializa el repositorio y recupera todos los pedidos de él.
     * @param application El contexto de la aplicación.
     */
    public OrderDetailsViewModel(Application application) {
        super(application);
        mRepository = new OrderDetailsRepository(application);
        mAllOrdersDetails = mRepository.getAllOrdersDetails();
    }

    /**
     * Recupera el repositorio de pedidos.
     * @return OrderDetailsRepository que representa el repositorio de pedidos.
     */
    public OrderDetailsRepository getRepository() {
        return mRepository;
    }

    /**
     * Recupera LiveData que contiene la lista de todos los pedidos.
     * @return LiveData<List<Orders>> que representa todos los pedidos.
     */
    LiveData<List<OrderDetails>> getAllOrdersDetails() {
        return mAllOrdersDetails;
    }

    /**
     * Recupera LiveData que contiene la lista de todos los pedidos de un pedido.
     * @param orderId El id del pedido del que se quieren recuperar los pedidos.
     * @return LiveData<List<Orders>> que representa todos los pedidos de un pedido.
     */
    LiveData<List<OrderDetails>> getOrdersDetailsByOrderId(long orderId) {
        return mRepository.getOrdersDetailsByOrderId(orderId);
    }
    /**
     * Inserta un nuevo pedido en el repositorio.
     * @param order_details El pedido que se va a insertar.
     */
    public void insert(OrderDetails order_details) {
        mRepository.insert(order_details);
    }

    /**
     * Actualiza un pedido existente en el repositorio.
     * @param order_details El pedido que se va a actualizar.
     */
    public void update(OrderDetails order_details) {
        // cuestionable
        if (order_details.getQuantity() == 0) {
            mRepository.delete(order_details);
        }
        else {
            mRepository.update(order_details);
        }
    }

    /**
     * Elimina un pedido del repositorio.
     * @param order_details El pedido que se va a eliminar.
     */
    public void delete(OrderDetails order_details) {
        mRepository.delete(order_details);
    }
}
