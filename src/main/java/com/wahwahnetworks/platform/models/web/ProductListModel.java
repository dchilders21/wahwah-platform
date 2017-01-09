package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.Product;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@WebSafeModel
public class ProductListModel
{

	private List<ProductModel> products = new ArrayList<>();

	private int currentPage;
	private int currentPageSize;

	private int numberOfPages;

	public ProductListModel(Iterable<Product> products)
	{
		for (Product product : products)
		{
			this.add(new ProductModel(product,null));
		}

		setCurrentPage(0);
		setCurrentPageSize(this.products.size());
		setNumberOfPages(1);
	}

	public ProductListModel(Page<Product> products)
	{
		for (Product product : products)
		{
			this.add(new ProductModel(product,null));
		}

		setCurrentPage(products.getNumber());
		setCurrentPageSize(products.getNumberOfElements());
		setNumberOfPages(products.getTotalPages());
	}

	public void add(ProductModel product)
	{
		products.add(product);
	}

	@JsonProperty("products")
	public List<ProductModel> getProducts()
	{
		return products;
	}

	@JsonProperty("page_current")
	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	@JsonProperty("page_size")
	public int getCurrentPageSize()
	{
		return currentPageSize;
	}

	public void setCurrentPageSize(int currentPageSize)
	{
		this.currentPageSize = currentPageSize;
	}

	@JsonProperty("page_totalcount")
	public int getNumberOfPages()
	{
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages)
	{
		this.numberOfPages = numberOfPages;
	}
}
