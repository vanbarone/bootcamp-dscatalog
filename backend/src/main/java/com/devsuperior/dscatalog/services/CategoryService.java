package com.devsuperior.dscatalog.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;


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
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
		Page<Category> list = repo.findAll(pageRequest);
		
		return list.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		/* o método findById retorna uma variavel do tipo 'optional', isso quer dizer que
		 * essa variavel nunca será nula, ela pode ou não ter um objeto do tipo 'category'
		 * lá dentro.
		 * Você deve usar o método ".get()" para pegar o objeto porém se não encontrar vai estourar um erro não tratado
		 * então usamos o método ".orElseThrow()" que tb vai tentar pegar o objeto porém se der erro vai estourar a excessão que vc personalizou
		 */
		
		try {
			Optional<Category> obj = repo.findById(id);
			//Category entity = obj.get();
			Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
				
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		
		entity = repo.save(entity);
		
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		/* usar o método "getOne" ao invés do "findById" pq o "getOne" só vai acessar o banco 
		 * qdo vc chamar o método save, ou seja, só acessa o banco 1 vez, enquanto no "findById"
		 * o banco seria acessado 2x
		 */
		
		try {
			Category entity = repo.getOne(id);
			entity.setName(dto.getName());
		
			entity = repo.save(entity);
			
			return new CategoryDTO(entity);
			
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}
	
	public void delete(Long id) {
		/* Não utiliza a marcação "Transactional" pq senão não consegue capturar a 
		 * excessão que vem do banco
		 */
		
		try {
			repo.deleteById(id);
		
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
}
