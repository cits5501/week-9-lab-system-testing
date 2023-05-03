import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import flak.App;
import flak.Flak;
import flak.Form;
import flak.Response;
import flak.annotations.Post;
import flak.annotations.Route;
import flak.login.FlakSession;
import flak.login.FlakUser;
import flak.login.LoginRequired;
import flak.login.SessionManager;

public class HelloWorld {
  App app;
  
  public HelloWorld(int port) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    app = Flak.createHttpApp(port);
    app.scan(this);
  }
  
  @Route("/")
  public String root() {
    return "<html>To log in, visit the <a href='/login'>login</a> page.";
  }
  
  @Route("/login")
  public String login() throws IOException {
    Path filePath = Path.of("resources/login.html");
    String contents = Files.readString(filePath);
    return contents;
  }
  
  @Route("/app")
  @LoginRequired
  public String appPage(FlakUser user) {
    return "Welcome " + user.getId();
  }
  
  /**
   * Example of how form data can be parsed in a POST request.
   */
  @Post
  @Route("/login")
  public void login(Response r, Form form, SessionManager sessionManager) {
    // just for debugging
    System.err.println("form params were:" + form.parameters());
    
    String login = form.get("username");
    String pass = form.get("password");

    if (login.equals("foo") && pass.equals("bar")) {
      SessionManager dsm = sessionManager;
      FlakUser user = dsm.createUser(login);
      dsm.openSession(app, user, r);
      r.redirect("/app");
    }
    else
      r.redirect("/login");
  }
  
  @Route("/logout")
  public void logout(SessionManager sessionManager) {
    FlakSession session = sessionManager.getCurrentSession(app.getRequest());
    sessionManager.closeSession(session);
    app.getResponse().redirect("/login");
  }


  public static void main(String[] args) throws Exception {
    int port = 8080;
    //App app = Flak.createHttpApp(port);
    //app.scan(new HelloWorld());
    HelloWorld hw = new HelloWorld(port);    
    System.err.println("Serving on port " + port);
    hw.app.start();
    //Desktop.getDesktop().browse(new URI(app.getRootUrl()));
  }
  
//  @Before
//  public void setUp() throws Exception {
//    AppFactory factory = TestUtil.getFactory();
//    factory.setPort(9191);
//    app = factory.createApp();
//
//    preScan();
//    app.scan(this);
//
//    preStart();
//    app.start();
//
//    if (USE_PROXY) {
//      proxy = new DebugProxy(9092, "localhost", 9191);
//      client = new SimpleClient(app.getRootUrl().replace("9191", "9092"));
//    }
//    else {
//      client = new SimpleClient(app.getRootUrl());
//    }
//  }
  
}

