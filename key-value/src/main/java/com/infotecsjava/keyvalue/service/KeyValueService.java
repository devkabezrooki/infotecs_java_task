package com.infotecsjava.keyvalue.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.infotecsjava.keyvalue.model.KeyValueModel;

/*
 * Интерфейс, описывающий сервис-хранилище
 */
@Service
public interface KeyValueService {
	/*
	 * Операция чтения (get)
	 * @param key - ключ для хранилища
	 * @return данные, хранящиеся по ключу или null, если данных с таким ключем не существует
	 */
	public String getValue(String key);
	
	/*
	 *  Два варианта операции записи (set)
	 *  Если по переданному ключу уже хранятся данные - заменяет их, а также сбрасывает ttl на значение по умолчанию
	 *  Иначе - создает новую запись
	 *  @param key - ключ для хранилища
	 *  @param value - данные для хранилища
	 *  @param ttl - продолжительность жизни записи (если не задано, используется дефолтное значение
	 *  ttl = 600000)
	 *  @return true, если данные успешно записаны, else - иначе
	 */
	public boolean setValue(String key, String value);
	public boolean setValue(String key, String value, long ttl);
	
	/*
	 * Операция удаления (remove)
	 * Стирает данные, хранящиеся по заданному ключу, а также сбрасывает ttl на значение по умолчанию
	 * @param key - ключ для хранилища
	 * @return данные, хранившиеся по заданному ключу, или null, если таких данных не существует
	 */
	public String remove(String key);
	
	/*
	 * Операция сохранения текущего состояния (dump)
	 * Сохраняет текущее состояние хранилища в файл dump.txt в корневую папку проекта в виде json-строк
	 * При проблемах с обработкой файла или перевода объектов в json выбрасывает IOException
	 * @return текущее состояние проекта в формате json
	 */
	public String dump() throws IOException; 
	
	/*
	 * Операция загрузки состояния хранилища (load)
	 * Загружает текущее состояние хранилища из файла dump.txt, создаваемого
	 * При проблемах с обработкой файла или json-объектов выбрасывает IOException
	 */
	public void load() throws IOException;
	
	/*
	 * Операция получения состояния хранилища
	 * @return список всех пар ключ-значение, сохраненных на данный момент
	 */
	public List<KeyValueModel> getAll();
	
	/*
	 * Метод для проверки времени до удаления записей в хранилище
	 * Если время жизни какой-либо записи истекло - она удаляется из хранилища и выводится сообщение об этом в консоль
	 * @param n - количество милисекунд, через которое записи перепроверяются
	 */
	public void checkTtls(int n);
	
}
