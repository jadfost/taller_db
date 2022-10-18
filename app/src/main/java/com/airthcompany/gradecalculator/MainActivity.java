package com.airthcompany.gradecalculator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    String databaseTable;
    DataBaseAdapter myDb;
    final int NO_INPUT = -1;

    HashMap<String, Button> hypoButtons;

    float averageGrade, targetGrade , currentTotalWeight, currentSumAndProduct, targetTotalWeight, dimelo;
    float targetNeeded;

    int Cont=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        hypoButtons = new HashMap<>(); // inicializando botones de calificaciones hipotéticas

        averageGrade = targetGrade  = currentTotalWeight = currentSumAndProduct = targetNeeded = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        databaseTable = i.getStringExtra("table");

        openDB();
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout); // recuperando el diseño principal
        Button addButton = (Button) findViewById(R.id.button_add); // recuperando el boton de agregar

        Cursor cursorAll = myDb.getAllRows(databaseTable); // Mostrando todos los elementos
        DisplayAllStuff(mainLayout, cursorAll);

        setTitle(databaseTable);
        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(final View v) { // agregar botón al hacer clic en oyente

                // Creación de cuadro de diálogo de alerta
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Añadir Nota");
                //alert.setMessage("Ingrese el nombre, puntaje, puntaje máximo y peso del artículo");

                // Campos de entrada
                final EditText nameInput = new EditText(v.getContext());
                nameInput.setHint("Nombre");

                final EditText scoreInput = new EditText(v.getContext());
                scoreInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                scoreInput.setHint("Nota");

                final EditText maxInput = new EditText(v.getContext());
                maxInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                maxInput.setHint("Maxima Nota");



                // Diseño para campos de entrada
                LinearLayout alertLayout = new LinearLayout(v.getContext());
                alertLayout.setOrientation(LinearLayout.VERTICAL);

                // Colocación de campos de entrada en el diseño
                alertLayout.addView(nameInput);
                alertLayout.addView(scoreInput);
                alertLayout.addView(maxInput);
                //alertLayout.addView(weightInput);

                alert.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // recuperar datos de los campos EditText
                        Editable nameValue = nameInput.getText();
                        Editable scoreValue = scoreInput.getText();
                        Editable maxValue = maxInput.getText();
                        int ye = 0;
                        Cont=Cont+1;
                        //Editable weightValue = weightInput.getText();

                        Long newId;
                        //Insertar en la base de datos
                        if(scoreValue.toString().length() == 0 || maxValue.toString().length() == 0){ // si no hay calificación colocada
                             newId = myDb.insertItem(nameValue.toString().trim(), NO_INPUT, NO_INPUT, Integer.parseInt(String.valueOf(ye)), databaseTable);
                        } else {
                             newId = myDb.insertItem(nameValue.toString().trim(), Integer.parseInt(scoreValue.toString()), Integer.parseInt(maxValue.toString()), Integer.parseInt(String.valueOf(ye)), databaseTable);
                        }

                        Cursor cursor = myDb.getRow(newId, databaseTable);

                        //Recuperando el mismo elemento de la base de datos
                        if(cursor.moveToFirst()){
                            DisplayAllStuff(mainLayout, cursor);
                        }
                    }
                });// botón positivo final

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancelado.
                    }
                });// Finalizar botón cancelado

                // Colocar el diseño en el cuadro de diálogo de alerta
                alert.setView(alertLayout);
                alert.show();

            }
        });
    }

    // crea y muestra botones de elementos de la base de datos
    private void DisplayAllStuff(final LinearLayout layout, Cursor cursor){

        if(cursor.moveToFirst()){
            do{
                final int id = cursor.getInt(DataBaseAdapter.COL_ROWID);
                final String name = cursor.getString(DataBaseAdapter.COL_NAME);
                final int score = cursor.getInt(DataBaseAdapter.COL_SCORE);
                final int max = cursor.getInt(DataBaseAdapter.COL_MAX);
                final int weight = cursor.getInt(DataBaseAdapter.COL_WEIGHT);

                float targetTotal, have;
                final float grade;
                have = targetTotal = 0;
                if(score != NO_INPUT && max != NO_INPUT) {
                    grade = (float) score;
                    currentTotalWeight += weight;
                    targetTotalWeight += weight;
                    currentSumAndProduct += grade ;
                    averageGrade = (currentSumAndProduct / Cont);

                } else {

                    grade = 0; // RELLENO

                    targetTotalWeight += weight;
                    targetTotal = targetTotalWeight * targetGrade/100;
                    have = averageGrade * (currentTotalWeight)/100;
                    targetNeeded = 100*(targetTotal - have)/(targetTotalWeight - currentTotalWeight);

                }

                // Mostrar datos a través de TextView en un diseño

                final LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(param);

                // Botón de nombre
                final Button nameButton = new Button(this);
                LayoutParams nameButtonParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                nameButton.setBackgroundResource(R.drawable.button_bg);
                nameButtonParam.weight = 4;
                nameButton.setLayoutParams(nameButtonParam);
                nameButton.setText(name);

                //onClick para el botón de nombre
                nameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder nameButtonAlert = new AlertDialog.Builder(v.getContext());

                        nameButtonAlert.setMessage("Actualizar o eliminar elemento");

                        final EditText nameInput = new EditText(v.getContext());
                        nameInput.setHint("Nombre");

                        nameButtonAlert.setPositiveButton("Actualizar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Editable updatedName = nameInput.getText();

                                myDb.updateRow(id, updatedName.toString().trim(), score, max, weight, databaseTable);
                                nameButton.setText(updatedName);
                            }
                        });

                        //ELIMINAR ARTÍCULO CON UN CLIC
                        nameButtonAlert.setNeutralButton("Borrar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cont = Cont - 1;
                                myDb.deleteRow(id, databaseTable);
                                layout.removeView(row);


                                if(score != NO_INPUT && max != NO_INPUT) { // Caso de botón real
                                    currentSumAndProduct -= grade;
                                    currentTotalWeight -= weight;
                                } else { // boton hipotetico
                                    targetTotalWeight -= weight;
                                }

                                updateHypoButtons();
                                displayAverage();
                            }
                        });

                        nameButtonAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  NO HACE NADA
                            }
                        });

                        nameButtonAlert.setView(nameInput);
                        nameButtonAlert.show();

                        // métodos de actualización
                        updateHypoButtons();
                        displayAverage();
                    }
                });

                row.addView(nameButton);

                // divisor
                ImageView divider = new ImageView(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT));
                divider.setBackgroundColor(Color.BLACK);
                row.addView(divider);

                // Botón Nota y Máx Nota.
                final Button scoreButton = new Button(this);
                LayoutParams scoreButtonParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                scoreButton.setBackgroundResource(R.drawable.button_bg);
                scoreButtonParam.weight = 3;
                scoreButton.setLayoutParams(scoreButtonParam);

                // onClickListener para Nota y Maxima Nota
                scoreButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        // Crear cuadro de diálogo de alerta
                        AlertDialog.Builder scoreButtonAlert = new AlertDialog.Builder(v.getContext());
                        scoreButtonAlert.setMessage("Actualizar Nota y Nota máxima");

                        // Crear un diseño para llenar con campos de entrada de texto
                        LinearLayout updateScoreLayout = new LinearLayout(v.getContext());
                        updateScoreLayout.setOrientation(LinearLayout.VERTICAL);

                        // Creación de campos de entrada de texto
                        final EditText scoreInput = new EditText(v.getContext());
                        final EditText maxInput = new EditText(v.getContext());
                        scoreInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        scoreInput.setHint("Nota");
                        maxInput.setHint("Maxima Nota");

                        // agregar campos de entrada de texto al diseño
                        updateScoreLayout.addView(scoreInput);
                        updateScoreLayout.addView(maxInput);

                        // actualiza la Nota y la Nota máxima
                        scoreButtonAlert.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // recuperar datos de campos de texto
                                Editable updatedScore = scoreInput.getText();
                                Editable updatedMax = maxInput.getText();

                                // actualizando la base de datos
                                myDb.updateRow(id, name.toString().trim() , Integer.parseInt(updatedScore.toString()), Integer.parseInt(updatedMax.toString()), weight, databaseTable);

                                // actualizando texto en scoreButton
                                scoreButton.setText( updatedScore.toString()+"/"+updatedMax.toString());
                                scoreButton.setTextColor(Color.parseColor("#000000"));


                                hypoButtons.remove(name.toString());


                                // otros métodos de actualización
                                computeAverage();
                                displayAverage();
                                updateHypoButtons();
                            }
                        });

                        scoreButtonAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        scoreButtonAlert.setView(updateScoreLayout);
                        scoreButtonAlert.show();

                    }
                });

                // establecer texto en scoreButton
                if(score == -1){ // si no hay nota y max nota
                    scoreButton.setTextColor(Color.parseColor("#cccccc"));
                    scoreButton.setText(String.format("%.1f", targetNeeded));
                    hypoButtons.put(name, scoreButton);
                } else { // si hay
                    scoreButton.setText(Integer.toString(score)+"/"+Integer.toString(max));
                }

                updateHypoButtons();
                row.addView(scoreButton);

                //otro divisor
                ImageView divider3 = new ImageView(this);
                divider3.setLayoutParams(new LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT));
                divider3.setBackgroundColor(Color.BLACK);
                row.addView(divider3);

                // Botón de peso de Nota
                Button weightButton = new Button(this);
                LayoutParams weightButtonParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                weightButton.setBackgroundResource(R.drawable.button_bg);
                weightButtonParam.weight = 1;
                weightButton.setLayoutParams(weightButtonParam);
                weightButton.setText(Integer.toString(weight));
                row.addView(weightButton);

                layout.addView(row); // agregar fila al diseño principal

            } while(cursor.moveToNext());
        }

        displayAverage();

    }

    /**
     *
     */
    private void updateHypoButtons(){
        float targetTotal, have;

        targetTotal = targetGrade/100;
        have = averageGrade * 100;
        targetNeeded = 100*(targetTotal - have)/(targetTotalWeight - currentTotalWeight);

        String message = "";
        if(targetGrade == 0){
           message = "n/a";
        } else {
           message = "need "+String.format("%.1f", targetNeeded);
        }
        for( Map.Entry<String, Button> e : hypoButtons.entrySet()){
            e.getValue().setText(message);
        }

    }

    private void computeAverage(){
        Cursor cursor = myDb.getAllRows(databaseTable);

        targetTotalWeight = targetTotalWeight - currentTotalWeight;
        currentTotalWeight = currentSumAndProduct = 0;
        dimelo =0;

        if(cursor.moveToFirst()){
            do{

                final int score = cursor.getInt(DataBaseAdapter.COL_SCORE);
                final int max = cursor.getInt(DataBaseAdapter.COL_MAX);
                final int weight = cursor.getInt(DataBaseAdapter.COL_WEIGHT);


                final float grade;

                if(score != NO_INPUT && max != NO_INPUT) {
                    grade = (score);
                    currentSumAndProduct += grade;
                    averageGrade = (currentSumAndProduct / Cont);

                }
            } while(cursor.moveToNext());
        }
    }
    /**
     *
     */
    private void displayAverage(){


        averageGrade = currentSumAndProduct / Cont;

        TextView average = (TextView) findViewById(R.id.actual_average);
        @SuppressLint("DefaultLocale") String msg = String.format("%.1f", averageGrade);
        average.setText(msg);
    }

    /**
     *
     *
     * @param view
     */

    public void onClick_targetAverage(View view){
        AlertDialog.Builder targetAlert = new AlertDialog.Builder(view.getContext());

        TextView average = (TextView) findViewById(R.id.target_average);
        if (averageGrade>=3.0){
            String msg = "APROVANDO";
            average.setText(msg);
            updateHypoButtons();
        }else{
            String msg = "PERDIENDO";
            average.setText(msg);
            updateHypoButtons();
        }
    }

    private void openDB(){
        myDb = new DataBaseAdapter(this);
        myDb.open();
    }

    private void closeDB(){
        myDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflar el menú; esto agrega elementos a la barra de acción si está presente.        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar los clics del elemento de la barra de acción aquí. La barra de acción se
        // maneja automáticamente los clics en el botón Inicio/Arriba, hasta luego
        // a medida que especifica una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
