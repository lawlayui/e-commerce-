# e-commerce-
# E-Commerce DDD Architecture Design

**Status:** Design Phase (Pre-Implementation)  
**Last Updated:** 2026-10-04  
**Version:** 1.0

---

## Table of Contents
1. [Bounded Contexts Overview](#bounded-contexts-overview)
2. [Context Detail & Entities](#context-detail--entities)
3. [Events & Event Flow](#events--event-flow)
4. [Cross-Context Communication](#cross-context-communication)
5. [Main Workflows](#main-workflows)

---

## Bounded Contexts Overview

| # | Context | Responsibility | Aggregate Root | Type |
|---|---------|---|---|---|
| 1 | ACCOUNT | Authentication, user profile | User | Core |
| 2 | CATALOG | Product discovery (read-only) | CatalogProduct | Core |
| 3 | SELLER_PRODUCT | Seller manage products | SellerProduct | Core |
| 4 | INVENTORY | Physical stock management | InventoryItem | Core |
| 5 | CART | Temporary product holding | Cart | Core |
| 6 | ORDER | Order management & pricing | Order | Core |
| 7 | PAYMENT | Payment gateway integration | PaymentTransaction | Core |
| 8 | RATING_REVIEW | Product rating & review | Review | Core |
| 9 | NOTIFICATION | Send notifications | Notification | Supporting |

---

## Context Detail & Entities

### 1. ACCOUNT Context

**Responsibility:**
- User registration & login
- User profile management
- Role assignment (CUSTOMER, SELLER)
- Note: Authorization at infrastructure layer, not domain

**Entities:**

#### User (Aggregate Root)
```
Attributes:
- userId: UUID (PK, identity)
- email: string (unique)
- hashedPassword: string (never stored plain)
- role: Enum [CUSTOMER, SELLER]
- isActive: boolean
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- Email (email format validation)
- Password (hashing algorithm)
- Role (CUSTOMER | SELLER)
```

#### UserProfile (Entity)
```
Attributes:
- profileId: UUID (PK)
- userId: UUID (FK to User)
- fullName: string
- phoneNumber: string
- address: string
- city: string
- postalCode: string
- updatedAt: timestamp

Value Objects:
- PhoneNumber (validation)
- Address (address formatting)
```

**Invariants:**
- Email must be unique
- Password must be hashed
- Role must be valid enum
- One user can have only one profile

**Events Published:**
- `UserRegistered` (userId, email, role)
- `UserRoleChanged` (userId, oldRole, newRole)
- `UserProfileUpdated` (userId, updatedFields)

**Commands (Use Cases):**
- RegisterUser
- UpdateUserProfile
- ChangeUserRole

---

### 2. CATALOG Context

**Responsibility:**
- Product discovery for customers
- Read-only product information
- Aggregate rating from RATING_REVIEW context
- Driven by SELLER_PRODUCT context (published products)

**Entities:**

#### CatalogProduct (Aggregate Root)
```
Attributes:
- catalogProductId: UUID (PK, identity)
- sku: string (unique, from SELLER_PRODUCT)
- sellerId: UUID (reference to seller)
- name: string
- description: string
- price: Money (amount + currency)
- category: string
- imageUrl: string
- availability: Enum [AVAILABLE, OUT_OF_STOCK, DISCONTINUED]
- averageRating: decimal (0.0 - 5.0, from RATING_REVIEW)
- reviewCount: integer
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- SKU (unique identifier, immutable)
- Money (amount, currency)
- Category (standardized categories)
- ProductStatus (AVAILABLE | OUT_OF_STOCK | DISCONTINUED)
- Rating (0.0 - 5.0)
```

**Invariants:**
- SKU must be unique
- Price must be >= 0
- Rating aggregate only from verified purchases (from RATING_REVIEW)
- Product is read-only (no direct update from CATALOG context)

**Events Published:**
- `CatalogProductPublished` (sku, sellerId, name, price)
- `CatalogProductUpdated` (sku, updatedFields)
- `CatalogProductRatingUpdated` (sku, newAverageRating, newReviewCount)
- `CatalogProductDiscontinued` (sku)

**Events Listened:**
- `ProductPublished` (from SELLER_PRODUCT) → Create or update CatalogProduct
- `ProductUnpublished` (from SELLER_PRODUCT) → Mark as discontinued
- `ReviewPublished` (from RATING_REVIEW) → Update rating aggregate
- `ReviewDeleted` (from RATING_REVIEW) → Update rating aggregate
- `StockDeducted` (from INVENTORY) → Update availability
- `StockRestored` (from INVENTORY) → Update availability

**Commands (Use Cases):**
- BrowseProducts
- SearchProducts (by name, category)
- FilterProducts (by price range, rating, etc)
- GetProductDetail

---

### 3. SELLER_PRODUCT Context

**Responsibility:**
- Seller manage their own products
- Control product information (name, price, description)
- Publish/unpublish products to CATALOG
- Cannot manage stock (managed by INVENTORY)

**Entities:**

#### SellerProduct (Aggregate Root)
```
Attributes:
- sellerProductId: UUID (PK, identity)
- sku: string (unique per seller + product)
- sellerId: UUID (FK, owner of product)
- name: string
- description: string
- price: Money (amount + currency)
- category: string
- imageUrl: string
- isPublished: boolean (is visible in CATALOG)
- status: Enum [ACTIVE, INACTIVE, DISCONTINUED]
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- SKU (unique identifier)
- Money (amount, currency)
- Category
- ProductStatus (ACTIVE | INACTIVE | DISCONTINUED)
```

**Invariants:**
- Seller can only manage their own products (sellerId validation)
- Price must be >= 0
- Name and description cannot be empty
- SKU is immutable after creation
- Cannot modify stock (belongs to INVENTORY context)

**Events Published:**
- `ProductCreated` (sellerProductId, sku, sellerId, name, price)
- `ProductNameChanged` (sku, oldName, newName)
- `ProductPriceChanged` (sku, oldPrice, newPrice)
- `ProductDescriptionChanged` (sku, oldDescription, newDescription)
- `ProductPublished` (sku, sellerId) → triggers CATALOG to create/update
- `ProductUnpublished` (sku) → triggers CATALOG to discontinue
- `ProductStatusChanged` (sku, oldStatus, newStatus)
- `ProductDeleted` (sku)

**Commands (Use Cases):**
- CreateProduct (sellerId, name, description, price, category)
- UpdateProductName (sku, newName)
- UpdateProductPrice (sku, newPrice)
- UpdateProductDescription (sku, newDescription)
- PublishProduct (sku) → make visible in CATALOG
- UnpublishProduct (sku) → hide from CATALOG
- ChangeProductStatus
- DeleteProduct (soft delete)

---

### 4. INVENTORY Context

**Responsibility:**
- Source of truth for physical stock
- Track stock per SKU
- Handle stock reservation and deduction
- Manage stock batches (weight, size, expiredTime)
- Publish stock events for CATALOG and ORDER contexts

**Entities:**

#### InventoryItem (Aggregate Root)
```
Attributes:
- inventoryItemId: UUID (PK, identity)
- sku: string (unique, reference to product)
- quantity: integer (available quantity)
- quantityReserved: integer (soft-reserved for pending orders)
- quantityAvailable: integer (quantity - quantityReserved)
- weight: decimal (in kg)
- size: string (dimensions: e.g., "10x10x10 cm")
- expiredTime: timestamp (nullable, for perishable products)
- location: string (warehouse location)
- lastRestocked: timestamp
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- SKU
- Quantity (non-negative integer)
- Weight (positive decimal)
- Size (string format)
- ExpirationDate (nullable timestamp)
- Location (warehouse section)
```

#### StockAdjustment (Entity, optional tracking)
```
Attributes:
- adjustmentId: UUID (PK)
- inventoryItemId: UUID (FK)
- adjustmentType: Enum [RESTOCK, DAMAGE, LOSS, ADJUSTMENT]
- quantity: integer (positive or negative)
- reason: string
- createdBy: UUID (admin/staff who made adjustment)
- createdAt: timestamp
```

**Invariants:**
- Quantity must never be negative (hard constraint)
- quantityReserved must be <= quantity
- quantityAvailable = quantity - quantityReserved
- Cannot deduct stock below 0
- Stock can only be deducted after payment confirmed

**Events Published:**
- `StockReserved` (sku, quantity, orderId) → soft-reserve for pending order
- `StockDeducted` (sku, quantity) → hard-deduct after payment confirmed
- `StockRestored` (sku, quantity, reason) → restore if payment failed or order cancelled
- `StockAdjusted` (sku, quantity, adjustmentType, reason)
- `StockLow` (sku, currentQuantity, threshold) → alert seller/admin
- `StockExpired` (sku, expiredTime) → alert before expiration

**Events Listened:**
- `OrderCreated` (from ORDER) → Reserve stock
- `PaymentSucceeded` (from PAYMENT) → Hard-deduct stock
- `PaymentFailed` (from PAYMENT) → Restore reserved stock
- `OrderCancelled` (from ORDER) → Restore reserved stock

**Commands (Use Cases):**
- AddStock (sku, quantity, weight, size, expiredTime)
- ReserveStock (sku, quantity, orderId)
- DeductStock (sku, quantity) [only after payment confirmed]
- RestoreStock (sku, quantity, reason)
- AdjustStock (sku, quantity, adjustmentType, reason)
- GetInventoryItem (sku)
- CheckAvailability (sku, quantity) → returns true/false

---

### 5. CART Context

**Responsibility:**
- Temporary holding of products before checkout
- No pricing calculation (belongs to ORDER context)
- Can be abandoned or expired
- Simple add/remove operations

**Entities:**

#### Cart (Aggregate Root)
```
Attributes:
- cartId: UUID (PK, identity)
- customerId: UUID (FK, owner of cart)
- items: List<CartItem>
- status: Enum [ACTIVE, ABANDONED, CONVERTED_TO_ORDER]
- expiresAt: timestamp (e.g., 30 days from last update)
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- CartStatus (ACTIVE | ABANDONED | CONVERTED_TO_ORDER)
```

#### CartItem (Entity, part of Cart aggregate)
```
Attributes:
- cartItemId: UUID (PK)
- sku: string (reference to product, not full product data)
- quantity: integer
- addedAt: timestamp

Value Objects:
- SKU
- Quantity
```

**Invariants:**
- Cart belongs to one customer only
- CartItem quantity must be > 0
- Cannot have duplicate SKUs in same cart (merge if added again)
- Cart can be abandoned after expiration time

**Events Published:**
- `CartCreated` (cartId, customerId)
- `ItemAddedToCart` (cartId, sku, quantity)
- `ItemRemovedFromCart` (cartId, sku)
- `CartCleared` (cartId)
- `CartAbandoned` (cartId, reason)
- `CartConvertedToOrder` (cartId, orderId)

**Commands (Use Cases):**
- CreateCart (customerId)
- AddItemToCart (cartId, sku, quantity)
- RemoveItemFromCart (cartId, sku)
- UpdateItemQuantity (cartId, sku, newQuantity)
- ClearCart (cartId)
- GetCart (customerId)

---

### 6. ORDER Context

**Responsibility:**
- Manage order lifecycle (PENDING → PAID → SHIPPED → DELIVERED)
- Calculate order total (price, tax, shipping)
- Create order from cart
- Track order status
- Handle order cancellation

**Entities:**

#### Order (Aggregate Root)
```
Attributes:
- orderId: UUID (PK, identity)
- customerId: UUID (FK, customer)
- cartId: UUID (reference to cart)
- orderItems: List<OrderItem> (snapshot of products + prices at order time)
- orderStatus: Enum [PENDING, AWAITING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELLED]
- totalPrice: Money (subtotal + tax + shipping)
- subtotal: Money (sum of item prices)
- taxAmount: Money
- shippingCost: Money
- discountAmount: Money (if any)
- paymentMethodId: UUID (reference to chosen payment method)
- shippingAddress: Address
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- OrderStatus (PENDING | AWAITING_PAYMENT | PAID | SHIPPED | DELIVERED | CANCELLED)
- Money (amount + currency)
- Address (shipping address details)
```

#### OrderItem (Entity, part of Order aggregate)
```
Attributes:
- orderItemId: UUID (PK)
- sku: string
- productName: string
- quantity: integer
- unitPrice: Money (price at order creation time - snapshot)
- totalPrice: Money (unitPrice * quantity)
- sellerProductId: UUID (reference)
- status: Enum [PENDING, RESERVED, PAID, SHIPPED, DELIVERED]
- createdAt: timestamp

Value Objects:
- SKU
- Money
- OrderItemStatus
```

**Invariants:**
- Order must have at least 1 item
- Quantity must be > 0
- Total price must be calculated correctly
- Order status transitions must be valid
- OrderItem prices are immutable (snapshot from order time)
- Cannot cancel paid order (only after delivered)

**Events Published:**
- `OrderCreated` (orderId, customerId, orderItems, totalPrice)
  - Triggers INVENTORY to reserve stock
- `OrderStatusChanged` (orderId, oldStatus, newStatus)
- `OrderAwaitingPayment` (orderId)
- `OrderPaid` (orderId)
- `OrderCancelled` (orderId, reason)
- `OrderShipped` (orderId)
- `OrderDelivered` (orderId)

**Events Listened:**
- `CartConvertedToOrder` (from CART) → Create new Order
- `StockReserved` (from INVENTORY) → Confirm order can proceed
- `PaymentSucceeded` (from PAYMENT) → Change order status to PAID
- `PaymentFailed` (from PAYMENT) → Change order status to AWAITING_PAYMENT (allow retry)

**Commands (Use Cases):**
- CreateOrderFromCart (cartId, customerId, shippingAddress)
- CalculateOrderTotal (orderId) [includes tax, shipping calculation]
- ChoosePaymentMethod (orderId, paymentMethodId)
- ProcessCheckout (orderId) → initiates PAYMENT
- CancelOrder (orderId, reason)
- UpdateOrderStatus (orderId, newStatus)
- GetOrder (orderId)

---

### 7. PAYMENT Context

**Responsibility:**
- Payment gateway integration (Stripe, Xendit, etc)
- Process payment transactions
- Handle payment callbacks (success/failure)
- Manage payment methods

**Entities:**

#### PaymentTransaction (Aggregate Root)
```
Attributes:
- paymentTransactionId: UUID (PK, identity)
- orderId: UUID (FK, associated order)
- customerId: UUID (FK)
- amount: Money
- paymentMethod: Enum [CREDIT_CARD, BANK_TRANSFER, E_WALLET, CASH]
- externalTransactionId: string (from payment gateway: Stripe ID, Xendit ID)
- status: Enum [PENDING, PROCESSING, SUCCEEDED, FAILED, REFUNDED]
- failureReason: string (nullable, if failed)
- attemptCount: integer
- lastAttemptAt: timestamp
- succeededAt: timestamp (nullable)
- expiresAt: timestamp (e.g., 15 minutes from creation)
- createdAt: timestamp
- updatedAt: timestamp

Value Objects:
- Money
- PaymentMethod (CREDIT_CARD | BANK_TRANSFER | E_WALLET | CASH)
- PaymentStatus (PENDING | PROCESSING | SUCCEEDED | FAILED | REFUNDED)
```

**Invariants:**
- Amount must be > 0
- Payment must expire after certain time (e.g., 15 minutes)
- Each payment attempt must be tracked
- Cannot refund more than original amount
- External transaction ID is immutable after success

**Events Published:**
- `PaymentInitiated` (paymentTransactionId, orderId, amount)
- `PaymentProcessing` (paymentTransactionId, externalTransactionId)
- `PaymentSucceeded` (paymentTransactionId, orderId, amount)
  - Triggers INVENTORY to hard-deduct stock
  - Triggers ORDER to change status to PAID
- `PaymentFailed` (paymentTransactionId, orderId, failureReason)
  - Triggers INVENTORY to restore reserved stock
  - Triggers ORDER to allow retry
- `PaymentExpired` (paymentTransactionId, orderId)
- `PaymentRefunded` (paymentTransactionId, refundAmount, reason)

**External API Calls:**
- POST to payment gateway (Stripe/Xendit) to initiate payment
- Webhook callback from payment gateway

**Commands (Use Cases):**
- InitiatePayment (orderId, amount, paymentMethod)
- ProcessPaymentCallback (externalTransactionId, status, response)
- RetryPayment (paymentTransactionId)
- RefundPayment (paymentTransactionId, reason)
- CheckPaymentStatus (paymentTransactionId)

---

### 8. RATING_REVIEW Context

**Responsibility:**
- Manage product reviews and ratings
- Aggregate ratings for products
- Ensure only verified buyers can review
- Publish review events for CATALOG to update rating

**Entities:**

#### Review (Aggregate Root)
```
Attributes:
- reviewId: UUID (PK, identity)
- customerId: UUID (FK, reviewer)
- sku: string (product being reviewed)
- rating: Decimal (1.0 - 5.0)
- title: string
- content: string
- isVerifiedPurchase: boolean (only if customer bought this product)
- helpfulCount: integer (upvotes)
- unhelpfulCount: integer (downvotes)
- status: Enum [PENDING, APPROVED, REJECTED, DELETED]
- createdAt: timestamp
- updatedAt: timestamp
- deletedAt: timestamp (nullable, soft delete)

Value Objects:
- Rating (1.0 - 5.0 decimal)
- ReviewStatus (PENDING | APPROVED | REJECTED | DELETED)
```

**Invariants:**
- Only verified buyers (who ordered and received product) can review
- Rating must be between 1.0 and 5.0
- Review content cannot be empty
- One customer can have only one review per product (or update existing)
- Review cannot be edited after N days (e.g., 30 days)

**Events Published:**
- `ReviewCreated` (reviewId, sku, customerId, rating)
  - Triggers CATALOG to recalculate average rating
- `ReviewUpdated` (reviewId, sku, newRating)
  - Triggers CATALOG to recalculate average rating
- `ReviewDeleted` (reviewId, sku)
  - Triggers CATALOG to recalculate average rating
- `ReviewApproved` (reviewId, sku)
- `ReviewRejected` (reviewId, sku)

**Events Listened:**
- `OrderDelivered` (from ORDER) → Allow customer to create review

**Commands (Use Cases):**
- CreateReview (customerId, sku, rating, title, content) [verify purchase first]
- UpdateReview (reviewId, rating, title, content) [within edit window]
- DeleteReview (reviewId, reason)
- ApproveReview (reviewId) [by admin]
- RejectReview (reviewId, reason) [by admin]
- GetProductReviews (sku) [paginated]
- GetAverageRating (sku) → returns decimal (0.0 - 5.0)

---

### 9. NOTIFICATION Context

**Responsibility:**
- Send notifications (email, SMS, push)
- Listen to events from other contexts
- Track notification status
- Retry mechanism for failed notifications

**Note:** This is a supporting context, not core domain logic.

**Entities:**

#### Notification (Aggregate Root)
```
Attributes:
- notificationId: UUID (PK, identity)
- recipientId: UUID (customer or seller)
- recipientEmail: string
- notificationType: Enum [ORDER_CONFIRMATION, PAYMENT_SUCCESS, PAYMENT_FAILED, STOCK_ALERT, REVIEW_CREATED, SHIPPING_UPDATE]
- subject: string
- body: string
- channel: Enum [EMAIL, SMS, PUSH]
- status: Enum [PENDING, SENT, FAILED, RETRIED]
- attemptCount: integer
- lastAttemptAt: timestamp
- sentAt: timestamp (nullable)
- failureReason: string (nullable)
- relatedOrderId: UUID (nullable)
- relatedSku: string (nullable)
- createdAt: timestamp

Value Objects:
- NotificationType
- Channel (EMAIL | SMS | PUSH)
- NotificationStatus (PENDING | SENT | FAILED | RETRIED)
```

**Invariants:**
- Recipient email/phone must be valid
- Subject and body cannot be empty
- Max retry attempts = 3

**Events Published:**
- `NotificationSent` (notificationId, recipientId, channel)
- `NotificationFailed` (notificationId, reason)
- `NotificationRetried` (notificationId)

**Events Listened (Subscribers):**
- `UserRegistered` (from ACCOUNT) → Send welcome email
- `OrderCreated` (from ORDER) → Send order confirmation email
- `PaymentSucceeded` (from PAYMENT) → Send payment success email
- `PaymentFailed` (from PAYMENT) → Send payment failed email
- `OrderShipped` (from ORDER) → Send shipping notification
- `OrderDelivered` (from ORDER) → Send delivery confirmation
- `ReviewCreated` (from RATING_REVIEW) → Notify seller
- `StockLow` (from INVENTORY) → Alert seller
- `ProductPublished` (from SELLER_PRODUCT) → Confirm to seller

**Commands (Use Cases):**
- SendNotification (recipientId, notificationType, templateData)
- RetryFailedNotification (notificationId)
- GetNotificationStatus (notificationId)

---

## Events & Event Flow

### Event Registry (All Events)

| Event Name | Published By | Listened By | Payload |
|---|---|---|---|
| UserRegistered | ACCOUNT | NOTIFICATION | userId, email, role |
| UserRoleChanged | ACCOUNT | - | userId, oldRole, newRole |
| UserProfileUpdated | ACCOUNT | - | userId, updatedFields |
| CatalogProductPublished | CATALOG | - | sku, sellerId, name, price |
| CatalogProductUpdated | CATALOG | - | sku, updatedFields |
| CatalogProductRatingUpdated | CATALOG | - | sku, averageRating, reviewCount |
| CatalogProductDiscontinued | CATALOG | - | sku |
| ProductCreated | SELLER_PRODUCT | NOTIFICATION | sellerProductId, sku, name, price |
| ProductNameChanged | SELLER_PRODUCT | CATALOG | sku, newName |
| ProductPriceChanged | SELLER_PRODUCT | CATALOG | sku, newPrice |
| ProductDescriptionChanged | SELLER_PRODUCT | CATALOG | sku, newDescription |
| ProductPublished | SELLER_PRODUCT | CATALOG, NOTIFICATION | sku, sellerId |
| ProductUnpublished | SELLER_PRODUCT | CATALOG | sku |
| ProductStatusChanged | SELLER_PRODUCT | CATALOG | sku, newStatus |
| ProductDeleted | SELLER_PRODUCT | - | sku |
| StockReserved | INVENTORY | ORDER | sku, quantity, orderId |
| StockDeducted | INVENTORY | CATALOG, ORDER | sku, quantity |
| StockRestored | INVENTORY | ORDER | sku, quantity, reason |
| StockAdjusted | INVENTORY | CATALOG | sku, quantity, reason |
| StockLow | INVENTORY | NOTIFICATION | sku, currentQuantity, threshold |
| StockExpired | INVENTORY | NOTIFICATION | sku, expiredTime |
| CartCreated | CART | - | cartId, customerId |
| ItemAddedToCart | CART | - | cartId, sku, quantity |
| ItemRemovedFromCart | CART | - | cartId, sku |
| CartCleared | CART | - | cartId |
| CartAbandoned | CART | - | cartId, reason |
| CartConvertedToOrder | CART | ORDER | cartId, orderId |
| OrderCreated | ORDER | INVENTORY, NOTIFICATION | orderId, customerId, orderItems, totalPrice |
| OrderStatusChanged | ORDER | NOTIFICATION | orderId, oldStatus, newStatus |
| OrderAwaitingPayment | ORDER | - | orderId |
| OrderPaid | ORDER | INVENTORY, NOTIFICATION | orderId |
| OrderCancelled | ORDER | INVENTORY, NOTIFICATION | orderId, reason |
| OrderShipped | ORDER | NOTIFICATION | orderId |
| OrderDelivered | ORDER | RATING_REVIEW, NOTIFICATION | orderId |
| PaymentInitiated | PAYMENT | NOTIFICATION | paymentTransactionId, orderId, amount |
| PaymentProcessing | PAYMENT | - | paymentTransactionId, externalTransactionId |
| PaymentSucceeded | PAYMENT | ORDER, INVENTORY, NOTIFICATION | paymentTransactionId, orderId, amount |
| PaymentFailed | PAYMENT | ORDER, INVENTORY, NOTIFICATION | paymentTransactionId, orderId, failureReason |
| PaymentExpired | PAYMENT | ORDER, NOTIFICATION | paymentTransactionId, orderId |
| PaymentRefunded | PAYMENT | NOTIFICATION | paymentTransactionId, refundAmount |
| ReviewCreated | RATING_REVIEW | CATALOG, NOTIFICATION | reviewId, sku, customerId, rating |
| ReviewUpdated | RATING_REVIEW | CATALOG | reviewId, sku, newRating |
| ReviewDeleted | RATING_REVIEW | CATALOG | reviewId, sku |
| ReviewApproved | RATING_REVIEW | NOTIFICATION | reviewId, sku |
| ReviewRejected | RATING_REVIEW | - | reviewId, sku, reason |
| NotificationSent | NOTIFICATION | - | notificationId, recipientId |
| NotificationFailed | NOTIFICATION | - | notificationId, reason |
| NotificationRetried | NOTIFICATION | - | notificationId |

---

## Cross-Context Communication

### Communication Matrix

| Initiator | Event | Listeners | Action |
|---|---|---|---|
| SELLER_PRODUCT | ProductPublished | CATALOG | Create or update CatalogProduct |
| SELLER_PRODUCT | ProductPriceChanged | CATALOG | Update product price |
| SELLER_PRODUCT | ProductNameChanged | CATALOG | Update product name |
| SELLER_PRODUCT | ProductUnpublished | CATALOG | Mark as discontinued |
| CART | CartConvertedToOrder | ORDER | Create new Order |
| ORDER | OrderCreated | INVENTORY, NOTIFICATION | Reserve stock, send confirmation email |
| PAYMENT | PaymentSucceeded | ORDER, INVENTORY, NOTIFICATION | Update order to PAID, deduct stock, send success email |
| PAYMENT | PaymentFailed | ORDER, INVENTORY, NOTIFICATION | Mark order AWAITING_PAYMENT, restore stock, send failure email |
| ORDER | OrderCancelled | INVENTORY, NOTIFICATION | Restore reserved stock, send cancellation email |
| ORDER | OrderDelivered | RATING_REVIEW, NOTIFICATION | Allow customer to review, send delivery confirmation |
| INVENTORY | StockDeducted | CATALOG | Update product availability |
| INVENTORY | StockRestored | CATALOG | Update product availability |
| INVENTORY | StockLow | NOTIFICATION | Alert seller about low stock |
| RATING_REVIEW | ReviewCreated | CATALOG, NOTIFICATION | Recalculate average rating, notify seller |
| RATING_REVIEW | ReviewDeleted | CATALOG | Recalculate average rating |

### Event Publishing Mechanism

**Recommended Approach for MVP:**
- **Database-driven events** (simple, no external dependencies)
  - Store events in `OutboxEvent` table
  - Polling mechanism (every 5-10 seconds) picks up new events
  - Consumer processes event, marks as processed
- **Alternative (if scalability needed):** RabbitMQ, Kafka (later phase)

---

## Main Workflows

### Workflow 1: Customer Checkout & Payment

```
STEP 1: Customer add items to cart
├─ CART context: AddItemToCart
├─ Events: ItemAddedToCart published (but not acted upon yet)

STEP 2: Customer proceed to checkout
├─ CART context: CartConvertedToOrder (cartId, customerId, shippingAddress)
├─ Events: CartConvertedToOrder published
│
├─ ORDER context: Listen to CartConvertedToOrder
│  ├─ Action: Create new Order (orderId)
│  ├─ Snapshot OrderItem (name, price) from CATALOG at this moment
│  ├─ Calculate total: subtotal + tax + shipping
│  ├─ Order state: PENDING
│  └─ Events: OrderCreated published
│
├─ INVENTORY context: Listen to OrderCreated
│  ├─ Action: Reserve stock for each OrderItem (soft-reserve)
│  ├─ quantityReserved += qty_ordered
│  ├─ quantityAvailable = quantity - quantityReserved
│  └─ Events: StockReserved published (for each item)
│
└─ NOTIFICATION context: Listen to OrderCreated
   └─ Action: Send "Order Confirmation" email

STEP 3: Customer choose payment method & pay
├─ ORDER context: ChoosePaymentMethod(orderId, paymentMethod)
├─ ORDER context: ProcessCheckout(orderId)
│  └─ Events: OrderAwaitingPayment published
│
└─ PAYMENT context: Listen to OrderAwaitingPayment (or explicit call)
   ├─ Action: InitiatePayment(orderId, amount, paymentMethod)
   ├─ Call payment gateway (Stripe/Xendit)
   ├─ Store PaymentTransaction (status: PENDING, externalId: from gateway)
   └─ Events: PaymentInitiated published

STEP 4a: Payment Succeeded ✅
├─ Payment gateway callback → PaymentProcessing
│  └─ Events: PaymentProcessing published
│
├─ PAYMENT context: Listen to payment success callback
│  ├─ Action: Update PaymentTransaction (status: SUCCEEDED, succeededAt: now)
│  └─ Events: PaymentSucceeded published (orderId, paymentTransactionId)
│
├─ ORDER context: Listen to PaymentSucceeded
│  ├─ Action: Update Order (state: PAID)
│  └─ Events: OrderPaid published
│
├─ INVENTORY context: Listen to PaymentSucceeded
│  ├─ Action: Hard-deduct stock (committed)
│  ├─ quantity -= qty_ordered
│  ├─ quantityReserved -= qty_ordered
│  └─ Events: StockDeducted published
│
├─ CATALOG context: Listen to StockDeducted
│  └─ Action: Update CatalogProduct.availability
│
└─ NOTIFICATION context: Listen to PaymentSucceeded & OrderPaid
   ├─ Send "Payment Success" email to customer
   ├─ Send "Order Received" email to seller
   └─ (Later) Send "Order Shipped" when order state changes

STEP 4b: Payment Failed ❌
├─ Payment gateway callback → PaymentFailed
│  └─ Events: PaymentFailed published
│
├─ PAYMENT context: Update PaymentTransaction (status: FAILED, failureReason)
│  └─ Events: PaymentFailed published
│
├─ ORDER context: Listen to PaymentFailed
│  ├─ Action: Keep order state as AWAITING_PAYMENT (allow retry)
│  └─ NOTE: Do NOT cancel order immediately, allow customer to retry
│
├─ INVENTORY context: Listen to PaymentFailed
│  ├─ Action: Restore reserved stock
│  ├─ quantityReserved -= qty_ordered
│  ├─ quantityAvailable = quantity - quantityReserved
│  └─ Events: StockRestored published
│
└─ NOTIFICATION context: Listen to PaymentFailed
   └─ Send "Payment Failed, Please Retry" email to customer

STEP 5: Order Fulfillment (Async, later workflow)
├─ Seller receives notification → ships order
├─ ORDER state: SHIPPED
├─ NOTIFICATION: Send tracking info to customer
├─ ORDER state: DELIVERED
└─ RATING_REVIEW context: Allow customer to create review
```

**Key Decisions in Flow 1:**
1. **Stock reserved at OrderCreated, deducted at PaymentSucceeded**
   - Benefit: Payment failure doesn't affect inventory
   - Invariant: quantityReserved prevents overselling
2. **Order stays in AWAITING_PAYMENT if payment fails (not cancelled)**
   - Benefit: Customer can retry payment without creating new order
   - UX: Better experience for payment hiccups
3. **CATALOG updated when stock actually deducted (after payment)**
   - Benefit: Availability always reflects committed inventory
   - Invariant: INVENTORY is source of truth

---

### Workflow 2: Seller Update Product

```
STEP 1: Seller update product info
├─ SELLER_PRODUCT context: UpdateProductName(sku, newName)
├─ Action: Validation (seller owns this product, name not empty)
├─ Action: Update SellerProduct.name
└─ Events: ProductNameChanged published (sku, newName)

STEP 2: CATALOG listens to ProductNameChanged
├─ CATALOG context: Listen to ProductNameChanged
├─ Action: Update CatalogProduct.name
└─ Events: CatalogProductUpdated published (metadata only)

STEP 3: Similar for ProductPriceChanged, ProductDescriptionChanged
├─ SELLER_PRODUCT publishes event
└─ CATALOG listens & updates

STEP 4: Seller cannot update stock directly
├─ Stock management ONLY via INVENTORY context
├─ If seller wants to add stock → must go through INVENTORY
│  (This could be a separate workflow, e.g., "Restock")
└─ (Implementation: API layer enforces this constraint)

NOTE: Seller does NOT control stock, INVENTORY does.
```

**Key Decisions in Flow 2:**
1. **Price changes update CATALOG immediately**
   - New orders will see updated price
   - Existing orders keep original price (OrderItem is snapshot)
2. **Stock is NOT updated via SELLER_PRODUCT context**
   - Only INVENTORY context can modify stock
   - Seller cannot accidentally break inventory

---

### Workflow 3: Product Review & Rating

```
STEP 1: Order delivered
├─ ORDER context: OrderDelivered (orderId)
└─ Events: OrderDelivered published

STEP 2: RATING_REVIEW context listens to OrderDelivered
├─ Action: Mark order as "eligible for review"
├─ Notify customer (via NOTIFICATION) that they can now review

STEP 3: Customer creates review
├─ RATING_REVIEW context: CreateReview(customerId, sku, rating, title, content)
├─ Validation: 
│  ├─ Customer must have ordered and received this product
│  ├─ Rating must be 1.0 - 5.0
│  └─ Cannot have more than 1 review per product per customer
├─ Action: Create Review (status: PENDING or auto-approve based on business rule)
└─ Events: ReviewCreated published (sku, customerId, rating)

STEP 4: CATALOG context listens to ReviewCreated
├─ CATALOG context: Recalculate average rating for product
├─ Action: Update CatalogProduct.averageRating & reviewCount
│  ├─ Formula: averageRating = sum(all_reviews.rating) / count(all_reviews)
│  └─ Only count APPROVED reviews (if moderation enabled)
└─ Events: CatalogProductRatingUpdated published

STEP 5: NOTIFICATION context listens to ReviewCreated
└─ Action: Notify seller "New review on your product"

STEP 6: (Optional) Admin review moderation
├─ RATING_REVIEW context: ApproveReview(reviewId)
├─ Action: Update Review (status: APPROVED)
└─ Events: ReviewApproved published
   └─ Triggers CATALOG to recalculate rating again

STEP 7: Customer can edit or delete review
├─ RATING_REVIEW context: UpdateReview(reviewId, rating, content)
├─ Constraint: Can only edit within 30 days of creation
└─ Events: ReviewUpdated published
   └─ Triggers CATALOG to recalculate rating
```

**Key Decisions in Flow 3:**
1. **Only verified buyers can review**
   - RATING_REVIEW validates purchase history
   - Prevents fake reviews from non-buyers
2. **Rating aggregate calculated in CATALOG**
   - RATING_REVIEW owns raw review data
   - CATALOG maintains aggregate (for performance)
   - Event-driven update when review changes
3. **One review per customer per product**
   - Prevent spam/duplicate reviews
   - Allow edit instead of multiple reviews

---

## Implementation Notes

### Database Design (High-level)

**Tables per Context:**

```
ACCOUNT Context:
- users (userId, email, hashedPassword, role)
- user_profiles (profileId, userId, fullName, phone, address)

SELLER_PRODUCT Context:
- seller_products (sellerProductId, sellerId, sku, name, price, description)

CATALOG Context:
- catalog_products (catalogProductId, sku, name, price, averageRating, reviewCount)

INVENTORY Context:
- inventory_items (inventoryItemId, sku, quantity, quantityReserved, weight, size, expiredTime)
- stock_adjustments (adjustmentId, inventoryItemId, adjustmentType, quantity, reason)

CART Context:
- carts (cartId, customerId, status, expiresAt)
- cart_items (cartItemId, cartId, sku, quantity)

ORDER Context:
- orders (orderId, customerId, orderStatus, totalPrice, subtotal, tax, shipping)
- order_items (orderItemId, orderId, sku, productName, quantity, unitPrice)

PAYMENT Context:
- payment_transactions (paymentTransactionId, orderId, amount, paymentMethod, status, externalId)

RATING_REVIEW Context:
- reviews (reviewId, customerId, sku, rating, title, content, isVerifiedPurchase, status)

NOTIFICATION Context:
- notifications (notificationId, recipientId, notificationType, channel, status, sentAt)

Cross-Context:
- outbox_events (eventId, aggregateId, eventType, payload, publishedAt, processedAt)
  [Used for event publishing & delivery]
```

### Event Handling Strategy

**For MVP (Simple, no external message queue):**
1. **Outbox Pattern:**
   - Event published → stored in `outbox_events` table + committed with business logic
   - Separate polling job every 5-10 sec picks up unpublished events
   - Consumer reads & publishes to listeners
   - Listener handles event, sends ACK
   - Outbox marks event as processed

2. **Event Listeners (In-Process):**
   - Use Spring's `@EventListener` or `ApplicationEventPublisher`
   - Within same transaction or separate (depending on consistency need)
   - Retry mechanism for failed event handlers

**For Future (Scalability):**
- Switch to Kafka/RabbitMQ
- Distributed tracing & monitoring
- DLQ (Dead Letter Queue) for failed events

---

## Validation Checklist (Before Coding)

- [ ] All 9 contexts clearly defined & non-overlapping
- [ ] All entities & value objects documented with attributes
- [ ] All events published per context listed
- [ ] All events listeners mapped (context receiving events)
- [ ] Workflow scenarios traced through all contexts
- [ ] Invariants clearly stated (what cannot break)
- [ ] Database schema sketch reviewed
- [ ] Event publishing mechanism decided (Outbox, Kafka, etc)
- [ ] Authorization strategy clear (where & how roles enforced)
- [ ] Testing strategy (unit, integration, e2e)

---

## Next Steps

1. **Review & Refine (Yours):**
   - Read this document
   - Identify gaps, conflicts, or unclear areas
   - Update/correct based on your business rules
   - Add any missing contexts or entities

2. **Validate Edge Cases (Yours):**
   - What if stock reserved but order never completes payment?
   - What if customer tries to order 1000 items of out-of-stock product?
   - What if payment gateway returns weird response?
   - How do you handle concurrent orders on same stock?

3. **Tech Stack Decision (Yours + me):**
   - Confirm Spring Boot + MySQL
   - Decide on event mechanism (Outbox Pattern vs Kafka)
   - Project structure (packages by context)
   - Testing framework (JUnit, Testcontainers)

4. **Implement Phase 1 (High Priority):**
   - ACCOUNT + CATALOG (read-only)
   - SELLER_PRODUCT (create product)
   - CART (add/remove items)

5. **Implement Phase 2 (Order Flow):**
   - ORDER (checkout)
   - PAYMENT (simple, mock initially)
   - INVENTORY (stock management)

6. **Implement Phase 3 (Polish):**
   - RATING_REVIEW
   - NOTIFICATION
   - Advanced features (refund, moderation, etc)

---

**Version History:**
- v1.0 (2026-10-04): Initial design document
