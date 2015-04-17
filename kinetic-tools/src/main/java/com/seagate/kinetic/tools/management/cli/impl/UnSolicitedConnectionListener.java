package com.seagate.kinetic.tools.management.cli.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kinetic.client.ConnectionListener;

import com.seagate.kinetic.common.lib.KineticMessage;
import com.seagate.kinetic.proto.Kinetic.Command.GetLog.Configuration;
import com.seagate.kinetic.proto.Kinetic.Command.GetLog.Configuration.Interface;

public class UnSolicitedConnectionListener implements ConnectionListener {
    private Map<KineticDevice, String> devices;

    public UnSolicitedConnectionListener() {
        this.devices = new HashMap<KineticDevice, String>();
    }

    @Override
    public void onMessage(KineticMessage message) {
        KineticDevice device = new KineticDevice();
        Configuration configuration = null;
        if (null != message && null != message.getCommand()
                && null != message.getCommand().getBody()
                && null != message.getCommand().getBody().getGetLog())

            configuration = message.getCommand().getBody().getGetLog()
                    .getConfiguration();

        if (null != configuration) {
            List<Interface> itfs = configuration.getInterfaceList();
            List<String> inet4 = new ArrayList<String>();
            if (null != itfs && 0 != itfs.size()) {
                if (null != itfs.get(0) && null != itfs.get(0).getIpv4Address()) {
                    inet4.add(itfs.get(0).getIpv4Address().toStringUtf8());
                }
                if (null != itfs.get(1) && null != itfs.get(1).getIpv4Address()) {
                    inet4.add(itfs.get(1).getIpv4Address().toStringUtf8());
                }
            }
            device.setInet4(inet4);
            device.setModel(configuration.getModel());
            device.setPort(configuration.getPort());
            device.setTlsPort(configuration.getTlsPort());
            device.setFirmwareVersion(configuration.getVersion());
            device.setSerialNumber(configuration.getSerialNumber()
                    .toStringUtf8());
            device.setWwn(configuration.getWorldWideName().toStringUtf8());
        }

        synchronized (devices) {
            this.devices.put(device, "");
        }
    }

    public Map<KineticDevice, String> getDevices() {
        return this.devices;
    }
}