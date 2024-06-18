package es.unizar.eina.fleetfeast.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Comparator;
import java.util.List;

import es.unizar.eina.fleetfeast.database.Plate;
import es.unizar.eina.fleetfeast.R;

/**
 * @author Abel Romeo
 * @author Diego Roldán
 * */
public class ListaPlatos extends AppCompatActivity {
    private PlateViewModel mPlateViewModel;

    public static final int ACTIVITY_CREATE = 1;

    public static final int ACTIVITY_EDIT = 2;

    static final int INSERT_ID = Menu.FIRST;
    static final int DELETE_ID = Menu.FIRST + 1;
    static final int EDIT_ID = Menu.FIRST + 2;

    RecyclerView mRecyclerView;

    RadioGroup mRadioGroup;

    RadioButton mRadioName;
    RadioButton mRadioCat;
    RadioButton mRadioBoth;

    PlateListAdapter mAdapter;

    FloatingActionButton mFab;

    /**
     * Crea la actividad.
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platelist);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioName = findViewById(R.id.radioButton);
        mRadioCat = findViewById(R.id.radioButton2);
        mRadioBoth = findViewById(R.id.radioButton3);

        mRadioName.setChecked(true);
        mRadioCat.setChecked(false);
        mRadioBoth.setChecked(false);

        mPlateViewModel = new ViewModelProvider(this).get(PlateViewModel.class);


        mRadioName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                updateList("name");
                mPlateViewModel.getAllPlatesSortBy("name").observe(this, plates -> {
                    // Update the cached copy of the notes in the adapter.
                    mAdapter.submitList(plates);
                });
            }
        });
        mRadioCat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                updateList("category");
                mPlateViewModel.getAllPlatesSortBy("category").observe(this, plates -> {
                    // Update the cached copy of the notes in the adapter.
                    mAdapter.submitList(plates);
                });
            }
        });
        mRadioBoth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                updateList("both");
                mPlateViewModel.getAllPlatesSortBy("both").observe(this, plates -> {
                    // Update the cached copy of the notes in the adapter.
                    mAdapter.submitList(plates);
                });
            }
        });
        updateList("name");

        mAdapter = new PlateListAdapter(new PlateListAdapter.PlateDiff());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            createPlate();
        });

        registerForContextMenu(mRecyclerView);
    }

    /**
     * Actualiza la lista de pedidos y la muestra en pantalla.
     */
    private void updateList(String orderby) {

        System.out.println("is going to order by: " + orderby);
        mPlateViewModel.getAllPlates().observe(this, plates -> {
            // Update the cached copy of the notes in the adapter.
            List<Plate> filteredList = orderAndFilter(plates, orderby);
            mAdapter.submitList(filteredList);
        });
    }

    /**
     * Ordena y filtra la lista de pedidos.
     * @param plateList La lista de pedidos.
     * @param orderby El criterio de ordenación.
     * @return La lista de pedidos ordenada y filtrada.
     */
    private List<Plate> orderAndFilter(List<Plate> plateList, String orderby) {
        if (orderby.equals("name")) {
            plateList.sort(Comparator.comparing(Plate::getName));

        } else if (orderby.equals("category")) {
            plateList.sort(Comparator.comparing(Plate::getCategory));

        } else if (orderby.equals("both")) {
            plateList.sort(Comparator.comparing(Plate::getCategory).thenComparing(Plate::getName));
        }
        return plateList;
    }


    /**
     * Crea el menu de opciones en la barra de accion.
     * @param menu El menu de opciones.
     * @return `true` si se ha creado correctamente.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.add_plate);
        return result;
    }

    /**
     * Maneja la seleccion de elementos en el menu de opciones.
     * @param item El elemento del menú seleccionado.
     * @return `true` si se ha manejado correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createPlate();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        if (resultCode != RESULT_OK) {
            Toast.makeText(
                    getApplicationContext(),
                    "ERROR: alguno de los campos vacios",
                    Toast.LENGTH_LONG).show();
        } else {
            switch (requestCode) {
                case ACTIVITY_CREATE:
                    Plate newPlate = new Plate(extras.getString(PlateEdit.PLATE_NAME)
                            , extras.getString(PlateEdit.PLATE_DESCRIPTION)
                            , extras.getString(PlateEdit.PLATE_CATEGORY)
                            , extras.getFloat(PlateEdit.PLATE_PRIZE));
                    mPlateViewModel.insert(newPlate);
                    break;
                case ACTIVITY_EDIT:
                    int id = extras.getInt(PlateEdit.PLATE_ID);
                    float prize = extras.getFloat(PlateEdit.PLATE_PRIZE);

                    Plate updatedPlate = new Plate(extras.getString(PlateEdit.PLATE_NAME)
                            , extras.getString(PlateEdit.PLATE_DESCRIPTION)
                            , extras.getString(PlateEdit.PLATE_CATEGORY)
                            , prize);
                    updatedPlate.setId(id);
                    mPlateViewModel.update(updatedPlate);
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
        Plate current = mAdapter.getCurrent();
        switch (item.getItemId()) {
            case DELETE_ID:
                Toast.makeText(
                        getApplicationContext(),
                        "Deleting " + current.getName(),
                        Toast.LENGTH_LONG).show();
                mPlateViewModel.delete(current);
                return true;
            case EDIT_ID:
                editPlate(current);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Inicia la actividad de creación de un nuevo plato.
     */
    private void createPlate() {
        Intent intent = new Intent(this, PlateEdit.class);
        startActivityForResult(intent, ACTIVITY_CREATE);
    }

    /**
     * Inicia la actividad de edición de un plato existente.
     * @param current El plato a editar.
     */
    private void editPlate(Plate current) {
        Intent intent = new Intent(this, PlateEdit.class);
        intent.putExtra(PlateEdit.PLATE_NAME, current.getName());
        intent.putExtra(PlateEdit.PLATE_DESCRIPTION, current.getDescription());
        intent.putExtra(PlateEdit.PLATE_CATEGORY, current.getCategory());
        intent.putExtra(PlateEdit.PLATE_PRIZE, current.getPrize());
        intent.putExtra(PlateEdit.PLATE_ID, current.getId());
        startActivityForResult(intent, ACTIVITY_EDIT);
    }
}