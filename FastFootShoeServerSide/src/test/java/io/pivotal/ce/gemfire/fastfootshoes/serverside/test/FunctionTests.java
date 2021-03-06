package io.pivotal.ce.gemfire.fastfootshoes.serverside.test;

import static org.junit.Assert.assertTrue;
import io.pivotal.ce.gemfire.fastfootshoes.model.Customer;
import io.pivotal.ce.gemfire.fastfootshoes.model.Product;
import io.pivotal.ce.gemfire.fastfootshoes.model.Transaction;
import io.pivotal.ce.gemfire.fastfootshoes.repositories.CustomerRepository;
import io.pivotal.ce.gemfire.fastfootshoes.repositories.ProductRepository;
import io.pivotal.ce.gemfire.fastfootshoes.repositories.TransactionRepository;
import io.pivotal.ce.gemfire.fastfootshoes.serverside.functions.OrderCounterCaller;
import io.pivotal.ce.gemfire.fastfootshoes.serverside.functions.ProductGroupCounterCaller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("/cache-config.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FunctionTests {

	static Log log = LogFactory.getLog(FunctionTests.class);

	private Customer customer;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ProductRepository productRepository;

	@Before
	public void setUp() throws Exception {
		DateTime dt = new DateTime();
		DateTime lastYear = dt.minusYears(1);
		DateTime lastMonth = dt.minusMonths(1);
		DateTime TwoMonthAgo = dt.minusMonths(1);
		DateTime lastWeek = dt.minusWeeks(1);

		// create a customer
		customer = new Customer();
		customer.setBirthday(new Date());
		customer.setCity("Toronto");
		customer.setEmailAddress("email@email.com");
		customer.setId("1");
		customer.setName("Biff Tannen");
		customerRepository.save(customer);

		// create some products
		Product product1 = new Product();
		product1.setId("1");
		product1.setStockOnHand(25);
		product1.setBrand("Nike");
		product1.setType("Soccer");

		Product product2 = new Product();
		product2.setId("2");
		product2.setStockOnHand(25);
		product2.setBrand("Nike");
		product2.setType("Running");

		Product product3 = new Product();
		product3.setId("3");
		product3.setStockOnHand(25);
		product3.setBrand("Nike");
		product3.setType("Cross Trainer");

		Product product4 = new Product();
		product4.setId("4");
		product4.setStockOnHand(25);
		product4.setBrand("Aidias");
		product4.setType("Boxing");

		productRepository.save(product1);
		productRepository.save(product2);
		productRepository.save(product3);
		productRepository.save(product4);

		// create a couple of valid transactions
		Transaction txn1 = new Transaction();
		txn1.setCustomerId("1");
		txn1.setId("1");
		txn1.setMarkUp(new Double(11));
		txn1.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn1.setProductId("1");
		txn1.setRetailPrice(new Double(20));
		txn1.setQuantity(2);
		txn1.setTransactionDate(lastMonth.toDate());
		// 1

		Transaction txn2 = new Transaction();
		txn2.setCustomerId("1");
		txn2.setId("2");
		txn2.setMarkUp(new Double(11));
		txn2.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn2.setProductId("3");
		txn2.setRetailPrice(new Double(20));
		txn2.setQuantity(3);
		txn2.setTransactionDate(lastMonth.toDate());
		// 2

		Transaction txn3 = new Transaction();
		txn3.setCustomerId("1");
		txn3.setId("3");
		txn3.setMarkUp(new Double(11));
		txn3.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn3.setProductId("4");
		txn3.setRetailPrice(new Double(20));
		txn3.setQuantity(3);
		txn3.setTransactionDate(lastMonth.toDate());
		// 3

		// 3 on the same day = 4

		Transaction txn4 = new Transaction();
		txn4.setCustomerId("1");
		txn4.setId("4");
		txn4.setMarkUp(new Double(11));
		txn4.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn4.setProductId("4");
		txn4.setRetailPrice(new Double(20));
		txn4.setQuantity(4);
		txn4.setTransactionDate(new Date());
		// 5 + 1 for BDay = 6

		Transaction txn5 = new Transaction();
		txn5.setCustomerId("1");
		txn5.setId("5");
		txn5.setMarkUp(new Double(11));
		txn5.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn5.setProductId("4");
		txn5.setRetailPrice(new Double(20));
		txn5.setQuantity(1);
		txn5.setTransactionDate(lastWeek.toDate());
		// 7

		Transaction txn6 = new Transaction();
		txn6.setCustomerId("1");
		txn6.setId("6");
		txn6.setMarkUp(new Double(11));
		txn6.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn6.setProductId("4");
		txn6.setRetailPrice(new Double(20));
		txn6.setQuantity(5);
		txn6.setTransactionDate(TwoMonthAgo.toDate());
		// 8

		// create a non valid transaction

		// too old
		Transaction txn7 = new Transaction();
		txn7.setCustomerId("1");
		txn7.setId("7");
		txn7.setMarkUp(new Double(11));
		txn7.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn7.setProductId("4");
		txn7.setRetailPrice(new Double(20));
		txn7.setQuantity(2);
		txn7.setTransactionDate(lastYear.toDate());

		// different customer
		Transaction txn8 = new Transaction();
		txn8.setCustomerId("2");
		txn8.setId("8");
		txn8.setMarkUp(new Double(11));
		txn8.setOrderStatus(Transaction.ORDER_COMPLETED);
		txn8.setProductId("4");
		txn8.setRetailPrice(new Double(20));
		txn8.setQuantity(5);
		txn8.setTransactionDate(lastWeek.toDate());

		// canceled order
		Transaction txn9 = new Transaction();
		txn9.setCustomerId("1");
		txn9.setId("9");
		txn9.setMarkUp(new Double(11));
		txn9.setOrderStatus(Transaction.ORDER_CANCELLED);
		txn9.setProductId("4");
		txn9.setRetailPrice(new Double(20));
		txn9.setQuantity(1);
		txn9.setTransactionDate(new Date());

		transactionRepository.save(txn1);
		transactionRepository.save(txn2);
		transactionRepository.save(txn3);
		transactionRepository.save(txn4);
		transactionRepository.save(txn5);
		transactionRepository.save(txn6);
		transactionRepository.save(txn7);
		transactionRepository.save(txn8);
		transactionRepository.save(txn9);

	}

	@Autowired
	@Qualifier("orderCounterCaller")
	private OrderCounterCaller orderCounterCaller;

	@Autowired
	@Qualifier("productGroupCounterCaller")
	ProductGroupCounterCaller productGroupCounterCaller;

	@Test
	public void testOrderCounter() {
		List<Integer> counts = orderCounterCaller.countTransactions(customer);
		int count = counts.get(0);
		System.out.println("Got back: " + count);
		assertTrue(8 == count);
	}

	@Test
	public void testBrandCount() {
		List<Map<String, AtomicInteger>> brandCountList = productGroupCounterCaller.countByBrand();
		System.out.println(brandCountList.get(0).get("Nike").intValue());
		assertTrue(brandCountList.get(0).get("Nike").intValue() == 75);
		assertTrue(brandCountList.get(0).get("Aidias").intValue() == 25);

	}

	@Test
	public void testTypeCount() {
		List<Map<String, AtomicInteger>> typeCountList = productGroupCounterCaller
				.countByType();
		assertTrue(typeCountList.get(0).get("Running").intValue() == 25);
	}

}
