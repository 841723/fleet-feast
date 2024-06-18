package es.unizar.eina.fleetfeast.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Clase anotada como entidad que representa un plato y que consta de nombre,
 * descripción, categoría y precio.
 *
 * Esta clase define la estructura de un plato en la base de datos, incluyendo
 * sus propiedades como el nombre, descripción, categoría y precio. Además,
 * proporciona métodos para acceder y modificar estos atributos.
 *
 * @author Abel Romeo
 * @author Diego Roldán
 */
@Entity(tableName = "plate")
public class Plate {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @NonNull
    @ColumnInfo(name = "prize")
    private Float prize;

    /**
     * Constructor de la clase Plate.
     *
     * @param name        El nombre del plato.
     * @param description La descripción del plato.
     * @param category    La categoría del plato.
     * @param prize       El precio del plato.
     */
    public Plate(@NonNull String name, String description,
                 @NonNull String category, @NonNull Float prize) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.prize = prize;
    }

    /**
     * Constructor de la clase Plate a partir de otro plato.
     * @param plate El plato a copiar.
     */
    public Plate(Plate plate) {
        this.id = plate.getId();
        this.name = plate.getName();
        this.description = plate.getDescription();
        this.category = plate.getCategory();
        this.prize = plate.getPrize();
    }

    /** Devuelve el identificador del plato
     */
    public int getId(){
        return this.id;
    }

    /** Permite actualizar el identificador de un plato
     * @param id El nuevo identificador del plato.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Permite actualizar el identificador de un plato
     * @param category La nueva categoría del plato.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /** Devuelve el nombre del plato
     */
    public String getName(){
        return this.name;
    }

    /** Devuelve la descripción del plato
     */
    public String getDescription(){
        return this.description;
    }

    /** Devuelve la categoría del plato
     */
    public String getCategory(){
        return this.category;
    }

    /** Devuelve el precio del plato
     */
    public Float getPrize(){
        return this.prize;
    }

    /** Permite actualizar el precio de un plato
     * @param prize El nuevo precio del plato.
     */
    public void setPrize(float prize) {
        this.prize = prize;
    }
}
