package com.example.apiecommerce.domain.creditCard;

import com.example.apiecommerce.domain.creditCard.dto.CreditCardDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final CreditCardDtoMapper creditCardDtoMapper;
    private final UserRepository userRepository;

    public CreditCardService(CreditCardRepository creditCardRepository, CreditCardDtoMapper creditCardDtoMapper, UserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.creditCardDtoMapper = creditCardDtoMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public CreditCardForReturnDto addCreditCard(String userMail, CreditCardDto creditCardDto){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        CreditCard creditCard = creditCardDtoMapper.map(creditCardDto);
        creditCard.setUser(user);
        CreditCard savedCreditCard = creditCardRepository.save(creditCard);
        return creditCardDtoMapper.mapForReturn(savedCreditCard);
    }

    
}
