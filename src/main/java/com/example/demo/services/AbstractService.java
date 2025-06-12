package com.example.demo.services;

import java.util.List;

public interface AbstractService<T>  {
    T save(T dto);

    List<T> findAll();

    T findById(Integer id);

    void delete(Integer id);
}
