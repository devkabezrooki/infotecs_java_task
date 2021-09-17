package com.infotecsjava.keyvalue.repository;

import com.infotecsjava.keyvalue.model.KeyValueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Класс, описывающий репозиторий для хранения пар ключ-значение
 * Наследуется от JpaRepository
 */
@Repository
public interface KeyValueRepository extends JpaRepository<KeyValueModel, String> {	
}
