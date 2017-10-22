package cn.shu;

import cn.shu.dao.UserDao;
import cn.shu.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDemo {
	@Autowired
	UserDao userDao;

	@Test
	public void contextLoad(){
		User user = new User();
		user.setFilmId(1);
		user.setName("135_1504859384362");
		user.setPasswd("mt42L5KjMUiHo148");
		Date date = new Date();
		user.setCreateDate(date);
		user.setUpdateDate(date);
		user.setExpireDate(new Date(date.getTime()+259200000L));
		System.out.println(userDao.save(user));
		return;
	}
}
