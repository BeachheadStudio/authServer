package com.example.authserver.route;

import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("auth")
@Api(value = "/auth", description = "Auth Example")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthHandler {

    @POST
    @ApiOperation(value = "Validate creds with first party", response = AuthHandler.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid request"),
        @ApiResponse(code = 500, message = "Server is down")
    })
    public String getIt() {
        return "Got it!";
    }
}
