package org.springframework.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class UserServiceImpl implements UserService {

	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(User user) {
		jdbcTemplate.update("update t_user set name = 'aa' where id = ?",new Object[]{1},new int[]{java.sql.Types.INTEGER});
		User result = jdbcTemplate.queryForObject("select * from t_user where id = " + 1, new UserRowMapper());
		System.out.println(result);
	}

	@Override
	public User getUser(String id) {
		User result = jdbcTemplate.queryForObject("select * from t_user where id = " + 1, new UserRowMapper());
		return result;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public User queryUser(String id) {
		User result = jdbcTemplate.queryForObject("select * from t_user where id = " + 1, User.class);
		System.out.println(result);
		return result;
	}

	@Override
	public void saveWithException() {
		jdbcTemplate.update("update t_user set name = 'cc' where id = ?",new Object[]{1},new int[]{java.sql.Types.INTEGER});
		User result = jdbcTemplate.queryForObject("select * from t_user where id = " + 1, new UserRowMapper());
		System.out.println(result);
		//throw new RuntimeException();
	}

}
