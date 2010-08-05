/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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
package org.glassfish.web.admin.cli;

import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.Cluster;
import org.glassfish.api.admin.RuntimeType;
import org.glassfish.api.admin.ServerEnvironment;
import org.glassfish.internal.api.Target;
import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.util.LocalStringManagerImpl;
import com.sun.enterprise.util.SystemPropertyConstants;
import com.sun.grizzly.config.dom.FileCache;
import com.sun.grizzly.config.dom.Http;
import com.sun.grizzly.config.dom.Protocol;
import com.sun.grizzly.config.dom.Protocols;
import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.config.support.CommandTarget;
import org.glassfish.config.support.TargetType;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.component.Habitat;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.config.ConfigSupport;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

/**
 * Command to create http element within a protocol element
 *
 * Sample Usage : create-http protocol_name
 *
 * domain.xml element example
 *
 * &lt;http max-connections=&quot;250&quot; default-virtual-server=&quot;server&quot; server-name=&quot;&quot;&gt; &lt;file-cache enabled=&quot;false&quot; /&gt; &lt;/http&gt;
 *
 * @author Justin Lee
 */
@Service(name = "create-http")
@Scoped(PerLookup.class)
@I18n("create.http")
@Cluster({RuntimeType.DAS, RuntimeType.INSTANCE})
@TargetType({CommandTarget.DAS,CommandTarget.STANDALONE_INSTANCE,CommandTarget.CLUSTER,CommandTarget.CONFIG})
public class CreateHttp implements AdminCommand {
    final private static LocalStringManagerImpl localStrings =
        new LocalStringManagerImpl(CreateHttp.class);
    @Param(name = "protocolname", primary = true)
    String protocolName;
    @Param(name = "request-timeout-seconds", optional = true)
    String requestTimeoutSeconds;
    @Param(name = "timeout-seconds", defaultValue = "30", optional = true)
    String timeoutSeconds;
    @Param(name = "max-connection", defaultValue = "256", optional = true)
    String maxConnections;
    @Param(name = "default-virtual-server")
    String defaultVirtualServer;
    @Param(name = "dns-lookup-enabled", defaultValue = "false", optional = true)
    Boolean dnsLookupEnabled = false;
    @Param(name = "servername", optional = true)
    String serverName;
    @Param(name = "xpowered", optional = true, defaultValue = "true")
    Boolean xPoweredBy = false;
    @Param(name = "target", optional = true, defaultValue = SystemPropertyConstants.DEFAULT_SERVER_INSTANCE_NAME)
    String target;
    @Inject(name = ServerEnvironment.DEFAULT_INSTANCE_NAME)
    Config config;
    @Inject
    Habitat habitat;
    @Inject
    Domain domain;

    /**
     * Executes the command with the command parameters passed as Properties
     * where the keys are the parameter names and the values the parameter
     * values.
     *
     * @param context information
     */
    public void execute(AdminCommandContext context) {
        Target targetUtil = habitat.getComponent(Target.class);
        Config newConfig = targetUtil.getConfig(target);
        if (newConfig!=null) {
            config = newConfig;
        }
        final ActionReport report = context.getActionReport();
        // check for duplicates
        Protocols protocols = config.getNetworkConfig().getProtocols();
        Protocol protocol = null;
        for (Protocol p : protocols.getProtocol()) {
            if(protocolName.equals(p.getName())) {
                protocol = p;
            }
        }
        if (protocol == null) {
            report.setMessage(localStrings.getLocalString("create.http.fail.protocolnotfound",
                "The specified protocol {0} is not yet configured. Please create one", protocolName));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }
        if (protocol.getHttp() != null) {
            report.setMessage(localStrings.getLocalString("create.http.fail.duplicate",
                "An http element for {0} already exists. Cannot add duplicate http", protocolName));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }
        // Add to the <network-config>
        try {
            ConfigSupport.apply(new SingleConfigCode<Protocol>() {
                public Object run(Protocol param) throws TransactionFailure {
                    Http http = param.createChild(Http.class);
                    final FileCache cache = http.createChild(FileCache.class);
                    cache.setEnabled("false");
                    http.setFileCache(cache);
                    http.setDefaultVirtualServer(defaultVirtualServer);
                    http.setDnsLookupEnabled(dnsLookupEnabled == null ? null : dnsLookupEnabled.toString());
                    http.setMaxConnections(maxConnections);
                    http.setRequestTimeoutSeconds(requestTimeoutSeconds);
                    http.setTimeoutSeconds(timeoutSeconds);
                    http.setXpoweredBy(xPoweredBy == null ? null : xPoweredBy.toString());
                    http.setServerName(serverName);
                    param.setHttp(http);
                    return http;
                }
            }, protocol);
        } catch (TransactionFailure e) {
            report.setMessage(localStrings.getLocalString("create.http.redirect.fail",
                "Failed to create http-redirect for {0}: {1}",
                protocolName,
                (e.getMessage() == null ? "No reason given." : e.getMessage())));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setFailureCause(e);
            return;
        }
        report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
    }
}
