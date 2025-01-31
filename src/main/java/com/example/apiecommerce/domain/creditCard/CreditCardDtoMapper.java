package com.example.apiecommerce.domain.creditCard;

import com.example.apiecommerce.domain.creditCard.dto.CreditCardDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import org.springframework.stereotype.Service;

@Service
public class CreditCardDtoMapper {

    CreditCard map(CreditCardDto creditCardDto){
        if (creditCardDto == null){
            return null;
        }
        CreditCard creditCard = new CreditCard();
        creditCard.setCardNumber(creditCardDto.getCardNumber());
        creditCard.setCardValidity(creditCardDto.getCardValidity());
        creditCard.setCardCVV(creditCardDto.getCardCVV());
        return creditCard;
    }

    CreditCardForReturnDto mapForReturn(CreditCard creditCard){
        if (creditCard == null){
            return null;
        }
        CreditCardForReturnDto card = new CreditCardForReturnDto();
        card.setId(creditCard.getId());
        card.setAbbreviationCardNumber(creditCard.getCardNumber().substring(0,5) + " **** **** ****");
        card.setAbbreviationCardValidity(creditCard.getCardValidity().charAt(0) + "*/**");
        card.setAbbreviationCardCVV(creditCard.getCardCVV().charAt(0) + "**");
        return card;
    }

}
