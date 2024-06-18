package es.unizar.eina.fleetfeast.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.unizar.eina.fleetfeast.R;
import es.unizar.eina.fleetfeast.database.OrderDetails;
import es.unizar.eina.fleetfeast.database.Orders;
import es.unizar.eina.fleetfeast.database.Plate;
import es.unizar.eina.fleetfeast.database.SobreTests;
import es.unizar.eina.fleetfeast.database.UnitTests;
import es.unizar.eina.fleetfeast.database.VolumenTests;

/**
 * Clase que representa la actividad principal de la aplicación FleetFeast.
 *
 * Esta actividad proporciona botones para acceder a las listas
 * de platos y pedidos. Al hacer clic en uno de los botones, se
 * inicia la actividad correspondiente.
 *
 * Implementa la interfaz View.OnClickListener para gestionar los
 * clics en los botones.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonPlates, buttonOrders;
    static final int TEST_UNIT_ID = Menu.FIRST;
    static final int TEST_VOL_ID = Menu.FIRST + 1;
    static final int TEST_MAX_ID = Menu.FIRST + 2;
    static final int TEST_DESC_ID = Menu.FIRST + 3;
    static final int DELETE_PLATES_ID = Menu.FIRST + 4;
    static final int DELETE_ORDERS_ID = Menu.FIRST + 5;
    static final int DEFAULT_ID = Menu.FIRST + 6;


    PlateViewModel mPlateViewModel;
    OrderViewModel mOrderViewModel;
    OrderDetailsViewModel mOrderDetailsViewModel;

    TextView textTestView;


    /**
     * Se llama cuando la actividad se crea por primera vez.
     * @param savedInstanceState El estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlates = (Button) findViewById(R.id.PLATOS);
        buttonOrders = (Button) findViewById(R.id.PEDIDOS);
        buttonPlates.setOnClickListener(this);
        buttonOrders.setOnClickListener(this);

        mPlateViewModel = new ViewModelProvider(this).get(PlateViewModel.class);
        mOrderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        mOrderDetailsViewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);

        textTestView = findViewById(R.id.textTestView);

        textTestView.setText("");
        textTestView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==buttonPlates.getId()){
            Intent Fleetfeast = new Intent(this, ListaPlatos.class);
            startActivity(Fleetfeast);
        } else if (v.getId()==buttonOrders.getId()){
            Intent orderslist = new Intent(this, ListaPedidos.class);
            startActivity(orderslist);
        }
    }

    /**
     * Crea el menu de opciones en la barra de accion.
     * @param menu El menu de opciones.
     * @return `true` si se ha creado correctamente.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, TEST_UNIT_ID, Menu.NONE,"unit test");
        menu.add(Menu.NONE, TEST_VOL_ID, Menu.NONE,"volumen test");
        menu.add(Menu.NONE, TEST_MAX_ID, Menu.NONE,"max test");
        menu.add(Menu.NONE, TEST_DESC_ID, Menu.NONE,"max description test");
        menu.add(Menu.NONE, DELETE_PLATES_ID, Menu.NONE,"delete all plates");
        menu.add(Menu.NONE, DELETE_ORDERS_ID, Menu.NONE,"delete all orders");
        menu.add(Menu.NONE, DEFAULT_ID, Menu.NONE,"add default values");
        return result;
    }

    /**
     * Maneja la seleccion de elementos en el menu de opciones.
     * @param item El elemento del menú seleccionado.
     * @return `true` si se ha manejado correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        textTestView.setText("test...");
        textTestView.setVisibility(View.VISIBLE);

        switch (item.getItemId()) {
            case TEST_UNIT_ID:
                System.out.println("unit test");
                if (new UnitTests(mPlateViewModel,mOrderViewModel).run()) {
                    testOK();
                }
                else {
                    textTestView.setText("test ERROR");
                }
                return true;
            case TEST_VOL_ID:
                System.out.println("volumen test");
                if (new VolumenTests(mPlateViewModel,mOrderViewModel,mOrderDetailsViewModel).run()) {
                    testOK();
                }
                else {
                    textTestView.setText("test ERROR");
                }
                return true;
            case TEST_MAX_ID:
                System.out.println("max test");
                new VolumenTests(mPlateViewModel,mOrderViewModel,mOrderDetailsViewModel).testMaxData();
                testOK();
                return true;
            case TEST_DESC_ID:
                System.out.println("max description test");
                if (new SobreTests(mPlateViewModel,mOrderViewModel,mOrderDetailsViewModel).run()) {
                    testOK();
                }
                else {
                    textTestView.setText("test ERROR");
                }
                testOK();
                return true;
            case DELETE_PLATES_ID:
                System.out.println("delete all plates");
                mPlateViewModel.getRepository().deleteAll();
                testOK();
                return true;
            case DELETE_ORDERS_ID:
                System.out.println("delete all plates");
                mOrderViewModel.getRepository().deleteAll();
                testOK();
                return true;
            case DEFAULT_ID:
                System.out.println("default");
                defaultValues();
                testOK();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void testOK() {
        textTestView.setText("test OK");
        textTestView.setVisibility(View.VISIBLE);
    }



    /**
     * Añade valores por defecto a la base de datos.
     */
    private void defaultValues() {
        mPlateViewModel.getRepository().deleteAll();
        mOrderViewModel.getRepository().deleteAll();
        mOrderDetailsViewModel.getRepository().deleteAll();
        List<Long> plate_ids = new ArrayList<>();
        List<Long> order_ids = new ArrayList<>();

        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Hamburguesa", "Carne", "SEGUNDO", 7.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Ensalada de Pollo", "Pollo", "PRIMERO", 6.0f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Tiramisú", "Postre", "POSTRE", 4.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Pasta Carbonara", "Pasta", "SEGUNDO", 8.2f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Pizza Margarita", "Mozzarella y Tomate", "SEGUNDO", 9.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Sopa de Tomate", "Tomate fresco con hierbas", "PRIMERO", 5.8f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Brownie de Chocolate", "Con nueces", "POSTRE", 3.9f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Tacos de Pescado", "Tortillas de maíz con pescado fresco", "SEGUNDO", 10.0f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Ensalada César", "Pollo a la parrilla con aderezo César", "PRIMERO", 7.7f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Mousse de Frambuesa", "Postre suave y afrutado", "POSTRE", 4.2f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Lasagna", "Capas de pasta con carne y queso", "SEGUNDO", 11.75f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Sopa de Lentejas", "Lentejas con vegetales", "PRIMERO", 5.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Cheesecake", "Pastel de queso con base de galleta", "POSTRE", 6.8f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Pollo al Curry", "Pollo en salsa de curry con arroz", "SEGUNDO", 9.2f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Ensalada Griega", "Lechuga, tomate, pepino y queso feta", "PRIMERO", 8.0f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Helado de Vainilla", "Con sirope de chocolate", "POSTRE", 4.0f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Tacos de Carne Asada", "Tortillas con carne asada y guacamole", "SEGUNDO", 10.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Gazpacho", "Sopa fría de tomate y verduras", "PRIMERO", 6.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Muffin de Arándanos", "Con trozos de arándanos", "POSTRE", 3.5f)));
        plate_ids.add(mPlateViewModel.insertAndWait(new Plate("Pescado a la Parrilla", "Filete de pescado con limón y hierbas", "SEGUNDO", 12.0f)));

        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Abel", 123456789, "2023/12/30  21:30", "SOLICITADO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Diego", 123456789, "2024/01/04  20:00", "SOLICITADO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Marta", 123456789, "2023/12/31  23:00", "SOLICITADO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Ana", 987654321, "2023/12/20  22:45", "RECOGIDO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Carlos", 987654321, "2023/12/19  20:30", "RECOGIDO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Luisa", 987654321, "2023/12/18  22:15", "RECOGIDO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Elena", 987654321, "2023/12/23  19:30", "PREPARADO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Juan", 987654321, "2023/12/23  20:45", "PREPARADO")));
        order_ids.add(mOrderViewModel.insertAndWait(new Orders("Laura", 987654321, "2023/12/23  22:00", "PREPARADO")));

        for (int i = 0; i < order_ids.size();) {
            Random rand = new Random();
            int randomNum = rand.nextInt(plate_ids.size());
            int randomCant = rand.nextInt(4)+1;
            float randomPrice = rand.nextFloat()*10+1.5f;
            mOrderDetailsViewModel.insert(new OrderDetails(order_ids.get(i), Math.toIntExact(plate_ids.get(randomNum)), randomCant, randomPrice));
            if (rand.nextInt(2) == 1) {
               i++;
            }
        }
    }
}
