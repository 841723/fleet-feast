package es.unizar.eina.fleetfeast.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base de datos de la aplicación FleetFeast
 *
 * Esta clase representa la base de datos principal utilizada por la aplicación
 * FleetFeast. Contiene definiciones de las entidades (tablas) y métodos de
 * acceso a los DAOs asociados. Además, implementa un patrón Singleton para
 * garantizar una única instancia de la base de datos. También incluye una
 * inicialización de datos de ejemplo en caso de que la base de datos esté
 * recién creada.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */

@Database(entities = {Orders.class, Plate.class, OrderDetails.class}, version = 3, exportSchema = false)
public abstract class FleetfeastRoomDatabase extends RoomDatabase {

    public abstract OrdersDao ordersDao();
    public abstract PlateDao plateDao();
    public abstract OrderDetailsDao orderDetDao();
    private static volatile FleetfeastRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Obtiene la instancia única de la base de datos FleetFeast.
     *
     * @param context El contexto de la aplicación.
     * @return La instancia única de la base de datos FleetFeast.
     */
    static FleetfeastRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FleetfeastRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FleetfeastRoomDatabase.class, "fleetfeast_database")
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more plates, just add them.
                OrdersDao ordersDao = INSTANCE.ordersDao();
                ordersDao.deleteAll();
                PlateDao plateDao = INSTANCE.plateDao();
                plateDao.deleteAll();
                OrderDetailsDao orderDetDao = INSTANCE.orderDetDao();
                orderDetDao.deleteAll();
//
//
//                Plate plate1 = new Plate("Plate 1's name",
//                        "Plate 1's description", "PRIMERO", 5.3F);
//                Plate plate2 = new Plate("Plate 2's name",
//                        "Plate 2's description", "SEGUNDO", 8.6F);
//                Orders order1 = new Orders("Jacinto", 987654321,
//                        "22-11-2023, 20:00");
//                //OrderDetailsDao det1 = new OrderDetailsDao(order1.getId(),plate1.getId(),5);
//                plateDao.insert(plate1);
//                plateDao.insert(plate2);
//                ordersDao.insert(order1);
//                //orderDetDao.insert(det1);

            });
        }
    };

    // Definir la migración
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Añadir código para la migración
            // Por ejemplo, puedes ejecutar SQL para agregar una nueva columna.
            database.execSQL("ALTER TABLE orders ADD COLUMN state TEXT NOT NULL DEFAULT 'SOLICITADO'");

        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Añadir código para la migración
            // Por ejemplo, puedes ejecutar SQL para agregar una nueva columna.
            database.execSQL("ALTER TABLE orderDetails ADD COLUMN prize REAL NOT NULL");

        }
    };

}
