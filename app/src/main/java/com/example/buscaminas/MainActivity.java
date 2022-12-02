package com.example.buscaminas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    //laayout donde se desarrolla el juego
    TableLayout tableLayout;
    //array list que sera el patron con el que se generara el juego
    ArrayList<Integer> patron;
    //array para almacenar la posicion de las bombas que se han encontrado
    ArrayList<Integer> posicionB;
    //array para almacenar los botones pulsados
    ArrayList<Integer> pulsados;
    //int para almacenar la semilla con la que se generara el juego
    int semilla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableLayout = findViewById(R.id.tablero);
        patron = new ArrayList<Integer>();
        posicionB = new ArrayList<Integer>();
        pulsados = new ArrayList<Integer>();
        //seleccionamos la semilla con la que se genera el juego y creamos la tabla del juego
        semilla = 8;
        crearTabla(8);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int n = item.getItemId();
        switch (n) {
            case R.id.dificultad:
                //Dialog donde podremos seleccionar la dificultad
                String[] dificultad = {"8x8(10 bombas)", "12x12(30 bombas)", "16x16(60 bombas)"};
                AlertDialog.Builder dificultades = new AlertDialog.Builder(this);
                dificultades.setTitle("Dificultades");
                dificultades.setSingleChoiceItems(dificultad, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 2:
                                semilla = 16;
                                crearTabla(16);
                                break;
                            case 0:
                                semilla = 8;
                                crearTabla(8);
                                break;
                            case 1:
                                semilla = 12;
                                crearTabla(12);
                                break;
                        }
                    }
                });
                dificultades.setPositiveButton("Aceptar", null);
                dificultades.show();
                break;
            case R.id.instrucciones:
                //Alert dialog para mostar las instrucciones
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Instrucciones");
                builder.setMessage("El usuario puede efectuar dos acciones: Marcar mina (click largo) o descubrir mina (click corto).\n" +
                        "\n" +
                        "Si realiza un click largo en una mina se pondra una bandera y saldra que ha descubieto una mina. Si las descubres todas ganas.\n" +
                        "\n" +
                        "Si haces un click largo donde no hay bomba o clica normal en una bomba perdera.");
                builder.setPositiveButton("Aceptar", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.nuevo:
                //recreamos el juego al darle a nuevo
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo para crear el tablero
     *
     * @param num num para saber el Filas X Columnas
     */
    public void crearTabla(int num) {
        //borramos las views del table layout
        tableLayout.removeAllViews();
        //generamos un patron
        this.generarPatron();
        int k = 0;
        //Generamos filas y columnes que contendran ImageButtons si son bombas o Botones
        //Esto lo sabremos por que hemos asociado cada boton a una posicion del array patron para saber que son y cuantas bombas tienen alrededor
        for (int i = 0; i < num; i++) {
            //Creamos fila
            TableRow fila = new TableRow(getApplicationContext());

            // Creamos parámetros
            TableLayout.LayoutParams lpFila = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
            lpFila.weight = 1;

            //Asignamos parámetros
            fila.setLayoutParams(lpFila);

            for (int j = 0; j < num; j++) {
                if (patron.get(k) != -1) {
                    //Creamos params
                    TableRow.LayoutParams lpBoton = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    lpBoton.weight = 1;
                    // Creamos botón
                    Button boton = new Button(getApplicationContext());
                    boton.setId(k);
                    boton.setOnClickListener(this::voltear);
                    boton.setOnLongClickListener(this::bandera);
                    //Asignamos parámetros
                    boton.setLayoutParams(lpBoton);
                    boton.setTextSize(25 - num);
                    //Añadir botón a fila
                    fila.addView(boton);
                } else {
                    //Creamos params
                    TableRow.LayoutParams lpBotonImg = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    lpBotonImg.weight = 1;
                    // Creamos botón
                    ImageButton botonImg = new ImageButton(getApplicationContext());
                    botonImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    botonImg.setMaxWidth(16);
                    botonImg.setMaxHeight(16);
                    botonImg.setId(k);
                    botonImg.setAdjustViewBounds(true);
                    botonImg.setOnClickListener(this::voltear);
                    botonImg.setOnLongClickListener(this::bandera);
                    //Asignamos parámetros
                    botonImg.setLayoutParams(lpBotonImg);
                    //Añadir botón a fila
                    fila.addView(botonImg);
                }
                k++;
            }

            tableLayout.addView(fila);

        }
    }

    /**
     * Metodo para colocar banderas en el tablero
     *
     * @param view boton que hace el click largo
     * @return
     */
    private boolean bandera(View view) {
        //si el boton no es una bomba aparecera un AlertDialog que indicara que ha ganado el juego
        if (patron.get(view.getId()) != -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ha perdido");
            builder.setMessage("Usted ha intentado colocar una bandera donde no habia una bomba");
            builder.setCancelable(false);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    recreate();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            //si el boton es una bomba comprobamos que no este ya marcada y si es la ultima en ser marcada para indicar una victoria
        } else if (!posicionB.contains(view.getId())) {
            ImageButton ib = findViewById(view.getId());
            ib.setImageResource(R.drawable.bandera);
            posicionB.add(view.getId());
            Toast.makeText(this, "Ha encontrado una mina", Toast.LENGTH_SHORT).show();
            switch (semilla) {
                case 8:
                    if (posicionB.size() == 10)
                        Toast.makeText(this, "Has ganado", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    if (posicionB.size() == 30)
                        Toast.makeText(this, "Has ganado", Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    if (posicionB.size() == 60)
                        Toast.makeText(this, "Has ganado", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        return true;
    }

    /**
     * Metodo para generar un patron
     */
    public void generarPatron() {
        //rellenaremos el array de 0 y llamaremos al metodo bombas para añadir bombas segun la semilla
        patron.clear();
        posicionB.clear();
        pulsados.clear();
        for (int i = 0; i < (semilla * semilla); i++) {
            patron.add(0);
        }
        switch (semilla) {
            case 8:
                //10 minas
                this.bombas(10);
                break;
            case 12:
                //30 minas
                this.bombas(30);
                break;
            case 16:
                //60 minas
                this.bombas(60);
                break;
        }
        this.calculabombas();
    }

    /**
     * Metodo para ingresar bombas
     *
     * @param numbomba numero de bombas
     */
    public void bombas(int numbomba) {
        //introducimos el numero de bombas indicado y las barajamos en el array
        for (int i = 0; i < numbomba; i++) {
            patron.set(i, -1);
        }
        Collections.shuffle(patron);
    }

    /**
     * Metodo para calcular donde estan las bombas
     */
    private void calculabombas() {
        //segun la posicion del array calcula cuantas bombas hay cerca acorde a la posicion en la matriz y a la semilla y cambia el 0 en el array por el numero de bombas
        for (int i = 0; i < patron.size(); i++) {
            int count = 0;
            if (patron.get(i) != -1) {
                if (i != semilla - 1 && i != patron.size() - 1 && (i + 1) % semilla != 0 && patron.get(i + 1) == -1)
                    count++;
                if (i != patron.size() - semilla && i != patron.size() - 1 && i < patron.size() - semilla - 1 && patron.get(i + semilla) == -1)
                    count++;
                if (i != semilla - 1 && i != patron.size() - semilla && i != patron.size() - 1 && (i + 1) % semilla != 0 && i < patron.size() - semilla - 1 && patron.get(i + semilla + 1) == -1)
                    count++;
                if (i != 0 && i != patron.size() - semilla && i != patron.size() - 1 && i % semilla != 0 && i < patron.size() - semilla - 1 && patron.get(i + semilla - 1) == -1)
                    count++;
                if (i != 0 && i != patron.size() - semilla && i % semilla != 0 && patron.get(i - 1) == -1)
                    count++;
                if (i != 0 && i != semilla - 1 && i > semilla - 1 && patron.get(i - semilla) == -1)
                    count++;
                if (i != 0 && i != semilla - 1 && i != patron.size() - 1 && (i + 1) % semilla != 0 && i > semilla - 1 && patron.get(i - semilla + 1) == -1)
                    count++;
                if (i != 0 && i != semilla - 1 && i != patron.size() - semilla && i % semilla != 0 && i > semilla - 1 && patron.get(i - semilla - 1) == -1)
                    count++;
                patron.set(i, count);
            }
        }
    }

    /**
     * Metodo para voltear un boton cuando se le pulsa
     *
     * @param view boton pulsado
     */
    public void voltear(View view) {
        int id = view.getId();
        //si el boton no es una bomba...
        if (patron.get(id) != -1) {
            //si no se ha pulsado ya...
            if (!pulsados.contains(id)) {
                Button b = findViewById(id);
                //si es cero llamamos al metodo limpiar ceros para recorrer el area de alrededor y voltear los botones
                if (patron.get(id) == 0) {
                    limpiarceros(b);
                } else {
                    //si no es cero mostramos cuantas minas hay a su alrededor
                    b.setText("" + patron.get(id));
                }
                pulsados.add(id);
            }
        } else {
            //si es una bomba que no ha sido descubierta
            if (!posicionB.contains(id)) {
                //mostramos la bomba y un dialogo indicando que ha perdido
                ImageButton ib = findViewById(view.getId());
                ib.setImageResource(R.drawable.bomba);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ha perdido");
                builder.setCancelable(false);
                builder.setMessage("Usted ha accionado una bomba");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recreate();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    /**
     * Metodo para encontrar 0 y hacerlos no clicables
     *
     * @param b boton
     */
    public void limpiarceros(Button b) {
        int i = b.getId();
        if (patron.get(i) == 0 && !pulsados.contains(i)) {
            b.setEnabled(false);
            pulsados.add(i);
            //busca numeros delante y arriba
            if(i+semilla<patron.size()-1&&patron.get(i+semilla)!=-1)limpiarceros(findViewById(i+semilla));
            for (int j = i+1; j <patron.size(); j++) {
                if(patron.get(j)!=-1) {
                    limpiarceros(findViewById(j));
                    if (j + semilla < patron.size() - 1&&patron.get(j+semilla)!=-1) limpiarceros(findViewById(j + semilla));
                    if (patron.get(j) > 0) break;
                }
            }
            //busca numeros debajo y atras
            if(i-semilla>=0&&patron.get(i-semilla)!=-1)limpiarceros(findViewById(i-semilla));
            for (int j = i-1; j >=0; j--) {
                if(patron.get(j)!=-1) {
                    limpiarceros(findViewById(j));
                    if (j - semilla >=0&&patron.get(j-semilla)!=-1) limpiarceros(findViewById(j - semilla));
                    if (patron.get(j) > 0) break;
                }
            }
        } else if (!pulsados.contains(i)&&patron.get(i)!=-1) {
            b.setText("" + patron.get(i));
            pulsados.add(i);
        }
    }
}
