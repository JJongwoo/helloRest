package kr.ac.hansung.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import kr.ac.hansung.exception.ErrorResponse;
import kr.ac.hansung.exception.UserDuplicatedException;
import kr.ac.hansung.exception.UserNotFoundException;
import kr.ac.hansung.model.User;
import kr.ac.hansung.service.UserService;

@RestController
@RequestMapping("/api")
public class RestAPIController {

	@Autowired
	UserService userService;
	
	// --- Retrieve All Users ---
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() { //header, body(json), HTTPStatus
		List<User> users = userService.findAllUsers();
		if(users.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	// --- Retrieve Single User ---
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	public ResponseEntity<User> getUser(@PathVariable("id")long id) { //header, body(json), HTTPStatus
		
		User user = userService.findById(id);
		if(user==null){
			//return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			//to do list: custom exception
			throw new UserNotFoundException(id);
		}
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	
	// --- Create a User ---
		@RequestMapping(value="/users", method=RequestMethod.POST) //request body(json)
		public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) { //json으로 된 바디가 객체로 넘어오게됨
			
			if(userService.isUserExist(user)) {
				//to do list: exception		
				throw new UserDuplicatedException(user.getName());
			}
			userService.saveUser(user);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("api/users/{id}").
									buildAndExpand(user.getId()).toUri());//save한 user의 id를 갖고와서 "api/users/{id}"를 uri형태로 바꾼후 setLocation
			return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		}
		
		@RequestMapping(value="/users/{id}", method=RequestMethod.POST)
		public ResponseEntity<User> updateUser(@PathVariable("id")long id, @RequestBody User user){
			
			User currentUser = userService.findById(id);
			
			if(currentUser == null) {
				throw new UserNotFoundException(id);
			}
			
			currentUser.setName(user.getName());
			currentUser.setAge(user.getAge());
			currentUser.setSalary(user.getSalary());
			
			userService.updateUser(currentUser);
			return new ResponseEntity<User>(currentUser, HttpStatus.OK);
		}
		
		@RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
		public ResponseEntity<User> deleteUser(@PathVariable("id")long id){
			
			User currentUser = userService.findById(id);
			
			if(currentUser == null) {
				throw new UserNotFoundException(id);
			}
			
			userService.deleteUserById(id);
			return new ResponseEntity<User>(currentUser, HttpStatus.NO_CONTENT);
		}
		
		@RequestMapping(value="/users", method=RequestMethod.DELETE)
		public ResponseEntity<User> deleteAllUser(){
			
						
			userService.deleteAllUsers();
			return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
		}
		
		
		@ExceptionHandler(UserNotFoundException.class)
		public ResponseEntity<ErrorResponse> handleUserNotFoundException(HttpServletRequest req, UserNotFoundException ex){
			
			ErrorResponse errorResponse = new ErrorResponse();
			
			String requestURL = req.getRequestURL().toString();
			errorResponse.setRequestURL(requestURL);
			errorResponse.setErrorCode("user.notfound.exception");
			errorResponse.setErrorMsg("user with id" + ex.getUserId() + " not found");
			
			return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
		}
		
		@ExceptionHandler(UserDuplicatedException.class)
		public ResponseEntity<ErrorResponse> handleUserDuplicatedException(HttpServletRequest req, UserDuplicatedException ex){
			
			ErrorResponse errorResponse = new ErrorResponse();
			
			String requestURL = req.getRequestURL().toString();
			errorResponse.setRequestURL(requestURL);
			errorResponse.setErrorCode("user.duplicated.exception");
			errorResponse.setErrorMsg("Unable to create. A user with name " + ex.getUsername() + " already exist");
			
			return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.CONFLICT);
		}
		
}
