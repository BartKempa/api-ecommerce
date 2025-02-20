package com.example.apiecommerce.domain.delivery;

import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import com.example.apiecommerce.domain.delivery.dto.DeliveryUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepositoryMock;
    @Mock
    private DeliveryDtoMapper deliveryDtoMapperMock;
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService(deliveryRepositoryMock, deliveryDtoMapperMock);
    }

    @Test
    void shouldSaveDelivery() {
        // given
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryName("Poczta");
        deliveryDto.setDeliveryTime("3-4 dni");
        deliveryDto.setDeliveryCharge(15.80);

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryName("Poczta");
        delivery.setDeliveryTime("3-4 dni");
        delivery.setDeliveryCharge(15.80);

        DeliveryDto expectedDeliveryDto = new DeliveryDto();
        expectedDeliveryDto.setId(1L);
        expectedDeliveryDto.setDeliveryName("Poczta");
        expectedDeliveryDto.setDeliveryTime("3-4 dni");
        expectedDeliveryDto.setDeliveryCharge(15.80);

        Mockito.when(deliveryDtoMapperMock.map(deliveryDto)).thenReturn(delivery);
        Mockito.when(deliveryRepositoryMock.save(delivery)).thenReturn(delivery);
        Mockito.when(deliveryDtoMapperMock.map(delivery)).thenReturn(expectedDeliveryDto);

        // when
        DeliveryDto actualDeliveryDto = deliveryService.saveDelivery(deliveryDto);

        // then
        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        Mockito.verify(deliveryRepositoryMock).save(captor.capture());
        Delivery capturedDelivery = captor.getValue();

        assertEquals(deliveryDto.getDeliveryName(), capturedDelivery.getDeliveryName());
        assertEquals(deliveryDto.getDeliveryTime(), capturedDelivery.getDeliveryTime());
        assertEquals(deliveryDto.getDeliveryCharge(), capturedDelivery.getDeliveryCharge());

        assertEquals(expectedDeliveryDto, actualDeliveryDto);

        Mockito.verify(deliveryDtoMapperMock).map(deliveryDto);
        Mockito.verify(deliveryDtoMapperMock).map(delivery);
    }

    @Test
    void shouldFindAllActiveDeliveries() {
        // given
        Delivery delivery1 = new Delivery();
        delivery1.setId(1L);
        delivery1.setDeliveryName("Poczta");
        delivery1.setDeliveryTime("3-4 dni");
        delivery1.setDeliveryCharge(15.80);
        delivery1.setActive(true);

        Delivery delivery2 = new Delivery();
        delivery2.setId(2L);
        delivery2.setDeliveryName("Kurier");
        delivery2.setDeliveryTime("2 dni");
        delivery2.setDeliveryCharge(25.80);
        delivery2.setActive(false);

        DeliveryDto deliveryDto1 = new DeliveryDto();
        deliveryDto1.setId(1L);
        deliveryDto1.setDeliveryName("Poczta");
        deliveryDto1.setDeliveryTime("3-4 dni");
        deliveryDto1.setDeliveryCharge(15.80);

        List<Delivery> deliveryList = List.of(delivery1, delivery2);

        Mockito.when(deliveryRepositoryMock.findAll()).thenReturn(deliveryList);
        Mockito.when(deliveryDtoMapperMock.map(delivery1)).thenReturn(deliveryDto1);

        // when
        List<DeliveryDto> allActiveDeliveriesResult = deliveryService.findAllActiveDeliveries();

        // then
        assertEquals(1, allActiveDeliveriesResult.size());
        assertEquals(deliveryDto1, allActiveDeliveriesResult.get(0));
    }

    @Test
    void shouldGetEmptyListWhenNoDeliveries() {
        // given
        Mockito.when(deliveryRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // when
        List<DeliveryDto> allActiveDeliveriesResult = deliveryService.findAllActiveDeliveries();

        // then
        assertTrue(allActiveDeliveriesResult.isEmpty());
    }

    @Test
    void shouldGetEmptyListWhenAllDeliveriesAreInactive() {
        // given
        Delivery delivery1 = new Delivery();
        delivery1.setId(1L);
        delivery1.setDeliveryName("Poczta");
        delivery1.setDeliveryTime("3-4 dni");
        delivery1.setDeliveryCharge(15.80);
        delivery1.setActive(false);

        Delivery delivery2 = new Delivery();
        delivery2.setId(2L);
        delivery2.setDeliveryName("Kurier");
        delivery2.setDeliveryTime("2 dni");
        delivery2.setDeliveryCharge(25.80);
        delivery2.setActive(false);

        List<Delivery> deliveryList = List.of(delivery1, delivery2);

        Mockito.when(deliveryRepositoryMock.findAll()).thenReturn(deliveryList);

        // when
        List<DeliveryDto> allActiveDeliveriesResult = deliveryService.findAllActiveDeliveries();

        // then
        assertTrue(allActiveDeliveriesResult.isEmpty());
    }

    @Test
    void shouldFindDeliveryById() {
        //given
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryName("Poczta");
        delivery.setDeliveryTime("3-4 dni");
        delivery.setDeliveryCharge(15.80);
        delivery.setActive(true);

        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setId(1L);
        deliveryDto.setDeliveryName("Poczta");
        deliveryDto.setDeliveryTime("3-4 dni");
        deliveryDto.setDeliveryCharge(15.80);

        Mockito.when(deliveryRepositoryMock.findById(1L)).thenReturn(Optional.of(delivery));
        Mockito.when(deliveryDtoMapperMock.map(delivery)).thenReturn(deliveryDto);

        //when
        Optional<DeliveryDto> result = deliveryService.findDeliveryById(1L);

        //then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Poczta", result.get().getDeliveryName());
        assertEquals("3-4 dni", result.get().getDeliveryTime());
        assertEquals(15.80, result.get().getDeliveryCharge());
        Mockito.verify(deliveryDtoMapperMock).map(delivery);
    }

    @Test
    void shouldThrowExceptionWhenTryFindNotExistDeliveryById() {
        //given
        long nonExistingDeliveryId = 111L;

        Mockito.when(deliveryRepositoryMock.findById(nonExistingDeliveryId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> deliveryService.findDeliveryById(nonExistingDeliveryId));

        //then
        assertTrue(exc.getMessage().contains("Delivery not found"));
        Mockito.verify(deliveryRepositoryMock).findById(nonExistingDeliveryId);
    }

    @Test
    void shouldDeleteDelivery() {
        //given
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryName("Poczta");
        delivery.setDeliveryTime("3-4 dni");
        delivery.setDeliveryCharge(15.80);
        delivery.setActive(true);

        Mockito.when(deliveryRepositoryMock.findById(1L)).thenReturn(Optional.of(delivery));

        //when
        deliveryService.deleteDelivery(1L);

        //then
        assertFalse(delivery.isActive());
        Mockito.verify(deliveryRepositoryMock).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTryDeleteNotExistDelivery() {
        //given
        long nonExistingDeliveryId = 111L;

        Mockito.when(deliveryRepositoryMock.findById(nonExistingDeliveryId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> deliveryService.deleteDelivery(nonExistingDeliveryId));

        //then
        assertTrue(exc.getMessage().contains("Delivery not found"));
        Mockito.verify(deliveryRepositoryMock).findById(nonExistingDeliveryId);}

    @Test
    void shouldNotChangeStatusWhenDeleteAlreadyInactiveDelivery() {
        //given
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryName("Poczta");
        delivery.setDeliveryTime("3-4 dni");
        delivery.setDeliveryCharge(15.80);
        delivery.setActive(false);

        Mockito.when(deliveryRepositoryMock.findById(1L)).thenReturn(Optional.of(delivery));

        //when
        deliveryService.deleteDelivery(1L);

        //then
        assertFalse(delivery.isActive());
        Mockito.verify(deliveryRepositoryMock).findById(1L);
    }

    @Test
    void shouldUpdateAllDetailsAboutDelivery() {
        //given
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryName("Poczta");
        delivery.setDeliveryTime("3-4 dni");
        delivery.setDeliveryCharge(15.80);
        delivery.setActive(true);

        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        deliveryUpdateDto.setDeliveryName("Super poczta");
        deliveryUpdateDto.setDeliveryTime("2-3 dni");
        deliveryUpdateDto.setDeliveryCharge(16.80);

        Mockito.when(deliveryRepositoryMock.findById(1L)).thenReturn(Optional.of(delivery));

        //when
        deliveryService.updateDelivery(1L, deliveryUpdateDto);

        //then
        Mockito.verify(deliveryRepositoryMock).findById(1L);
        assertEquals("Super poczta", delivery.getDeliveryName());
        assertEquals("2-3 dni", delivery.getDeliveryTime());
        assertEquals(16.80, delivery.getDeliveryCharge());
    }

    @Test
    void shouldUpdatePartDetailsAboutDelivery() {
        //given
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setDeliveryName("Poczta");
        delivery.setDeliveryTime("3-4 dni");
        delivery.setDeliveryCharge(15.80);
        delivery.setActive(true);

        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        deliveryUpdateDto.setDeliveryName("Super poczta");

        Mockito.when(deliveryRepositoryMock.findById(1L)).thenReturn(Optional.of(delivery));

        //when
        deliveryService.updateDelivery(1L, deliveryUpdateDto);

        //then
        Mockito.verify(deliveryRepositoryMock).findById(1L);
        assertEquals("Super poczta", delivery.getDeliveryName());
        assertEquals("3-4 dni", delivery.getDeliveryTime());
        assertEquals(15.80, delivery.getDeliveryCharge());
    }

    @Test
    void shouldThrowExceptionWhenUpdateNotExistDelivery() {
        //given
        long nonExistingDeliveryId = 111L;
        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        Mockito.when(deliveryRepositoryMock.findById(nonExistingDeliveryId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> deliveryService.updateDelivery(111L, deliveryUpdateDto));

        //then
        assertTrue(exc.getMessage().contains("Delivery not found"));
    }
}