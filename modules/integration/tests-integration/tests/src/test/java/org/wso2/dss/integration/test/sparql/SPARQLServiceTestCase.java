/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.dss.integration.test.sparql;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.dss.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class SPARQLServiceTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(SPARQLServiceTestCase.class);

    private final String serviceName = "SPARQLDataService";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        String serviceEndPoint = getServiceUrlHttp(serviceName);
        String resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                new DataHandler(new URL("file:///" + resourceFileLocation +File.separator + "dbs" + File.separator +
                        "sparql" + File.separator + "SPARQLDataService.dbs")));
        log.info(serviceName + " uploaded");
    }

    @Test(groups = "wso2.dss", description = "Check whether service deployed or not")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"})
    public void getAllBookmarkData() throws RemoteException, XPathExpressionException {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
        OMElement payload = fac.createOMElement("getBookmarks", omNs);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "getBookmarks");
        log.info("Response : " + result);
        Assert.assertTrue(result.toString().contains("<bookmark>"), "Expected Result not found on response message");
        Assert.assertTrue(result.toString().contains("http://semantic.eea.europa.eu/home/roug/bookmarks"),
                "Expected Result not found on response message");

    }

    @AfterClass(alwaysRun = true, groups = "wso2.dss", description = "delete service")
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

}
