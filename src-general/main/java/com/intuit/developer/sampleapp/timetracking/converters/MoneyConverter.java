package com.intuit.developer.sampleapp.timetracking.converters;

import org.joda.money.Money;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * A class that knows how to convert Joda Money objects to and from a string.
 * Used in ORM deserialization/serialization
 * <p/>
 * User: russellb337
 * Date: 6/25/14
 * Time: 8:41 AM
 */
@Converter
public class MoneyConverter implements AttributeConverter<Money, String> {
    @Override
    public String convertToDatabaseColumn(Money money) {
        return money.toString();
    }

    @Override
    public Money convertToEntityAttribute(String s) {
        return Money.parse(s);
    }
}
