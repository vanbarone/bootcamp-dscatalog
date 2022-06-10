package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService service;
	
	private long idExists;
	private long idNotExists;
	private long idDependent;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;
		idNotExists = 2L;
		idDependent = 3L;
		
		productDTO = Factory.createProductDTO();
		
		page = new PageImpl<>(List.of(productDTO));
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(idExists)).thenReturn(productDTO);
		Mockito.when(service.findById(idNotExists)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.update(eq(idExists), any())).thenReturn(productDTO);
		Mockito.when(service.update(eq(idNotExists), any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.insert(any())).thenReturn(productDTO);
		
		Mockito.doNothing().when(service).delete(idExists);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(idNotExists);
		Mockito.doThrow(DatabaseException.class).when(service).delete(idDependent);
	}
	
	@Test
	public void findAllShouldReturnPageProductDTO() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products")
				                                  .accept(MediaType.APPLICATION_JSON)
				                               );
		
		result.andExpect(status().isOk());
		
		//pode ser feito td na mesma linha
		//mockMvc.perform(get("/products")).andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", idExists)
				                                  .accept(MediaType.APPLICATION_JSON)
                                               ); 
		
		result.andExpect(status().isOk());
		
		//verifica se o campo id existe no corpo da resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());	
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdNotExist() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", idNotExists)
				                                  .accept(MediaType.APPLICATION_JSON)
                                               );
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", idExists)
				                                  .content(jsonBody)
				                                  .contentType(MediaType.APPLICATION_JSON)
				                                  .accept(MediaType.APPLICATION_JSON)
				                               );
		
		result.andExpect(status().isOk());
		
		//verifica se o campo id existe no corpo da resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());	
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdNotExist() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", idNotExists)
				                                  .content(jsonBody)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .accept(MediaType.APPLICATION_JSON)
                                               );
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExist() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", idExists)
				                                  .accept(MediaType.APPLICATION_JSON)
				                               );
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdNotExist() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", idNotExists)
				                                  .accept(MediaType.APPLICATION_JSON)
				                               );
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnBadRequestWhenIdNotExist() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", idDependent)
				                                  .accept(MediaType.APPLICATION_JSON)
				                               );
		
		result.andExpect(status().isBadRequest());
	}
	
	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(post("/products")
				                                  .content(jsonBody)
				                                  .contentType(MediaType.APPLICATION_JSON)
				                                  .accept(MediaType.APPLICATION_JSON)
				                               );
		
		result.andExpect(status().isCreated());
		
		//verifica se o campo id existe no corpo da resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());	
	}
	
}
