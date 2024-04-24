package cz.cvut.fit.household.service.interfaces;

import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.entity.user.VerificationToken;

import java.util.List;
import java.util.Optional;


public interface UserService {

    /**
     * Create or update user.
     *
     * @param user which is going to be added in database
     * @return freshly saved user
     */
    User createOrUpdateUser(User user);

    /**
     * Retrieve all existing users.
     *
     * @return all existed users in database
     */
    List<User> findAllUsers();

    /**
     * Retrieve list of users whose username matches with search term.
     *
     * @param searchTerm is username which probably similar to some users usernames
     * @return list of users, whose usernames match with given username
     */
    List<User> findUsersBySearchTerm(String searchTerm);

    /**
     * Retrieve  user with given username.
     *
     * @param username of required user
     * @return optional of user with given username, otherwise empty optional
     */
    Optional<User> findUserByUsername(String username);

    Optional<User> findByEmail(String email);

    /**
     * Checks if user with given username exists.
     *
     * @param username of needed username
     * @return true if user founded, otherwise false
     */
    Boolean exists(String username);

    /**
     * Delete user with given username.
     *
     * @param username of needed user
     */
    void deleteUserByUsername(String username);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String verificationToken);
}
