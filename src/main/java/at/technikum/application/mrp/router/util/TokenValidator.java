package at.technikum.application.mrp.router.util;

public class TokenValidator {

    public boolean isValidToken(String token)
    {
        if(token.endsWith("-mrpToken"))
        {
            return true;
        }
           return false;
    }
}
