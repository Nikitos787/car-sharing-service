package project.security;

import project.exception.AuthenticationException;
import project.model.user.User;

public interface AuthenticationService {

    User register(String email, String password, String firstName, String lastName);

    User login(String email, String password) throws AuthenticationException;
}
