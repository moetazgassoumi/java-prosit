package com.esprit.Interfaces;

import java.util.List;

public interface EntityCrud<T> {
    public void addEntity(T t);
    public void updateEntity(T t);
    public void deleteEntity(int id);
    public List<T> displayEntities();
    public T findEntity(int id);
}

