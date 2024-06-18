package es.unizar.eina.fleetfeast.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.fleetfeast.ui.OrderDetailsViewModel;
import es.unizar.eina.fleetfeast.ui.OrderViewModel;
import es.unizar.eina.fleetfeast.ui.PlateViewModel;

public class VolumenTests {

    PlateRepository mPlateRepository;
    OrdersRepository mOrdersRepository;
    OrderDetailsRepository mOrdersDetailsRepository;

    final int NUM_PLATES = 100;
    final int NUM_ORDERS = 2000;

    public VolumenTests(PlateViewModel mPlateViewModel, OrderViewModel mOrdersViewModel, OrderDetailsViewModel mOrdersDetailsViewModel) {
        this.mPlateRepository = mPlateViewModel.getRepository();
        this.mOrdersRepository = mOrdersViewModel.getRepository();
        this.mOrdersDetailsRepository = mOrdersDetailsViewModel.getRepository();
    }

    /**
     * Ejecuta todos los tests de volumen de la base de datos.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    public boolean run() {
        return testPlate() && testOrders();
    }

    /**
     * Inserta NUM_PLATES (numero maximo de platos esperados) platos en la base de datos.
     * @return true si todos los platos se han insertado correctamente, false en caso contrario.
     */
    private boolean testPlate() {
        Log.d("VolumenTests", "testPlate");
        mPlateRepository.deleteAll();
        List<Plate> plates = new ArrayList<>();
        for (int i = 0; i < NUM_PLATES; i++) {
            plates.add(new Plate("vol_test_" + i, "PRIMERO", "PRIMERO", (float)i));
        }

        for (int i = 0; i < NUM_PLATES; i++) {
            mPlateRepository.insertAndWait(plates.get(i));
            System.out.println("inserted plate " + i);
        }

        Log.d("VolumenTests", "testPlate: OK");
        return true;
    }

    /**
     * Inserta NUM_ORDERS (numero maximo de pedidos esperados) pedidos en la base de datos.
     * @return true si todos los pedidos se han insertado correctamente, false en caso contrario.
     */
    private boolean testOrders() {
        Log.d("VolumenTests", "testOrders");
        mOrdersRepository.deleteAll();
        mOrdersDetailsRepository.deleteAll();
        List<Orders> orders = new ArrayList<>();

        for (int i = 0; i < NUM_ORDERS; i++) {
            orders.add(new Orders("vol_test_" + i, 900000000 + i, "2024/01/12  21:00", "SOLICITADO"));
        }

        long plate_id1 = mPlateRepository.insertAndWait(new Plate("vol_test_plate1", "PRIMERO", "PRIMERO", 10.0f));
        long plate_id2 = mPlateRepository.insertAndWait(new Plate("vol_test_plate2", "SEGUNDO", "SEGUNDO", 15.0f));
        for (int i = 0; i < NUM_ORDERS; i++) {
            long order_id = mOrdersRepository.insertAndWait(orders.get(i));
            mOrdersDetailsRepository.insertAndWait(new OrderDetails(order_id, (int)plate_id1, 1, 10.0f));
            mOrdersDetailsRepository.insertAndWait(new OrderDetails(order_id, (int)plate_id2, 1, 15.0f));
            System.out.println("inserted order " + i);
        }

        Log.d("VolumenTests", "testOrders: OK");
        return true;
    }

    /**
     * Inserta datos hasta que la base de datos se llene.
     *         19468 pedidos con 1 orderdetails cada uno.
     *                                        974 platos.
     */
    public void testMaxData() {
        Log.d("VolumenTests", "testMaxData");
        mPlateRepository.deleteAll();
        mOrdersRepository.deleteAll();
        mOrdersDetailsRepository.deleteAll();

        try {
            int plates_added = 0, orders_added = 0;
            while (true) {
                long plate_id = mPlateRepository.insertAndWait(new Plate("vol_test_max_" + plates_added, "PRIMERO", "PRIMERO", 10.0f));
                plates_added++;
                System.out.println("inserted plate " + plates_added);

                for (int j = 0; j < NUM_ORDERS / NUM_PLATES; j++) {
                    long order_id = mOrdersRepository.insertAndWait(new Orders("vol_test_max_" + orders_added,
                            900000000 + orders_added, "2024/01/12  21:00", "SOLICITADO"));
                    mOrdersDetailsRepository.insertAndWait(new OrderDetails(order_id, (int) plate_id, 1, 10.0f));
                    orders_added++;
                    System.out.println("inserted order " + orders_added);
                }
            }
        }
        catch (Exception e) {
            Log.d("VolumenTests", "testMaxData: " + e.getMessage());
            e.printStackTrace();
        }
    }
}