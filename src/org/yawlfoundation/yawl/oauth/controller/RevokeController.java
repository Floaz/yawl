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
package org.yawlfoundation.yawl.oauth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * The controller for revoking a token.
 * This has the meaning of logging out.
 *
 * @author Philipp R. Thomas
 * @date 2016-12-15
 */
@RestController
public class RevokeController {

    @Autowired
    private TokenStore tokenStore;

    @RequestMapping(value = "/oauth/revoke-token", method = RequestMethod.POST)
    public void revokeToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            throw new RuntimeException("Not authorized!");
        }

        String tokenValue = authHeader.replace("Bearer", "").trim();

        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);

        if (accessToken == null) {
            throw new RuntimeException("No token stored with this value!");
        }

        tokenStore.removeAccessToken(accessToken);
    }


//    @RequestMapping(value = "/oauth/all-tokens", method = RequestMethod.GET)
//    public Collection<OAuth2AccessToken> getAllTokens() {
//        return tokenStore.findTokensByClientId("yawldashboard");
//    }



//    @Autowired
//    private AuthorizationServerTokenServices    authorizationServerTokenServices;
//
//    @Autowired
//    private ConsumerTokenServices               consumerTokenServices;

//    @RequestMapping(value = "/oauth/revoke-token")
//    public void logout(Principal principal) {
//        OAuth2Authentication authentication = (OAuth2Authentication) principal;
//        if(authentication == null) {
//            throw new RuntimeException("No authentication!");
//        }
//
//        OAuth2AccessToken token = authorizationServerTokenServices.getAccessToken(authentication);
//        if(token == null) {
//            throw new RuntimeException("No token stored!");
//        }
//
//        consumerTokenServices.revokeToken(token.getValue());
//    }
}