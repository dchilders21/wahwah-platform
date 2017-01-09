package com.wahwahnetworks.platform.lib.google;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.wahwahnetworks.platform.data.entities.GoogleDataStore;
import com.wahwahnetworks.platform.data.entities.GoogleDataStoreValue;
import com.wahwahnetworks.platform.data.repos.GoogleDataStoreRepository;
import com.wahwahnetworks.platform.data.repos.GoogleDataStoreValueRepository;
import com.wahwahnetworks.platform.lib.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jhaygood on 4/11/16.
 */

@Component
@Scope("prototype")
public class RedPandaDataStore<V extends Serializable> implements DataStore<V> {

    private RedPandaDataStoreFactory dataStoreFactory;
    private String id;

    @Autowired
    private GoogleDataStoreRepository dataStoreRepository;

    @Autowired
    private GoogleDataStoreValueRepository dataStoreValueRepository;

    public RedPandaDataStore(RedPandaDataStoreFactory dataStoreFactory, String id){
        this.dataStoreFactory = dataStoreFactory;
        this.id = id;
    }

    private GoogleDataStore getDataStore(){
        GoogleDataStore dataStore = dataStoreRepository.findByGoogleValueId(id);

        if(dataStore == null){
            dataStore = new GoogleDataStore();
            dataStore.setGoogleValueId(id);
            dataStoreRepository.save(dataStore);
        };

        return dataStore;
    }

    protected Class<V> getClassType(){
        return (Class<V>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public DataStoreFactory getDataStoreFactory() {
        return dataStoreFactory;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    @Transactional
    public int size() throws IOException {
        return dataStoreValueRepository.countByDataStore(getDataStore());
    }

    @Override
    public boolean isEmpty() throws IOException {
        return size() == 0;
    }

    @Override
    public boolean containsKey(String key) throws IOException {
        return keySet().contains(key);
    }

    @Override
    public boolean containsValue(V value) throws IOException {
        return values().contains(value);
    }

    @Override
    public Set<String> keySet() throws IOException {
        List<GoogleDataStoreValue> values = dataStoreValueRepository.findByDataStore(getDataStore());
        Set<String> keys = values.stream().map(v -> v.getKey()).collect(Collectors.toSet());
        return keys;
    }

    @Override
    public Collection<V> values() throws IOException {
        List<GoogleDataStoreValue> values = dataStoreValueRepository.findByDataStore(getDataStore());

        List<String> jsonValues = values.stream().map(v -> v.getValue()).collect(Collectors.toList());

        List<V> results = new ArrayList<>();

        for(String value : jsonValues){
            results.add(JsonSerializer.deserialize(value,getClassType()));
        }

        return results;
    }

    @Override
    public V get(String key) throws IOException {
        GoogleDataStoreValue value = dataStoreValueRepository.findByDataStoreAndKey(getDataStore(),key);

        if (value != null) {
            return JsonSerializer.deserialize(value.getValue(),getClassType());
        }

        return null;
    }

    @Override
    public DataStore<V> set(String key, V value) throws IOException {
        GoogleDataStoreValue dataStoreValue = dataStoreValueRepository.findByDataStoreAndKey(getDataStore(),key);

        if(dataStoreValue == null){
            dataStoreValue = new GoogleDataStoreValue();
            dataStoreValue.setDataStore(getDataStore());
            dataStoreValue.setKey(key);
        }

        String data = JsonSerializer.serialize(value);
        dataStoreValue.setValue(data);

        dataStoreValueRepository.save(dataStoreValue);

        return this;
    }

    @Override
    public DataStore<V> clear() throws IOException {
        Iterable<GoogleDataStoreValue> values = dataStoreValueRepository.findByDataStore(getDataStore());

        for(GoogleDataStoreValue value : values){
            dataStoreValueRepository.delete(value);
        }

        return this;
    }

    @Override
    public DataStore<V> delete(String key) throws IOException {
        GoogleDataStoreValue dataStoreValue = dataStoreValueRepository.findByDataStoreAndKey(getDataStore(),key);
        dataStoreValueRepository.delete(dataStoreValue);

        return this;
    }
}
