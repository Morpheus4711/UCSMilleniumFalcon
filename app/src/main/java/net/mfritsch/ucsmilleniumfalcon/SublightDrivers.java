package net.mfritsch.ucsmilleniumfalcon;

import android.widget.CompoundButton;
import android.widget.Switch;

public class SublightDrivers extends MainActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = SublightDrivers.class.getSimpleName();

    Switch impulseSwitch = findViewById(R.id.impulse);

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Log.v(TAG, "Switch State impulse - " + isChecked);

        if (checkIPAddress()) {
            Logging.writeLog(TAG, "Switch State impulse - " + isChecked);
           /* MediaPlayer ring2 = MediaPlayer.create(MainActivity.this, R.raw.falcon_fly);
            ring.stop();
            if (isChecked) {
                ring2.start();
            }
*/
            Falcon falcon = new Falcon();
            falcon.setSublightDrivers(isChecked);
            TCPClient myClient = new TCPClient(sIPAddress, iPort, JsonUtil.toJSon(falcon));
            myNet = new Thread(myClient);
            myNet.start();
        }
    }

    ;
}
