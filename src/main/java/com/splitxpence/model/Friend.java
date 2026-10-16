package com.splitxpence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;


    public Friend() {
    }


    public Friend(
            User user,
            User friend) {

        this.user = user;
        this.friend = friend;
    }


    public Long getId() {
        return id;
    }


    public User getUser() {
        return user;
    }


    public User getFriend() {
        return friend;
    }
}