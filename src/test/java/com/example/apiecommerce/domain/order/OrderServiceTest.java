package com.example.apiecommerce.domain.order;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.cart.Cart;
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
import com.example.apiecommerce.domain.product.ProductService;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private DateTimeProvider dateTimeProviderMock;
    @Mock
    private AddressRepository addressRepositoryMock;
    @Mock
    ProductRepository productRepositoryMock;
    @Mock
    private OrderDtoMapper orderDtoMapperMock;
    @Mock
    private OrderRepository orderRepositoryMock;
    @Mock
    private OrderItemRepository orderItemRepositoryMock;
    @Mock
    private DeliveryRepository deliveryRepositoryMock;
    @Mock
    private ProductService productServiceMock;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(userRepositoryMock, cartServiceMock, dateTimeProviderMock, addressRepositoryMock, productRepositoryMock, orderDtoMapperMock, orderRepositoryMock, orderItemRepositoryMock, deliveryRepositoryMock, productServiceMock);
    }

    @Test
    void shouldCreateOrder() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart();
        cart.setCreationDate(now);
        cart.setId(1L);
        user.setCart(cart);

        CartItemFullDto cartItemFullDto1 = new CartItemFullDto(1L, 2L, 1L, 1L, "Pillsner", 10.50);
        CartItemFullDto cartItemFullDto2 = new CartItemFullDto(2L, 1L, 1L, 2L, "Lech", 8.80);
        List<CartItemFullDto> cartItemFullDtoList = List.of(cartItemFullDto1, cartItemFullDto2);
        CartDetailsDto cartDetailsDto = new CartDetailsDto(cartItemFullDtoList, 29.90);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryCharge(5.0);

        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(34.90);
        order.setOrderDate(now);
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.NEW);
        order.setDelivery(delivery);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartServiceMock.findUserCart("test@mail.com")).thenReturn(Optional.of(cartDetailsDto));
        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(deliveryRepositoryMock.findById(1L)).thenReturn(Optional.of(delivery));
        Mockito.when(orderRepositoryMock.save(Mockito.any(Order.class))).thenReturn(order);
        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product1));
        Mockito.when(productRepositoryMock.findById(2L)).thenReturn(Optional.of(product2));

        Mockito.doNothing().when(cartServiceMock).deleteCartWithoutIncreasingStock("test@mail.com");
        OrderFullDto orderFullDto = new OrderFullDto();
        orderFullDto.setId(1L);
        orderFullDto.setOrderTotalPrice(34.90);
        orderFullDto.setOrderPaymentStatus(PaymentStatus.PENDING.name());
        orderFullDto.setOrderStatus(OrderStatus.NEW.name());
        Mockito.when(orderDtoMapperMock.map(Mockito.any(Order.class))).thenReturn(orderFullDto);

        // when
        OrderFullDto orderFullDtoResult = orderService.createOrder("test@mail.com", 1L, 1L);

        // then
        assertNotNull(orderFullDtoResult);
        assertEquals(1L, orderFullDtoResult.getId());
        assertEquals(34.90, orderFullDtoResult.getOrderTotalPrice());
        assertEquals(PaymentStatus.PENDING.name(), orderFullDtoResult.getOrderPaymentStatus());
        assertEquals(OrderStatus.NEW.name(), orderFullDtoResult.getOrderStatus());

        Mockito.verify(orderRepositoryMock, Mockito.times(1)).save(Mockito.any(Order.class));
        Mockito.verify(orderItemRepositoryMock, Mockito.times(1)).saveAll(Mockito.anySet());
        Mockito.verify(cartServiceMock, Mockito.times(1)).deleteCartWithoutIncreasingStock("test@mail.com");
    }

    @Test
    void shouldReturnOrderWhenUserIsOwner() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);

        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(34.90);
        order.setOrderDate(now);
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.NEW);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        OrderFullDto orderFullDto = new OrderFullDto();
        orderFullDto.setId(1L);
        orderFullDto.setOrderTotalPrice(34.90);
        orderFullDto.setOrderPaymentStatus(PaymentStatus.PENDING.name());
        orderFullDto.setOrderStatus(OrderStatus.NEW.name());
        Mockito.when(orderDtoMapperMock.map(Mockito.any(Order.class))).thenReturn(orderFullDto);

        //when
        OrderFullDto orderFullDtoResult = orderService.findOrderById(1L, "test@mail.com").orElseThrow();

        //then
        Mockito.verify(userRepositoryMock).findByEmail("test@mail.com");
        Mockito.verify(orderRepositoryMock).findById(1L);
        Mockito.verify(orderDtoMapperMock).map(order);
        assertNotNull(orderFullDtoResult);
        assertEquals(1L, orderFullDtoResult.getId());
        assertEquals(34.90, orderFullDtoResult.getOrderTotalPrice());
        assertEquals(PaymentStatus.PENDING.name(), orderFullDtoResult.getOrderPaymentStatus());
        assertEquals(OrderStatus.NEW.name(), orderFullDtoResult.getOrderStatus());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> orderService.findOrderById(1L, "test@mail.com"));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> orderService.findOrderById(1L, "test@mail.com"));
    }

    @Test
    void shouldThrowExceptionWhenOrderBelongsToAnotherUser() {
        // given
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test@mail.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("other@mail.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user2);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user1));
        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.findOrderById(1L, "test@mail.com"));
    }

    @Test
    void shouldDeleteOrderById() {
        //given
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setOrderItemQuantity(2L);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setOrderItemQuantity(3L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(Set.of(orderItem1, orderItem2));

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.deleteOrderById(1L);

        // then
        Mockito.verify(productServiceMock).updateProductQuantityInDb(1L, -2L);
        Mockito.verify(productServiceMock).updateProductQuantityInDb(2L, -3L);
        Mockito.verify(orderRepositoryMock).delete(order);
    }

    @Test
    void shouldDeleteOrderWithoutProducts() {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(Collections.emptySet());

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.deleteOrderById(1L);

        // then
        Mockito.verify(productServiceMock, Mockito.never()).updateProductQuantityInDb(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(orderRepositoryMock).delete(order);
    }

    @Test
    void shouldThrowExceptionWhenDeleteNotExistOrderById() {
        // given
        long nonExistingOrderId = 111L;

        Mockito.when(orderRepositoryMock.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> orderService.deleteOrderById(111L));

        //then
        assertTrue(exc.getMessage().contains("Order not found"));
    }

    @Test
    void shouldFindAllPaginatedOrders() {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(34.90);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.NEW);

        OrderMainInfoDto orderMainInfoDto = new OrderMainInfoDto();
        orderMainInfoDto.setOrderTotalPrice(34.90);
        orderMainInfoDto.setOrderPaymentStatus(PaymentStatus.PENDING.name());
        orderMainInfoDto.setOrderStatus(OrderStatus.NEW.name());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotalPrice(15.50);
        order2.setPaymentStatus(PaymentStatus.PENDING);
        order2.setOrderStatus(OrderStatus.NEW);

        OrderMainInfoDto orderMainInfoDto2 = new OrderMainInfoDto();
        orderMainInfoDto2.setOrderTotalPrice(15.50);
        orderMainInfoDto2.setOrderPaymentStatus(PaymentStatus.PENDING.name());
        orderMainInfoDto2.setOrderStatus(OrderStatus.NEW.name());

        List<Order> ordersList = List.of(order2, order);

        Pageable pageable = PageRequest.of(0, 3, Sort.by("orderTotalPrice").ascending());

        PageImpl<Order> orders = new PageImpl<>(ordersList, pageable, ordersList.size());

        Mockito.when(orderRepositoryMock.findAll(Mockito.any(Pageable.class))).thenReturn(orders);
        Mockito.when(orderDtoMapperMock.mapToMainInfo(order)).thenReturn(orderMainInfoDto);
        Mockito.when(orderDtoMapperMock.mapToMainInfo(order2)).thenReturn(orderMainInfoDto2);

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "orderTotalPrice";
        String sortDirection = "ASC";

        // when
        Page<OrderMainInfoDto> allPaginatedOrders = orderService.findAllPaginatedOrders(pageNumber, pageSize, sortField, sortDirection);

        // then
        assertThat(allPaginatedOrders.getTotalElements(), is(2L));
        assertThat(allPaginatedOrders.getContent().get(0).getOrderTotalPrice(), is(15.50));
        assertThat(allPaginatedOrders.getContent().get(1).getOrderTotalPrice(), is(34.90));
    }

    @Test
    void shouldProcessPayment() {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(34.90);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.NEW);

        OrderFullDto orderFullDto = new OrderFullDto();
        orderFullDto.setId(1L);
        orderFullDto.setOrderTotalPrice(34.90);
        orderFullDto.setOrderPaymentStatus(PaymentStatus.PENDING.name());
        orderFullDto.setOrderStatus(OrderStatus.NEW.name());

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(orderDtoMapperMock.map(order)).thenReturn(orderFullDto);

        //when
        Optional<OrderFullDto> orderFullDtoResult = orderService.processPayment(1L);

        //then
        assertTrue(orderFullDtoResult.isPresent());
        assertEquals(OrderStatus.NEW.name(), orderFullDtoResult.get().getOrderStatus());
        assertEquals(34.90, orderFullDtoResult.get().getOrderTotalPrice());
    }

    @Test
    void shouldThrowExceptionWhenProcessPaymentAndOrderNotExist() {
        // given
        long nonExistingOrder = 111L;

        Mockito.when(orderRepositoryMock.findById(nonExistingOrder)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> orderService.processPayment(nonExistingOrder));

        //then
        assertTrue(exc.getMessage().contains("Order not found"));
    }

    @Test
    void shouldCancelOrderById() {
        //given
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setOrderItemQuantity(2L);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setOrderItemQuantity(3L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.NEW);
        order.setOrderItems(Set.of(orderItem1, orderItem2));

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.cancelOrderById(1L);

        // then
        Mockito.verify(productServiceMock).updateProductQuantityInDb(1L, -2L);
        Mockito.verify(productServiceMock).updateProductQuantityInDb(2L, -3L);
        assertEquals(order.getOrderStatus(), OrderStatus.CANCELLED);
        assertEquals(0L, orderItem1.getOrderItemQuantity());
        assertEquals(0L, orderItem2.getOrderItemQuantity());
    }

    @Test
    void shouldThrowExceptionWhenCancelNotExistOrderById() {
        // given
        long nonExistingOrder = 111L;

        Mockito.when(orderRepositoryMock.findById(nonExistingOrder)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> orderService.cancelOrderById(nonExistingOrder));

        //then
        assertTrue(exc.getMessage().contains("Order not found"));
    }

    @Test
    void shouldThrowExceptionWhenCancelOrderWithStatusSuccess() {
        //given
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setOrderItemQuantity(2L);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setOrderItemQuantity(3L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.SUCCESS);
        order.setOrderItems(Set.of(orderItem1, orderItem2));

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrderById(1L));

        //then
        assertTrue(exc.getMessage().contains("Only status 'NEW' can be changed into 'CANCELLED'"));
    }

    @Test
    void shouldCancelOrderWithoutOrderItems() {
        //given
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.NEW);
        order.setOrderItems(Collections.emptySet());

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.cancelOrderById(1L);

        // then
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        Mockito.verifyNoInteractions(productServiceMock);
    }

    @Test
    void shouldThrowExceptionWhenCancelAlreadyCancelledOrder() {
        //given
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CANCELLED);

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrderById(1L));

        //then
        assertTrue(exc.getMessage().contains("Only status 'NEW' can be changed into 'CANCELLED'"));
    }

    @Test
    void successOrderById() {
        //given
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.NEW);
        order.setPaymentStatus(PaymentStatus.COMPLETED);

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.successOrderById(1L);

        // then
        assertEquals(order.getOrderStatus(), OrderStatus.SUCCESS);
    }

    @Test
    void shouldThrowExceptionWhenSuccessNotExistOrderById() {
        // given
        long nonExistingOrder = 111L;

        Mockito.when(orderRepositoryMock.findById(nonExistingOrder)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> orderService.successOrderById(nonExistingOrder));

        //then
        assertTrue(exc.getMessage().contains("Order not found"));
    }

    @Test
    void shouldThrowExceptionWhenOrderPaymentStatusIsNotCompleted() {
        //given
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.NEW);
        order.setPaymentStatus(PaymentStatus.PENDING);

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> orderService.successOrderById(1L));

        //then
        assertTrue(exc.getMessage().contains("Only orders with payment status 'COMPLETED' can be marked as 'SUCCESS'"));
    }

    @Test
    void shouldThrowExceptionWhenOrderStatusIsNotNew() {
        //given
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> orderService.successOrderById(1L));

        //then
        assertTrue(exc.getMessage().contains("Only orders with status 'NEW' can be changed into 'SUCCESS'"));
    }

    @Test
    void shouldThrowExceptionWhenOrderIsAlreadySuccess() {
        //given
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.SUCCESS); 
        order.setPaymentStatus(PaymentStatus.COMPLETED);

        Mockito.when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));

        // when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> orderService.successOrderById(1L));

        //then
        assertTrue(exc.getMessage().contains("Only orders with status 'NEW' can be changed into 'SUCCESS'"));
    }





}