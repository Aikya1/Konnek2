package com.android.konnek2.call.services;

import java.util.Collection;
import java.util.List;

/**
 * Created by Lenovo on 08-11-2017.
 */

public interface QMBaseCache<T,ID> {

    void create(T object);

    void createOrUpdate(T object);

    void createOrUpdateAll(Collection<T> objectsCollection);

    T get(ID id);

    List<T> getAll();

    List<T> getAllSorted(String sortedColumn, boolean ascending);

    List<T> getByColumn(String column, String value);

    List<T> getByColumn(String column, Collection<String> values);

    void update(T object);

    void updateAll(Collection<T> objectsCollection);

    void delete(T object);

    void deleteById(ID id);

    boolean exists(ID id);

    void clear();
}
