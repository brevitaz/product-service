package com.brevitaz;


import com.brevitaz.model.Product;
import com.brevitaz.model.SearchRequestModel;
import com.brevitaz.model.SearchResponseModel;
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

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;


@RunWith(RandomizedRunner.class)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@SpringBootTest(classes = ProductApp.class)
public class ProductSearchServiceTest {

	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private ProductService productService;

	@Autowired
	ElasticSearchTestUtils elasticSearchTestUtils;

	@Before
	public void setUpData() throws Exception
	{
		elasticSearchTestUtils.setUpIndex();
		elasticSearchTestUtils.setUpTestData();
	}

	@Test
	public void getAllDataTest()
	{
		SearchResponseModel<Product> response = searchProduct(10,1,"");
		Assert.assertThat(response.getResult(), hasSize(7));
		Assert.assertEquals((long) response.getTotalPage(), 1);
	}

	@Test
	public void pageSizeTest()
	{
		SearchResponseModel<Product> response = searchProduct(2,4,"");
		Assert.assertThat(response.getResult(), hasSize(1));
		Assert.assertEquals((long) response.getTotalPage(), 4);
	}

	@Test
	public void searchResponseTest()
	{
		SearchResponseModel<Product> response = searchProduct(10,1,"mobile");
		Assert.assertThat(response.getResult(), hasSize(6));
		Assert.assertEquals((long) response.getTotalPage(), 1);
		Assert.assertThat(response.getResult(), not(
				hasItem(hasProperty("code", is("0007")))));
	}

	@Test
	public void searchResponseOrderTest()
	{
		SearchResponseModel<Product> responseForIphoneBlack = searchProduct(10,1,"iphone black");
		Assert.assertThat(responseForIphoneBlack.getResult(), hasSize(7));
		Assert.assertEquals((long) responseForIphoneBlack.getTotalPage(), 1);
		Assert.assertThat(responseForIphoneBlack.getResult(), contains(
				hasProperty("code", is("0001")),
				hasProperty("code", is("0004")),
				hasProperty("code", is("0002")),
				hasProperty("code", is("0006")),
				hasProperty("code", is("0003")),
				hasProperty("code", is("0005")),
				hasProperty("code", is("0007"))
		));

		SearchResponseModel<Product> responseForIphoneCase = searchProduct(10,1,"iphone case");
		Assert.assertThat(responseForIphoneCase.getResult(), hasSize(7));
		Assert.assertEquals((long) responseForIphoneCase.getTotalPage(), 1);
		Assert.assertThat(responseForIphoneCase.getResult(), contains(
				hasProperty("code", is("0007")),
				hasProperty("code", is("0003")),
				hasProperty("code", is("0001")),
				hasProperty("code", is("0004")),
				hasProperty("code", is("0002")),
				hasProperty("code", is("0005")),
				hasProperty("code", is("0006"))
		));
	}

	private SearchResponseModel<Product> searchProduct(int pageSize, int page, String searchText)
	{
		SearchRequestModel iphoneCaseSearchRequest = new SearchRequestModel();
		iphoneCaseSearchRequest.setPageSize(pageSize);
		iphoneCaseSearchRequest.setPage(page);
		iphoneCaseSearchRequest.setSearchText(searchText);
		return productService.searchProduct(iphoneCaseSearchRequest);
	}

	@After
	public void deleteIndex()
	{
		elasticSearchTestUtils.deleteIndexIfExist();
	}
}
