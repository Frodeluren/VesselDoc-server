package net.vesseldoc.server.service;

import net.vesseldoc.server.model.DAOUser;
import net.vesseldoc.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /*
    /**
     * Get displayName for user
     *
     * @return the display name
     *
    public String getDisplayNameForUser(String username) { return repository.findDisplayNameForUser(username);
    }
*/
    /**
     * Get UserDetails for user
     *
     * @return the user details
     */
    public DAOUser getUserDetails(String username) { return repository.getUserDetails(username);
    }

}
