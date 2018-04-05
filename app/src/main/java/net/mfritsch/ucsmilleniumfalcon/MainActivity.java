package net.mfritsch.ucsmilleniumfalcon;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int last_language = 0;
    private String sLanguage = "";
    private static final String TAG = MainActivity.class.getSimpleName();
    final Context context = this;
    Thread myNet;
    private SharedPreferences mSharedPreferences;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    private String sIPAddress = "Not defined";
    private int iPort;
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

        recover();
        textElement = findViewById(R.id.conection);
        updateConnection();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //PreferenceManager.setDefaultValues(this, R.xml.languages, false);
        //PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        //PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);

        Switch impulseSwitch = findViewById(R.id.impulse);
        impulseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.v(TAG, "Switch State impulse - " + isChecked);

                if (checkIPAddress()) {
                    Logging.writeLog(TAG, "Switch State impulse - " + isChecked);
                    MediaPlayer ring2 = MediaPlayer.create(MainActivity.this, R.raw.falcon_fly);
                    ring.stop();
                    if (isChecked) {
                        ring2.start();
                    }

                    Falcon falcon = new Falcon();
                    falcon.setSublightDrivers(isChecked);
                    JsonUtil jsonString = new JsonUtil();
                    //Log.v(TAG, "JSON String - " + jsonString.toJSon(falcon));
                    //Logging.writeLog(TAG, "JSON String - " + jsonString.toJSon(falcon));
                    //writeLog(TAG, "JSON String - " + jsonString.toJSon(falcon));
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
        //int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.action_connect:
                recover();
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
                /*
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Do nothing here because we override this button later to change the close behaviour.
                                        //However, we still need this because on older versions of Android unless we
                                        //pass a handler the button doesn't get instantiated

                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get user input and set it to result
                        sIPAddress = ipAddress.getText().toString();

                        try {
                            iPort = Integer.parseInt(port.getText().toString());
                            Logging.writeLog(TAG, "Port " + port.getText().toString() + " wurde vom user einegeben");

                            Logging.writeLog(TAG, "Connecting to IP Address " + sIPAddress + " and port " + iPort);
                            TCPClient myClient = new TCPClient(sIPAddress, iPort, "Bin da! JUHU");
                            myNet = new Thread(myClient);
                            myNet.start();
                            Toast.makeText(getApplicationContext(), "Connection to  " + sIPAddress + "saved", Toast.LENGTH_LONG).show();

                            store("IPAddress", sIPAddress);

                            dialog.dismiss();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), "Es wurde kein korrekter Port vom user eingegeben - " + e, Toast.LENGTH_LONG).show();
                            Logging.writeLog(TAG, "Es wurde kein korrekter Port vom user eingegeben - " + e);

                        } catch (Exception e) {
                            Logging.writeLog(TAG, "Es wurde kein Port vom user eingegeben - " + e);
                        }
                    }
                });
                */

                // create alert dialog
                // AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                //alertDialog.show();

                return true;

            case R.id.action_language2:

                // TextView textView = (TextView)findViewById(R.id.alertDialogTextView);
                //final TextView textViewTmp = textView;

                // Each image in array will be displayed at each item beginning.
                int[] xmlList_image = getResources().getIntArray(R.array.country_flags);

                Logging.writeLog(TAG, "Flaggen resoruce: " + xmlList_image[0] + " " + xmlList_image[1]);

                final int[] xmlList_language_values = getResources().getIntArray(R.array.language_values);
                final String[] language_list = getResources().getStringArray(R.array.language_values);

                Logging.writeLog(TAG, "Language_list Werte: " + language_list[0] + " " + language_list[1]);

                final int[] imageIdArr = {R.drawable.en, R.drawable.de};
                Logging.writeLog(TAG, "Flaggen Resource2: " + imageIdArr[0] + " " + imageIdArr[1]);
                // Each item text.
                final String[] xmlList_entries = getResources().getStringArray(R.array.language_entries);

                //int first = Integer.parseInt(xmlList_entries[0]);

                // Image and text item data's key.
                final String CUSTOM_ADAPTER_IMAGE = "image";
                final String CUSTOM_ADAPTER_TEXT = "text";

                // Create a alert dialog builder.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Set icon value
                builder.setIcon(R.mipmap.ic_launcher);
                // Set title value.
                builder.setTitle(R.string.action_language2);

                // Create SimpleAdapter list data.
                List<Map<String, Object>> dialogItemList = new ArrayList<Map<String, Object>>();
                int listItemLen = imageIdArr.length;
                for (int i = 0; i < listItemLen; i++) {
                    Map<String, Object> itemMap = new HashMap<String, Object>();
                    itemMap.put(CUSTOM_ADAPTER_IMAGE, imageIdArr[i]);
                    itemMap.put(CUSTOM_ADAPTER_TEXT, xmlList_entries[i]);

                    dialogItemList.add(itemMap);
                }
                // Create SimpleAdapter object.
                SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, dialogItemList,
                        R.layout.activity_alert_dialog_simple_adapter_row,
                        new String[]{CUSTOM_ADAPTER_IMAGE, CUSTOM_ADAPTER_TEXT},
                        new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});


                recover();
                Log.v(TAG, "Letzte gewaehlte Sprache: " + last_language);
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
                        Logging.writeLog(TAG, "aus_Text: " + selectedPosition);
                        last_language = selectedPosition;
                        Logging.writeLog(TAG, "last_lang_Text: " + last_language);
                        Toast.makeText(getApplicationContext(), "Language: " + xmlList_entries[selectedPosition].toString(), Toast.LENGTH_LONG).show();

                        //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", language_list[selectedPosition]).apply();
                        // setLangRecreate(language_list[selectedPosition]);

                        //store(language_list[selectedPosition], last_language);
                    }
                });
                //Create alert dialog object via builder
                AlertDialog alert = builder.create();
                //Show the dialog
                alert.show();

                return true;

            case R.id.action_language:
                Toast.makeText(this, R.string.action_language, Toast.LENGTH_SHORT).show();
                final Intent iLanguage = new Intent(this, LanguageActivity.class);
                startActivityForResult(iLanguage, 1);
                return true;

            case R.id.action_log:
                //Inflate the XML view.
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myScrollView = inflater.inflate(R.layout.scroll_text, null, false);

                // textViewWithScroll is the name of our TextView on scroll_text.xml
                TextView tv = myScrollView.findViewById(R.id.textViewWithScroll);

                // Initializing a blank textview so that we can just append a text later
                tv.setText("");

                // Display the text 10 times so that it will exceed the device screen height and be able to scroll
                tv.append(Logging.readLog());

                new AlertDialog.Builder(MainActivity.this).setView(myScrollView)
                        .setTitle("Log")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @TargetApi(11)
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

    final String PREFS_NAME = "net.mfritsch.UCSMilleniumFalcon.PREFERENCE_FILE_KEY";

    public void LoadLanguage() {
        SharedPreferences shp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = shp.getString("iLANGUAGE", "0");

        // der rest nur für umschreiben der language
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        // store(language, 1); // 1 ist nur symbolisch und nicht nötig
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /*
        public String read(String s) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String name = prefs.getString(s, "Not defined");//"Not defined" is the default value.
            int Port = prefs.getInt(s, 0);//"Not defined" is the default value.
            Logging.writeLog(TAG, "Value of " + s + " is " + name);
            return name;
        }

        public int read(String s) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            Logging.writeLog(TAG, "Value of " + s + " is " + name);
            return name;
        }
        +/

        /**
         * @param s
         * @param v
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

    private void recover() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sIPAddress = settings.getString("IPAddress", "Not defined");
        iPort = settings.getInt("Port", FALCON_DEFAULT_PORT);
    }

    private void updateConnection() {
        String text = getString(R.string.connection, sIPAddress, iPort, "success");
        textElement.setText(text); //leave this line to assign a string resource
    }

    private boolean checkIPAddress() {
        if (sIPAddress.equals("Not defined")) {
            return false;
            //Toast.makeText(getApplicationContext(), "Please set first the IP of the Millenium Falcon", Toast.LENGTH_LONG).show();
            // Logging.writeLog(TAG, "Please set first the IP of the Millenium Falcon");
        } else {
            return true;
        }
    }
}