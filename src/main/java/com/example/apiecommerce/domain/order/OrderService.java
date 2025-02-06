package com.example.apiecommerce.domain.order;

import com.example.apiecommerce.domain.DataTimeProvider;
import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import com.example.apiecommerce.domain.delivery.Delivery;
import com.example.apiecommerce.domain.delivery.DeliveryRepository;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.domain.order.dto.OrderMainInfoDto;
import com.example.apiecommerce.domain.orderItem.OrderItem;
import com.example.apiecommerce.domain.orderItem.OrderItemRepository;
import com.example.apiecommerce.domain.product.Product;
import com.example.apiecommerce.domain.product.ProductRepository;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final CartService cartService;
    private final DataTimeProvider dataTimeProvider;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderDtoMapper orderDtoMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;

    public OrderService(UserRepository userRepository, CartService cartService, DataTimeProvider dataTimeProvider, AddressRepository addressRepository, ProductRepository productRepository, OrderDtoMapper orderDtoMapper, OrderRepository orderRepository, OrderItemRepository orderItemRepository, DeliveryRepository deliveryRepository) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.dataTimeProvider = dataTimeProvider;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional
    public OrderFullDto createOrder(String userMail, long addressId, long deliveryId){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        CartDetailsDto cart = cartService.findCartDetailsById(user.getCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        if (!address.getUser().equals(user)){
            throw new IllegalArgumentException("Address not belong to the specified user");
        }

        Order order = new Order();
        order.setTotalPrice(cart.getTotalCost() + delivery.getDeliveryCharge());
        order.setOrderDate(dataTimeProvider.getCurrentTime());
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setDelivery(delivery);

        Order savedOrder = orderRepository.save(order);

        Set<OrderItem> orderItems = getOrderItems(order, cart, savedOrder);
        cartService.deleteCart(user.getCart().getId());
        orderItemRepository.saveAll(orderItems);
        return orderDtoMapper.map(savedOrder);
    }

    private Set<OrderItem> getOrderItems(Order order, CartDetailsDto cart, Order savedOrder) {
        Set<OrderItem> orderItems = order.getOrderItems();
        List<CartItemFullDto> cartItems = cart.getCartItems();

        for (CartItemFullDto cartItemFullDto : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemQuantity(cartItemFullDto.getCartItemQuantity());
            orderItem.setOrder(savedOrder);
            Product product = productRepository.findById(cartItemFullDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            orderItem.setProduct(product);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    public Optional<OrderFullDto> findOrderById(Long orderId){
        return orderRepository.findById(orderId).map(orderDtoMapper::map);
    }

    @Transactional
    public void deleteOrderById(Long orderId){
        Order orderToDelete = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (orderToDelete.getAddress() != null) {
            orderToDelete.getAddress().setOrder(null);
        }
        orderRepository.delete(orderToDelete);
    }

    public Page<OrderMainInfoDto> findAllPaginatedOrders(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        List<String> allowedFields = List.of("orderDate", "orderTotalPrice", "userEmail", "userPhoneNumber");
        if (!allowedFields.contains(sortField)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortField);
        }
        int pageIndex = Math.max(pageNumber -1, 0);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        return orderRepository.findAll(pageable)
                .map(orderDtoMapper::mapToMainInfo);
    }

    @Transactional
    public Optional<OrderFullDto> processPayment(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        boolean isPaymentSuccessful = new Random().nextBoolean();
        order.setPaymentStatus(isPaymentSuccessful ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);

        orderRepository.save(order);
        
        return Optional.of(orderDtoMapper.map(order));
    }


}
