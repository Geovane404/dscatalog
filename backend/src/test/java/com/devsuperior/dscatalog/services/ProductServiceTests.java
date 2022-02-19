package com.devsuperior.dscatalog.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

//-TESTE DE UNIDADE: não carrega o contexto da aplicação - Mockito/Mock -// 
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	private long exintingId;
	private long nonExistingId;
	private long dependentId;
	
	private Product product;
	private ProductDTO productDTO;
	private Category category;
	private PageImpl<Product> page;
	
	@BeforeEach
	void setUp() throws Exception {
		exintingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		//RETURN:quando(when)--ação(thenReturn)
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.findById(exintingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.getOne(exintingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(exintingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		//VOID: ação -- quando
		Mockito.doNothing().when(repository).deleteById(exintingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	//---findALLPaged(repository.findAll):
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0 , 10);
		
		Page<ProductDTO> result = service.findALLPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository).findAll(pageable);
	}
	
	//---findById(repository.findById):
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() {
	
		ProductDTO result  = service.findById(exintingId);
		 
		Assertions.assertNotNull(result);
		Mockito.verify(repository).findById(exintingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNoExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			 service.findById(nonExistingId);
		});
		
		Mockito.verify(repository).findById(nonExistingId);
	}	
	
	//---update(repository.save):
	@Test
	public void updateShoulReturnProductDTOWhenIdExist() {
		
		ProductDTO result = service.update(exintingId, productDTO);
		
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdNoExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
	}
	
	//---Delete(repository.deleteById):
	@Test
	public void deleteShouldDoNothingWhenIdExists () {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(exintingId);
		}); 
		Mockito.verify(repository, Mockito.times(1)).deleteById(exintingId);
	}
	
	@Test
	public void deleShouldThrowResourceNotFoundExceptionWhenIdNoExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		verify(repository, times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		verify(repository, times(1)).deleteById(dependentId);
	}
	
}
