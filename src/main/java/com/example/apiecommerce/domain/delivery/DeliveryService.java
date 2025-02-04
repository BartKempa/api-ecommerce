package com.example.apiecommerce.domain.delivery;

import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class DeliveryService {
   private final DeliveryRepository deliveryRepository;
   private final DeliveryDtoMapper deliveryDtoMapper;

    public DeliveryService(DeliveryRepository deliveryRepository, DeliveryDtoMapper deliveryDtoMapper) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryDtoMapper = deliveryDtoMapper;
    }

    @Transactional
    public DeliveryDto saveDelivery(DeliveryDto deliveryDto){
        Delivery deliveryToSaved = deliveryDtoMapper.map(deliveryDto);
        Delivery savedDelivery = deliveryRepository.save(deliveryToSaved);
        return deliveryDtoMapper.map(savedDelivery);
    }

    public List<DeliveryDto> findAllDeliveries(){
        return StreamSupport.stream(deliveryRepository.findAll().spliterator(), false)
                .map(deliveryDtoMapper::map)
                .toList();
    }


}
