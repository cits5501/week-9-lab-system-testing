/**
 * 
 */

/**
 * check whether user+password combination is valid.
 */
public interface Authenticator {

  boolean isValid(String username, String password);

}
