package com.jhon.Liter.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
