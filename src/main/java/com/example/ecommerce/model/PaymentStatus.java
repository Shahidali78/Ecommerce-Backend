package com.example.ecommerce.model;

/**
 * Enumeration capturing the lifecycle of a payment. Keeping the list small
 * makes it easy to handle states in the order workflow. You can extend
 * values such as REFUNDED or CANCELLED depending on your requirements.
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}