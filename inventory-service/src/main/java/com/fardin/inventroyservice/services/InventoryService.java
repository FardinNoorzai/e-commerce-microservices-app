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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        for (CartItem cartItem : event.getCartItems()) {
            BigDecimal transactionTotal = BigDecimal.ZERO;

            List<Item> savedItems = itemRepository.findByProductId(cartItem.getProductId().intValue());
            BigDecimal entryTotal = productEntryRepository
                    .calculateInventoryByProductId(cartItem.getProductId().intValue())
                    .orElse(BigDecimal.ZERO);

            for (Item savedItem : savedItems) {
                if (!InventoryStates.REJECTED.equals(savedItem.getTransaction().getState())) {
                    transactionTotal = transactionTotal.add(savedItem.getQuantity());
                }
            }

            BigDecimal currentRequestedQty = new BigDecimal(cartItem.getQuantity());
            if (transactionTotal.add(currentRequestedQty).compareTo(entryTotal) > 0) {
                isValid = false;
                System.out.println("now enough inventory quiting...");
                break;
            }
        }

        transaction.setUsername(event.getUsername());
        transaction.setCheckoutId(event.getId());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setState(isValid ? InventoryStates.HOLD : InventoryStates.REJECTED);

        List<Item> items = new ArrayList<>();
        for (CartItem cartItem : event.getCartItems()) {
            Item item = new Item();
            item.setProductId(cartItem.getProductId().intValue());
            item.setQuantity(BigDecimal.valueOf(cartItem.getQuantity()));
            item.setTransaction(transaction);
            items.add(item);
        }

        transaction.setItems(items);
        transactionRepository.save(transaction);


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
