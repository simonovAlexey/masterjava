package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;

public class MailWSClientMain {
    public static void main(String[] args) {
        MailWSClient.sendMail(
                ImmutableSet.of(new Addressee("Григорий Кислин <nci@tut.by>")),
                ImmutableSet.of(new Addressee("Мастер Java <3694369@gmail.com>")), "Subject", "Body");
    }
}