package com.example.educonnect.educonnect.Entities;

public class UserSession {
    private static UserSession instance;

    private int userId;
    private String userName;
    private String prenom;
    private UserRole role;


    public UserSession(int userId, String userName, String prenom, UserRole role) {
        this.userId = userId;
        this.userName = userName;
        this.prenom = prenom;
        this.role = role;

    }

    public static UserSession getInstace(int userId, String userName, String prenom, UserRole role) {
        if(instance == null) {
            instance = new UserSession(userId, userName, prenom, role);
        }
        return instance;
    }

    public static UserSession initializeUserSession(User u1) {
        instance = new UserSession(u1.getId(), u1.getNom(), u1.getPrenom(), u1.getRole());
        ApplicationContext.getInstance().setUserSession(instance);
        return instance;
    }
}
