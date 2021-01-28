package com.uniwill.dao.mongo.dal;

import com.uniwill.dao.mongo.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
	@Override
	Optional<Person> findById(String id);

	@Override
	List<Person> findAll();

	@Override
	void deleteById(String id);

	@Override
	Person save(Person peson);


}
