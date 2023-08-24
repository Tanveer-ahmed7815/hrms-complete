package com.te.flinko.repository.account;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.dto.account.mongo.ContactPerson;

public interface ContactPersonRepository extends MongoRepository<ContactPerson, String>{

}
