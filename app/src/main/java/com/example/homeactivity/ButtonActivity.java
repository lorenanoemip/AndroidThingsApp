package com.example.homeactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

import java.io.IOException;


public class ButtonActivity extends Activity {
    private static final String TAG = "ButtonActivity";
    private static final String BUTTON_PIN_NAME = "BCM18"; // GPIO port wired to the button

    private Gpio buttonGpio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            // Step 1. Create GPIO connection.
            buttonGpio = manager.openGpio(BUTTON_PIN_NAME);
            Log.d(TAG, "Configured the button");
            // Step 2. Configure as an input.
            buttonGpio.setDirection(Gpio.DIRECTION_IN);
            Log.d(TAG, "Establish the direction");
            // Step 3. Enable edge trigger events.
            buttonGpio.setEdgeTriggerType(Gpio.EDGE_RISING);
            Log.d(TAG, "Establish the trigger type");
            // Step 4. Register an event callback.
            Log.d(TAG, "Registered the callback");
            buttonGpio.registerGpioCallback(mCallback);

        } catch (IOException e) {
            Log.e(TAG, "Error on create PeripheralIO API", e);
        }
    }

    // Step 4. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO changed, button pressed");

            // Step 5. Return true to keep callback active.
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step 6. Close the resource
        if (buttonGpio != null) {
            buttonGpio.unregisterGpioCallback(mCallback);
            try {
                buttonGpio.close();

            } catch (IOException e) {
                Log.e(TAG, "Error on destroy PeripheralIO API", e);
            }
        }
    }
}