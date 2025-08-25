package com.fardin.inventroyservice.services;

import com.fardin.inventroyservice.models.Item;
import com.fardin.inventroyservice.models.Transaction;
import com.fardin.inventroyservice.repository.ItemRepository;
import com.fardin.inventroyservice.repository.ProductEntryRepository;
import com.fardin.inventroyservice.repository.TransactionRepository;
import com.shopmate.events.CartItem;
import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.InventoryValidationEvent;
import com.shopmate.events.PaymentSuccessDto;
import com.shopmate.states.InventoryStates;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ProductEntryRepository productEntryRepository;

    @Transactional
    public InventoryValidationEvent calculateInventory(CheckoutEvent event) {
        boolean isValid = true;
        Transaction transaction = new Transaction();

        List<Integer> productIds = event.getCartItems()
                .stream()
                .map(cartItem -> cartItem.getProductId().intValue())
                .collect(Collectors.toList());

        List<Item> savedItems = itemRepository.findByProductIdIn(productIds);

        Map<Integer, BigInteger> productInventoryTotalsRaw = productEntryRepository
                .calculateInventoryByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(InventoryTotal::getProductId, InventoryTotal::getTotal));

        Map<Integer, BigDecimal> productInventoryTotals = productInventoryTotalsRaw.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new BigDecimal(e.getValue())
                ));

        Map<Integer, BigDecimal> productTransactionTotals = new HashMap<>();
        for (Item item : savedItems) {
            if (!InventoryStates.REJECTED.equals(item.getTransaction().getState())) {
                Integer pid = item.getProductId();
                BigDecimal qty = item.getQuantity();
                productTransactionTotals.merge(pid, qty, BigDecimal::add);
            }
        }

        for (CartItem cartItem : event.getCartItems()) {
            Integer pid = cartItem.getProductId().intValue();
            BigDecimal requestedQty = BigDecimal.valueOf(cartItem.getQuantity());
            BigDecimal totalInventory = productInventoryTotals.getOrDefault(pid, BigDecimal.ZERO);
            BigDecimal currentTransactionTotal = productTransactionTotals.getOrDefault(pid, BigDecimal.ZERO);

            if (currentTransactionTotal.add(requestedQty).compareTo(totalInventory) > 0) {
                isValid = false;
                System.out.println("Not enough inventory for productId: " + pid);
                break;
            }
        }

        transaction.setUsername(event.getUsername());
        transaction.setCheckoutId(event.getId());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setState(isValid ? InventoryStates.HOLD : InventoryStates.REJECTED);

        List<Item> items = event.getCartItems().stream().map(cartItem -> {
            Item item = new Item();
            item.setProductId(cartItem.getProductId().intValue());
            item.setQuantity(BigDecimal.valueOf(cartItem.getQuantity()));
            item.setTransaction(transaction);
            return item;
        }).collect(Collectors.toList());

        transaction.setItems(items);
        transactionRepository.save(transaction);

        InventoryValidationEvent e = new InventoryValidationEvent();
        e.setValid(isValid);
        e.setCheckoutId(event.getId());
        e.setInventoryId(transaction.getId());
        return e;
    }

    @Transactional
    public InventoryValidationEvent insertOrderWithoutInventoryCheck(CheckoutEvent event) {
        Transaction transaction = new Transaction();
        transaction.setUsername(event.getUsername());
        transaction.setCheckoutId(event.getId());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setState(InventoryStates.HOLD);
        List<Item> items = event.getCartItems().stream().map(cartItem -> {
            Item item = new Item();
            item.setProductId(cartItem.getProductId().intValue());
            item.setQuantity(BigDecimal.valueOf(cartItem.getQuantity()));
            item.setTransaction(transaction);
            return item;
        }).collect(Collectors.toList());

        transaction.setItems(items);
        transactionRepository.save(transaction);

        InventoryValidationEvent response = new InventoryValidationEvent();
        response.setValid(true);
        response.setCheckoutId(event.getId());
        response.setInventoryId(transaction.getId());

        return response;
    }

    @Transactional
    public InventoryValidationEvent randomCheck(CheckoutEvent event) {
        boolean isValid;

        // Extract all product IDs from cart
        List<Long> productIds = event.getCartItems().stream()
                .map(CartItem::getProductId)
                .toList();

        Set<Long> allowedProducts = Set.of(13L, 18L);

        // Check if only 13 and/or 18 are present
        boolean allAllowed = productIds.stream().allMatch(allowedProducts::contains);
        isValid = allAllowed;

        // But also enforce count rule: max 2 items allowed
        if (isValid && productIds.size() > 2) {
            isValid = false;
        }

        if (!isValid) {
            System.out.println("❌ Inventory Rejected: Product(s) not allowed or too many.");
        } else {
            System.out.println("✅ Inventory Approved for: " + productIds);
        }

        // You can still create a transaction record if needed
        Transaction transaction = new Transaction();
        transaction.setUsername(event.getUsername());
        transaction.setCheckoutId(event.getId());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setState(isValid ? InventoryStates.HOLD : InventoryStates.REJECTED);

        List<Item> items = event.getCartItems().stream().map(cartItem -> {
            Item item = new Item();
            item.setProductId(cartItem.getProductId().intValue());
            item.setQuantity(BigDecimal.valueOf(cartItem.getQuantity()));
            item.setTransaction(transaction);
            return item;
        }).toList();

        transaction.setItems(items);
        transactionRepository.save(transaction);

        // Return event
        InventoryValidationEvent e = new InventoryValidationEvent();
        e.setValid(isValid);
        e.setCheckoutId(event.getId());
        e.setInventoryId(transaction.getId());

        return e;
    }




    public void updateOrderStatus(PaymentSuccessDto message) {
        List<Transaction> transactions = Collections.singletonList(transactionRepository.findByCheckoutId(message.getOrderId()).orElse(null));
        for (Transaction t : transactions) {
            t.setState(InventoryStates.COMPLETED);
        }
        transactionRepository.saveAll(transactions);
    }
}
