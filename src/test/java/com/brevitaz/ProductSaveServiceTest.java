package com.brevitaz;


import com.brevitaz.model.Product;
import com.brevitaz.services.ProductService;
import com.brevitaz.util.ElasticSearchTestUtils;
import com.carrotsearch.randomizedtesting.RandomizedRunner;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RunWith(RandomizedRunner.class)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@SpringBootTest(classes = ProductApp.class)
public class ProductSaveServiceTest
{
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private ProductService productService;

	@Autowired
	ElasticSearchTestUtils elasticSearchTestUtils;

	@Before
	public void setUpIndex() throws Exception
	{
		elasticSearchTestUtils.setUpIndex();
	}

	@Test
	public void testSaveAndGetById()
	{
		Product newProduct = createNewProduct();
		String productId = productService.saveProduct(newProduct);

		elasticSearchTestUtils.refreshIndex();

		Assert.assertNotNull(productId);
		newProduct.setId(productId);

		Product fetchedProduct =  productService.getProductById(productId);
		Assert.assertEquals(newProduct, fetchedProduct);
	}

	private Product createNewProduct ()
	{
		Map specificationsMap = new HashMap<String, Object>();
		specificationsMap.put("brand","apple");
		specificationsMap.put("model","X");
		specificationsMap.put("camera","16mp rear camera");
		specificationsMap.put("battery","3400 mah");
		specificationsMap.put("storage","64gb");
		specificationsMap.put("colour","black");
		specificationsMap.put("modelYear", 2019);

		Product newProduct = new Product();
		newProduct.setTitle("Apple Iphone X (64gb)");
		newProduct.setCode("0002");
		newProduct.setDescription("iPhone X features a 5.8-inch Super Retina display with HDR and True Tone and 64gb RAM. iPhone X Charges wirelessly. Resists water and dust.");
		newProduct.setSpecifications(specificationsMap);
		newProduct.setAuthor("author");
		newProduct.setCategories(new ArrayList<>(Arrays.asList("electronics","mobile")));
		newProduct.setTags(new ArrayList<>(Arrays.asList("mobile","apple","iphone")));
		newProduct.setReviewScore(3);
		newProduct.setImageUrl("https://images-na.ssl-images-amazon.com/images/I/51R4ZvEJUPL._SY679_.jpg");
		newProduct.setPrice(3600d);
		return newProduct;
	}

	@After
	public void deleteIndex()
	{
		elasticSearchTestUtils.deleteIndexIfExist();
	}
}
