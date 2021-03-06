package controllers;

import play.mvc.*;

import play.api.Environment;
import play.data.*;
import play.db.ebean.Transactional;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import models.users.*;

import views.html.*;

public class LoginController extends Controller {
    private FormFactory formFactory;
 
    @Inject
    public LoginController(FormFactory f) {
        this.formFactory = f;
    }
    
    public Result login() {
        Form<Login> loginForm = formFactory.form(Login.class);
        return ok(login.render(loginForm,User.getUserById(session().get("email"))));
}
public Result loginSubmit() {
    Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();

    if (loginForm.hasErrors()) {
        return badRequest(login.render(loginForm,User.getUserById(session().get("email"))));
    } else {
        // Up to here, the logic is the same as in the addItemSubmit() action
        // method of the HomeController. However, while the data from the item 
        // form was used to create an entry in the corresponding database table,
        // here the data is not stored but rather its successful validation used
        // as a prompt for the beginning of a new session.

        // Clear any existing session
        session().clear();

        // Store the logged-in email in a cookie i.e. start a new session
        session("email", loginForm.get().getEmail());

        return redirect(controllers.routes.HomeController.index());
    }
}
public Result logout() {
    // To log the user out, we just delete the current session
    session().clear();
    flash("success", "You have been logged out");
    return redirect(routes.LoginController.login());
}

public Result registerUser() {
    Form<User> userForm = formFactory.form(User.class);
    return ok(registerUser.render(userForm,User.getUserById(session().get("email"))));
}

public Result registerUserSubmit() {
    Form<User> newUserForm = formFactory.form(User.class).bindFromRequest();
    if (newUserForm.hasErrors()) {
        return badRequest(registerUser.render(newUserForm,User.getUserById(session().get("email"))));
    } else {
        User newUser = newUserForm.get();

        if(User.getUserById(newUser.getEmail())==null){
            newUser.save();
        }else{
            newUser.update();
    }
    flash("success", "User " + newUser.getName() + " was added/updated.");
    return redirect(controllers.routes.LoginController.login()); 
    }
}


}