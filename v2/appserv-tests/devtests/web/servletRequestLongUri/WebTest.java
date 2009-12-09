/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/**
 * Unit test for testing very long request URI.
 *
 * There was a claim that an URI of length 3427 was correctly mapped to
 * the target servlet, whereas an URI of length 3428 was somehow "swallowed"
 * by the web container and no longer mapped to the target servlet and
 * resulting in a 200 response code.
 *
 * This purpose of this unit test is to make sure that this is not the case.
 */
public class WebTest {

    private static final String TEST_NAME = "servlet-request-long-uri";

    private static final SimpleReporterAdapter stat =
        new SimpleReporterAdapter("appserv-tests");

    private String host;
    private String port;
    private String contextRoot;

    public WebTest(String[] args) {
        host = args[0];
        port = args[1];
        contextRoot = args[2];
    }
    
    public static void main(String[] args) {

        stat.addDescription("Unit test for testing very long request URI");
        WebTest webTest = new WebTest(args);
        try {
            // Connect with request URI of length 3427
            webTest.doTest("/entries12345/3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30b", "3427");
            // Connect with request URI of length 3428
            webTest.doTest("/entries12345/3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30e86c-a339-3390-a5c5-2e4074036145,3d30bb", "3428");
            stat.addStatus(TEST_NAME, stat.PASS);
        } catch (Exception ex) {
            stat.addStatus(TEST_NAME, stat.FAIL);
            ex.printStackTrace();
        }
	stat.printSummary();
    }

    public void doTest(String pathInfo, String expected) throws Exception {
        URL url = new URL("http://" + host  + ":" +
            port + contextRoot + "/TestServlet" + pathInfo);
        System.out.println("Connecting to: " + url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) { 
            throw new Exception("Wrong response code. Expected: 200" +
                ", received: " + responseCode);
        }

        InputStream is = null;
        BufferedReader input = null;
        try {
            is = conn.getInputStream();
            input = new BufferedReader(new InputStreamReader(is));
            String line = input.readLine();
            if (!expected.equals(line)) {
                throw new Exception("Wrong response. Expected: " +
                    expected + ", received: " + line);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

}
