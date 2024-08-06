package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.LogAutoServiceModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LogAutoServiceRepository extends CrudRepository<LogAutoServiceModel, Long> {
}
