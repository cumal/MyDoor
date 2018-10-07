package es.krotos.mydoor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class Main2Activity extends AppCompatActivity {

    private BluetoothAdapter BTAdapter = null;
    private BluetoothSocket BTSocket = null;
    private BluetoothDevice myDoor = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String myDoor_mac = "null";
    private String myDoor_code = "null";
    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;
    private static String FILENAME = "DoorData";
    private Integer reps = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        File file = new File(getFilesDir(),FILENAME);
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = openFileInput(FILENAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    myDoor_mac = bufferedReader.readLine();
                    myDoor_code = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        BTAdapter.cancelDiscovery();
        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (Objects.equals(device.getAddress(), myDoor_mac)) {
                    myDoor = device;
                    Toast.makeText(getBaseContext(),"Device: " + device.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (myDoor == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Delete data and retry.")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void openDoor(View view){
        Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
        BluetoothSocket tmp = null;
        try {
            tmp = myDoor.createRfcommSocketToServiceRecord(BTMODULEUUID);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(),"Cannot create socket", Toast.LENGTH_SHORT).show();
        }
        if (tmp != null) {
            BTSocket = tmp;
        }
        if (BTSocket != null) {
            boolean sent = false;
            Integer count = 0;
            while (count < reps && !sent) {
                try {
                    BTSocket.connect();
                    if (BTSocket.isConnected()) {
                        Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
                        OutputStream tmpOut = null;
                        try {
                            tmpOut = BTSocket.getOutputStream();
                        } catch (IOException e) {

                        }
                        mmOutStream = tmpOut;
                        String s = "*" + myDoor_code + "#";
                        mmOutStream.write(s.getBytes());
                        sent = true;
                    }
                } catch (IOException connectException) {
                    Toast.makeText(getBaseContext(), "Error connecting", Toast.LENGTH_SHORT).show();
                }
                int millis = 500;
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count += 1;
            }
            if (sent) {
                int millis = 500;
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finishConn(view);
            } else {
                Toast.makeText(getBaseContext(), "Cannot connect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cancelConn(View view) {
        if (BTSocket != null) {
            try {
                BTSocket.close();
                if (!BTSocket.isConnected()) {
                    Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException closeException) {
                Toast.makeText(getBaseContext(), "Unable to close comm", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void finishConn(View view) {
        if (BTSocket != null) {
            try {
                BTSocket.close();
            } catch (IOException closeException) {
                Toast.makeText(getBaseContext(), "Unable to close comm", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

