/*
 * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */
package org.yawlfoundation.yawl.admin;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * The application configuration for all internal services.
 *
 * @author Philipp Thomas
 * @date 09/11/2016
 */
public class IndexPageServlet extends HttpServlet {


    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException
    {
        String fullContextPath = request.getSession().getServletContext().getContextPath()+"/";

        ClassPathResource classPathResource = new ClassPathResource("index.html");
        Reader reader = new InputStreamReader(classPathResource.getInputStream());
        String content = FileCopyUtils.copyToString(reader);
        String result = content.replaceFirst("<base href=\"/\">", "<base href=\""+fullContextPath+"\">");

        response.setContentType("text/html");
        response.setStatus(200);
        response.getWriter().println(result);
    }

}
