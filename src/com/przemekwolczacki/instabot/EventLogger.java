package com.przemekwolczacki.instabot;

import javax.swing.*;

public class EventLogger {
    public static void StartFollowLikePoolMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Rozpoczęto obserwowanie i lajkowanie użytkowników z bazy \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void EndFollowLikePoolMessage(JTextArea eventLogArea, int i){
        if(i==0) eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Dodaj użytkowników do bazy \n");
        else if (i==1) eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Przekroczono limit obserwacji/lajków na dziś \n");
        else eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Zakończono lajkowanie/obserwowanie \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void AddingToDbMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Rozpoczęto dodawanie linków do profili w bazie\n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void EmptyPoolMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Dodaj użytkowników do bazy \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void ReachedUserLimitInDbMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Limit użytkowników w bazie przekroczony \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void SomethingWentWrongMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Coś poszło nie tak. Nie można dodać do puli \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void EndOfAddingToDbMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Zakończono dodawanie linków do profili w bazie \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void StartDeleteObservationsMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Rozpoczęto usuwanie osób obserwowanych dłużej niż 3 dni \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void NoOneToRemoveMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Aktualnie nie obserwujesz nikogo dłużej niż 3 dni \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void EndDeleteObservationsMessage(JTextArea eventLogArea){
        eventLogArea.append("========= [" + DateModeler.GetTimeOfDay() + "] Zakończono usuwanie obserwacji trwających dłużej niż 3 dni \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void FollowUserMessage(JTextArea eventLogArea, String user){
        eventLogArea.append("[" + DateModeler.GetTimeOfDay() + "] Zaobserwowano użytkownika " + user  + "\n");

        eventLogArea.update(eventLogArea.getGraphics());
    }

    public static void LikeUserMessage(JTextArea eventLogArea, String user){
        eventLogArea.append("[" + DateModeler.GetTimeOfDay() + "] Polubiono post " + user + " \n");

        eventLogArea.update(eventLogArea.getGraphics());
    }
}
