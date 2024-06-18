package es.unizar.eina.fleetfeast.database;

import android.util.Log;

import es.unizar.eina.fleetfeast.ui.OrderDetailsViewModel;
import es.unizar.eina.fleetfeast.ui.OrderViewModel;
import es.unizar.eina.fleetfeast.ui.PlateViewModel;

public class SobreTests {

    /************************************************************************************
     *             ESTE TEST PROVOCA QUE EL DISPOSITIVO QUEDE INUTILIZABLE              *
     ************************************************************************************/

    PlateRepository mPlateRepository;
    OrdersRepository mOrdersRepository;
    OrderDetailsRepository mOrdersDetailsRepository;

    final int NUM_PLATES = 100;
    final int NUM_ORDERS = 2000;

    public SobreTests(PlateViewModel mPlateViewModel, OrderViewModel mOrdersViewModel,
                      OrderDetailsViewModel mOrdersDetailsViewModel) {
        this.mPlateRepository = mPlateViewModel.getRepository();
        this.mOrdersRepository = mOrdersViewModel.getRepository();
        this.mOrdersDetailsRepository = mOrdersDetailsViewModel.getRepository();
    }

    /**
     * Ejecuta todos los tests de sobrecarga de la base de datos.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    public boolean run() {
        return maxDescriptionSize();
    }


    /**
     * Inserta datos hasta que la descripción de un plato ocupe el máximo tamaño permitido.
     *                                   soporta descripciones de hasta 2486273 caracteres.
     *
     *************************************************************************************
     *              ESTE TEST PROVOCA QUE EL DISPOSITIVO QUEDE INUTILIZABLE              *
     *************************************************************************************
     *
     * @return true si se ha insertado correctamente, false en caso contrario.
     */
    private boolean maxDescriptionSize() {
        Log.d("SobrecargaTests", "maxDescriptionSize");
        mPlateRepository.deleteAll();
        long res;
        try {
            int des_len = 1;
            String description = "";
            while (true) {
                res = mPlateRepository.insertAndWait(new Plate("vol_test_len_" + des_len, description, "PRIMERO", 10.0f));
                if (res == -1) {
                    throw new Exception("maxDescriptionSize: insertAndWait returned -1");
                }
                des_len += 1024;
                description += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
                System.out.print("inserted plate with description.length:" + des_len);
                System.out.println("\treturned id: " + res);
            }
        }
        catch (Exception e) {
            Log.d("SobrecargaTests", "maxDescriptionSize: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Inserta datos hasta que la base de datos se llene.
     *         19468 pedidos con 1 orderdetails cada uno.
     *         974 platos.
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