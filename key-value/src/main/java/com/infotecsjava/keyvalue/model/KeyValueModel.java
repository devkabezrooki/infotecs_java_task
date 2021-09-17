package com.infotecsjava.keyvalue.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Класс, описывающий сущность "ключ-значение"
 */
@Entity
public class KeyValueModel {
	@Id
	@JsonProperty("Key")
	private String key;
	
	@JsonProperty("Value")
	private String value;
	
	@JsonProperty("TTl")
	private long ttl;
	
	
	@JsonIgnore
	private long defaultTtl = 600000; //значение ttl по умолчанию
	
	public KeyValueModel() {
		super();
	}
	
	public KeyValueModel(String key, String value) {
		this.key = key;
		this.value = value;
		this.ttl = this.defaultTtl;
	}
	
	public KeyValueModel(String key, String value, long ttl) {
		this.key = key;
		this.value = value;
		this.ttl = ttl;
	}

	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public long getTtl() {
		return this.ttl;
	}
	
	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
	
	//Метод "сбрасывает" ttl в значение по умолчанию
	public void resetTtl() {
		this.ttl = this.defaultTtl;
	}
	
	/* 
	 * Метод для проверки времени до удаления записи
	 * @return true, если время жизни записи истекло, иначе - false
	 */
	public boolean checkTtl(int n) {
		this.ttl -= n;
		return (this.ttl <= 0);
	}
	
	//Переопределение метода toString для представления пары "ключ-значение" в формате Json
	@Override
	public String toString() {
		 try {
			return new ObjectMapper().writeValueAsString(this);
		 }
		 catch (JsonProcessingException e) {
			return null;
		 }
	 }
	
}
