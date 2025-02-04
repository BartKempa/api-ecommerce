package com.example.apiecommerce.domain.delivery;

import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import org.springframework.stereotype.Service;

@Service
public class DeliveryDtoMapper {

    Delivery map(DeliveryDto deliveryDto){
        if (deliveryDto == null){
            return null;
        }
        Delivery delivery = new Delivery();
        delivery.setId(deliveryDto.getId());
        delivery.setDeliveryName(deliveryDto.getDeliveryName());
        delivery.setDeliveryTime(deliveryDto.getDeliveryTime());
        delivery.setDeliveryCharge(deliveryDto.getDeliveryCharge());
        return delivery;
    }

    DeliveryDto map(Delivery delivery){
        if (delivery == null){
            return null;
        }
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setId(delivery.getId());
        deliveryDto.setDeliveryName(delivery.getDeliveryName());
        deliveryDto.setDeliveryTime(delivery.getDeliveryTime());
        deliveryDto.setDeliveryCharge(delivery.getDeliveryCharge());
        return deliveryDto;
    }
}
