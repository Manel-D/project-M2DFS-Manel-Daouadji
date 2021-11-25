package com.example.ServiceMeteoVille.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Api
@RestController
public class ServiceMeteoVilleController {
    private int codeCity;

    @Autowired
    RestTemplate restTemplate;

    @ApiOperation(value = "Recuperation de la meteo de la ville", response = Iterable.class, tags = "getMeteoCurrent")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success | OK"),
            @ApiResponse(code = 401, message = "error | Unauthorized"),
            @ApiResponse(code = 403, message = "error | Forbidden"),
            @ApiResponse(code = 404, message = "error | Not found"),
            @ApiResponse(code = 500, message = "error | Internal server ")
    })
    @RequestMapping(value = "getMeteoCurrent/{city}", method = RequestMethod.GET)
    public String getMeteoByNameCity(@PathVariable String city) {
        int code = this.getCodeCity(city);

        String response = restTemplate.exchange("http://dataservice.accuweather.com/currentconditions/v1/" + code
                        + "?apikey=vBo98I3wICJJm7RpgOzOJj6UVZ1LvGmw&language=fr-FR&details=false",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }, code).getBody();
        return response;
    }

    @ApiOperation(value = "Recuperation de la meteo du jour", response = Iterable.class, tags = "getDayMeteo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success | OK"),
            @ApiResponse(code = 401, message = "error | Unauthorized"),
            @ApiResponse(code = 403, message = "error | Forbidden"),
            @ApiResponse(code = 404, message = "error | Not found"),
            @ApiResponse(code = 500, message = "error | Internal server ")
    })
    @RequestMapping(value = "getDayMeteo/{city}", method = RequestMethod.GET)
    public String get1DayDailyForecasts(@PathVariable String city) {
        int code = this.getCodeCity(city);
        String response = restTemplate.exchange("http://dataservice.accuweather.com/forecasts/v1/daily/1day/" + code +
                        "?apikey=vBo98I3wICJJm7RpgOzOJj6UVZ1LvGmw&language=fr-FR&details=false",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }, code).getBody();
        return response;

    }
    public Integer getCodeCity(String ville) {
        codeCity = -2;
        String response = restTemplate.exchange("http://dataservice.accuweather.com/locations/v1/cities" +
                        "/search?apikey=vBo98I3wICJJm7RpgOzOJj6UVZ1LvGmw&q=" + ville
                        + "&language=fr-FR&details=false",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<String>() {
                },
                ville).getBody();
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonResponse;
            jsonResponse = jsonArray.getJSONObject(0);
            codeCity = jsonResponse.getInt("Key");
            System.out.println("code est: " + codeCity);
        } catch (JSONException err) {
            System.out.println("Error");
        }
        return codeCity;
    }
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
