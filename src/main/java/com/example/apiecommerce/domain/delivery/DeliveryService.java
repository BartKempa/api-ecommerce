package com.example.apiecommerce.domain.delivery;

import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import com.example.apiecommerce.domain.delivery.dto.DeliveryUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public List<DeliveryDto> findAllActiveDeliveries(){
        return deliveryRepository.findAll()
                .stream()
                .filter(Delivery::isActive)
                .map(deliveryDtoMapper::map)
                .toList();
    }

    public Optional<DeliveryDto> findDeliveryById(Long id){
        return deliveryRepository.findById(id)
                .map(deliveryDtoMapper::map);
    }

    @Transactional
    public void deleteDelivery(Long deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        delivery.setActive(false);
    }

    @Transactional
    public void updateDelivery(Long deliveryId, DeliveryUpdateDto deliveryUpdateDto){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        if (deliveryUpdateDto.getDeliveryName() != null){
            delivery.setDeliveryName(deliveryUpdateDto.getDeliveryName());
        }
        if (deliveryUpdateDto.getDeliveryTime() != null){
            delivery.setDeliveryTime(deliveryUpdateDto.getDeliveryTime());
        }
        if (deliveryUpdateDto.getDeliveryCharge() != null){
            delivery.setDeliveryCharge(deliveryUpdateDto.getDeliveryCharge());
        }
        deliveryRepository.save(delivery);
    }
}
