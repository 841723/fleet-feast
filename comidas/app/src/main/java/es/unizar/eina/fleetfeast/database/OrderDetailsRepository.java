package es.unizar.eina.fleetfeast.database;


import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio de funciones de los detalles de los pedidos
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class OrderDetailsRepository {

    private OrderDetailsDao mOrderDetDao;
    private LiveData<List<OrderDetails>> mAllOrderDet;

    // Order that in order to unit test the OrderRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    public OrderDetailsRepository(Application application) {
        FleetfeastRoomDatabase db =
                FleetfeastRoomDatabase.getDatabase(application);
        mOrderDetDao = db.orderDetDao();
        mAllOrderDet = mOrderDetDao.getAllOrderDetails();
    }


    /** Obtiene todos los detalles de los pedidos ordenados por nombre de forma ascendente.
     * @return un objeto LiveData con la lista de detalles de los pedidos.
     */
    public LiveData<List<OrderDetails>> getAllOrdersDetails() {
        return mAllOrderDet;
    }

    /**
     * Elimina todos los detalles de los pedidos de la base de datos.
     */
    public void deleteAll() {
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            mOrderDetDao.deleteAll();
        });
    }

    /** Obtiene los detalles de un pedido
     * @param orderId identificador del pedido
     * @return un objeto LiveData con la lista de detalles de los pedidos.
     */
    public LiveData<List<OrderDetails>> getOrdersDetailsByOrderId(long orderId) {
        return mOrderDetDao.getOrdersDetailsByOrderId(orderId);
    }


    /** Inserta los detalles de un pedido
     * @param orderDet detalles de pedido a insertar
     * @return un valor entero largo con el identificador
     * del pedido que se ha creado.
     */
    public long insert(OrderDetails orderDet) {

        float prize = orderDet.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        orderDet.setPrize(prize);

        final long[] result = {0};
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mOrderDetDao.insert(orderDet);
        });
        return result[0];
    }

    /** Modifica los detalles de un pedido
     * @param orderDet detalles de pedido a modificar
     * @return un valor entero con el número de filas modificadas.
     */
    public int update(OrderDetails orderDet) {

        float prize = orderDet.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        orderDet.setPrize(prize);

        final int[] result = {0};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mOrderDetDao.update(orderDet);
        });
        return result[0];
    }

    /** Elimina los detalles de un pedido
     * @param orderDet
     * @return un valor entero con el número de filas eliminadas.
     */
    public int delete(OrderDetails orderDet) {
        final int[] result = {0};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mOrderDetDao.delete(orderDet);
        });
        return result[0];
    }

    final long TIMEOUT = 10000;

    /** Inserta los detalles de un pedido y espera a que se inserten
     * @param orderDet detalles de pedido a insertar
     * @return un valor entero largo con el identificador
     * del pedido que se ha creado.
     */
    public long insertAndWait(OrderDetails orderDet) {

        float prize = orderDet.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        orderDet.setPrize(prize);

        AtomicLong result = new AtomicLong();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mOrderDetDao.insert(orderDet));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("OrdersDetailsRepository", "insertAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }
}
