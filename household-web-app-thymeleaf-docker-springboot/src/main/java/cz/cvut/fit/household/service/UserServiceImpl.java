package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.entity.user.VerificationToken;
import cz.cvut.fit.household.repository.VerificationTokenRepository;
import cz.cvut.fit.household.repository.user.jpa.UserRepository;
import cz.cvut.fit.household.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    @Override
    public User createOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findUsersBySearchTerm(String searchTerm) {
        return userRepository.searchByUsername(searchTerm);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findById(username);
    }

    @Override public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public Boolean exists(String username) {
        return userRepository.existsById(username);
    }

    @Override
    public void deleteUserByUsername(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken);
    }
}
