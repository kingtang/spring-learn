package org.springframework.dao;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
	void save(User user);
	
	void saveWithException();
	
	User getUser(String id);
	
	User queryUser(String id);
}
