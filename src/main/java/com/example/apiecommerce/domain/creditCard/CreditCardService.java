package com.example.apiecommerce.domain.creditCard;

import com.example.apiecommerce.domain.creditCard.dto.CreditCardDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.checkerframework.checker.optional.qual.OptionalBottom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    public CreditCardForReturnDto addCreditCard(String userMail, CreditCardDto creditCardDto) {
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validateCardValidity(creditCardDto.getCardValidity());

        CreditCard creditCard = creditCardDtoMapper.map(creditCardDto);
        creditCard.setUser(user);
        CreditCard savedCreditCard = creditCardRepository.save(creditCard);
        return creditCardDtoMapper.mapForReturn(savedCreditCard);
    }

    private void validateCardValidity(String validity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiry = YearMonth.parse(validity, formatter);
        if (cardExpiry.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card validity date is expired");
        }
    }

    public Optional<CreditCardForReturnDto> getCreditCardById(Long id){
        return creditCardRepository.findById(id)
                .map(creditCardDtoMapper::mapForReturn);
    }

    @Transactional
    public void deleteCreditCard(Long creditCardId){
        if (!creditCardRepository.existsById(creditCardId)) {
            throw new EntityNotFoundException("Credit card not found");
        }
        creditCardRepository.deleteById(creditCardId);
    }


}
