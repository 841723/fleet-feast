package es.unizar.eina.send;

import android.app.Activity;

public interface SendImplementor {
    /**  Actualiza la actividad desde la cual se abrira la actividad de envio */
    public void setSourceActivity(Activity source);

    /**  Recupera la actividad desde la cual se abrira la actividad de envio */
    public Activity getSourceActivity();

    /** Permite lanzar la actividad encargada de gestionar el envio de notas */
    public void send (String phone, String message);
}
