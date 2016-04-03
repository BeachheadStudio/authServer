package com.example.authserver.route;

import com.example.authserver.model.AmazonAuth;
import com.example.authserver.model.AppleAuth;
import com.example.authserver.model.Auth;
import com.example.authserver.model.GoogleAuth;
import com.example.authserver.service.auth.AmazonAuthService;
import com.example.authserver.service.auth.AppleAuthService;
import com.example.authserver.service.auth.AuthService;
import com.example.authserver.service.auth.GoogleAuthService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("auth")
@Api(value = "/auth", description = "Auth Example")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthHandler {
    private static final Logger logger = LogManager.getLogger(AuthHandler.class);

    @POST
    @ApiOperation(value = "Validate creds with first party", response = AuthHandler.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid request"),
        @ApiResponse(code = 500, message = "Server is down")
    })
    public Response authCheck(String data) {
        logger.debug("Starting auth check");
        if(data == null || data.equals("")) {
            return Response.status(400).build();
        }

        Auth auth = AuthService.detectAuthJson(data);
        if(auth instanceof AppleAuth) {
            AppleAuthService authService = new AppleAuthService();

            if(!authService.isFirstPartyAuthed((AppleAuth) auth)) {
                return Response.status(400).build();
            }
        }

        if(auth instanceof GoogleAuth) {
            GoogleAuthService authService = new GoogleAuthService();

            if(!authService.isFirstPartyAuthed((GoogleAuth) auth)) {
                return Response.status(400).build();
            }
        }

        if(auth instanceof AmazonAuth) {
            AmazonAuthService authService = new AmazonAuthService();

            if(!authService.isFirstPartyAuthed((AmazonAuth) auth)) {
                return Response.status(400).build();
            }
        }

        return Response.ok()
                .cookie(new NewCookie("session-token", UUID.randomUUID().toString()))
                .build();
    }
}
