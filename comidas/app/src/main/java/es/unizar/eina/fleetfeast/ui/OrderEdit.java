package es.unizar.eina.fleetfeast.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.unizar.eina.fleetfeast.R;
import es.unizar.eina.fleetfeast.database.OrderDetails;
import es.unizar.eina.fleetfeast.database.Orders;

/**
 * Pantalla utilizada para la creación o edición de un pedido.
 *
 * Esta actividad permite al usuario ingresar o editar información relacionada
 * con un pedido, incluyendo nombre del cliente, teléfono, fecha, y platos
 * seleccionados. Los datos ingresados se devuelven a la actividad llamadora.
 *
 * Utiliza un conjunto de claves para pasar datos a través de intents,
 * como ORDER_NAME, ORDER_PHONE, etc.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class OrderEdit extends AppCompatActivity {

    public static final String ORDER_NAME = "name";
    public static final String ORDER_PHONE = "phone";
    public static final String ORDER_DATE = "date";
    public static final String ORDER_STATE = "state";
    public static final String ORDER_ID = "id";

    private EditText mNameText;
    private TextView mTotalPriceText;
    private EditText mPhoneText;
    private EditText mDateText;
    private Button mDateButton;
    private Spinner mStateSpinner;
    private RecyclerView mRecyclerViewOrderDetails;
    private Integer mRowId;

    private OrderDetailsListAdapter mAdapter;
    private OrderDetailsViewModel mOrderDetailsViewModel;

    private Button mSaveButton;
    private Button mAddPlatesButton;

    private long order_id = -1;

    /** Devuelve el id del pedido.
     * Si no existe, crea un pedido vacío en la base de datos y devuelve su id.
     * @return El id del pedido que se esta editando.
     */
    private long getOrder_id() {
        if (order_id == -1) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                order_id = extras.getLong(OrderEdit.ORDER_ID);
            }
            else {
                // insert into database an empty order
                Orders order = new Orders("", 0, "", "SOLICITADO");

                OrderViewModel mOrdersViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
                order_id = mOrdersViewModel.insertAndWait(order);
                System.out.println("CREADO NUEVO order_id: " + order_id);
            }
        }
        return order_id;
    }

    /**
     * Crea la actividad.
     * @param savedInstanceState El estado de la instancia guardada.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderedit);

        order_id = getOrder_id();

        mNameText = findViewById(R.id.name);
        mTotalPriceText = findViewById(R.id.total_price_text);
        mPhoneText = findViewById(R.id.phone);
        mDateText = findViewById(R.id.date_text);
        mDateButton = findViewById(R.id.date);
        mStateSpinner = findViewById(R.id.state);
        mAddPlatesButton = findViewById(R.id.button);

        mRecyclerViewOrderDetails = findViewById(R.id.recyclerview_plates);
        PlateViewModel mPlateViewModel = new ViewModelProvider(this).get(PlateViewModel.class);
        mOrderDetailsViewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);

        mAdapter = new OrderDetailsListAdapter(new OrderDetailsListAdapter.OrderDetailsDiff(),
                mPlateViewModel, mOrderDetailsViewModel);
        mRecyclerViewOrderDetails.setAdapter(mAdapter);
        mRecyclerViewOrderDetails.setLayoutManager(new LinearLayoutManager(this));

        mOrderDetailsViewModel.getOrdersDetailsByOrderId(order_id).observe(this, plates -> {
            // Update the cached copy of the notes in the adapter.
            mAdapter.submitList(plates);
            float sum = 0.0f;
            for (OrderDetails plate : plates) {
                sum += plate.getPrize() * plate.getQuantity();
            }
            sum *= 100;
            sum = Math.round(sum);
            sum /= 100;
            mTotalPriceText.setText(sum+"");
        });

        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker = findViewById(R.id.timePicker);
        datePicker.setVisibility(android.view.View.GONE);
        timePicker.setVisibility(android.view.View.GONE);

        mAddPlatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderEdit.this, ListaPlatosParaAnadir.class);
                System.out.println("order_id añadido: " + order_id);
                intent.putExtra(OrderEdit.ORDER_ID, order_id);
                startActivity(intent);
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Mostrar el DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        OrderEdit.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Configurar el TimePickerDialog
                                showTimePicker(view,year,month,dayOfMonth);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });


        mSaveButton = findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(view -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            try {
                Date date = sdf.parse(mDateText.getText().toString());
                if (date != null) {
                    System.out.println("DATE: " + date.toString());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    // entre las 19:30 y las 23:00
                    boolean correctTime =
                        (hourOfDay == 19 && minute >= 30) ||
                        (hourOfDay > 19 && hourOfDay < 23) ||
                        (hourOfDay == 23 && minute == 0);

                    if (date.before(new Date())) {
                        System.out.println("ERROR: FECHA ANTERIOR A LA ACTUAL");
                        Toast.makeText(
                                getApplicationContext(),
                                "ERROR: FECHA ANTERIOR A LA ACTUAL",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if ((dayOfWeek == Calendar.MONDAY) || (!correctTime)) {
                        System.out.println("ERROR: HORARIO DE RECOGIDA - MARTES A DOMINGO DE 19:30 A 23:00");
                        Toast.makeText(
                                getApplicationContext(),
                                "ERROR: HORARIO DE RECOGIDA - MARTES A DOMINGO DE 19:30 A 23:00",
                                Toast.LENGTH_LONG).show();

                        return;
                    }
                    else {
                        System.out.println("HORARIO DE RECOGIDA CORRECTO");
                    }
                }
                else {
                    System.out.println("DATE: NULL");
                }
            }
            catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                return;
            }

            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mNameText.getText()) || TextUtils.isEmpty(mPhoneText.getText())
                    || TextUtils.isEmpty(mDateText.getText())) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: FALTAN CAMPOS POR RELLENAR",
                        Toast.LENGTH_LONG).show();
            }
            else {
                replyIntent.putExtra(OrderEdit.ORDER_NAME, mNameText.getText().toString());
                replyIntent.putExtra(OrderEdit.ORDER_PHONE, Integer.parseInt(mPhoneText.getText().toString()));
                replyIntent.putExtra(OrderEdit.ORDER_DATE, mDateText.getText().toString());
                replyIntent.putExtra(OrderEdit.ORDER_STATE, mStateSpinner.getSelectedItem().toString());
                replyIntent.putExtra(OrderEdit.ORDER_ID, order_id);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });


        populateFields();
    }

    /**
     * Puebla los campos de la pantalla con los datos recibidos.
     */
    private void populateFields() {
        mRowId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mNameText.setText(extras.getString(OrderEdit.ORDER_NAME));
            mPhoneText.setText(extras.getLong(OrderEdit.ORDER_PHONE)+"");
            mDateText.setText(extras.getString(OrderEdit.ORDER_DATE));

            order_id = extras.getLong(OrderEdit.ORDER_ID);

            String state = extras.getString(OrderEdit.ORDER_STATE);
            if (state != null) {
                switch (state) {
                    case "SOLICITADO":
                        mStateSpinner.setSelection(0);
                        break;
                    case "PREPARADO":
                        mStateSpinner.setSelection(1);
                        break;
                    case "RECOGIDO":
                        mStateSpinner.setSelection(2);
                        break;
                    default:
                        mStateSpinner.setSelection(0);
                        break;
                }
            }
        }
    }

    /**
     * Muestra el TimePickerDialog.
     * @param datePicker El DatePickerDialog.
     * @param year El año seleccionado.
     * @param month El mes seleccionado.
     * @param dayOfMonth El día seleccionado.
     */
    private void showTimePicker(DatePicker datePicker, int year, int month, int dayOfMonth) {
        // Obtener la hora actual
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Mostrar el TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                OrderEdit.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Aquí puedes manejar la fecha y hora seleccionadas
                        // Por ejemplo, mostrarlas en un TextView
                        String minute_str, hourOfDay_str,dayOfMonth_str,month_str;
                        if (minute < 10) {
                            minute_str = "0" + minute;
                        } else {
                            minute_str = "" + minute;
                        }
                        if (hourOfDay < 10) {
                            hourOfDay_str = "0" + hourOfDay;
                        } else {
                            hourOfDay_str = "" + hourOfDay;
                        }
                        if ((datePicker.getMonth()+1) < 10) {
                            month_str = "0"+(datePicker.getMonth()+1);
                        }
                        else {
                            month_str = ""+(datePicker.getMonth()+1);
                        }
                        if (datePicker.getDayOfMonth() < 10) {
                            dayOfMonth_str = "0"+datePicker.getDayOfMonth();
                        }
                        else {
                            dayOfMonth_str = ""+datePicker.getDayOfMonth();
                        }

                       String dateTime = datePicker.getYear() + "/" +
                                month_str + "/" + dayOfMonth_str +
                                "   " + hourOfDay_str + ":" + minute_str;
                        // Puedes mostrarlo en un TextView o realizar cualquier otra acción
                        // Aquí solo lo imprimo en la consola para demostración
                        mDateText.setText(dateTime);
                    }
                },
                hour, minute, true);
        timePickerDialog.show();
    }
}
