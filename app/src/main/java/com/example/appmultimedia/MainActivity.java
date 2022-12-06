package com.example.appmultimedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener{

    //grabador
    private MediaRecorder mediaRecorder = null;
    //reproductor
    private MediaPlayer mediaPlayer = null;
    //permisos
    private final String[]permissions = {Manifest.permission.RECORD_AUDIO};
    // variable de permiso concedido
    private boolean permissionToRecordAccepted = false;
    //botones
    Button btnReproducir, btnPausar, btnAvanzar, btnRetroceder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = findViewById(R.id.layout);
        Grabacion btnGrabacion = new Grabacion(this);// capturamos el boton creado por nosotros
        layout.addView(btnGrabacion);
        // Asociamos botones
        btnReproducir = findViewById(R.id.btnReproducir);
        btnPausar = findViewById(R.id.btnPausar);
        btnAvanzar = findViewById(R.id.btnAvanzar);
        btnRetroceder = findViewById(R.id.btnRetroceder);
        // Desactivamos por defecto pausar,avanzar y retroceder
        btnPausar.setEnabled(false);
        btnAvanzar.setEnabled(false);
        btnRetroceder.setEnabled(false);
        // Ponemos botones a la escucha
        btnReproducir.setOnClickListener(this);
        btnPausar.setOnClickListener(this);
        btnAvanzar.setOnClickListener(this);
        btnRetroceder.setOnClickListener(this);

    }
    // Metodo que es llamado al dar al boton de grabar
    public void empezarGrabacion(){
        //Chequeamos si tiene el permiso o no
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            //Inicializamos
            mediaRecorder = new MediaRecorder();
            //Seleccionamos la fueste de audio el microfono
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //Configurar la grabacion
            //Configurar el formanto de salida
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //Buscamos la ruta para guardar la grabacion
            String ruta = getFilesDir().getAbsolutePath() + File.separator + "Grabacion.3gp";
            mediaRecorder.setOutputFile(ruta);
            //Seleccionamos el codificador
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                //Preparamos la grabacion
                mediaRecorder.prepare();
            } catch (IOException e) {
                //Si falla la preparacion liberamos el recurso
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                e.printStackTrace();
                Toast.makeText(this, "Ha fallado el objeto MediaRecorder que permite la grabación de audio", Toast.LENGTH_SHORT).show();
            }
            //Comenzamos la grabación
            Toast.makeText(this, "Grabando audio", Toast.LENGTH_SHORT).show();
            mediaRecorder.start();
        }
        else{
            //Si no tenemos el permiso hacemos una petición
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1112);
        }

    }
    // Método que comprueba los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1112:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ){
            Toast.makeText(this, "Sin el permiso de grabación no podrá grabar audio", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            //Si nos dan el permito grabamos
            empezarGrabacion();
        }
    }


    // Metodo encargado de parar la grabación cuando volvemos a dar al boton de "Grabar/Parar grabacación"
    public void pararGrabacion(){
        mediaRecorder.stop();
        // resetamos para que no quede ninguna configuracion
        mediaRecorder.reset();
        //liberamos el objeto
        mediaRecorder.release();
        mediaRecorder=null;
        Toast.makeText(this, "Grabación detenida", Toast.LENGTH_SHORT).show();
    }


    // Manejador evento click
    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnReproducir:
                play();
                break;
            case R.id.btnPausar:
                pause();
                break;
            case R.id.btnAvanzar:
                avanzar();
                break;
            case R.id.btnRetroceder:
                retroceder();
                break;

        }


    }
    // Metodo llamado al pulsar el boton de reproducir
    public void play(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer= MediaPlayer.create(this,R.raw.sonadrumsiroko);   // iniciamos mediaPlayer y apuntamos al archivo mp3
        if(mediaPlayer==null){  // avisamos al usuario si no se encuentra el archivo de audio
            Toast.makeText(this, "No se ha encontrado el archivo de audio", Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.setOnCompletionListener(this);  //le añadimos el listener de evento para cuando termine la reproduccion
        mediaPlayer.start();    // empieza a reproducir el audio
        btnReproducir.setEnabled(false);
        btnPausar.setEnabled(true);
        btnAvanzar.setEnabled(true);
        btnRetroceder.setEnabled(true);

    }
    // Metodo llamado al pulsar el botón de pausar
    public void pause(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btnReproducir.setEnabled(true);
            btnPausar.setEnabled(false);
            btnAvanzar.setEnabled(false);
            btnRetroceder.setEnabled(false);

        }
    }
    // Metodo llamado al pulsar el botón de avanzar. Avanza 5 seg
    public void avanzar(){
        if(mediaPlayer!=null){
            int posicion = mediaPlayer.getCurrentPosition();    //posicion actual de reproduccion
            mediaPlayer.seekTo(posicion+5000);              // apuntamos a una nueva posicion

        }else{
            Toast.makeText(this, "No se ha podido avanzar en el audio", Toast.LENGTH_SHORT).show();
        }

    }
    // Metodo llamado al pulsar el botón de retroceder. Retrocede 5 seg
    public void retroceder(){
        if(mediaPlayer!=null){
            int posicion=mediaPlayer.getCurrentPosition();  //posicion actual de la reproduccion
            mediaPlayer.seekTo(posicion-5000);          //apuntamos a una nueva posicion
        }else{
            Toast.makeText(this, "No se ha podido retroceder el audio", Toast.LENGTH_SHORT).show();
        }

    }
    // Metodo que se invocará cuando el audio haya completado su reproduccion
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        btnReproducir.setEnabled(true); // vuelve a activar el boton de reproducir
    }
}