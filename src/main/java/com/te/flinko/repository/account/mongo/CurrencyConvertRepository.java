package com.te.flinko.repository.account.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.account.mongo.CurrencyConvert;

public interface CurrencyConvertRepository extends MongoRepository<CurrencyConvert, String> {

}
