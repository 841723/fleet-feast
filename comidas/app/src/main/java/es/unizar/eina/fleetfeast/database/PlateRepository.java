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
 * Repositorio de funciones de los platos
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class PlateRepository {

    private PlateDao mPlateDao;
    private LiveData<List<Plate>> mAllPlates;

    // Plate that in order to unit test the PlateRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public PlateRepository(Application application) {
        FleetfeastRoomDatabase db =
                FleetfeastRoomDatabase.getDatabase(application);
        mPlateDao = db.plateDao();
        mAllPlates = mPlateDao.getAllPlatesSortByName();
    }

    /** Devuelve todos los platos
     * @return una lista de platos
     */
    public LiveData<List<Plate>> getAllPlates() {
        return mAllPlates;
    }

    /** Elimina todos los platos de la base de datos
     */
    public void deleteAll() {
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            mPlateDao.deleteAll();
        });
    }

    /** Devuelve todos los platos ordenados por un criterio
     * @param sort criterio de ordenación
     * @return una lista de platos ordenada
     */
    public LiveData<List<Plate>> getAllPlatesSortBy(String sort) {
        switch (sort) {
            case "name":
                return mPlateDao.getAllPlatesSortByName();
            case "category":
                return mPlateDao.getAllPlatesSortByCategory();
            case "both":
                return mPlateDao.getAllPlatesSortByCatAndName();
        }
        return mPlateDao.getAllPlatesSortByName();
    }

    /** Inserta un plato
     * @param plate plato a insertar
     * @return un valor entero largo con el identificador del plato que se ha creado.
     */
    public long insert(Plate plate) {
        if (plate == null
                || plate.getName().length() == 0
                || (plate.getCategory() != "PRIMERO" && plate.getCategory() != "SEGUNDO" && plate.getCategory() != "POSTRE")
                || plate.getPrize() < 0.0f) {
            return -1;
        }

        float prize = plate.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        plate.setPrize(prize);

        final long[] result = {0};
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mPlateDao.insert(plate);
        });
        return result[0];
    }

    /** Modifica un plato
     * @param plate plato a modificar
     * @return un valor entero con el número de filas modificadas.
     */
    public int update(Plate plate) {
        if (plate == null
                || plate.getName().length() == 0
                || (plate.getCategory() != "PRIMERO" && plate.getCategory() != "SEGUNDO" && plate.getCategory() != "POSTRE")
                || plate.getPrize() < 0.0f) {
            return -1;
        }

        float prize = plate.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        plate.setPrize(prize);

        final int[] result = {0};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mPlateDao.update(plate);
        });
        return result[0];
    }

    /** Elimina un plato
     * @param plate plato a eliminar
     * @return un valor entero con el número de filas eliminadas.
     */
    public int delete(Plate plate) {
        final int[] result = {0};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mPlateDao.delete(plate);
        });
        return result[0];
    }

    /** Devuelve un plato dado un identificador
     * @param id identificador del plato
     * @return un plato
     */
    public Plate getPlateById(int id) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Plate[] result = {null};
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result[0] = mPlateDao.getPlateById(id);
            latch.countDown(); // Indicar que la operación ha finalizado
        });
        try {
            latch.await(); // Bloquear el hilo principal hasta que la operación se complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    /** Devuelve una lista de platos que no están en un pedido dado
     * @param orderid identificador del pedido
     * @return una lista de platos
     */
    public LiveData<List<Plate>> getAllPlatesNotInOrder(long orderid) {
        return mPlateDao.getAllPlatesNotInOrder(orderid);
    }


     private final long TIMEOUT = 10000;
    /** Inserta un plato y espera a que se complete la operación
     * para devolver el identificador del plato que se ha creado.
     * @param plate plato a insertar
     * @return un valor entero largo con el identificador
     */
    public long insertAndWait(Plate plate) {
        if (plate == null
                || plate.getName().length() == 0
                || (plate.getCategory() != "PRIMERO" && plate.getCategory() != "SEGUNDO" && plate.getCategory() != "POSTRE")
                || plate.getPrize() < 0.0f) {
            return -1;
        }

        float prize = plate.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        plate.setPrize(prize);

        AtomicLong result = new AtomicLong();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mPlateDao.insert(plate));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("PlateRepository", "insertAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }

    /** Modifica un plato y espera a que se complete la operación
     * para devolver el número de filas modificadas.
     * @param plate plato a modificar
     * @return un valor entero con el número de filas modificadas.
     */
    public int updateAndWait(Plate plate) {
        if (plate == null
                || plate.getName().length() == 0
                || (plate.getCategory() != "PRIMERO" && plate.getCategory() != "SEGUNDO" && plate.getCategory() != "POSTRE")
                || plate.getPrize() < 0.0f) {
            return 0;
        }

        float prize = plate.getPrize();
        prize *= 100;
        prize = Math.round(prize);
        prize /= 100;
        plate.setPrize(prize);

        AtomicInteger result = new AtomicInteger();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mPlateDao.update(plate));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("PlateRepository", "updateAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }

    /** Elimina un plato y espera a que se complete la operación
     * para devolver el número de filas eliminadas.
     * @param plate plato a eliminar
     * @return un valor entero con el número de filas eliminadas.
     */
    public int deleteAndWait(Plate plate) {
        AtomicInteger result = new AtomicInteger();
        Semaphore semaphore = new Semaphore(0);
        FleetfeastRoomDatabase.databaseWriteExecutor.execute(() -> {
            result.set(mPlateDao.delete(plate));
            semaphore.release();
        });
        try {
            semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d("PlateRepository", "deleteAndWait: " + e.getMessage());
            e.printStackTrace();
        }
        return result.get();
    }
}

