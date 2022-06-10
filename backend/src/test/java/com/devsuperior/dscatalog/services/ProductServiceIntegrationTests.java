package com.devsuperior.dscatalog.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional  //faz com que o banco volte ao estado original após cada teste
public class ProductServiceIntegrationTests {

	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private long idExists;
	private long idNotExists;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;		
		idNotExists = 1000L;
		countTotalProducts = 25L;
	
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(idExists);
		
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
				service.delete(idNotExists);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());	//verifica se o resultado voltou vazio
		Assertions.assertEquals(0, result.getNumber());   //verifica se a página que voltou é a 0
		Assertions.assertEquals(10, result.getSize());    //verifica se a quantidade de elementos é 10	
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());   //verifica se a quantidade total de elementos é a esperada
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyWhenPageNotExist() {
		
		PageRequest pageRequest = PageRequest.of(50, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertTrue(result.isEmpty());	
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByName() {
		
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());	
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());   	
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
	
	
}
