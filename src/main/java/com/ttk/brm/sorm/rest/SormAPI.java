package com.ttk.brm.sorm.rest;

import com.ttk.brm.sorm.core.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path(value = "/api/v1.0/sorm")
public class SormAPI {
    private static String ACCESS = "Access-Control-Allow-Origin";
    private static String ACCESS_VALUE = "*";

    @POST
    @Path("/auth")
    @Produces("application/json")
    public Response login_POST(
            @Context HttpServletRequest req,
            @FormParam("login") String login,
            @FormParam("password") String password
    ) {
        JSONObject json = new JSONObject();

        if (login == null || password == null) {
            json.put("code", 400);
            json.put("codeNote", "Не заданы обязательные параметры");
            return Response.status(400).header(ACCESS, ACCESS_VALUE).entity(json.toString()).build();
        }

        User user = User.findByLoginAndPassword(login, User.generateSignSHA256HEX(password));
        if (user == null) {
            json.put("code", 401);
            json.put("codeNote", "Пользователь с таким логин/паролем не найден");
            return Response.status(401).header(ACCESS, ACCESS_VALUE).entity(json.toString()).build();
        }
        user.setSessionId(UUID.randomUUID().toString());

        User.setUserInPool(user.getSessionId(), user);
        //User user = User.findByLoginAndPassword(login, User.generateSignSHA256HEX(password));

        json.put("code", Status.OK.getStatusCode());
        json.put("codeNote", "ОК");
        json.put("sessionId", user.getSessionId());
        return Response.status(Status.OK).header(ACCESS, ACCESS_VALUE).entity(json.toString()).build();
    }

    @GET
    @Path("/getInformationByPhoneFiz")
    @Produces("application/json")
    public Response getInformationByPhoneFiz(
            @Context HttpServletRequest req,
            @QueryParam("phone") String phone,
            @HeaderParam("sessionId") String sessionId
    ) {
        System.out.println("test()");
        JSONObject json = new JSONObject();

        User user = User.findBySessionId(sessionId);
        if (user == null) {
            json.put("code", Status.UNAUTHORIZED.getStatusCode());
            json.put("codeNote", "Ошибка авторизации. Сессия неверна");
            return Response.status(Status.UNAUTHORIZED).header(ACCESS, ACCESS_VALUE).entity(json.toString()).build();
        }
        if (phone == null) {
            json.put("code", Status.BAD_REQUEST.getStatusCode());
            json.put("codeNote", "Телефон не задан");
            return Response.status(Status.BAD_REQUEST).header(ACCESS, ACCESS_VALUE).entity(json.toString()).build();
        }

        json.put("code", 0);
        json.put("codeNote", "ОК");

        JSONObject sub = new JSONObject();
        sub.put("fio", "Иванов Иван Иванович");
        sub.put("address", "г. Москва, Петровский бульвар, д.12, к.1, кв. 21");
        sub.put("account_no", "XJ123456");

        JSONArray orders = new JSONArray();
        JSONObject order = new JSONObject();
        order.put("orderNo", "XJ123456-1");
        order.put("date_from", "01.01.2017");
        order.put("date_to", "30.03.2017");
        order.put("service", "Услуги междугородной и международной телефонной связи");

        orders.put(order);
        sub.put("orders", orders);

        json.put("fiz", sub);
        return Response.status(Status.OK).header(ACCESS, ACCESS_VALUE).entity(json.toString()).build();
    }
}
