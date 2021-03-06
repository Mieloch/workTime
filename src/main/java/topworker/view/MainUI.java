package topworker.view;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.BadCredentialsException;
import topworker.event.LoginEvent;
import topworker.utils.MessagesBundle;
import topworker.view.naviagtion.calendar.WorkCalendarViewImpl;
import topworker.view.naviagtion.download.Download;
import topworker.view.naviagtion.information.Home;
import topworker.view.naviagtion.login.Login;
import topworker.view.naviagtion.signup.SignUp;
import topworker.view.naviagtion.summary.SummaryView;
import topworker.view.utils.AuthenticationService;
import topworker.view.utils.Icons;
import topworker.view.utils.ViewChangeSecurityChecker;

import java.util.Locale;

@Theme("topworkertheme")
@SpringUI()
public class MainUI extends UI {

    @Autowired
    private MessagesBundle messagesBundle;

    @Autowired
    private SpringViewProvider viewProvider;
    private VerticalLayout rootLayout;

    private Navigator navigator;
    private Panel contentPanel;
    private HorizontalLayout contentLayout;
    private GridLayout upperLayout;
    private HorizontalLayout bottomLayout;
    private Button loginButton;
    private Button logoutButton;
    private View languageChanger;



    AuthenticationService authenticationService;


    public MainUI() {
        authenticationService = new AuthenticationService();

    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("Work Time");
        logoutButton = createLogoutButton();
        loginButton = createLoginButton();
        initRootLayout();
        initNavigator();
    }


    public void handleLogoutEvent() {
        try {
            navigator.navigateTo(Home.VIEW_NAME);
            authenticationService.handleLogout();
            removeNavigationButtons();
            addNotLoggedButtons();
        } catch (BadCredentialsException exception) {
            Notification.show(messagesBundle.getMessage("logout_error"), Notification.Type.ERROR_MESSAGE);
        }

    }

    @EventListener
    public void handleLoginEvent(LoginEvent loginEvent) {
        try {
            authenticationService.handleAuthentication(loginEvent.getLogin(), loginEvent.getPassword());
            upperLayout.removeAllComponents();
            addNavigationButtons();
            navigator.navigateTo(WorkCalendarViewImpl.VIEW_NAME);
        } catch (BadCredentialsException exception) {
            Notification.show(messagesBundle.getMessage("log_in_error"), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void initNavigator() {

        navigator = new Navigator(this, contentPanel);
        navigator.addViewChangeListener(new ViewChangeSecurityChecker(this));
        navigator.addProvider(viewProvider);
        navigator.addView("", Home.class);

    }

    private void addNavigationButtons() {
        Button calendarButton = createNavigationButton("", WorkCalendarViewImpl.VIEW_NAME, "calendar");
        Button listButton = createNavigationButton("", SummaryView.VIEW_NAME, "list");
        Button homeButton = createNavigationButton("", Home.VIEW_NAME, "home");
        Button downloadButton = createNavigationButton("", Download.VIEW_NAME, "download");
        upperLayout.addComponent(calendarButton, 0, 0);
        upperLayout.addComponent(listButton, 1, 0);
        upperLayout.addComponent(homeButton, 6, 0);
        upperLayout.addComponent(logoutButton, 7, 0);
        upperLayout.addComponent(downloadButton, 5, 0);
        upperLayout.setComponentAlignment(listButton, Alignment.MIDDLE_CENTER);
        upperLayout.setComponentAlignment(calendarButton, Alignment.MIDDLE_CENTER);
        upperLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
        upperLayout.setComponentAlignment(downloadButton, Alignment.MIDDLE_CENTER);
        upperLayout.setComponentAlignment(logoutButton, Alignment.MIDDLE_CENTER);
    }

    private void removeNavigationButtons() {
        upperLayout.removeAllComponents();
    }


    private void initRootLayout() {
        rootLayout = new VerticalLayout();
        setContent(rootLayout);
        rootLayout.setSizeFull();
        rootLayout.addStyleName("menu-panel");

        initBottomLayout();
        initContentLayout();
        initUpperLayout();

        rootLayout.addComponent(upperLayout);
        rootLayout.addComponent(contentLayout);
        rootLayout.addComponent(bottomLayout);

        rootLayout.setExpandRatio(upperLayout, 1.5f);
        rootLayout.setExpandRatio(contentLayout, 6.0f);
        rootLayout.setExpandRatio(bottomLayout, 0.6f);

    }

    private void initUpperLayout() {
        upperLayout = new GridLayout();
        upperLayout.setRows(1);
        upperLayout.setColumns(8);
        upperLayout.setSizeFull();
        if (authenticationService.isAuthenticated()) {
            addNavigationButtons();
        } else {
            addNotLoggedButtons();
        }
    }

    private void initContentLayout() {
        contentLayout = new HorizontalLayout();
        contentLayout.addStyleName("content-panel");
        contentLayout.setSizeFull();
        contentPanel = new Panel();
        contentPanel.setSizeFull();
        contentLayout.addComponent(contentPanel);
    }

    private void initBottomLayout() {
        bottomLayout = new HorizontalLayout();
        bottomLayout.setSizeFull();
        HorizontalLayout languageButtons = new HorizontalLayout();
        languageButtons.addComponent(createLanguageButton(new Locale("PL", "pl"), Icons.PL));
        languageButtons.addComponent(createLanguageButton(new Locale("EN", "en"), Icons.GB));
        bottomLayout.addComponent(languageButtons);
        bottomLayout.addStyleName("menu-panel");
    }


    private Button createNavigationButton(String caption, final String navigationDestination, String style) {
        Button button = new Button(caption);
        button.setWidth(80f, Unit.PERCENTAGE);
        button.setHeight(80f, Unit.PERCENTAGE);

        button.addStyleName(style);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(navigationDestination);
            }
        });
        return button;
    }

    private Button createLogoutButton() {
        Button logoutButton = new Button("");
        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                handleLogoutEvent();
            }
        });
        logoutButton.addStyleName("logout");
        logoutButton.setWidth(80f, Unit.PERCENTAGE);
        logoutButton.setHeight(80f, Unit.PERCENTAGE);
        return logoutButton;
    }

    private Button createLoginButton() {
        Button loginButton = new Button("");
        loginButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(Login.VIEW_NAME);
            }
        });
        loginButton.addStyleName("login");
        loginButton.setWidth(80f, Unit.PERCENTAGE);
        loginButton.setHeight(80f, Unit.PERCENTAGE);
        return loginButton;
    }

    private void addNotLoggedButtons() {
        Button homeButton = createNavigationButton("", Home.VIEW_NAME, "home");
        Button signupButton = createNavigationButton("", SignUp.VIEW_NAME, "signup");
        upperLayout.addComponent(signupButton, 5, 0);
        upperLayout.setComponentAlignment(signupButton, Alignment.MIDDLE_CENTER);
        upperLayout.addComponent(homeButton, 6, 0);
        upperLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
        upperLayout.addComponent(loginButton, 7, 0);
        upperLayout.setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
    }

    private Button createLanguageButton(final Locale locale, String icon) {
        Button btn = new Button();
        btn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!messagesBundle.getLocale().equals(locale)) {
                    messagesBundle.changeBundle(locale);
                    languageChanger.enter(null);
                }
            }
        });
        btn.setIcon(new ThemeResource(icon));
        btn.addStyleName("transparent_btn");
        return btn;
    }

    public void setLanguageChanger(View languageChanger) {
        this.languageChanger = languageChanger;
    }

}