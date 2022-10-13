package com.airthcompany.gradecalculator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.widget.TextView;


public class CoursesActivity extends AppCompatActivity {

    public DataBaseAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        setTitle("Clases");
        openDB();

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.add_course);
        Button addCourseButton = (Button) findViewById(R.id.button_add);

        Cursor cursor = myDb.getAllTables();
        DisplayTables(mainLayout, cursor, "");

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder addCourseAlert = new AlertDialog.Builder(v.getContext());
                addCourseAlert.setTitle("Agregar Materia");

                final EditText courseInput = new EditText(v.getContext());
                courseInput.setHint("Nombre de la Materia");

                addCourseAlert.setPositiveButton("Establecer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Editable courseName = courseInput.getText();

                        myDb.createTable(courseName.toString().trim()); // creating table
                        Cursor c = myDb.getAllTables();

                        DisplayTables(mainLayout, c, courseName.toString().trim());

                    }
                });

                addCourseAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancelado.
                    }
                });

                addCourseAlert.setView(courseInput);
                addCourseAlert.show();

            }
        }); // finalice el oyente onclick para el botón Agregar curso

    }


    public void DisplayTables(final LinearLayout layout, Cursor cursor, final String table){
        if(cursor.moveToFirst()){
            do{

                @SuppressLint("Range") final String tableName = cursor.getString(cursor.getColumnIndex("name"));

                final LinearLayout innerLayout = new LinearLayout(this);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                final Button nameButton = new Button(innerLayout.getContext());
                LinearLayout.LayoutParams nameButtonParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                nameButton.setBackgroundResource(R.drawable.button_bg);
                nameButton.setLayoutParams(nameButtonParam);
                nameButton.setText(tableName);
                nameButton.setGravity(Gravity.CENTER_VERTICAL);

                nameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                        alert.setTitle("¿Que sigue?");
                        alert.setPositiveButton("Proceder", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                                nextScreen.putExtra("table", tableName);

                                startActivity(nextScreen);
                            }
                        });

                        alert.setNeutralButton("Borrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDb.deleteTable(tableName);
                                layout.removeView(nameButton);

                            }
                        });

                        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // NO HACE NADA
                            }
                        });

                        alert.show();



                    }
                });

                if( (table.length() == 0 || tableName.equals(table)) && !tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
                    layout.addView(nameButton);
                }

            } while(cursor.moveToNext());
        }
    }


    private void openDB(){
        myDb = new DataBaseAdapter(this);
        myDb.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar los clics del elemento de la barra de acción aquí. La barra de acción se
        // maneja automáticamente los clics en el botón Inicio/Arriba, hasta luego
        // a medida que especifica una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplificableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

