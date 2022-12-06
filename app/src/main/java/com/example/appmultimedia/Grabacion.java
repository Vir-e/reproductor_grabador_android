package com.example.appmultimedia;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class Grabacion extends androidx.appcompat.widget.AppCompatButton{
    // variable de control para la grabacion
    private boolean grabando = true;
    // activity de la clase principal
    MainActivity activity;

    public Grabacion(@NonNull Context context) {
        super(context);
        setBackgroundColor(Color.parseColor("#5C0808"));
        setTextColor(Color.WHITE);
        setTypeface(null, Typeface.BOLD);
        setText("Grabar Audio");
        setOnClickListener(clicker);
        activity = (MainActivity) context;  //traemos la activity desde la que se va a llevar la lógica
    }

    // manejador del evento
    OnClickListener clicker = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(grabando){
                setText("Parar grabación");
                grabando=false;
                // empezar a grabar
                activity.empezarGrabacion();

            }else{
                setText("Grabar Audio");
                grabando=true;
                // Parar grabacion
                activity.pararGrabacion();
            }

        }
    };


}
