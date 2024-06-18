package es.unizar.eina.fleetfeast.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.fleetfeast.ui.OrderViewModel;
import es.unizar.eina.fleetfeast.ui.PlateViewModel;

public class UnitTests {

    PlateRepository mPlateRepository;
    OrdersRepository mOrdersRepository;

    public UnitTests(PlateViewModel mPlateViewModel, OrderViewModel mOrdersViewModel) {
        this.mPlateRepository = mPlateViewModel.getRepository();
        this.mOrdersRepository = mOrdersViewModel.getRepository();
    }

    /**
     * Ejecuta los tests unitarios de la base de datos.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    public boolean run() {
        return testPlate() && testOrders();
    }

    /**
     * Ejecuta los tests de la tabla Plate.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testPlate() {
        return testPlateInsert() && testPlateUpdate() && testPlateDelete();
    }

    /**
     * Ejecuta los tests de inserción de la tabla Plate.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testPlateInsert() {
        List<Plate> plates = new ArrayList<>();
        List<String> results = new ArrayList<>();
        // casos de prueba positivos
        plates.add(new Plate("canelones", "canelones con canela", "PRIMERO", 11.5f));
        results.add(">0");
        plates.add(new Plate("canelones", "canelones con canela", "SEGUNDO", 11.5f));
        results.add(">0");
        plates.add(new Plate("canelones", "canelones con canela", "POSTRE", 11.5f));
        results.add(">0");
        // casos de prueba negativos
        plates.add(new Plate("", "error", "POSTRE", 11.5f));
        results.add("-1");
        plates.add(new Plate("error", "error", "OTRO", 11.5f));
        results.add("-1");
        plates.add(new Plate("error", "error", "OTRO", -11.5f));
        results.add("-1");

        boolean error = false;
        long result = -10;
        for (int i = 0; i < plates.size(); i++) {
            Plate plate = plates.get(i);
            try {
                result = mPlateRepository.insertAndWait(plate);
                if (!checkResult(result,results.get(plates.indexOf(plate)))) {
                    Log.d("testPlateInsert", "ERROR testPlateInsert pos " + i + " -> " + results.get(plates.indexOf(plate)) + " != " + result);
                    error = true;
                    return false;
                }
                else {
                    Log.d("testPlateInsert","SUCCESS testPlateInsert pos " +i +" -> "+ results.get(plates.indexOf(plate)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return !error;
    }

    /**
     * Ejecuta los tests de actualización de la tabla Plate.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testPlateUpdate() {
        List<Plate> plates = new ArrayList<>();
        List<String> results = new ArrayList<>();
        long id = mPlateRepository.insertAndWait(new Plate("prueba", "prueba", "PRIMERO", 2.5f));

        // casos de prueba positivos
        Plate plate = new Plate("canelones", "canelones con canela", "PRIMERO", 11.5f);
        plate.setId((int)id);
        results.add("1");
        plates.add(plate);

        plate.setCategory("SEGUNDO");
        results.add("1");
        plates.add(plate);

        plate.setCategory("POSTRE");
        results.add("1");
        plates.add(plate);

        // casos de prueba negativos
        plate = new Plate("", "error", "POSTRE", 11.5f);
        plate.setId((int)id);
        results.add("0");
        plates.add(plate);

        plate = new Plate("error", "error", "OTRO", 11.5f);
        plate.setId((int)id);
        results.add("0");
        plates.add(plate);

        plate = new Plate("error", "error", "POSTRE", -11.5f);
        plate.setId((int)id);
        results.add("0");
        plates.add(plate);

        plate = new Plate("error", "error", "PRIMERO", 11.5f);
        plate.setId(-1);
        results.add("0");
        plates.add(plate);

        boolean error = false;
        long result = -10;
        for (int i = 0; i < plates.size(); i++) {
            plate = plates.get(i);
            try {
                result = mPlateRepository.updateAndWait(plate);
                if (!checkResult(result,results.get(plates.indexOf(plate)))) {
                    Log.d("testPlateUpdate","ERROR testPlateUpdate pos " +i +" -> "+ results.get(plates.indexOf(plate)) + " != " + result);
                    error = true;
                }
                else {
                    Log.d("testPlateUpdate","SUCCESS testPlateUpdate pos " +i +" -> "+ results.get(plates.indexOf(plate)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !error;
    }

    /**
     * Ejecuta los tests de borrado de la tabla Plate.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testPlateDelete() {
        List<Plate> plates = new ArrayList<>();
        List<String> results = new ArrayList<>();

        Plate plate = new Plate("prueba", "prueba", "PRIMERO", 2.5f);
        long id = mPlateRepository.insertAndWait(plate);

        // casos de prueba positivos
        plate.setId((int) id);
        results.add("1");
        plates.add(new Plate(plate));

        mPlateRepository.insertAndWait(plate);
        // casos de prueba negativos
        plate.setId(-1);
        results.add("0");
        plates.add(new Plate(plate));


        boolean error = false;
        long result = -10;
        for (int i = 0; i < plates.size(); i++) {
            plate = plates.get(i);
            try {
                result = mPlateRepository.deleteAndWait(plate);
                if (!checkResult(result, results.get(plates.indexOf(plate)))) {
                    Log.d("testPlateDelete", "ERROR testPlateDelete pos " + i + " -> " + results.get(plates.indexOf(plate)) + " != " + result);
                    error = true;
                } else {
                    Log.d("testPlateDelete", "SUCCESS testPlateDelete pos " + i + " -> " + results.get(plates.indexOf(plate)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !error;
    }

    /**
     * Ejecuta los tests de la tabla Orders.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testOrders() {
        return testOrdersInsert() && testOrdersUpdate() && testOrdersDelete();
    }

    /**
     * Ejecuta los tests de inserción de la tabla Orders.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testOrdersInsert() {
        List<Orders> orders = new ArrayList<>();
        List<String> results = new ArrayList<>();
        // casos de prueba positivos
        orders.add(new Orders("pedido", 123456789, "2024/01/04  21:30", "SOLICITADO"));
        results.add(">0");
        orders.add(new Orders("pedido", 123456789, "2024/01/04  21:30", "PREPARADO"));
        results.add(">0");
        orders.add(new Orders("pedido", 123456789, "2024/01/04  21:30", "RECOGIDO"));
        results.add(">0");
        // casos de prueba negativos
        orders.add(new Orders("", 123456789, "2024/01/04  21:30", "RECOGIDO"));
        results.add("-1");
        orders.add(new Orders("pedido", 123, "2024/01/04  21:30", "RECOGIDO"));
        results.add("-1");
        orders.add(new Orders("pedido", 123456789, "21:30  2024/01/04", "RECOGIDO"));
        results.add("-1");
        orders.add(new Orders("pedido", 123456789, "21:30  2024/00001/04", "RECOGIDO"));
        results.add("-1");
        orders.add(new Orders("pedido", 123456789, "2024/01/04  21:30", "pedido"));
        results.add("-1");

        boolean error = false;
        long result = -10;
        for (int i = 0; i < orders.size(); i++) {
            Orders order = orders.get(i);
            try {
                result = mOrdersRepository.insertAndWait(order);
                if (!checkResult(result, results.get(orders.indexOf(order)))) {
                    error = true;
                    Log.d("testOrdersInsert", "ERROR testOrdersInsert pos " + i + " -> " + results.get(orders.indexOf(order)) + " != " + result);
                } else {
                    Log.d("testOrdersInsert", "SUCCESS testOrdersInsert pos " + i + " -> " + results.get(orders.indexOf(order)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !error;
    }

    /**
     * Ejecuta los tests de actualización de la tabla Orders.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testOrdersUpdate() {
        List<Orders> orders = new ArrayList<>();
        List<String> results = new ArrayList<>();

        long id = mOrdersRepository.insertAndWait(new Orders("pedido", 123456789, "2024/01/04  21:30", "RECOGIDO"));

        // casos de prueba positivos
        Orders order = new Orders("pedido", 123456789, "2024/01/04  21:30", "SOLICITADO");
        order.setId((int) id);
        results.add("1");
        orders.add(order);

        order.setState("PREPARADO");
        results.add("1");
        orders.add(order);

        order.setState("RECOGIDO");
        results.add("1");
        orders.add(order);

        // casos de prueba negativos
        order = new Orders("", 123456789, "2024/01/04  21:30", "RECOGIDO");
        order.setId((int)id);
        results.add("0");
        orders.add(order);

        order = new Orders("pedido", 123, "2024/01/04  21:30", "RECOGIDO");
        order.setId((int)id);
        results.add("0");
        orders.add(order);

        order = new Orders("pedido", 123456789, "21:30  2024/01/04", "RECOGIDO");
        order.setId((int)id);
        results.add("0");
        orders.add(order);

        order = new Orders("pedido", 123456789, "21:30  2024/00001/04", "RECOGIDO");
        order.setId((int)id);
        results.add("0");
        orders.add(order);

        order = new Orders("pedido", 123456789, "2024/01/04  21:30", "pedido");
        order.setId((int)id);
        results.add("0");
        orders.add(order);

        order = new Orders("pedido", 123456789, "2024/01/04  21:30", "SOLICITADO");
        order.setId(-1);
        results.add("0");
        orders.add(order);

        boolean error = false;
        long result = -10;
        for (int i = 0; i < orders.size(); i++) {
            order = orders.get(i);
            try {
                result = mOrdersRepository.updateAndWait(order);
                if (!checkResult(result, results.get(orders.indexOf(order)))) {
                    error = true;
                    Log.d("testOrdersUpdate", "ERROR testOrdersUpdate pos " + i + " -> " + results.get(orders.indexOf(order)) + " != " + result);
                } else {
                    Log.d("testOrdersUpdate", "SUCCESS testOrdersUpdate pos " + i + " -> " + results.get(orders.indexOf(order)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !error;
    }

    /**
     * Ejecuta los tests de borrado de la tabla Orders.
     * @return true si todos los tests han sido satisfactorios, false en caso contrario.
     */
    private boolean testOrdersDelete() {
        List<Orders> orders = new ArrayList<>();
        List<String> results = new ArrayList<>();

        // casos de prueba positivos
        Orders order = new Orders("pedido", 123456789, "2024/01/04  21:30", "SOLICITADO");
        long id = mOrdersRepository.insertAndWait(order);

        order.setId(id);
        results.add("1");
        orders.add(order);

        // casos de prueba negativos
        order = new Orders("pedido", 123456789, "2024/01/04  21:30", "SOLICITADO");
        order.setId(-1);
        results.add("0");
        orders.add(order);

        boolean error = false;
        long result = -10;
        for (int i = 0; i < orders.size(); i++) {
            order = orders.get(i);
            try {
                result = mOrdersRepository.deleteAndWait(order);
                if (!checkResult(result, results.get(orders.indexOf(order)))) {
                    error = true;
                    Log.d("testOrdersDelete", "ERROR testOrdersDelete pos " + i + " -> " + results.get(orders.indexOf(order)) + " != " + result);
                } else {
                    Log.d("testOrdersDelete", "SUCCESS testOrdersDelete pos " + i + " -> " + results.get(orders.indexOf(order)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !error;
    }

    /**
     * Comprueba si el resultado de una operación coincide con el esperado.
     * @param result resultado de la operación.
     * @param expected resultado esperado.
     * @return true si el resultado coincide con el esperado, false en caso contrario.
     */
    private boolean checkResult(long result, String expected) {
        if (expected.equals(">0")) {
            return result > 0;
        }
        else if (expected.equals("=0")) {
            return result == 0;
        }
        else if (expected.equals("1")) {
            return result == 1;
        }
        else if (expected.equals("0")) {
            return result == 0;
        }
        else if (expected.equals("-1")) {
            return result == -1;
        }
        else {
            return false;
        }
    }
}