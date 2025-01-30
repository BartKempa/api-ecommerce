package com.example.apiecommerce.domain.order;

import com.example.apiecommerce.domain.DataTimeProvider;
import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.domain.orderItem.OrderItem;
import com.example.apiecommerce.domain.orderItem.OrderItemRepository;
import com.example.apiecommerce.domain.product.Product;
import com.example.apiecommerce.domain.product.ProductRepository;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

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


    public OrderService(UserRepository userRepository, CartService cartService, DataTimeProvider dataTimeProvider, AddressRepository addressRepository, ProductRepository productRepository, OrderDtoMapper orderDtoMapper, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.dataTimeProvider = dataTimeProvider;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    @Transactional
    public OrderFullDto createOrder(String userMail, long addressId){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        CartDetailsDto cart = cartService.getCartDetailsById(user.getCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().equals(user)){
            throw new IllegalArgumentException("Address not belong to the specified user");
        }

        Order order = new Order();
        order.setTotalPrice(cart.getTotalCost());
        order.setOrderDate(dataTimeProvider.getCurrentTime());
        order.setUser(user);
        order.setAddress(address);

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

    public Optional<OrderFullDto> getOrderById(Long orderId){
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
}
