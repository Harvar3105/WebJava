package app.api;


import app.dal.UserDao;
import app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private UserDao dao;

    @Autowired
    public UserController(UserDao dao) { this.dao = dao; }

    @GetMapping("/version")
    public ResponseEntity<Integer> getVersion(){
        return ResponseEntity.ok(1);
    }

    @ResponseBody
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers()
    {
        return ResponseEntity.ok(dao.getAllUsers());
    }

    @ResponseBody
    @GetMapping(value = "/users/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #user == authentication.name")
    public ResponseEntity<User> getUserByName(@PathVariable String user){
        return ResponseEntity.ok(dao.getUserByName(user));
    }
}
