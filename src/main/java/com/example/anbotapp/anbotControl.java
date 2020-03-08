package com.example.anbotapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


public class anbotControl extends ActionBarActivity {

   // ImageViews to be used
    ImageView Up, Down, Left, Right, Speak, Discnt;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the anbotControl
        setContentView(R.layout.activity_led_control);

        //call the widgets
        Up = (ImageView) findViewById(R.id.up_btn);
        Down = (ImageView) findViewById(R.id.down_btn);
        Left = (ImageView) findViewById(R.id.left_btn);
        Right = (ImageView) findViewById(R.id.right_btn);
        Speak = (ImageView) findViewById(R.id.speak_btn);
        Discnt = (ImageView) findViewById(R.id.dis_btn);

        new ConnectBT().execute(); //Call the class to connect

        Up.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                MoveUp();
            }
        }));

        Down.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                MoveDown();
            }
        }));

        Right.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                MoveRight();
            }
        }));

        Left.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                MoveLeft();
            }
        }));

        Speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Speak();
            }
        });

        Discnt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect();
            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void MoveUp()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void MoveDown()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void MoveLeft()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("2".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void MoveRight()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("3".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void Speak()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("4".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }

    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(anbotControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    public class RepeatListener implements OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;
        private View touchedView;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if(touchedView.isEnabled()) {
                    handler.postDelayed(this, normalInterval);
                    clickListener.onClick(touchedView);
                } else {
                    // if the view was disabled by the clickListener, remove the callback
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                }
            }
        };

        /**
         * https://stackoverflow.com/questions/4284224/android-hold-button-to-repeat-action
         * @param initialInterval The interval after first click event
         * @param normalInterval The interval after second and subsequent click
         *       events
         * @param clickListener The OnClickListener, that will be called
         *       periodically
         */
        public RepeatListener(int initialInterval, int normalInterval,
                              OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    touchedView = view;
                    touchedView.setPressed(true);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                    return true;
            }

            return false;
        }

    }
}
