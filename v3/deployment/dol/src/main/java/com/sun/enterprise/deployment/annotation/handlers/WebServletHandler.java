/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package com.sun.enterprise.deployment.annotation.handlers;

import com.sun.enterprise.deployment.EnvironmentProperty;
import com.sun.enterprise.deployment.WebComponentDescriptor;
import com.sun.enterprise.deployment.annotation.context.WebBundleContext;
import com.sun.enterprise.deployment.annotation.context.WebComponentContext;
import org.glassfish.apf.AnnotationInfo;
import org.glassfish.apf.AnnotationProcessorException;
import org.glassfish.apf.HandlerProcessingResult;
import org.glassfish.apf.ResultType;
import org.jvnet.hk2.annotations.Service;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;

/**
 * This handler is responsible in handling
 * javax.servlet.annotation.WebServlet.
 *
 * @author Shing Wai Chan
 */
@Service
public class WebServletHandler extends AbstractWebHandler {
    public WebServletHandler() {
    }

    /**
     * @return the annotation type this annotation handler is handling
     */
    public Class<? extends Annotation> getAnnotationType() {
        return WebServlet.class;
    }

    protected HandlerProcessingResult processAnnotation(AnnotationInfo ainfo,
            WebComponentContext[] webCompContexts)
            throws AnnotationProcessorException {

        HandlerProcessingResult result = null;
        for (WebComponentContext webCompContext : webCompContexts) {
            result = processAnnotation(ainfo,
                    webCompContext.getDescriptor());
            if (result.getOverallResult() == ResultType.FAILED) {
                break;
            }
        }
        return result;
    }

    protected HandlerProcessingResult processAnnotation(
            AnnotationInfo ainfo, WebBundleContext webBundleContext)
            throws AnnotationProcessorException {

        WebServlet webServletAn = (WebServlet)ainfo.getAnnotation();

        Class webCompClass = (Class)ainfo.getAnnotatedElement();
        String servletName = webServletAn.name();
        if (servletName == null || servletName.length() == 0) {
            servletName = webCompClass.getName();
        }

        WebComponentDescriptor webCompDesc =
            webBundleContext.getDescriptor().getWebComponentByCanonicalName(servletName);
        if (webCompDesc == null) {
            webCompDesc = new WebComponentDescriptor();
            webCompDesc.setName(servletName);
            webCompDesc.setCanonicalName(servletName);
        }
        
        HandlerProcessingResult result = processAnnotation(ainfo, webCompDesc);
        if (result.getOverallResult() == ResultType.PROCESSED) {
            webBundleContext.getDescriptor().addWebComponentDescriptor(webCompDesc, true);
            WebComponentContext webCompContext = new WebComponentContext(webCompDesc);
            // we push the new context on the stack...
            webBundleContext.getProcessingContext().pushHandler(webCompContext);
        }

        return result;
    }

    private HandlerProcessingResult processAnnotation(
            AnnotationInfo ainfo, WebComponentDescriptor webCompDesc)
            throws AnnotationProcessorException {

        Class webCompClass = (Class)ainfo.getAnnotatedElement();
        if (!HttpServlet.class.isAssignableFrom(webCompClass)) {
            log(Level.SEVERE, ainfo,
                localStrings.getLocalString(
                "enterprise.deployment.annotation.handlers.needtoextend",
                "The Class {0} having annotation {1} need to be a derived class of {2}.",
                new Object[] { webCompClass.getName(), WebServlet.class.getName(), HttpServlet.class.getName() }));
            return getDefaultFailedResult();
        }

        WebServlet webServletAn = (WebServlet)ainfo.getAnnotation();

        String webCompImpl = webCompDesc.getWebComponentImplementation();
        if (webCompImpl != null && webCompImpl.length() > 0 &&
                (!webCompImpl.equals(webCompClass.getName()) || !webCompDesc.isServlet())) {

            String messageKey = null;
            String defaultMessage = null;

            if (webCompDesc.isServlet()) {
                messageKey = "enterprise.deployment.annotation.handlers.servletimpldontmatch";
                defaultMessage = "The servlet '{0}' has implementation '{1}' in xml. It does not match with '{2}' from annotation @{3}.";
            } else {
                messageKey = "enterprise.deployment.annotation.handlers.servletimpljspdontmatch";
                defaultMessage = "The servlet '{0}' is a jsp '{1}' in xml. It does not match with '{2}' from annotation @{3}.";
            }
            
            log(Level.SEVERE, ainfo,
                localStrings.getLocalString(messageKey, defaultMessage,
                new Object[] { webCompDesc.getName(), webCompImpl, webCompClass.getName(),
                WebServlet.class.getName() }));
            return getDefaultFailedResult();
        }
        webCompDesc.setServlet(true);
        webCompDesc.setWebComponentImplementation(webCompClass.getName());

        if (webCompDesc.getUrlPatternsSet().size() == 0) {
            String[] urlPatterns = webServletAn.urlPatterns();
            if (urlPatterns == null || urlPatterns.length == 0) {
                urlPatterns = webServletAn.value();
            }

            boolean validUrlPatterns = false;
            if (urlPatterns != null && urlPatterns.length > 0) {
                validUrlPatterns = true;
                for (String up : urlPatterns) {
                    if (up == null || up.length() == 0) {
                        validUrlPatterns = false;
                        break;
                    }
                    webCompDesc.addUrlPattern(up);
                }
            }

            if (!validUrlPatterns) {
                String urlPatternString =
                    (urlPatterns != null) ? Arrays.toString(urlPatterns) : "";

                throw new IllegalArgumentException(localStrings.getLocalString(
                        "enterprise.deployment.annotation.handlers.invalidUrlPatterns",
                        "Invalid url patterns for {0}: {1}.",
                        new Object[] { webCompClass, urlPatternString }));
            }
        }

        if (webCompDesc.getLoadOnStartUp() == null) {
            webCompDesc.setLoadOnStartUp(webServletAn.loadOnStartup());
        }

        WebInitParam[] initParams = webServletAn.initParams();
        if (initParams != null && initParams.length > 0) {
            for (WebInitParam initParam : initParams) {
                webCompDesc.addInitializationParameter(
                        new EnvironmentProperty(
                            initParam.name(), initParam.value(),
                            initParam.description()));
            }
        }

        if (webCompDesc.getSmallIconUri() == null) {
            webCompDesc.setSmallIconUri(webServletAn.smallIcon());
        }
        if (webCompDesc.getLargeIconUri() == null) {
            webCompDesc.setLargeIconUri(webServletAn.largeIcon());
        }

        if (webCompDesc.getDescription() == null ||
                webCompDesc.getDescription().length() > 0) {
            webCompDesc.setDescription(webServletAn.description());
        }

        if (webCompDesc.isAsyncSupported() == null) {
            webCompDesc.setAsyncSupported(webServletAn.asyncSupported());
        }

        return getDefaultProcessedResult();
    }
}
