package kr.ac.hansung.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import kr.ac.hansung.model.User;

@Service
public class UserService {

	//webapp은 리퀘스트가 올때마다 스레드가 만들어짐 이 스레드들이 변수에 접근시 상호배제가 중요 
	//상호배제를 수행 하면서 id를 증가시키기 위한 변수 AtomicLong
	private static final AtomicLong counter = new AtomicLong();

	private static List<User> users;
	
	public UserService() {
		users = new ArrayList<User>();
		
		users.add(new User(counter.incrementAndGet(), "Sam", 30, 70000));
		users.add(new User(counter.incrementAndGet(), "Tom", 40, 50000));
		users.add(new User(counter.incrementAndGet(), "Jerome", 45, 30000));
		users.add(new User(counter.incrementAndGet(), "Silvia", 50, 40000));
	}
	
	public List<User> findAllUsers(){
		return users;
	}
	
	public User findById(long id) {
		for(User user : users) {
			if(user.getId() ==id) {
				return user;
			}
		}
		
		return null;
	}
	
	public User findByName(String name) {
		for(User user : users) {
			if(user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}
	
	public void saveUser(User user) {
		user.setId(counter.incrementAndGet());
		users.add(user);
	}
	
	public void updateUser(User user) {
		int index = users.indexOf(user);
		users.set(index,  user);
	}
	
	public void deleteUserById(long id) {
		for(Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			if(user.getId() == id) {
				iterator.remove();
			}
		}
	}
	
	public boolean isUserExist(User user) {
		return findByName(user.getName())!=null;
	}
	
	public void deleteAllUsers() {
		users.clear();
	}
}
