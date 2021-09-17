package com.infotecsjava.keyvalue.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import com.infotecsjava.keyvalue.model.KeyValueModel;
import com.infotecsjava.keyvalue.repository.KeyValueRepository;

/*
 * Класс для тестирования сервиса
 */
@SpringBootTest
class KeyValueServiceTest {

	@TestConfiguration
    class KeyValueServiceImplTestContextConfiguration {
 
        @Bean
        public KeyValueService keyValueService() {
            return new KeyValueServiceImpl();
        }
    }

    @Autowired
    private KeyValueService service;

    @MockBean
    private KeyValueRepository repository;
 
    //Перед вызовом каждого теста инициализируется репозиторий
	@BeforeEach
	void setUp() throws Exception {
		
		KeyValueModel model = new KeyValueModel("key", "value");
		List<KeyValueModel> list = new ArrayList();
		list.add(model);
		
		Mockito.doReturn(true).when(repository).existsById(model.getKey());
		Mockito.doReturn(model).when(repository).getById(model.getKey());
	}

	/*
	 * Тест корректного использования метода getValue
	 * Ожидается, что при значении параметра key = "key", метод вернет "value"
	 */
	@Test
	void testCorrectGetValue() {
		assertEquals("value", service.getValue("key"));
	}
	
	/*
	 * Тест вызова метода getValue с ключом, которого нет в репозитории
	 * Ожидается, что в этом случае метод вернет null
	 */
	@Test
	void testNotExistingKeyGetValue() {
		assertEquals(null, service.getValue("key1"));
	}
	
	/*
	 * Тест вызова метода setValue с новой парой ключ-значение
	 * Ожидается, что в этом случае метод вернет true
	 */
	@Test
	void testCorrectSetValueNew() {
		assertTrue(service.setValue("key1", "value1"));
	}
	
	/*
	 * Тест вызова метода setValue с уже существующем в репозитории ключе
	 * Ожидается, что в этом случае метод вернет true
	 */
	@Test
	void testCorrectSetValueExist() {
		assertTrue(service.setValue("key", "value1"));
	}
	
	/*
	 * Тест вызова метода setValue для некорректного ключа
	 * Ожидается, что в этом случае метод вернет false
	 */
	@Test
	void testIncorrectSetValue() {
		assertFalse(service.setValue(null, "value1"));
	}
	
	/*
	 * Тест корректного использования метода remove
	 * Ожидается, что при значении параметра key = "key", метод вернет "value"
	 */
	@Test
	void testCorrectRemove() {
		assertEquals("value", service.remove("key"));
	}
	
	/*
	 * Тест вызова метода getValue с ключом, которого нет в репозитории
	 * Ожидается, что в этом случае метод вернет null
	 */
	@Test
	void testNotExisitingKeyRemove() {
		assertEquals(null, service.remove("key1"));
	}

}
