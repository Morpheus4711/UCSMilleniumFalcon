package net.mfritsch.ucsmilleniumfalcon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int last_language = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    final String PREFS_NAME = "net.mfritsch.UCSMilleniumFalcon.PREFERENCE_FILE_KEY";
    final Context context = this;
    Thread myNet;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public String sIPAddress = "Not defined";
    public int iPort;
    private int FALCON_DEFAULT_PORT = 7541;
    TextView textElement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logging.writeLog(TAG, "Application started\n");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final MediaPlayer ring = MediaPlayer.create(MainActivity.this, R.raw.star_wars_theme);
        ring.start();

        Configuration config = getBaseContext().getResources().getConfiguration();

        Logging.writeLog(TAG, "Sprache der app beim Start " + config.locale.getLanguage());

        recover("Port");
        recover("IPAddress");
       // recover("Language");

        //setLangRecreate("de");
        textElement = findViewById(R.id.conection);
        updateConnection();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        Switch impulseSwitch = findViewById(R.id.impulse);
        impulseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkIPAddress()) {
                    Logging.writeLog(TAG, "Switch State impulse - " + isChecked);
                    MediaPlayer ring2 = MediaPlayer.create(MainActivity.this, R.raw.falcon_fly);
                    ring.stop();
                    if (isChecked) {
                        ring2.start();
                    }

                    Falcon falcon = new Falcon();
                    falcon.setSublightDrivers(isChecked);
                    TCPClient myClient = new TCPClient(sIPAddress, iPort, JsonUtil.toJSon(falcon));
                    myNet = new Thread(myClient);
                    myNet.start();
                }
            }
        });

        Switch landing_feetSwitch = findViewById(R.id.landing_feet);
        landing_feetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logging.writeLog(TAG, "Switch State landing gear - " + isChecked);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to clsoe the app!", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_connect:
                recover("Port");
                recover("IPAddress");
                Toast.makeText(this, R.string.action_connect, Toast.LENGTH_SHORT).show();
                // get connect.xml view
                LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.connect, null);

                final EditText ipAddress = promptsView.findViewById(R.id.ip_address);
                if (!sIPAddress.equals("Not defined")) {
                    ipAddress.setText(sIPAddress);
                }
                final EditText port = promptsView.findViewById(R.id.port42);
                if (iPort != FALCON_DEFAULT_PORT) {
                    port.setText(String.valueOf(iPort));
                }

                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(promptsView)
                        .setTitle("Connection")
                        .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sIPAddress = ipAddress.getText().toString();
                                if (sIPAddress.equals("Not defined") || sIPAddress.equals("")) {
                                    Toast.makeText(getApplicationContext(), "Please set first the IP/Hostname of the Millenium Falcon", Toast.LENGTH_LONG).show();
                                    Logging.writeLog(TAG, "Please set first the IP/Hostname of the Millenium Falcon");
                                } else {
                                    try {
                                        if (!port.getText().toString().equals("")) {
                                            iPort = Integer.parseInt(port.getText().toString());
                                        } else {
                                            iPort = FALCON_DEFAULT_PORT;
                                        }
                                        Logging.writeLog(TAG, "Port " + iPort + " wurde vom user einegeben");
/*
                                        Logging.writeLog(TAG, "Connecting to IP Address " + sIPAddress + " and port " + iPort);
                                        TCPClient myClient = new TCPClient(sIPAddress, iPort, "Bin da! JUHU");
                                        myNet = new Thread(myClient);
                                        myNet.start();
*/
                                        Toast.makeText(getApplicationContext(), "Connection to " + sIPAddress + " and Port " + iPort + " saved", Toast.LENGTH_LONG).show();

                                        store("IPAddress", sIPAddress);
                                        store("Port", iPort);
                                        updateConnection();
                                        dialog.dismiss();

                                    } catch (NumberFormatException e) {
                                        Toast.makeText(getApplicationContext(), "Es wurde kein korrekter Port vom user eingegeben - " + e, Toast.LENGTH_LONG).show();
                                        Logging.writeLog(TAG, "Es wurde kein korrekter Port vom user eingegeben - " + e);

                                    } catch (Exception e) {
                                        Logging.writeLog(TAG, "Es wurde kein Port vom user eingegeben - " + e);
                                    }
                                }
                            }
                        });
                    }
                });
                dialog.show();

                return true;

            case R.id.action_language:
                final String[] language_value = getResources().getStringArray(R.array.language_values);
                Logging.writeLog(TAG, "Language_list Werte: " + language_value[0] + " " + language_value[1]);

                // Create a alert dialog builder.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Set icon value
                builder.setIcon(R.mipmap.ic_launcher);
                // Set title value.
                builder.setTitle(R.string.action_language);

                recover("Language");
                Logging.writeLog(TAG, "Letzte gewaehlte Sprache: " + last_language);

                // Set the data adapter.
                builder.setSingleChoiceItems(R.array.language_entries, last_language, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                        Logging.writeLog(TAG, "last_lang_Text: " + last_language);
                        Toast.makeText(getApplicationContext(), "Language: " + selectedPosition, Toast.LENGTH_LONG).show();

                        setLangRecreate(language_value[selectedPosition]);
                        store("Language", selectedPosition);
                    }
                });
                //Create alert dialog object via builder
                AlertDialog alert = builder.create();
                //Show the dialog
                alert.show();

                return true;

            case R.id.action_log:
                //Inflate the XML view.
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myScrollView = inflater.inflate(R.layout.scroll_text, null, false);

                // textViewWithScroll is the name of our TextView on scroll_text.xml
                TextView tv = myScrollView.findViewById(R.id.textViewWithScroll);

                // Initializing a blank textview so that we can just append a text later
                tv.setText(Logging.readLog());

                new AlertDialog.Builder(MainActivity.this).setView(myScrollView)
                        .setTitle("Log")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();

                return true;

            case R.id.action_about:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.action_about)
                        .setMessage(R.string.about_text)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
                return true;
            default:
        }
        return super.
                onOptionsItemSelected(item);
    }

    /**
     * @param langval Uebergabe von der Sprache
     */
    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();

        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    /**
     * @param s stest
     * @param v vtest
     */
    public void store(String s, String v) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(s, v);
        Logging.writeLog(TAG, "Saved " + s + " with value " + v);

        // Apply editings
        editor.apply();
    }

    public void store(String s, int i) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(s, i);
        Logging.writeLog(TAG, "Saved " + s + " with value " + i);

        // Apply editings
        editor.apply();
    }

    private void recover(String s) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Logging.writeLog(TAG, "string is " + s);
        switch (s) {
            case "IPAddress":
                sIPAddress = settings.getString("IPAddress", "Not defined");
                break;
            case "Port":
                iPort = settings.getInt("Port", FALCON_DEFAULT_PORT);
                break;
            case "Language":
                last_language = settings.getInt("Language", 0);
                Logging.writeLog(TAG, "recovered " + s + " with value " + last_language);
                break;
            default:
        }
    }

    private void updateConnection() {
        String text = getString(R.string.connection, sIPAddress, iPort, "success");
        textElement.setText(text); //leave this line to assign a string resource
    }

    public boolean checkIPAddress() {
        return !sIPAddress.equals("Not defined");
        //Toast.makeText(getApplicationContext(), "Please set first the IP of the Millenium Falcon", Toast.LENGTH_LONG).show();
        // Logging.writeLog(TAG, "Please set first the IP of the Millenium Falcon");
    }
}