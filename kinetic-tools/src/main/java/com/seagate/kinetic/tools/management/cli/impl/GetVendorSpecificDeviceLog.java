/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.cli.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import kinetic.admin.Device;
import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.common.util.MessageUtil;

public class GetVendorSpecificDeviceLog extends AbstractCommand {
    private byte[] vendorSpecificName;
    private String outputFilePath;

    public GetVendorSpecificDeviceLog(String vendorSpecificNameInString,
            String drivesInputFile, String outputFilePath, boolean useSsl,
            long clusterVersion, long identity, String key, long requestTimeout)
            throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesInputFile);
        this.outputFilePath = outputFilePath;
        this.vendorSpecificName = null;
        parseVendorSpecificDeviceName(vendorSpecificNameInString);
    }

    private void parseVendorSpecificDeviceName(String vendorSpecificNameInString) {
        if (vendorSpecificNameInString != null) {
            vendorSpecificName = vendorSpecificNameInString.getBytes(Charset
                    .forName("UTF-8"));
        }
    }

    private void getVendorSpecificDeviceLog() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new getVendorSpecificDeviceLogThread(device,
                    vendorSpecificName));
        }
        poolExecuteThreadsInGroups(threads);
    }

    private String device2Json(KineticDevice kineticDevice, Device device) {
        StringBuffer sb = new StringBuffer();
        sb.append(" {\n");
        sb.append("   \"device\":");
        sb.append(MessageUtil.toJson(kineticDevice));
        sb.append(",\n");

        sb.append("   \"vendorspecificname\":");
        sb.append(MessageUtil.toJson(new String(device.getName())));
        sb.append(",\n");

        sb.append("   \"vendorspecificvalue\":");
        sb.append(new String(device.getValue()));
        sb.append("\n }");

        return sb.toString();
    }

    private void persist2File(String sb) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFilePath);
        fos.write(sb.getBytes("UTF-8"));
        fos.flush();
        fos.close();
    }

    class getVendorSpecificDeviceLogThread extends AbstractWorkThread {
        private byte[] vendorSpecificName;

        public getVendorSpecificDeviceLogThread(KineticDevice device,
                byte[] vendorSpecificName) throws KineticException {
            super(device);
            this.vendorSpecificName = vendorSpecificName;
        }

        @Override
        void runTask() throws KineticToolsException {
            try {
                Device vendorSpecficInfo = adminClient
                        .getVendorSpecificDeviceLog(vendorSpecificName);
                String vendorSpecficInfo2Json = device2Json(device,
                        vendorSpecficInfo);
                synchronized (sb) {
                    sb.append(vendorSpecficInfo2Json + "\n");
                }
                report.reportSuccess(device);
                report.setAdditionMessage(device, vendorSpecficInfo);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            getVendorSpecificDeviceLog();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        try {
            persist2File(sb.toString());
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
        System.out.println("Save logs to " + outputFilePath + " completed.");
    }
}