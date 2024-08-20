package com.example.dailyplanner;


import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dailyplanner.DB.SQLiteHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;


public class CreateAppointmentScreen extends AppCompatActivity implements View.OnClickListener{

    EditText titleET, detailsET;
    Button saveBtn;
    SQLiteHandler dbHandler;

    private String date;
    private EditText timeET;

    //Thesaurus stuff

    Button timePickerButton;

    PopupWindow popupWindow;

    //variables to store the input from the text box
    private String inputWord;
    //constant for the thesaurus service key

    //variable to store the language
    private String lang = "en_US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment_screen);

        Intent intent = getIntent();
        date = intent.getStringExtra("Date");
        Toast.makeText(getBaseContext() , date , Toast.LENGTH_SHORT).show();

        //initializing the edit text boxes
        titleET = (EditText) findViewById(R.id.titleEditText);
        timeET = (EditText) findViewById(R.id.timeEditText);
        detailsET = (EditText) findViewById(R.id.detailsEditText);


        timePickerButton = findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(this);



        saveBtn = (Button) findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);

        /**
         * create a new database handler. null can be passed because the helper has all the constants
         * 1 is the database version
         */
        dbHandler = new SQLiteHandler(this, null, null, 1);
        //dbHandler.clearTable("appointments");
        printDatabase();
    }
    

    /**
     * This method prints the current database
     */
    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        Toast.makeText(getBaseContext() , dbString , Toast.LENGTH_LONG).show();
        titleET.setText(""); timeET.setText("");detailsET.setText("");
    }


    @Override
    public void onClick(View v) {
        //Hides the virtual keyboard when the buttons are clicked
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (v.getId() == R.id.saveButton)
        {

            String time = timeET.getText().toString();
            String title = titleET.getText().toString();
            String details = detailsET.getText().toString();

            if (TextUtils.isEmpty(time)){

                timeET.setError("Please set a time for the appointment.");
                return;

            }else if (TextUtils.isEmpty(title)) {

                titleET.setError("Please set a title for the appointment.");
                return;

            }else if(TextUtils.isEmpty(details)) {

                detailsET.setError("Please set a details for the appointment.");
                return;

            }else {

                Appointment appointment = new Appointment(date, time, title, details);
                int i = dbHandler.createAppointment(appointment);
                if (i == 1) {

                    errorDialog("Appointment " + title + " on " + date + " was created successfully.");
                    printDatabase();
                    startActivity(new Intent(CreateAppointmentScreen.this, MainScreen.class));
                    finish();

                } else if (i == -1) {

                    errorDialog("Appointment "+ title +" already exists, please choose a different event title");
                }


            }

        }

        else if(v.getId()==R.id.timePickerButton){
            showTimePickerDialog();
        }
    }
    private void showTimePickerDialog() {
        final Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        timeET.setText(time);
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }




    /**
     * This function creates a dialog box which takes
     * @param error String parameter which is passed
     */
    public void errorDialog(String error)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.DilumCustomDialogTheme);
        builder.setMessage(error);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    ////////
    //Helper method to determine if Internet connection is available.





    /**
     * AsyncTask that will download the xml file for us and store it locally.
     * After the download is done we'll parse the local file.
     */


    /**
     * This method will try to download the xml form the internet
     * @param URL URL to make the request
     * @param fos The name to store the XML file
     */
    public static void DownloadFromUrl(String URL, FileOutputStream fos) {
        try {

            java.net.URL url = new URL(URL); //URL of the file

            /* Open a connection to that URL. */
            URLConnection connection = url.openConnection();


            //input stream that'll read from the connection
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            //buffer output stream that'll write to the xml file
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            //write to the file while reading
            byte data[] = new byte[1024];
            int count;
            //loop and read the current chunk
            while ((count = bis.read(data)) != -1) {
                //write this chunk
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();

        } catch (IOException e) {
        }
    }
}