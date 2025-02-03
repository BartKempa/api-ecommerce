package com.example.apiecommerce.domain.creditCard;

import com.example.apiecommerce.domain.creditCard.dto.CreditCardDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import org.springframework.stereotype.Service;

@Service
public class CreditCardDtoMapper {
    CreditCard map(CreditCardDto creditCardDto) {
        if (creditCardDto == null) {
            return null;
        }
        CreditCard creditCard = new CreditCard();
        creditCard.setCardNumber(creditCardDto.getCardNumber());
        creditCard.setCardValidity(creditCardDto.getCardValidity());
        return creditCard;
    }

    CreditCardDto map(CreditCard creditCard){
        if (creditCard == null){
            return null;
        }
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setId(creditCard.getId());
        creditCardDto.setCardNumber(creditCard.getCardNumber());
        creditCardDto.setCardValidity(creditCard.getCardValidity());
        return creditCardDto;
    }

    public CreditCardForReturnDto mapForReturn(CreditCard creditCard) {
        if (creditCard == null) {
            return null;
        }
        CreditCardForReturnDto card = new CreditCardForReturnDto();
        card.setId(creditCard.getId());
        card.setAbbreviationCardNumber("**** **** **** " + creditCard.getCardNumber().substring(creditCard.getCardNumber().length() - 4));
        card.setAbbreviationCardValidity("**/**");
        return card;
    }
}
