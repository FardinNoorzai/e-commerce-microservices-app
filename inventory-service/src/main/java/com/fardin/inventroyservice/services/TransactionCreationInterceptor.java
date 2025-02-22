package com.fardin.inventroyservice.services;

import com.fardin.inventroyservice.models.Transaction;
import com.shopmate.states.InventoryStates;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Service;

@RepositoryEventHandler(Transaction.class)
@Service
public class TransactionCreationInterceptor {

    @HandleBeforeCreate
    public void beforeCreate(Transaction transaction) {
        transaction.setState(InventoryStates.REJECTED);
    }
}
