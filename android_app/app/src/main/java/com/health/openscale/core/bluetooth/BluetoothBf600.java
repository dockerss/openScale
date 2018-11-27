/* Copyright (C) 2018 Erik Johansson <erik@ejohansson.se>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.health.openscale.core.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.health.openscale.core.utils.Converters;

import java.util.Calendar;

public class BluetoothBf600 extends BluetoothCommunication {

    public BluetoothBf600(Context context) {
        super(context);
    }

    @Override
    public String driverName() {
        return "BF600";
    }

    @Override
    protected boolean nextInitCmd(int stateNr) {
        switch (stateNr) {
            case 0:
                setIndicationOn(BluetoothGattUuid.SERVICE_WEIGHT_SCALE,
                        BluetoothGattUuid.CHARACTERISTIC_WEIGHT_MEASUREMENT,
                        BluetoothGattUuid.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION);
                break;
            case 1:
                setIndicationOn(BluetoothGattUuid.SERVICE_BODY_COMPOSITION,
                        BluetoothGattUuid.CHARACTERISTIC_BODY_COMPOSITION_MEASUREMENT,
                        BluetoothGattUuid.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION);
                break;
            case 2:
                byte[] time = new byte[10];
                Calendar cal = Calendar.getInstance();

                Converters.toInt16Le(time, 0, cal.get(Calendar.YEAR));
                time[2] = (byte) (cal.get(Calendar.MONTH) + 1);
                time[3] = (byte) cal.get(Calendar.DAY_OF_MONTH);
                time[4] = (byte) cal.get(Calendar.HOUR_OF_DAY);
                time[5] = (byte) cal.get(Calendar.MINUTE);
                time[6] = (byte) cal.get(Calendar.SECOND);
                time[7] = 0; // day of week (0 = unknown)
                time[8] = 0; // fractions of a second
                time[9] = 0; // adjust reason

                writeBytes(BluetoothGattUuid.SERVICE_CURRENT_TIME,
                        BluetoothGattUuid.CHARACTERISTIC_CURRENT_TIME, time);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    protected boolean nextBluetoothCmd(int stateNr) {
        return false;
    }

    @Override
    protected boolean nextCleanUpCmd(int stateNr) {
        return false;
    }

    @Override
    public void onBluetoothDataChange(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic gattCharacteristic) {

    }
}
