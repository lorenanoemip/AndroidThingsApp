package com.example.homeactivity;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.google.android.things.pio.PeripheralManager;
//
//import static android.content.ContentValues.TAG;
//
///**
// * Skeleton of an Android Things activity.
// * <p>
// * Android Things peripheral APIs are accessible through the class
// * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
// * set it to HIGH:
// *
// * <pre>{@code
// * PeripheralManagerService service = new PeripheralManagerService();
// * mLedGpio = service.openGpio("BCM6");
// * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
// * mLedGpio.setValue(true);
// * }</pre>
// * <p>
// * For more complex peripherals, look for an existing user-space driver, or implement one if none
// * is available.
// *
// * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
// */
//public class MainActivity extends Activity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        PeripheralManager manager = PeripheralManager.getInstance();
//        Log.d(TAG, "Available GPIO: " + manager.getGpioList());
//
////        led.setOnClickListener {
////            Log.d(TAG,"Starting led activity");
////            startActivity(Intent(this, BlinkActivity::class.java))
////        }
////
////        button.setOnClickListener {
////            Log.d("Starting button activity")
////            startActivity(Intent(this, ButtonActivity::class.java))
////        }
//
//    }
//}



import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Gpio mLedGpio;
    private Gpio mLedGpio2;
    private ButtonInputDriver mButtonInputDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting ButtonActivity");

        PeripheralManager manager = PeripheralManager.getInstance();

        try {
            Log.i(TAG, "Configuring GPIO pins");
            mLedGpio = manager.openGpio("BCM21");
            mLedGpio2 = manager.openGpio("BCM5");
            mLedGpio.setValue(false);
            mLedGpio2.setValue(false);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            Log.i(TAG, "Registering button driver");
            // Initialize and register the InputDriver that will emit SPACE key events
            // on GPIO state changes.
            mButtonInputDriver = new ButtonInputDriver(
                    "BCM18",
                    Button.LogicState.PRESSED_WHEN_HIGH,
                    KeyEvent.KEYCODE_SPACE);



        } catch (IOException e) {
            Log.e(TAG, "Error configuring GPIO pins", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mButtonInputDriver.register();
       // m2ButtonInputDriver.register();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn on the LED
            try{
                setLedValue(!mLedGpio.getValue());
            }
            catch (IOException e){
                Log.e(TAG,"error on key down");
            }
            return true;
        }
        //return super.onKeyDown(keyCode, event);
        return true;
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_SPACE) {
//            // Turn off the LED
//            setLedValue(false);
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    /**
     * Update the value of the LED output.
     */
    private void setLedValue(boolean value) {
        try {
            mLedGpio.setValue(value);
            mLedGpio2.setValue(value);
            Log.d(TAG,"value has been set");
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (mButtonInputDriver != null) {
            mButtonInputDriver.unregister();
            try {
                mButtonInputDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Button driver", e);
            } finally{
                mButtonInputDriver = null;
            }
        }

        if (mLedGpio != null) {
            try {
                mLedGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally{
                mLedGpio = null;
            }
            mLedGpio = null;
        }
    }
}