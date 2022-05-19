package com.devsuperior.dscatalog.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;


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

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		/* o método findById retorna uma variavel do tipo 'optional', isso quer dizer que
		 * essa variavel nunca será nula, ela pode ou não ter um objeto do tipo 'category'
		 * lá dentro.
		 * Você deve usar o método ".get()" para pegar o objeto porém se não encontrar vai estourar um erro não tratado
		 * então usamos o método ".orElseThrow()" que tb vai tentar pegar o objeto porém se der erro vai estourar a excessão que vc personalizou
		 */
		Optional<Category> obj = repo.findById(id);
		//Category entity = obj.get();
		Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
				
		return new CategoryDTO(entity);
	}
}
