package com.casky.dlna.control;

import android.util.Log;

import com.casky.dlna.utils.Utils;

import org.fourthline.cling.model.meta.Device;

import java.io.UnsupportedEncodingException;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：DeviceDisplay   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:54:34   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:54:34   
* 修改备注：   
* 版本： 1.0   
*
 */
public class DeviceDisplay {

    private Device device;

    public DeviceDisplay(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDisplay that = (DeviceDisplay) o;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        String name =
                device.getDetails() != null && device.getDetails().getFriendlyName() != null
                        ? device.getDetails().getFriendlyName()
                        : device.getDisplayString();
        Log.d("syo", "device name " + name);
        byte [] temp;
        try {
			temp = name.getBytes(Utils.getEncoding(name));
	        name = new String(temp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        // Display a little star while the device is being loaded (see performance optimization earlier)
        return device.isFullyHydrated() ? name : name + " *";
    }
}
