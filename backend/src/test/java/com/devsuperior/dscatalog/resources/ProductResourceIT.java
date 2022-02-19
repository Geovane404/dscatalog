package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

//--TESTE DE INTEGRAÇÃO: resouce/service/repository --//
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProduct;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProduct = 25L;
	}

	//resource - findAll
	@Test 
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		
		 ResultActions result = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				 .accept(MediaType.APPLICATION_JSON));
		
		 result.andExpect(status().isOk());
		 result.andExpect(jsonPath("$.totalElements").value(countTotalProduct));
		 result.andExpect(jsonPath("$.content").exists());
		 result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		 result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		 result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
}
