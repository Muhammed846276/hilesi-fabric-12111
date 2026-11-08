package com.example.hilesi.util;
import java.util.HashSet;
import java.util.Set;
public final class FriendManager {
    private static final Set<String> FRIENDS = new HashSet<>();
    private FriendManager(){}
    public static boolean isFriend(String name){ return name!=null && FRIENDS.contains(name.toLowerCase()); }
    public static void toggle(String name){ if(name==null)return; String n=name.toLowerCase(); if(FRIENDS.contains(n)) FRIENDS.remove(n); else FRIENDS.add(n); }
    public static Set<String> all(){ return FRIENDS; }
}
