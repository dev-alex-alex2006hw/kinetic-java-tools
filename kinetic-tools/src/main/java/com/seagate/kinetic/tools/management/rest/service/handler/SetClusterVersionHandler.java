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
package com.seagate.kinetic.tools.management.rest.service.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.setclversion.SetClusterVersionRequest;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * Set erase pin service handler.
 * 
 * @author chiaming
 *
 */
public class SetClusterVersionHandler extends GenericServiceHandler implements
        ServiceHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return SetClusterVersionRequest.class;
    }

    @Override
    protected void transformRequestParams(HttpServletRequest httpRequest,
            RestRequest req) {
        if (httpRequest.getContentLength() <= 0) {

            SetClusterVersionRequest request = (SetClusterVersionRequest) req;

            Map<String, String[]> params = httpRequest.getParameterMap();

            String[] newclversion = params.get("newclversion");
            if (newclversion != null) {
                request.setNewClversion(Integer.parseInt(newclversion[0]));
            }
        }
    }

}
