package es.krotos.mydoor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static String FILENAME = "DoorData";
    private static int REQUEST_BLUETOOTH = 1;
    private ListView pairedListView;
    private ArrayList<String> devicesList = new ArrayList<String>();
    private ArrayList<String> macList = new ArrayList<String>();
    private BluetoothAdapter BTAdapter;
    private String mac_address = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            // El Bluetooth está apagado, solicitamos permiso al usuario para iniciarlo
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // REQUEST_ENABLE_BT es un valor entero que vale 1
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
        }

        File file = new File(getFilesDir(),FILENAME);
        if (file.exists()) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
        }

        pairedListView = (ListView) findViewById(R.id.DeviceList);

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                String dev_name = device.getName();
                String dev_addre = device.getAddress();
                devicesList.add(dev_name);
                macList.add(dev_addre);
            }
        }
        if (!devicesList.isEmpty()) {
            ArrayAdapter mArrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, devicesList);
            pairedListView.setAdapter(mArrayAdapter);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Not devices")
                    .setMessage("First pair the MyDoor device")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //mac_address = pairedListView.getItemAtPosition(i).toString();
                mac_address = macList.get(i);
                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                intent.putExtra("MacDevice", mac_address);
                startActivity(intent);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                ArrayAdapter mArrayAdapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1, devicesList);
                pairedListView.setAdapter(mArrayAdapter);
                //pairedListView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.support_simple_spinner_dropdown_item, devicesList));
            }
        }
    };

    private void getmac( String macdir ) {
        mac_address=macdir;
    }

    public void getDevices() {
        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = pairedListView.getItemAtPosition(i).toString();
                getmac(s);
            }
        });
    }
}
