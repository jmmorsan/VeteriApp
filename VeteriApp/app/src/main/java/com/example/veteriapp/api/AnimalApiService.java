package com.example.veteriapp.api;

import com.example.veteriapp.model.AnimalFact;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interfaz de Servicio de API para curiosidades animales.
 * 
 * Define los endpoints para la comunicación con el servicio externo
 * Dog API mediante Retrofit para la obtención de datos dinámicos.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public interface AnimalApiService {

    @GET("facts")
    Call<AnimalFact> getRandomDogFact();
}