package es.unizar.eina.fleetfeast.database;


import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
  * Repositorio de funciones de los pedidos
  *
  * @author Abel Romeo
  * @author Diego Roldán
  */
public class OrdersRepository {

    private OrdersDao mOrderDao;
    private LiveData<List<Orders>> mAllOrders;

    // Order that in order to unit test the OrderRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public OrdersRepository(Application application) {
        FleetfeastRoomDatabase db =
                FleetfeastRoomDatabase.getDatabase(application);
        mOrderDao = db.ordersDao();
        mAllOrders = mOrderDao.getOrderedOrders();
    }

    /** Obtiene todos los pedidos ordenados por fecha de forma ascendente.
     * @return Una lista observable de pedidos.
     */
    public LiveData<List<Orders>> getAllOrders() {
        return mAllOrders;
    }

    /**
     * Elimina todos los pedidos de la base de datos.
     */
    public void deleteAll() {
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            mOrderDao.deleteAll();
        });
    }

    /** Inserta un pedido
     * @param order El pedido a insertar.
     * @return un valor entero largo con el identificador
     * del pedido que se ha creado.
     */
    public long insert(Orders order) {
        String regex = "\\d{4}/\\d{2}/\\d{2}  \\d{2}:\\d{2}";
        if (order == null
                || order.getName().length() <= 0
                || Long.toString(order.getPhone()).length() != 9
                || order.getDate().length() != 17
                || !order.getDate().matches(regex)
                || (order.getState()!="SOLICITADO" && order.getState()!="PREPARADO" && order.getState()!="RECOGIDO")) {
            return -1;
        }

        final long[] result = {0};
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mOrderDao.insert(order);
        });
        return result[0];
    }

     /** Modifica un pedido
     * @param order El pedido a modificar.
     * @return un valor entero con el número de filas modificadas.
     */
    public int update(Orders order) {
        String regex = "\\d{4}/\\d{2}/\\d{2}  \\d{2}:\\d{2}";
        if (order == null
                || order.getName().length() <= 0
                || Long.toString(order.getPhone()).length() != 9
                || order.getDate().length() != 17
                || !order.getDate().matches(regex)
                || (order.getState()!="SOLICITADO" && order.getState()!="PREPARADO" && order.getState()!="RECOGIDO")) {
            return 0;
        }

        final int[] result = {0};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mOrderDao.update(order);
        });
        return result[0];
    }

    /** Elimina un pedido
     * @param order El pedido a eliminar.
     * @return un valor entero con el número de filas eliminadas.
     */
    public int delete(Orders order) {
        final int[] result = {0};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mOrderDao.delete(order);
        });
        return result[0];
    }

    final long TIMEOUT = 10000;
    /** Inserta un pedido y espera a que se complete la operación
     * para devolver el identificador del pedido que se ha creado.
     * @param order El pedido a insertar.
     * @return un valor entero largo con el identificador
     */
    public long insertAndWait(Orders order) {
        String regex = "\\d{4}/\\d{2}/\\d{2}  \\d{2}:\\d{2}";
        if (order == null
            || order.getName().length() <= 0
            || Long.toString(order.getPhone()).length() != 9
            || order.getDate().length() != 17
            || !order.getDate().matches(regex)
            || (order.getState()!="SOLICITADO" && order.getState()!="PREPARADO" && order.getState()!="RECOGIDO")) {
            return -1;
        }
        AtomicLong result = new AtomicLong();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mOrderDao.insert(order));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("OrdersRepository", "insertAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }

    /** Modifica un pedido y espera a que se complete la operación
     * para devolver el número de filas modificadas.
     * @param order El pedido a modificar.
     * @return un valor entero con el número de filas modificadas.
     */
    public long updateAndWait(Orders order) {
        String regex = "\\d{4}/\\d{2}/\\d{2}  \\d{2}:\\d{2}";
        if (order == null
                || order.getName().length() <= 0
                || Long.toString(order.getPhone()).length() != 9
                || order.getDate().length() != 17
                || !order.getDate().matches(regex)
                || (order.getState()!="SOLICITADO" && order.getState()!="PREPARADO" && order.getState()!="RECOGIDO")) {
            return 0;
        }

        AtomicLong result = new AtomicLong();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mOrderDao.update(order));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("OrdersRepository", "updateAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }

    /** Elimina un pedido y espera a que se complete la operación
     * para devolver el número de filas eliminadas.
     * @param order El pedido a eliminar.
     * @return un valor entero con el número de filas eliminadas.
     */
    public int deleteAndWait(Orders order) {
        AtomicInteger result = new AtomicInteger();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mOrderDao.delete(order));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("OrdersRepository", "deleteAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }
}
