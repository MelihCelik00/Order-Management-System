package com.oms.config;

/**
 * Utility class containing API endpoint constants.
 * This class cannot be instantiated or extended.
 */
public final class ApiEndpoints {
    // Base paths
    public static final String API_BASE = "/api";
    public static final String CUSTOMERS = API_BASE + "/customers";
    public static final String ORDERS = API_BASE + "/orders";

    /**
     * Customer-related endpoint paths.
     * This class cannot be instantiated.
     */
    public static final class Customer {
        public static final String CREATE = "";  // POST /api/customers
        public static final String GET_BY_ID = "/{id}";  // GET /api/customers/{id}
        public static final String GET_BY_EMAIL = "/email/{email}";  // GET /api/customers/email/{email}
        public static final String GET_ALL = "";  // GET /api/customers
        public static final String UPDATE = "/{id}";  // PUT /api/customers/{id}
        public static final String DELETE = "/{id}";  // DELETE /api/customers/{id}
        
        private Customer() {
            throw new AssertionError("Utility class - cannot be instantiated");
        }
    }

    /**
     * Order-related endpoint paths.
     * This class cannot be instantiated.
     */
    public static final class Order {
        public static final String CREATE = "";  // POST /api/orders
        public static final String GET_BY_ID = "/{id}";  // GET /api/orders/{id}
        public static final String GET_BY_CUSTOMER = "/customer/{customerId}";  // GET /api/orders/customer/{customerId}
        public static final String GET_ALL = "";  // GET /api/orders
        
        private Order() {
            throw new AssertionError("Utility class - cannot be instantiated");
        }
    }

    private ApiEndpoints() {
        throw new AssertionError("Utility class - cannot be instantiated");
    }
} 