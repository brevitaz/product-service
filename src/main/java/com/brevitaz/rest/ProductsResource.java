package com.brevitaz.rest;

import com.brevitaz.exceptions.DataNotFoundException;
import com.brevitaz.model.Product;
import com.brevitaz.model.SearchRequestModel;
import com.brevitaz.model.SearchResponseModel;
import com.brevitaz.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/products")
public class ProductsResource extends BaseResource
{

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public Product getProductById(@PathVariable("id") String id)
    {
        Product product = productService.getProductById(id);
        if(product == null)
        {
            throw new DataNotFoundException();
        }
        return product;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@RequestBody Product newProduct)
    {
        productService.saveProduct(newProduct);
        return "Product is saved with title: " + newProduct.getTitle();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public @ResponseBody
    SearchResponseModel<Product> searchProduct(@Valid @RequestBody SearchRequestModel productSearchRequest)
    {
        return productService.searchProduct(productSearchRequest);
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map handleDataNotFound(DataNotFoundException e)
    {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", "No data found for this request");
        return errorMap;
    }
}