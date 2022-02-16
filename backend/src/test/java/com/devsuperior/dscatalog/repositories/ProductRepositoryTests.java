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

//TESTE DE UNIDADE: carrega somente os componentes relacionados ao spring DATA JPA - injeção de Dependência //
@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long exintingId;
	private long nonExistId;
	private long countTotalProduct;

	@BeforeEach
	void setUp() throws Exception {
		exintingId = 1L;
		nonExistId = 1000L;
		countTotalProduct = 25L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExist() {
		
		repository.deleteById(exintingId);
		 Optional<Product> result = repository.findById(exintingId);
		 
		 Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistId);
		});
	}	
	
	@Test //save deve salvar nv obj e auto incrementar(id) qnd ID for nulo.(insert)
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProduct + 1, product.getId());
	}
	
	@Test
	public void findByIdShoulReturnOptionalNotNullWhenIdExist() {
		
		 Optional<Product> result = repository.findById(exintingId);
		 Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShoulReturnOptionalNullWhenIdNoExist() {
		
		 Optional<Product> result = repository.findById(nonExistId);
		 Assertions.assertTrue(result.isEmpty());
	}

}
