package com.knobtviker.android.things.contrib.community.driver.ds3231;

import android.hardware.Sensor;
import android.support.annotation.Nullable;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by bojan on 17/01/2018.
 */

@SuppressWarnings("WeakerAccess")
public class Ds3231SensorDriver implements AutoCloseable {
    // DRIVER parameters
    // documented at https://source.android.com/devices/sensors/hal-interface.html#sensor_t
    private static final String DRIVER_VENDOR = Ds3231.CHIP_VENDOR;
    private static final String DRIVER_NAME = Ds3231.CHIP_NAME;
    private static final int DRIVER_MIN_DELAY_US = Ds3231.TEMP_MEASUREMENT_INTERVAL_MS * 1000;
    private static final int DRIVER_MAX_DELAY_US = Ds3231.TEMP_MEASUREMENT_INTERVAL_MS * 1000;
    private static final float DRIVER_MAX_RANGE = Ds3231.MAX_TEMP_RANGE;
    private static final float DRIVER_POWER = Ds3231.MAX_POWER_CONSUMPTION_UA / 1000.f;
    private static final float DRIVER_RESOLUTION = Ds3231.TEMPERATURE_RESOLUTION;
    private static final int DRIVER_VERSION = 1;
    private static final String DRIVER_REQUIRED_PERMISSION = "";

    private Ds3231 device;

    private TemperatureUserDriver temperatureUserDriver;

    /**
     * Create a new framework sensor driver connected on the given bus and address.
     * The driver emits {@link Sensor} with temperature data when registered.
     *
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public Ds3231SensorDriver(String bus) throws IOException {
        this(bus, Ds3231.DEFAULT_I2C_ADDRESS);
    }

    /**
     * Create a new framework sensor driver connected on the given bus and address.
     * The driver emits {@link Sensor} with temperature data when registered.
     *
     * @param bus        I2C bus the sensor is connected to.
     * @param i2cAddress I2C address of the RTC
     * @throws IOException
     */
    public Ds3231SensorDriver(String bus, int i2cAddress) throws IOException {
        device = new Ds3231(bus, i2cAddress);
    }

    /**
     * Close the driver and the underlying device.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        unregisterTemperatureSensor();
        if (device != null) {
            try {
                device.close();
            } finally {
                device = null;
            }
        }
    }

    /**
     * Register a {@link UserSensor} that pipes temperature readings into the Android SensorManager.
     *
     * @see #unregisterTemperatureSensor()
     */
    public void registerTemperatureSensor() {
        if (device == null) {
            throw new IllegalStateException("cannot register closed driver");
        }

        if (temperatureUserDriver == null) {
            temperatureUserDriver = new TemperatureUserDriver();
            UserDriverManager.getManager().registerSensor(temperatureUserDriver.getUserSensor());
        }
    }

    /**
     * Unregister the temperature {@link UserSensor}.
     */
    public void unregisterTemperatureSensor() {
        if (temperatureUserDriver != null) {
            UserDriverManager.getManager().unregisterSensor(temperatureUserDriver.getUserSensor());
            temperatureUserDriver = null;
        }
    }

    /**
     * Expose device for other functional options and methods.
     */
    @Nullable
    public Ds3231 device() {
        return device;
    }

    private class TemperatureUserDriver extends UserSensorDriver {
        private UserSensor userSensor;

        private UserSensor getUserSensor() {
            if (userSensor == null) {
                userSensor = new UserSensor.Builder()
                    .setType(Sensor.TYPE_AMBIENT_TEMPERATURE)
                    .setName(DRIVER_NAME)
                    .setVendor(DRIVER_VENDOR)
                    .setVersion(DRIVER_VERSION)
                    .setMaxRange(DRIVER_MAX_RANGE)
                    .setResolution(DRIVER_RESOLUTION)
                    .setPower(DRIVER_POWER)
                    .setMinDelay(DRIVER_MIN_DELAY_US)
                    .setMaxDelay(DRIVER_MAX_DELAY_US)
                    .setRequiredPermission(DRIVER_REQUIRED_PERMISSION)
                    .setUuid(UUID.randomUUID())
                    .setDriver(this)
                    .build();
            }
            return userSensor;
        }

        @Override
        public UserSensorReading read() throws IOException {
            return new UserSensorReading(new float[]{device.readTemperature()});
        }
    }
}
