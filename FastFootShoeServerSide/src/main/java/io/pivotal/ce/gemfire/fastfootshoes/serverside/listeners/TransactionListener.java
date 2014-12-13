/**
 * 
 */
package io.pivotal.ce.gemfire.fastfootshoes.serverside.listeners;

import io.pivotal.ce.gemfire.fastfootshoes.model.Product;
import io.pivotal.ce.gemfire.fastfootshoes.model.Transaction;
import io.pivotal.ce.gemfire.fastfootshoes.repositories.ProductRepository;
import io.pivotal.ce.gemfire.fastfootshoes.serverside.ReferenceHelper;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.gemstone.gemfire.pdx.internal.PdxInstanceImpl;

/**
 * This will happen on the same thread as the put to the transaction region is
 * using
 * 
 * @author lshannon
 *
 */
@Component
public class TransactionListener extends CacheListenerAdapter<String, Object> implements Declarable {

	@Autowired
	private ProductRepository productRepository;

	public void init(Properties props) {
	}

	@Override
	public void afterCreate(EntryEvent<String, Object> entryEvent) {
		Transaction transaction = null;
		if (entryEvent.getNewValue() instanceof PdxInstanceImpl) {
			transaction = ReferenceHelper.toObject(entryEvent.getNewValue(), Transaction.class);
		}
		else {
			transaction = (Transaction)entryEvent.getNewValue();
		}
		if (transaction.getOrderStatus().equals(Transaction.ORDER_OPEN)) {
			Product product = resolveReference(productRepository.findOne(transaction.getProductId()));
			product.setStockOnHand(product.getStockOnHand() - transaction.getQuantity());
			productRepository.save(product);
		}
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
