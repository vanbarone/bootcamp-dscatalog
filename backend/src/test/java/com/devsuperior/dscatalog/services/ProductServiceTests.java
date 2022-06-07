package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	long idExists;
	long idNotExists;
	long idDependent;
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;		
		idNotExists = 500L;
		idDependent = 4L;
		
		Mockito.doNothing().when(repository).deleteById(idExists);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idNotExists);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependent);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExist() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(idExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idExists);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(idNotExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idNotExists);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdDependent() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(idDependent);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idDependent);
	}
	
	
	
}
