package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository repositoryCat;
	
	private long idExists;
	private long idNotExists;
	private long idDependent;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;		
		idNotExists = 2L;
		idDependent = 3L;
		
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
		category = Factory.createCategory();
		
		//Qdo o método retorno uma informação específica
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(idExists)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(idNotExists)).thenReturn(Optional.empty());
		
		Mockito.when(repository.getOne(idExists)).thenReturn(product);
		Mockito.when(repository.getOne(idNotExists)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repositoryCat.getOne(idExists)).thenReturn(category);
		Mockito.when(repositoryCat.getOne(idNotExists)).thenThrow(EntityNotFoundException.class);
		
		//Qdo o retorno do método é void, faz dessa forma
		Mockito.doNothing().when(repository).deleteById(idExists);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idNotExists);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependent);
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExist() {
		
		ProductDTO result = service.update(idExists, Factory.createProductDTO());
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).getOne(idExists);		
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			ProductDTO result = service.update(idNotExists, Factory.createProductDTO());
		});
		
		Mockito.verify(repository, Mockito.times(1)).getOne(idNotExists);		
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() {
		
		ProductDTO result = service.findById(idExists);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findById(idExists);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			ProductDTO result = service.findById(idNotExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(idNotExists);
	}
	
	@Test
	public void findAllPagedShouldReturnPageProductDTO() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository).findAll(pageable);
		//Pode usar assim tb
		//Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
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
