package es.unizar.eina.fleetfeast.ui;

import static es.unizar.eina.fleetfeast.ui.ListaPlatos.INSERT_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.FtsOptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Comparator;
import java.util.List;

import es.unizar.eina.fleetfeast.R;
import es.unizar.eina.fleetfeast.database.OrderDetails;
import es.unizar.eina.fleetfeast.database.Orders;
import es.unizar.eina.fleetfeast.database.Plate;
import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;


/**
 * Actividad que muestra la lista de pedidos.
 *
 * Esta actividad se encarga de mostrar la lista de pedidos almacenados.
 * Se utiliza un diseño definido en el recurso 'activity_lista_pedidos.xml'.
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class ListaPedidos extends AppCompatActivity {
    private OrderViewModel mOrderViewModel;
    public static final int ACTIVITY_CREATE = 1;

    public static final int ACTIVITY_EDIT = 2;

    static final int INSERT_ID = Menu.FIRST;
    static final int DELETE_ID = Menu.FIRST + 1;
    static final int EDIT_ID = Menu.FIRST + 2;
    static final int SEND_WAS_ID = Menu.FIRST + 3;
    static final int SEND_SMS_ID = Menu.FIRST + 4;

    RecyclerView mRecyclerView;

    OrderListAdapter mOrderAdapter;

    RadioGroup mRadioGroupOrder;
    RadioButton mRadioButtonOrderName;
    RadioButton mRadioButtonOrderPhone;
    RadioButton mRadioButtonOrderDate;

    CheckBox mCheckBoxFilterSolicitado;
    CheckBox mCheckBoxFilterPreparado;
    CheckBox mCheckBoxFilterRecogido;
    FloatingActionButton mFab;

    SendAbstraction mSendWasAbstraction;
    SendAbstraction mSendSMSAbstraction;

    /**
     * Se llama cuando la actividad se crea por primera vez.
     * @param savedInstanceState El estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);
        mSendWasAbstraction = new SendAbstractionImpl(this, "WhatsApp");
        mSendSMSAbstraction = new SendAbstractionImpl(this, "SMS");

        mRadioGroupOrder = findViewById(R.id.radioGroup_order);
        mRadioButtonOrderName = findViewById(R.id.radioButton_order);
        mRadioButtonOrderPhone = findViewById(R.id.radioButton2_order);
        mRadioButtonOrderDate = findViewById(R.id.radioButton3_order);

        mCheckBoxFilterSolicitado = findViewById(R.id.checkBox);
        mCheckBoxFilterPreparado = findViewById(R.id.checkBox2);
        mCheckBoxFilterRecogido = findViewById(R.id.checkBox3);

        mRadioButtonOrderName.setChecked(true);
        mRadioButtonOrderPhone.setChecked(false);
        mRadioButtonOrderDate.setChecked(false);

        mCheckBoxFilterSolicitado.setChecked(true);
        mCheckBoxFilterPreparado.setChecked(true);
        mCheckBoxFilterRecogido.setChecked(true);


        mRecyclerView = findViewById(R.id.recyclerview);
        mOrderAdapter = new OrderListAdapter(new OrderListAdapter.OrderDiff());
        mRecyclerView.setAdapter(mOrderAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mOrderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        updateList();
        mCheckBoxFilterSolicitado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateList();
        });
        mCheckBoxFilterPreparado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateList();
        });
        mCheckBoxFilterRecogido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateList();
        });
        mRadioButtonOrderName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateList();
        });
        mRadioButtonOrderPhone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateList();
        });
        mRadioButtonOrderDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateList();
        });

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            createOrder();
        });

        registerForContextMenu(mRecyclerView);
    }

    /**
     * Actualiza la lista de pedidos.
     */
    private void updateList() {
        boolean solicitado = mCheckBoxFilterSolicitado.isChecked();
        boolean preparado = mCheckBoxFilterPreparado.isChecked();
        boolean recogido = mCheckBoxFilterRecogido.isChecked();

        String orderby;
        if (mRadioButtonOrderName.isChecked()) {
            orderby = "name";
        } else if (mRadioButtonOrderPhone.isChecked()) {
            orderby = "phone";
        } else if (mRadioButtonOrderDate.isChecked()) {
            orderby = "date";
        } else {
            orderby = "none";
        }
        mOrderViewModel.getAllOrders().observe(this, orders -> {
            // Update the cached copy of the notes in the adapter.
//            eliminateEmptyOrders(orders);
            List<Orders> filteredList = orderAndFilter(orders, orderby, solicitado,
                    preparado, recogido);
            mOrderAdapter.submitList(filteredList);
        });
    }


    /**
     * Ordena y filtra la lista de pedidos.
     * @param ordersList La lista de pedidos.
     * @param orderby El criterio de ordenación.
     * @param solicitado El filtro de pedidos solicitados.
     * @param preparado El filtro de pedidos preparados.
     * @param recogido El filtro de pedidos recogidos.
     * @return La lista de pedidos ordenada y filtrada.
     */
    private List<Orders> orderAndFilter(List<Orders> ordersList, String orderby,
                                        boolean solicitado, boolean preparado, boolean recogido) {
        List<Orders> filteredList = new java.util.ArrayList<Orders>();
        if (orderby.equals("name")) {
            ordersList.sort(Comparator.comparing(Orders::getName));
        } else if (orderby.equals("phone")) {
            ordersList.sort(Comparator.comparing(Orders::getPhone));
        } else if (orderby.equals("date")) {
            ordersList.sort(Comparator.comparing(Orders::getDate));
        }

        for (Orders o : ordersList) {
            if (solicitado && o.getState().equals("SOLICITADO")) {
                filteredList.add(o);
            } else if (preparado && o.getState().equals("PREPARADO")) {
                filteredList.add(o);
            } else if (recogido && o.getState().equals("RECOGIDO")) {
                filteredList.add(o);
            }
        }
        return filteredList;
    }

    /**
     * Crea el menu de opciones en la barra de accion.
     * @param menu El menu de opciones.
     * @return `true` si se ha creado correctamente.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.add_order);
        return result;
    }

    /**
     * Maneja el resultado de las actividades iniciadas para obtener resultados.
     * @param requestCode Código de la solicitud.
     * @param resultCode Código de resultado.
     * @param data Datos resultantes de la actividad.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTIVITY_CREATE:
                case ACTIVITY_EDIT:
                    long id = extras.getLong(OrderEdit.ORDER_ID);
                    Orders updatedOrder = new Orders(
                            extras.getString(OrderEdit.ORDER_NAME),
                            extras.getInt(OrderEdit.ORDER_PHONE),
                            extras.getString(OrderEdit.ORDER_DATE),
                            extras.getString(OrderEdit.ORDER_STATE)
                    );
                    OrderDetailsViewModel mOrderDetailsViewModel = new ViewModelProvider(this)
                            .get(OrderDetailsViewModel.class);

                    // Define un Observer para escuchar los cambios en los datos
                    LiveData<List<OrderDetails>> orderDetailsLiveData = mOrderDetailsViewModel.getOrdersDetailsByOrderId(id);
                    Observer<List<OrderDetails>> observer = new Observer<List<OrderDetails>>() {
                        @Override
                        public void onChanged(List<OrderDetails> orderDetails) {
                            // Hacer algo con los datos
                            if (orderDetails.isEmpty()) {
                                Orders orderToDelete = new Orders(
                                        extras.getString(OrderEdit.ORDER_NAME),
                                        extras.getInt(OrderEdit.ORDER_PHONE),
                                        extras.getString(OrderEdit.ORDER_DATE),
                                        extras.getString(OrderEdit.ORDER_STATE)
                                );
                                orderToDelete.setId(id);
                                mOrderViewModel.delete(orderToDelete);

                                System.out.println("TO ELIMINATE order_id: " + id);
                            } else {
                                updatedOrder.setId(id);
                                mOrderViewModel.update(updatedOrder);
                            }

                            // Eliminar el observador después de la primera notificación
                            orderDetailsLiveData.removeObserver(this);
                        }
                    };
                    orderDetailsLiveData.observe(this, observer);

                    break;
            }
        }
    }

    /**
     * Maneja la selección de elementos en el menú contextual.
     * @param item El elemento del menú seleccionado.
     * @return `true` si se ha manejado correctamente.
     */
    public boolean onContextItemSelected(MenuItem item) {
        Orders current = mOrderAdapter.getCurrent();
        switch (item.getItemId()) {
            case DELETE_ID:
                Toast.makeText(
                        getApplicationContext(),
                        "Deleting " + current.getName(),
                        Toast.LENGTH_LONG).show();
                mOrderViewModel.delete(current);
                return true;
            case EDIT_ID:
                editOrder(current);
                return true;
            case SEND_WAS_ID:
                sendMessage(current, "WhatsApp");
                return true;
            case SEND_SMS_ID:
                sendMessage(current, "SMS");
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Inicia la actividad de creación de un nuevo pedido.
     */
    private void createOrder() {
        Intent intent = new Intent(this, OrderEdit.class);
        startActivityForResult(intent, ACTIVITY_CREATE);
    }

    /**
     * Inicia la actividad de edición de un pedido existente.
     * @param current El pedido a editar.
     */
    private void editOrder(Orders current) {
        Intent intent = new Intent(this, OrderEdit.class);
        intent.putExtra(OrderEdit.ORDER_NAME, current.getName());
        intent.putExtra(OrderEdit.ORDER_PHONE, current.getPhone());
        intent.putExtra(OrderEdit.ORDER_DATE, current.getDate());
        intent.putExtra(OrderEdit.ORDER_STATE, current.getState());
        intent.putExtra(OrderEdit.ORDER_ID, current.getId());
        System.out.println("EDITANDO order_id: " + current.getId());
        System.out.println("EXTRAS : " + intent.getExtras());
        startActivityForResult(intent,ACTIVITY_EDIT);
    }

    /**
     * Envía un mensaje al cliente.
     * @param current El pedido del cliente.
     * @param method El método de envío del mensaje. (WhatsApp o SMS)
     */
    private void sendMessage(Orders current, String method) {
        String message = "Pedido de " + current.getName() + " con teléfono " + current.getPhone() +
                " y fecha " + current.getDate() + " en estado " + current.getState();

        if (method.equals("WhatsApp"))
            mSendWasAbstraction.send("+34 "+current.getPhone(), message);
        else if (method.equals("SMS"))
            mSendSMSAbstraction.send("+34 "+current.getPhone(), message);
    }
}