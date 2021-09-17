package com.infotecsjava.keyvalue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotecsjava.keyvalue.model.KeyValueModel;
import com.infotecsjava.keyvalue.repository.KeyValueRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/*
 * Класс, реализующий интерфейс KeyValueService 
 */
@Service
@Transactional
public class KeyValueServiceImpl implements KeyValueService {
	//Репозиторий для хранения пар ключ-значение
	public KeyValueRepository repository;
	
	//Связываем компоненты сервиса и репозитория приложения
    @Autowired
    public void setKeyValueRepository(KeyValueRepository keyValueRepository) {
        this.repository = keyValueRepository;
    }
    
    public KeyValueServiceImpl(){
    	
    }
    
    @Override
    public List<KeyValueModel> getAll() {
    	return this.repository.findAll();
    }
    
	@Override
	public String getValue(String key) {
		if(this.repository.existsById(key)) {
			KeyValueModel KeyValue = this.repository.getById(key);
			return KeyValue.getValue();
		}
		else return null;
	}
	
	@Override
	public boolean setValue(String key, String value) {
		//Проверка на корректность введенных данных
		if(key == null || key.isEmpty() || key.isBlank() || value == null || value.isEmpty() || value.isBlank()) {
			return false;
		}
		if(!this.repository.existsById(key)) {
			KeyValueModel KeyValue = new KeyValueModel(key, value);
			this.repository.saveAndFlush(KeyValue);
			return true;
		}
		else {
			KeyValueModel KeyValue = this.repository.getById(key);
			KeyValue.setValue(value);
			KeyValue.resetTtl();
			this.repository.saveAndFlush(KeyValue);
			return true;
		}
	}

	@Override
	public boolean setValue(String key, String value, long ttl) {
		//Проверка на корректность введенных данных
		if(ttl <= 0 || key == null || key.isEmpty() || key.isBlank() || value == null || value.isEmpty() || value.isBlank()) {
			return false;
		}
		if(!this.repository.existsById(key)) {
			KeyValueModel KeyValue = new KeyValueModel(key, value, ttl);
			this.repository.saveAndFlush(KeyValue);
			return true;
		}
		else {
			KeyValueModel KeyValue = this.repository.getById(key);
			KeyValue.setValue(value);
			KeyValue.setTtl(ttl);
			this.repository.saveAndFlush(KeyValue);
			return true;
		}
	}


	@Override
	public String remove(String key) {
		if(this.repository.existsById(key)) {
			KeyValueModel KeyValue = this.repository.getById(key);
			String value = KeyValue.getValue();
			KeyValue.resetTtl();
			if(value != null) {
				KeyValue.setValue(null);
				this.repository.saveAndFlush(KeyValue);
				return value;
			}
			else {
				return null;
			}
		}
		else return null;
	}

	@Override
	public String dump() throws IOException {
		FileWriter writer = new FileWriter("dump.txt");
		String res = new String();
		List<KeyValueModel> keyValues = this.getAll();
		for(KeyValueModel keyValue : keyValues) {
			if(keyValue.toString() == null) {
				return null;
			}
			writer.write(keyValue.toString() + "\r\n");
			res += keyValue.toString() + "\r\n";
		}
		writer.flush();
		writer.close();
		return res;
	}

	@Override
	public void load() throws IOException {
		if(this.repository.count() != 0) this.repository.deleteAll();
		ObjectMapper mapper = new ObjectMapper();
		FileReader fr = new FileReader("dump.txt");
		BufferedReader reader = new BufferedReader(fr);
		String line = reader.readLine();
		while (line != null) {
			KeyValueModel KeyValue = mapper.readValue(line, KeyValueModel.class);
			this.repository.save(KeyValue);
			line = reader.readLine();
		}
		fr.close();
		reader.close();
	}
	
	@Override
	public void checkTtls(int n) {
		List<KeyValueModel> keyValues = this.getAll();
		for(KeyValueModel keyValue : keyValues) {
			if(keyValue.checkTtl(n)) {
				System.out.println("Lifetime of " + keyValue.getKey() + ":" 
				+ keyValue.getValue() + " is expired");
				this.repository.delete(keyValue);
			}
			else {
				this.repository.save(keyValue);
			}
		}
	}
	
}
