package com.devsuperior.dscatalog.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;


@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repo;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repo.findAll();
		
		List<CategoryDTO> listDTO = new ArrayList<>();
		
		/* Pode ser feito dessa forma ou então com expressão lambda como foi feito a seguir
		for (Category cat: list) {
			listDTO.add(new CategoryDTO(cat));
		}
		*/
		
		listDTO = list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		
		/*Expressão lambda explicação:
		 * stream é para pegar todos os elementos da lista
		 * map é para aplicar uma função a cada elemento da lista, no caso aqui está transformando cada elemento do tipo Category em CategoryDTO
		 * collect é para que essa stream volte a ser uma lista 
		 */
		
		return listDTO;
	}

}
