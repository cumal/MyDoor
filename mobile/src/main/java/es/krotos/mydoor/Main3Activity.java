package es.krotos.mydoor;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main3Activity extends AppCompatActivity {

    private static int REQUEST_BLUETOOTH = 1;
    private BluetoothAdapter BTAdapter = null;
    private static String FILENAME = "DoorData";
    private String MacDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent iin = getIntent();
        MacDevice = iin.getStringExtra("MacDevice");

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter == null) {
        new AlertDialog.Builder(this)
                .setTitle("Not compatible")
                .setMessage("Your phone does not support Bluetooth")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
        else {
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
        }
}

    public void sendMessage(View view) throws IOException {
        EditText et = findViewById(R.id.editText);
        String txt = et.getText().toString();

        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
        String mac = MacDevice + "\n";
        fos.write(mac.getBytes());
        fos.write(txt.getBytes());
        fos.close();

        if (BTAdapter != null) {
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Error in BTAdapter")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}

