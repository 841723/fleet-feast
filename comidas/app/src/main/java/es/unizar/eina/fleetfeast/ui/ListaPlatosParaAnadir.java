package es.unizar.eina.fleetfeast.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.unizar.eina.fleetfeast.R;
import es.unizar.eina.fleetfeast.database.Plate;

/**
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class ListaPlatosParaAnadir extends AppCompatActivity {
    private PlateViewModel mPlateViewModel;
    private int orderid;

    RecyclerView mRecyclerView;

    RadioGroup mRadioGroup;
    RadioButton mRadioName;
    RadioButton mRadioCat;
    RadioButton mRadioBoth;

    Button mButton;

    PlateListAdapterParaAnadir mAdapter;

    /**
     * Función que se ejecuta al crear la actividad
     * @param savedInstanceState Instancia de la actividad a guardar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platelist_para_anadir);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioName = findViewById(R.id.radioButton);
        mRadioCat = findViewById(R.id.radioButton2);
        mRadioBoth = findViewById(R.id.radioButton3);
        mButton = findViewById(R.id.button_save2);

        mRadioName.setChecked(true);
        mRadioCat.setChecked(false);
        mRadioBoth.setChecked(false);

        mPlateViewModel = new ViewModelProvider(this).get(PlateViewModel.class);
        OrderDetailsViewModel mOrderDetailsViewMolder = new ViewModelProvider(this).get(OrderDetailsViewModel.class);

        Bundle extras = getIntent().getExtras();
        do {
            extras = getIntent().getExtras();
        } while (extras == null);

        long orderidL = extras.getLong(OrderEdit.ORDER_ID);
        orderid = (int) orderidL;

        mAdapter = new PlateListAdapterParaAnadir(new PlateListAdapterParaAnadir.PlateDiff(), mOrderDetailsViewMolder, orderid);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRadioName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateList("name");
            }
        });

        mRadioCat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateList("category");
            }
        });

        mRadioBoth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateList("both");
            }
        });
//

        mButton.setOnClickListener(v -> {
            finish();
        });


        mPlateViewModel.getAllPlatesNotInOrder(orderid).observe(this, plates -> {
            List<Plate> filteredList = orderAndFilter(plates, "name");
            mAdapter.submitList(filteredList);
        });
    }

    /**
     * Función que actualiza la lista de platos
     * @param orderby Tipo de ordenación
     */
    private void updateList(String orderby) {
        mPlateViewModel.getAllPlatesNotInOrder(orderid).observe(this, plates -> {
            List<Plate> filteredList = orderAndFilter(plates, orderby);
            mAdapter.submitList(filteredList);
        });
    }

    /** Función que ordena y filtra la lista de platos
     * @param plateList Lista de platos
     * @param orderby Tipo de ordenación
     * @return Lista de platos ordenada y filtrada
     */
    private List<Plate> orderAndFilter(List<Plate> plateList, String orderby) {
        if (orderby.equals("name")) {
            plateList.sort(Comparator.comparing(Plate::getName));
        } else if (orderby.equals("category")) {
            plateList.sort(Comparator.comparing(Plate::getCategory, new PlateCategoryComparator()));
        } else if (orderby.equals("both")) {
            plateList.sort(Comparator.comparing(Plate::getCategory, new PlateCategoryComparator())
                    .thenComparing(Plate::getName));
        }
        return plateList;
    }

    /**
     * Clase que compara las categorías de los platos
     */
    private static class PlateCategoryComparator implements Comparator<String> {
        @Override
        public int compare(String category1, String category2) {
            // Asigna un valor numérico a cada categoría
            int value1 = getCategoryValue(category1);
            int value2 = getCategoryValue(category2);

            // Compara los valores numéricos
            return Integer.compare(value1, value2);
        }

        /**
         * Función que asigna un valor numérico a cada categoría
         * @param category Categoría del plato
         * @return Valor numérico de la categoría
         */
        private int getCategoryValue(String category) {
            switch (category) {
                case "PRIMERO":
                    return 1;
                case "SEGUNDO":
                    return 2;
                case "POSTRE":
                    return 3;
                default:
                    return 0; // Valor predeterminado para otras categorías
            }
        }
    }
}
