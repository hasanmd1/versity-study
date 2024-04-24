package cz.cvut.fit.household.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.repository.VerificationTokenRepository;
import cz.cvut.fit.household.repository.user.jpa.UserRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    User user1 = new User("user1","1","User","1","user1@gmail.com",null);
    User user2 = new User("user2","2","User","2","user2@gmail.com",null);
    List<User> users = Arrays.asList(user1,user2);

    @Before
    public void setup() {
        when(userRepository.findById(user1.getUsername())).thenReturn(Optional.of(user1));
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.existsById(user1.getUsername())).thenReturn(true);
        when(userRepository.existsById("non_existent")).thenReturn(false);
        when(userRepository.searchByUsername("user")).thenReturn(users);
        when(userRepository.save(user1)).thenReturn(user1);
    }

    @Test
    public void findUserByUsername() {
        Optional<User> user = userServiceImpl.findUserByUsername("user1");
        assertTrue("user doesn't exist",user.isPresent());
        assertEquals(user.get().getUsername(),user1.getUsername());
    }

    @Test
    public void NonExistentUser() {
        Optional<User> user = userServiceImpl.findUserByUsername("user3");
        assertFalse("user does exist",user.isPresent());
    }

    @Test
    public void findAllUsers() {
        List<User> usersRepository= userRepository.findAll();
        assertEquals("different size of the users' list", users.size(), usersRepository.size());

    }

    @Test
    public void exist() {
        Boolean exist = userServiceImpl.exists(user1.getUsername());
        assertEquals(true,exist);
    }

    @Test
    public void notExist() {
        Boolean exist = userServiceImpl.exists("non_existent");
        assertEquals(false,exist);
    }

    @Test
    public void findUsersBySearchTerm() {
        List<User> foundUsers = userServiceImpl.findUsersBySearchTerm("user");
        assertEquals(users, foundUsers);
    }

    @Test
    public void createOrUpdateUser() {
        String gmail="user1new@gmail.com";
        user1.setEmail(gmail);
        User user = userServiceImpl.createOrUpdateUser(user1);
        assertEquals(user.getEmail(),user1.getEmail());
    }


}
