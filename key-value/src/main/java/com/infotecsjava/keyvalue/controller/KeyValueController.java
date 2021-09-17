package com.infotecsjava.keyvalue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import com.infotecsjava.keyvalue.model.KeyValueModel;
import com.infotecsjava.keyvalue.service.KeyValueService;

import java.io.IOException;
import java.util.List;

/*
 * Контролер для обработки запросов к хранилищу
 */
@RestController
@EnableScheduling
public class KeyValueController {
	
	private KeyValueService keyValueService;
	
	//Связываем компоненты сервиса и контроллера приложения
	@Autowired
	public KeyValueController(KeyValueService keyValueService) {
	       this.keyValueService = keyValueService;
	}
	
	/*
	 * Метод для обработки операции чтения
	 * Обрабатывает Get-запросы на адрес /get/{key}
	 * @param key - ключ для хранилища
	 * @return данные, хранящиеся по ключу, и HTTP-статус 200 OK 
	 * Или сообщение об ошибке и статус 404 Not Found, если данные не обнаружены
	 */
	@GetMapping("/get/{key}")
	public ResponseEntity<String> getValue(@PathVariable(name = "key") String key) {
		String value = this.keyValueService.getValue(key);
		return value != null
		           ? new ResponseEntity<>(value, HttpStatus.OK)
		           : new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
	}
	
	/*
	 * Метод для обработки получения всех пар ключ-значение
	 * Обрабатывает Get-запросы на адрес /getall
	 * @return список всех пар ключ-значение и HTTP-статус 200 OK 
	 * Или сообщение об ошибке и статус 404 Not Found, если репозиторий пуст
	 */
	@GetMapping(value = "/getall")
	public ResponseEntity<?> getAll() {
	   final List<KeyValueModel> repository = keyValueService.getAll();

	   return repository != null &&  !repository.isEmpty()
	           ? new ResponseEntity<>(repository, HttpStatus.OK)
	           : new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
	}
	
	/*
	 * Метод для обработки операции записи без заданного параметра ttl
	 * Обрабатывает Post-запросы на адрес /set/{key}/{value}
	 * @param key - ключ для хранилища
	 * @param value - данные для хранилища
	 * @return сообщение "Ok" и HTTP-статус 201 Created
	 * Или сообщение об ошибке и статус 409 Conflict, если введенные данные некорректны
	 */
	@PostMapping(value = "/set/{key}/{value}")
	public ResponseEntity<String> setValue(@PathVariable(name = "key") String key,
			@PathVariable(name = "value") String value) {
		boolean set = keyValueService.setValue(key, value);
		return set != false ? new ResponseEntity<>("Ok", HttpStatus.CREATED)
				   			: new ResponseEntity<>("Incorrect data", HttpStatus.CONFLICT);
	}
	
	/*
	 * Метод для обработки операции записи с заданным параметром ttl
	 * Обрабатывает Post-запросы на адрес /set/{key}/{value}
	 * @param key - ключ для хранилища
	 * @param value - данные для хранилища
	 * @param ttl - продолжительность жизни записи
	 * @return сообщение "Ok" и HTTP-статус 201 Created
	 * Или сообщение об ошибке и статус 409 Conflict, если введенные данные некорректны
	 */
	@PostMapping(value = "/set/{key}/{value}/{ttl}")
	public ResponseEntity<String> setValue(@PathVariable(name = "key") String key, 
			@PathVariable(name = "value") String value, @PathVariable(name = "ttl") long ttl) {
		boolean set = keyValueService.setValue(key, value, ttl);
		return set != false ? new ResponseEntity<>("Ok", HttpStatus.CREATED)
	   			: new ResponseEntity<>("Incorrect data", HttpStatus.CONFLICT);
	}
	
	/*
	 * Метод для обработки операции удаления
	 * Обрабатывает Put-запросы на адрес /remove/{key}
	 * @param key - ключ для хранилища
	 * @return данные, хранившиеся по ключу, и HTTP-статус 200 OK 
	 * Или сообщение об ошибке и статус 404 Not Found, если данные не обнаружены
	 */
	@PutMapping(value = "/remove/{key}")
	public ResponseEntity<String> removeValue(@PathVariable(name = "key") String key) {
		String value = keyValueService.remove(key);
		return value != null
		           ? new ResponseEntity<>(value, HttpStatus.OK)
		           : new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
		
	}
	
	/*
	 * Метод для обработки операции сохранения состояния хранилища
	 * Обрабатывает Get-запросы на адрес /dump
	 * @return текущее состояние проекта в формате json и HTTP-статус 200 OK,
	 * Или сообщение об ошибке и статус 404 Not Found, если данные не обнаружены,
	 * Или сообщение об ошибке и статус 409 Conflict при проблемах с обработкой файла или перевода объектов в json
	 */
	@GetMapping(value = "/dump")
	public ResponseEntity<String> dump() {
		String dump;
		try {
			dump = this.keyValueService.dump();
			if(dump == null) {
				return new ResponseEntity<>("JSON processing error", HttpStatus.CONFLICT);
			}
			return !dump.isEmpty()
						? new ResponseEntity<>(dump, HttpStatus.OK)
						: new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
		} 
		catch (IOException e) {
			return new ResponseEntity<>("File processing error", HttpStatus.CONFLICT);
		}
	}
	
	/*
	 * Метод для обработки операции загрузки состояния хранилища
	 * Обрабатывает Post-запросы на адрес /load
	 * @return сообщение "Ok" и HTTP-статус 201 Created
	 * Или сообщение об ошибке и статус 409 Conflict при проблемах с обработкой файла или json-объектов
	 */
	@PostMapping(value = "/load")
	public ResponseEntity<String> load() {
		try {
			this.keyValueService.load();
			return new ResponseEntity<>("Ok", HttpStatus.CREATED);
		} 
		catch (IOException e) {
			return new ResponseEntity<>("File processing error", HttpStatus.CONFLICT);
		}
	}
	
	/*
	 * Метод для проверки времени до удаления записей в хранилище
	 * Запускается с интервалом в 1 миллисекунду независимо от предыдущего запуска
	 */
	@Scheduled(fixedRate = 1)
	public void checkTtl() {
		this.keyValueService.checkTtls(1);
	}
	
}
