package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	long idExists;
	long idNotExists;
	long qtdeTotalProdutos;

	@BeforeEach
	void setUp() throws Exception {
		idExists = 1;
		
		idNotExists = 500;
		
		qtdeTotalProdutos = 25;
	}
	
	
	@Test
	public void findByIdShouldReturnNotEmptyOptionalProductWhenExistId() {
		
		Optional<Product> obj = repository.findById(idExists);
		
		Assertions.assertTrue(obj.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalProductWhenNotExistId() {
		
		Optional<Product> obj = repository.findById(idNotExists);
		
		Assertions.assertTrue(obj.isEmpty());
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(qtdeTotalProdutos + 1, product.getId());
	}
	
	
	@Test
	public void deleteShouldDeleteObjWhenIdExists() {
		
		repository.deleteById(idExists);
		
		Optional<Product> obj = repository.findById(idExists);
		
		Assertions.assertFalse(obj.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(idNotExists);
		});
	}
}
