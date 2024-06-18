package es.unizar.eina.fleetfeast.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import es.unizar.eina.fleetfeast.R;

/**
 * Pantalla utilizada para la creación o edición de un plato.
 * Permite al usuario ingresar o editar información sobre un plato,
 * como nombre, descripción, categoría y precio.
 * Los datos pueden ser guardados o cancelados.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
public class PlateEdit extends AppCompatActivity {

    public static final String PLATE_NAME = "name";
    public static final String PLATE_DESCRIPTION = "description";
    public static final String PLATE_CATEGORY = "category";
    public static final String PLATE_PRIZE = "prize";
    public static final String PLATE_ID = "id";

    private EditText mNameText;
    private EditText mDescriptionText;
    private Spinner mCategorySpinner;
    private EditText mPrizeText;
    private Integer mRowId;
    Button mSaveButton;

    /**
     * Crea la pantalla de edición de platos.
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plateedit);

        mNameText = findViewById(R.id.name);
        mDescriptionText = findViewById(R.id.description);
        mCategorySpinner = findViewById(R.id.category);
        mPrizeText = findViewById(R.id.prize);

        mSaveButton = findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mNameText.getText())
                    || TextUtils.isEmpty(mDescriptionText.getText())
                    || TextUtils.isEmpty(mPrizeText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                replyIntent.putExtra(PlateEdit.PLATE_NAME, mNameText.getText().toString());
                replyIntent.putExtra(PlateEdit.PLATE_DESCRIPTION, mDescriptionText.getText().toString());
                replyIntent.putExtra(PlateEdit.PLATE_CATEGORY, mCategorySpinner.getSelectedItem().toString());
                replyIntent.putExtra(PlateEdit.PLATE_PRIZE, Float.parseFloat(mPrizeText.getText().toString()));
                if (mRowId != null) {
                    replyIntent.putExtra(PlateEdit.PLATE_ID, mRowId.intValue());
                }
                setResult(RESULT_OK, replyIntent);
            }
            finish();
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
            mNameText.setText(extras.getString(PlateEdit.PLATE_NAME));
            mDescriptionText.setText(extras.getString(PlateEdit.PLATE_DESCRIPTION));
            float prize = extras.getFloat(PlateEdit.PLATE_PRIZE);
            mPrizeText.setText(String.valueOf(prize));
            mRowId = extras.getInt(PlateEdit.PLATE_ID);

            String category = extras.getString(PlateEdit.PLATE_CATEGORY);
            if (category != null) {
                int spinnerPosition = 0;
                switch (category) {
                    case "PRIMERO":
                        spinnerPosition = 0;
                        break;
                    case "SEGUNDO":
                        spinnerPosition = 1;
                        break;
                    case "POSTRE":
                        spinnerPosition = 2;
                        break;
                    default:
                        spinnerPosition = 0;
                        break;
                }
                mCategorySpinner.setSelection(spinnerPosition);
            }
        }
    }
}
