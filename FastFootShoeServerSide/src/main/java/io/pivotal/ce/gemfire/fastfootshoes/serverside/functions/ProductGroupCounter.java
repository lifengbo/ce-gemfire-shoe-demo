package io.pivotal.ce.gemfire.fastfootshoes.serverside.functions;

import io.pivotal.ce.gemfire.fastfootshoes.model.Product;
import io.pivotal.ce.gemfire.fastfootshoes.repositories.ProductRepository;
import io.pivotal.ce.gemfire.fastfootshoes.serverside.ReferenceHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.stereotype.Component;

import com.gemstone.gemfire.pdx.internal.PdxInstanceImpl;

@Component
public class ProductGroupCounter {
	
	@Autowired
	private ProductRepository productRepository;
	
	@GemfireFunction
	public Map<String, AtomicInteger> countByBrand() {
		Map<String,AtomicInteger> results = new HashMap<String,AtomicInteger>();
		Collection<Product> products = productRepository.findAll();
		for (Object objProduct : products) {
			Product product = resolveReference(objProduct);
			if (results.containsKey(product.getBrand())) {
				results.get(product.getBrand()).addAndGet(product.getStockOnHand());
			}
			else {
				results.put(product.getBrand(), new AtomicInteger(product.getStockOnHand()));
			}
		}
		return results;
	}
	
	@GemfireFunction
	public Map<String, AtomicInteger> countByType() {
		Map<String,AtomicInteger> results = new HashMap<String,AtomicInteger>();
		Collection<Product> products = productRepository.findAll();
		for (Object obj : products) {
			Product product = resolveReference(obj);
			if (results.containsKey(product.getType())) {
				results.get(product.getType()).addAndGet(product.getStockOnHand());
			}
			else {
				results.put(product.getType(), new AtomicInteger(product.getStockOnHand()));
			}
		}
		return results;
	}
	
	private Product resolveReference(Object obj) {
			if (obj instanceof PdxInstanceImpl) {
				return ReferenceHelper.toObject(obj, Product.class);
			}
			else {
				return (Product)obj;
			}
	}

}
